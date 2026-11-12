package com.example;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.file.Path;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GameEngineTest {

    @TempDir
    Path directory;

    @Test
    void loadsAndProgressesThroughEverySavedMergeStep() throws Exception {
        GameEngine engine = loadedMergeEngine();

        assertArrayEquals(new int[] {4, 3, 2, 1}, engine.getNumbers());
        assertEquals(1, engine.getCurrentStepNumber());
        assertEquals(2, engine.getTotalSteps());

        assertTrue(engine.buzz(1));
        assertEquals(
            GameEngine.Result.STEP_CORRECT,
            engine.submitAnswer(new int[] {3, 4, 1, 2}));
        assertArrayEquals(new int[] {3, 4, 1, 2}, engine.getNumbers());
        assertEquals(2, engine.getCurrentStepNumber());

        assertTrue(engine.buzz(2));
        assertEquals(
            GameEngine.Result.CORRECT,
            engine.submitAnswer(new int[] {1, 2, 3, 4}));
        assertEquals(1, engine.getTeamWins(1));
        assertEquals(1, engine.getTeamWins(2));
        assertEquals(List.of(1, 2), engine.getDotWinners());
        assertFalse(engine.isPlaying());
    }

    @Test
    void onlyTheFirstBuzzOwnsAnAttempt() throws Exception {
        GameEngine engine = loadedMergeEngine();

        assertTrue(engine.buzz(2));
        assertFalse(engine.buzz(1));
        assertEquals(2, engine.getCurrentPlayer());
    }

    @Test
    void answerClockExpiresAfterTwentySeconds() throws Exception {
        GameEngine engine = loadedMergeEngine();
        assertEquals(20, engine.getSecondsLeft());
        engine.buzz(1);

        for (int second = 1; second < 20; second++) {
            assertFalse(engine.tick());
        }

        assertTrue(engine.tick());
        assertEquals(0, engine.getSecondsLeft());
    }

    @Test
    void wrongAnswerRevealsAndConsumesTheMissedStepWithoutClaimingDot()
            throws Exception {
        GameEngine engine = loadedMergeEngine();
        engine.buzz(1);

        assertEquals(
            GameEngine.Result.WRONG,
            engine.submitAnswer(new int[] {9, 9, 9, 9}));
        assertArrayEquals(new int[] {3, 4, 1, 2}, engine.getNumbers());
        assertEquals(1, engine.getTeamStrikes(1));
        assertEquals(List.of(), engine.getDotWinners());
        assertEquals(0, engine.getCurrentPlayer());
        assertFalse(engine.isBuzzed());
        assertEquals(2, engine.getCurrentStepNumber());
        assertTrue(engine.buzz(2));
    }

    @Test
    void timeoutRecordsStrikeAndAdvancesToTheNextStep() throws Exception {
        GameEngine engine = loadedMergeEngine();
        engine.buzz(1);

        assertEquals(GameEngine.Result.WRONG, engine.timeExpired());
        assertArrayEquals(new int[] {3, 4, 1, 2}, engine.getNumbers());
        assertEquals(1, engine.getTeamStrikes(1));
        assertEquals(2, engine.getCurrentStepNumber());
        assertEquals(0, engine.getCurrentPlayer());
        assertFalse(engine.isBuzzed());
        assertTrue(engine.buzz(2));
    }

    @Test
    void thirdStrikeForfeitsTheMatchToTheOtherTeam() throws Exception {
        GameEngine engine = configuredEngine();
        engine.loadQuestion(questionFileWithSteps(3), MergeSort.SORT_ID);

        for (int attempt = 0; attempt < 2; attempt++) {
            assertTrue(engine.buzz(1));
            assertEquals(GameEngine.Result.WRONG,
                engine.submitAnswer(new int[] {9, 9, 9, 9}));
        }
        assertTrue(engine.buzz(1));

        assertEquals(GameEngine.Result.MATCH_FORFEITED,
            engine.submitAnswer(new int[] {9, 9, 9, 9}));
        assertEquals(3, engine.getTeamStrikes(1));
        assertEquals(2, engine.getWinner());
        assertTrue(engine.isMatchOver());
        assertFalse(engine.isPlaying());
    }

    @Test
    void fifthCorrectAnswerWinsAndEveryCorrectAnswerClaimsOneDot()
            throws Exception {
        GameEngine engine = configuredEngine();
        engine.loadQuestion(questionFileWithSteps(5), MergeSort.SORT_ID);

        for (int step = 1; step <= 4; step++) {
            assertTrue(engine.buzz(1));
            assertEquals(GameEngine.Result.STEP_CORRECT,
                engine.submitAnswer(expectedStep(step)));
        }
        assertTrue(engine.buzz(1));

        assertEquals(GameEngine.Result.MATCH_WON,
            engine.submitAnswer(expectedStep(5)));
        assertEquals(5, engine.getTeamWins(1));
        assertEquals(List.of(1, 1, 1, 1, 1), engine.getDotWinners());
        assertEquals(1, engine.getWinner());
        assertTrue(engine.isMatchOver());
        assertTrue(engine.isRoundComplete());
        assertFalse(engine.isSeriesOver());
        assertEquals(1, engine.getRoundWins(1));
    }

    @Test
    void advancingARoundResetsDotsAndStrikesButKeepsTheSeriesScore()
            throws Exception {
        GameEngine engine = configuredEngine();
        engine.loadQuestion(questionFileWithSteps(5), MergeSort.SORT_ID);
        Matchup finalMatchup = null;
        for (int step = 1; step <= 5; step++) {
            finalMatchup = engine.getCurrentMatchup();
            engine.buzz(1);
            engine.submitAnswer(expectedStep(step));
        }

        engine.advanceRound();

        assertEquals(2, engine.getRoundNumber());
        assertEquals(1, engine.getRoundWins(1));
        assertEquals(0, engine.getTeamWins(1));
        assertEquals(0, engine.getTeamStrikes(1));
        assertEquals(List.of(), engine.getDotWinners());
        assertFalse(engine.isRoundComplete());
        assertEquals("Selection sort", engine.getRoundPolicyName());
        assertFalse(finalMatchup.team1Member().equals(
            engine.getCurrentMatchup().team1Member()));
        assertFalse(finalMatchup.team2Member().equals(
            engine.getCurrentMatchup().team2Member()));
    }

    @Test
    void mixedQuestionShowsOriginalAndChecksOnlyTheSelectedTargetStep()
            throws Exception {
        GameEngine engine = configuredEngine();
        Path question = questionFileWithSteps(5);

        engine.loadQuestion(question, MergeSort.SORT_ID, true);

        int target = engine.getCurrentStepNumber();
        assertTrue(target >= 1 && target <= 5);
        assertTrue(engine.isRandomTargetStep());
        assertArrayEquals(new int[] {4, 3, 2, 1}, engine.getNumbers());
        engine.buzz(1);
        assertEquals(GameEngine.Result.CORRECT,
            engine.submitAnswer(expectedStep(target)));
        assertFalse(engine.isPlaying());
    }

    @Test
    void sortedMergeDemoDoesNotCreateAnUnchangedScoringStep()
            throws Exception {
        GameEngine engine = loadedMergeEngine();

        assertFalse(engine.loadDemoQuestion(new int[] {1, 2, 3, 4}));

        assertArrayEquals(new int[] {4, 3, 2, 1}, engine.getNumbers());
        assertEquals(1, engine.getCurrentStepNumber());
        assertTrue(engine.isPlaying());
    }

    @Test
    void matchupsRotateAndConfigureMatchResetsTheScoreboard()
            throws Exception {
        GameEngine engine = configuredEngine();
        engine.loadQuestion(questionFile(), MergeSort.SORT_ID);
        Matchup first = engine.getCurrentMatchup();
        engine.buzz(1);
        engine.submitAnswer(new int[] {3, 4, 1, 2});
        Matchup second = engine.getCurrentMatchup();

        assertFalse(first.team1Member().equals(second.team1Member()));
        assertFalse(first.team2Member().equals(second.team2Member()));

        engine.configureMatch(
            new TeamRoster("Red", List.of("New Red")),
            new TeamRoster("Teal", List.of("New Teal")),
            new Random(7));

        assertEquals(0, engine.getTeamWins(1));
        assertEquals(0, engine.getTeamStrikes(1));
        assertEquals(List.of(), engine.getDotWinners());
        assertEquals(0, engine.getWinner());
        assertFalse(engine.isMatchOver());
        assertEquals(new Matchup("New Red", "New Teal"),
            engine.getCurrentMatchup());
    }

    @Test
    void finalSavedStepRotatesMatchupBeforeTheNextGeneratedQuestion()
            throws Exception {
        GameEngine engine = configuredEngine();
        engine.loadQuestion(questionFileWithSteps(1), MergeSort.SORT_ID);
        Matchup first = engine.getCurrentMatchup();
        engine.buzz(1);

        assertEquals(GameEngine.Result.CORRECT,
            engine.submitAnswer(expectedStep(1)));

        Matchup next = engine.getCurrentMatchup();
        assertFalse(first.team1Member().equals(next.team1Member()));
        assertFalse(first.team2Member().equals(next.team2Member()));
    }

    @Test
    void rejectsAQuestionWhoseIdentifierDoesNotMatchTheRound()
            throws Exception {
        Path question = questionFile();
        GameEngine engine = new GameEngine();
        assertThrows(
            java.io.IOException.class,
            () -> engine.loadQuestion(question, SelectionSort.SORT_ID));
    }

    private GameEngine loadedMergeEngine() throws Exception {
        GameEngine engine = configuredEngine();
        engine.loadQuestion(questionFile(), MergeSort.SORT_ID);
        return engine;
    }

    private GameEngine configuredEngine() {
        GameEngine engine = new GameEngine();
        engine.configureMatch(
            new TeamRoster("Red", List.of("Ava", "Noah")),
            new TeamRoster("Teal", List.of("Leo", "Zoe")),
            new Random(42));
        return engine;
    }

    private Path questionFile() throws Exception {
        int[] original = {4, 3, 2, 1};
        Map<String, int[]> steps = new LinkedHashMap<>();
        steps.put("step1", new int[] {3, 4, 1, 2});
        steps.put("step2", new int[] {1, 2, 3, 4});
        return QuestionWriter.writeNext(
            directory, MergeSort.SORT_ID, original, steps);
    }

    private Path questionFileWithSteps(int count) throws Exception {
        Map<String, int[]> steps = new LinkedHashMap<>();
        for (int step = 1; step <= count; step++) {
            steps.put("step" + step, expectedStep(step));
        }
        return QuestionWriter.writeNext(
            directory, MergeSort.SORT_ID, new int[] {4, 3, 2, 1}, steps);
    }

    private int[] expectedStep(int step) {
        return new int[] {step, step + 1, step + 2, step + 3};
    }
}
