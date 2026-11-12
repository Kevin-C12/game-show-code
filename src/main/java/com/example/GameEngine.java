package com.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameEngine {

    private static final int ANSWER_SECONDS = 20;

    public enum Result {
        STEP_CORRECT,
        CORRECT,
        WRONG,
        MATCH_WON,
        MATCH_FORFEITED
    }

    private final GameState state = new GameState();
    private final List<Integer> dotWinners = new ArrayList<>();
    private TeamRoster teamOne = new TeamRoster("Team 1", List.of("Player 1"));
    private TeamRoster teamTwo = new TeamRoster("Team 2", List.of("Player 2"));
    private MatchupScheduler matchupScheduler =
        new MatchupScheduler(teamOne, teamTwo, new Random());
    private Matchup currentMatchup = matchupScheduler.next();
    private Random random = new Random();
    private SeriesController series = new SeriesController(random);
    private int[] numbers;
    private List<int[]> steps = List.of();
    private int stepIndex;
    private boolean randomTargetStep;
    private String sortType;
    private int secondsLeft = ANSWER_SECONDS;
    private boolean buzzed;
    private boolean playing;
    private int lastAnswerTeam;

    public static final int[] SORTED_DEMO = {
        7, 15, 29, 42, 61, 83
    };

    public static final int[] UNSORTED_DEMO = {
        42, 15, 83, 7, 29, 61
    };

    public static final int[] WORST_CASE_DEMO = {
        83, 61, 42, 29, 15, 7
    };

    public void startQuestion() throws IOException {
        if (playing) {
            throw new IllegalStateException("Finish the current round first");
        }
        if (series.isRoundComplete()) {
            throw new IllegalStateException("Advance to the next round first");
        }

        QuestionRequest request = series.nextQuestion();
        String nextSort = request.sortId();
        Path questionFile;
        try {
            questionFile = switch (nextSort) {
                case InsertionSort.SORT_ID -> InsertionSort.run();
                case SelectionSort.SORT_ID -> SelectionSort.run();
                case MergeSort.SORT_ID -> MergeSort.run();
                default -> throw new IllegalStateException(
                    "Unknown sort: " + nextSort);
            };
        } catch (Exception exception) {
            throw new IOException("Could not generate question", exception);
        }

        loadQuestion(questionFile, nextSort, request.randomTargetStep());
    }

    void loadQuestion(Path questionFile, String expectedSort) throws IOException {
        loadQuestion(questionFile, expectedSort, false);
    }

    void loadQuestion(Path questionFile, String expectedSort,
                      boolean useRandomTargetStep) throws IOException {
        JsonObject question;
        try {
            question = JsonParser.parseString(Files.readString(questionFile))
                .getAsJsonObject();
        } catch (RuntimeException exception) {
            throw new IOException("Question JSON is invalid", exception);
        }

        if (!question.has("sort")
                || !expectedSort.equals(question.get("sort").getAsString())) {
            throw new IOException("Question sort does not match " + expectedSort);
        }
        if (!question.has("unOrderedList") || !question.has("sortSteps")) {
            throw new IOException("Question is missing required fields");
        }

        int[] original = toArray(question.getAsJsonArray("unOrderedList"));
        JsonObject savedSteps = question.getAsJsonObject("sortSteps");
        List<int[]> loadedSteps = new ArrayList<>();
        for (int index = 1; index <= savedSteps.size(); index++) {
            JsonArray savedStep = savedSteps.getAsJsonArray("step" + index);
            if (savedStep == null) {
                throw new IOException("Missing sort step " + index);
            }
            int[] step = toArray(savedStep);
            if (step.length != original.length) {
                throw new IOException("Sort step " + index + " has the wrong length");
            }
            loadedSteps.add(step);
        }
        if (loadedSteps.isEmpty()) {
            throw new IOException("Question has no sorting steps");
        }

        numbers = original;
        steps = List.copyOf(loadedSteps);
        randomTargetStep = useRandomTargetStep;
        stepIndex = useRandomTargetStep ? random.nextInt(steps.size()) : 0;
        sortType = expectedSort;
        openBuzzers(false);
    }

    public boolean loadDemoQuestion(int[] demoArray) {
        if (!playing) {
            return false;
        }

        List<int[]> demoSteps = new ArrayList<>(switch (sortType) {
            case SelectionSort.SORT_ID -> SelectionSort.stepsFor(demoArray).values();
            case MergeSort.SORT_ID -> MergeSort.stepsFor(demoArray).values();
            default -> InsertionSort.stepsFor(demoArray).values();
        });
        if (demoSteps.isEmpty()) {
            return false;
        }
        numbers = demoArray.clone();
        steps = List.copyOf(demoSteps);
        stepIndex = randomTargetStep ? random.nextInt(steps.size()) : 0;
        openBuzzers(false);
        return true;
    }

    private void openBuzzers(boolean rotateMatchup) {
        if (rotateMatchup) {
            currentMatchup = matchupScheduler.next();
        }
        state.clearCurrentPlayer();
        secondsLeft = ANSWER_SECONDS;
        buzzed = false;
        playing = true;
    }

    public boolean buzz(int player) {
        if (!playing || buzzed || (player != 1 && player != 2)) {
            return false;
        }

        state.setCurrentPlayer(player);
        buzzed = true;
        return true;
    }

    public Result submitAnswer(int[] answer) {
        if (!playing || !buzzed) {
            throw new IllegalStateException(
                "Question is not ready for submission");
        }

        lastAnswerTeam = state.getCurrentPlayer();
        if (!Arrays.equals(answer, steps.get(stepIndex))) {
            return resolveWrong();
        }

        state.recordCorrect();
        dotWinners.add(lastAnswerTeam);
        numbers = steps.get(stepIndex).clone();
        if (state.isMatchOver()) {
            finishRound();
            playing = false;
            return Result.MATCH_WON;
        }

        if (!randomTargetStep && advanceStep()) {
            openBuzzers(true);
            return Result.STEP_CORRECT;
        }

        currentMatchup = matchupScheduler.next();
        playing = false;
        return Result.CORRECT;
    }

    public boolean tick() {
        if (!playing || !buzzed) {
            return false;
        }
        secondsLeft = Math.max(0, secondsLeft - 1);
        return secondsLeft == 0;
    }

    public Result timeExpired() {
        if (!playing || !buzzed) {
            throw new IllegalStateException("No active question");
        }
        lastAnswerTeam = state.getCurrentPlayer();
        return resolveWrong();
    }

    private Result resolveWrong() {
        state.recordWrong();
        numbers = steps.get(stepIndex).clone();
        if (state.isMatchOver()) {
            finishRound();
            playing = false;
            return Result.MATCH_FORFEITED;
        }

        if (!randomTargetStep && advanceStep()) {
            openBuzzers(true);
        } else {
            currentMatchup = matchupScheduler.next();
            playing = false;
        }
        return Result.WRONG;
    }

    private void finishRound() {
        if (!series.isRoundComplete()) {
            series.recordRoundWinner(state.getWinner());
        }
    }

    private boolean advanceStep() {
        if (stepIndex >= steps.size() - 1) {
            return false;
        }
        stepIndex++;
        return true;
    }

    public void configureMatch(
            TeamRoster firstTeam, TeamRoster secondTeam, Random random) {
        teamOne = firstTeam;
        teamTwo = secondTeam;
        matchupScheduler = new MatchupScheduler(firstTeam, secondTeam, random);
        this.random = random;
        series = new SeriesController(random);
        currentMatchup = matchupScheduler.next();
        state.resetMatch();
        dotWinners.clear();
        numbers = null;
        steps = List.of();
        stepIndex = 0;
        randomTargetStep = false;
        sortType = null;
        secondsLeft = ANSWER_SECONDS;
        buzzed = false;
        playing = false;
        lastAnswerTeam = 0;
    }

    private int[] toArray(JsonArray json) throws IOException {
        if (json == null) {
            throw new IOException("Expected an array");
        }
        int[] array = new int[json.size()];
        try {
            for (int index = 0; index < json.size(); index++) {
                array[index] = json.get(index).getAsInt();
            }
        } catch (RuntimeException exception) {
            throw new IOException("Question arrays must contain integers", exception);
        }
        return array;
    }

    public int[] getNumbers() {
        return numbers == null ? new int[0] : numbers.clone();
    }

    public int getRoundNumber() {
        return series.getCurrentRound();
    }

    public int getCurrentStepNumber() {
        return stepIndex + 1;
    }

    public int getTotalSteps() {
        return steps.size();
    }

    public String getArrayStageLabel() {
        return ArrayStageLabel.forQuestion(
            series.getCurrentRound(), getCurrentStepNumber());
    }

    public String getSortName() {
        return displayName(sortType);
    }

    public String getNextSortName() {
        return switch (series.getCurrentRound()) {
            case 1 -> displayName(InsertionSort.SORT_ID);
            case 2 -> displayName(SelectionSort.SORT_ID);
            case 3 -> displayName(MergeSort.SORT_ID);
            default -> "mixed algorithms";
        };
    }

    private String displayName(String type) {
        if (SelectionSort.SORT_ID.equals(type)) {
            return "selection sort";
        }
        return MergeSort.SORT_ID.equals(type) ? "merge sort" : "insertion sort";
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public int getCurrentPlayer() {
        return state.getCurrentPlayer();
    }

    public int getTeamWins(int team) {
        return state.getTeamWins(team);
    }

    public int getTeamStrikes(int team) {
        return state.getTeamStrikes(team);
    }

    public int getWinner() {
        return state.getWinner();
    }

    public boolean isMatchOver() {
        return state.isMatchOver();
    }

    public boolean isRoundComplete() {
        return series.isRoundComplete();
    }

    public boolean isSeriesOver() {
        return series.isSeriesOver();
    }

    public int getSeriesWinner() {
        return series.getSeriesWinner();
    }

    public int getRoundWins(int team) {
        return series.getRoundWins(team);
    }

    public boolean isRandomTargetStep() {
        return randomTargetStep;
    }

    public String getRoundPolicyName() {
        return switch (series.getCurrentRound()) {
            case 1 -> "Insertion sort";
            case 2 -> "Selection sort";
            case 3 -> "Merge sort";
            default -> "Mixed algorithms · random target step";
        };
    }

    public void advanceRound() {
        if (playing) {
            throw new IllegalStateException("Finish the current round first");
        }
        series.advanceRound();
        state.resetMatch();
        dotWinners.clear();
        numbers = null;
        steps = List.of();
        stepIndex = 0;
        randomTargetStep = false;
        sortType = null;
        secondsLeft = ANSWER_SECONDS;
        buzzed = false;
        lastAnswerTeam = 0;
        currentMatchup = matchupScheduler.next();
    }

    public List<Integer> getDotWinners() {
        return List.copyOf(dotWinners);
    }

    public TeamRoster getTeam(int team) {
        if (team == 1) {
            return teamOne;
        }
        if (team == 2) {
            return teamTwo;
        }
        throw new IllegalArgumentException("Team must be 1 or 2");
    }

    public Matchup getCurrentMatchup() {
        return currentMatchup;
    }

    public int getLastAnswerTeam() {
        return lastAnswerTeam;
    }

    public boolean isBuzzed() {
        return buzzed;
    }

    public boolean isPlaying() {
        return playing;
    }

}
