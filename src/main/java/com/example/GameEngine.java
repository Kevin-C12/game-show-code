
package com.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

public class GameEngine {

    // Possible answer results
    public enum Result {
        CORRECT,
        WRONG,
        THREE_STRIKES,
        STEAL_FAILED
    }

    // Game state
    private final GameState state = new GameState();

    // Current question
    private int[] numbers;
    private int[] correctAnswer;
    private static final String[] SORTS = {
        "insertionSort", "selectionSort", "mergeSort"
    };
    private int roundNumber = 0;
    private String sortType;
    private boolean stealAttempt = false;

    // Timer and buzzer state
    private int secondsLeft = 30;
    private boolean buzzed = false;
    private boolean playing = false;

    // Demo arrays
    public static final int[] SORTED_DEMO = {
        7, 15, 29, 42, 61, 83
    };

    public static final int[] UNSORTED_DEMO = {
        42, 15, 83, 7, 29, 61
    };

    public static final int[] WORST_CASE_DEMO = {
        83, 61, 42, 29, 15, 7
    };

    // Start a new question
    public void startQuestion() throws IOException {

        if (playing) {
            throw new IllegalStateException("Finish the current round first");
        }

        String nextSort = SORTS[roundNumber % SORTS.length];

        // Generate question JSON
        try {
            switch (nextSort) {
                case "insertionSort" -> InsertionSort.run();
                case "selectionSort" -> SelectionSort.run();
                case "mergeSort" -> MergeSort.run();
                default -> throw new IllegalStateException("Unknown sort: " + nextSort);
            }
        } catch (Exception e) {
            throw new IOException("Could not generate question", e);
        }

        Path folder = Path.of(
            "src/main/java/com/example/json"
        );

        // Find newest numbered JSON file
        Path newest;

        try (Stream<Path> files = Files.list(folder)) {
            newest = files
                .filter(p -> p.getFileName().toString()
                    .matches(nextSort + "\\d+\\.json"))
                .max(Comparator.comparingInt(
                    p -> getQuestionNumber(p, nextSort)
                ))
                .orElseThrow(() ->
                    new IOException("No questions found")
                );
        }

        JsonObject question = JsonParser
            .parseString(Files.readString(newest))
            .getAsJsonObject();

        JsonArray original =
            question.getAsJsonArray("unOrderedList");

        JsonArray expected = question
            .getAsJsonObject("sortSteps")
            .getAsJsonArray("step1");

        numbers = toArray(original);
        correctAnswer = toArray(expected);
        sortType = nextSort;
        roundNumber++;
        stealAttempt = false;
        state.startRound();

        resetRound();
    }

    // Load a developer test question
    public void loadDemoQuestion(int[] demoArray) {

        if (!playing) {
            return;
        }

        numbers = demoArray.clone();
        correctAnswer = demoArray.clone();

        // Calculate the first step of the current round's sort.
        if ("selectionSort".equals(sortType)) {
            correctAnswer = SelectionSort.firstStep(demoArray);
        } else if ("mergeSort".equals(sortType)) {
            correctAnswer = MergeSort.firstStep(demoArray);
        } else if (correctAnswer.length > 1) {
            int key = correctAnswer[1];
            int j = 0;

            while (j >= 0 && correctAnswer[j] > key) {
                correctAnswer[j + 1] = correctAnswer[j];
                j--;
            }

            correctAnswer[j + 1] = key;
        }

        resetRound();
    }

    // Reset timer and buzzer for another attempt
    private void resetRound() {
        secondsLeft = 30;
        buzzed = false;
        playing = true;
    }

    // Activate buzzer, only once per attempt
    public boolean buzz(int team) {
        if (!playing || buzzed) {
            return false;
        }

        if (state.getCurrentTeam() == 0) {
            state.setCurrentTeam(team);
        } else if (state.getCurrentTeam() != team) {
            return false;
        }

        buzzed = true;
        return true;
    }

    // Check player answers
    public Result submitAnswer(int[] answer) {

        if (!playing || !buzzed) {
            throw new IllegalStateException(
                "Question is not ready for submission"
            );
        }

        if (Arrays.equals(answer, correctAnswer)) {

            state.addPoints(10);
            state.resetStrikes();
            playing = false;

            return Result.CORRECT;
        }

        return recordStrike();
    }

    // Add a strike and determine next turn
    private Result recordStrike() {

        state.addStrike();

        if (stealAttempt) {
            playing = false;
            return Result.STEAL_FAILED;
        }

        if (state.getStrikes() >= 3) {
            state.switchTeam();
            stealAttempt = true;
            resetRound();

            return Result.THREE_STRIKES;
        }

        // Keep same question for another attempt
        resetRound();
        return Result.WRONG;
    }

    // Timer countdown
    public boolean tick() {

        if (!playing || !buzzed) {
            return false;
        }

        secondsLeft = Math.max(0, secondsLeft - 1);

        return secondsLeft == 0;
    }

    // Time running out counts as a strike
    public Result timeExpired() {
        if (!playing || !buzzed) {
            throw new IllegalStateException(
                "No active question"
            );
        }

        return recordStrike();
    }

    // Convert JSON arrays to Java arrays
    private int[] toArray(JsonArray json) {

        int[] array = new int[json.size()];

        for (int i = 0; i < json.size(); i++) {
            array[i] = json.get(i).getAsInt();
        }

        return array;
    }

    // Extract the number from a generated sort question filename.
    private int getQuestionNumber(Path file, String type) {

        String name = file.getFileName().toString();

        return Integer.parseInt(
            name.replace(type, "")
                .replace(".json", "")
        );
    }

    // Getters used by App.java

    public int[] getNumbers() {
        return numbers == null ? new int[0] : numbers.clone();
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public String getSortName() {
        if ("selectionSort".equals(sortType)) {
            return "selection sort";
        }
        return "mergeSort".equals(sortType) ? "merge sort" : "insertion sort";
    }

    public String getNextSortName() {
        return switch (SORTS[roundNumber % SORTS.length]) {
            case "selectionSort" -> "selection sort";
            case "mergeSort" -> "merge sort";
            default -> "insertion sort";
        };
    }

    public boolean isStealAttempt() {
        return stealAttempt;
    }

    public int getRoundStrikes() {
        return Math.max(state.getTeam1Strikes(), state.getTeam2Strikes());
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public int getCurrentTeam() {
        return state.getCurrentTeam();
    }

    public int getTeam1Points() {
        return state.getTeam1Points();
    }

    public int getTeam2Points() {
        return state.getTeam2Points();
    }

    public int getStrikes() {
        return state.getStrikes();
    }

    public int getTeam1Strikes() {
        return state.getTeam1Strikes();
    }

    public int getTeam2Strikes() {
        return state.getTeam2Strikes();
    }

    public boolean isBuzzed() {
        return buzzed;
    }

    public boolean isPlaying() {
        return playing;
    }
}
