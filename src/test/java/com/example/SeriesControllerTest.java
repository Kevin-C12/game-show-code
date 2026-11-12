package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SeriesControllerTest {

    @Test
    void firstThreeRoundsUseTheirDedicatedAlgorithms() {
        SeriesController series = new SeriesController(new Random(42));

        assertEquals(InsertionSort.SORT_ID, series.nextQuestion().sortId());
        assertFalse(series.nextQuestion().randomTargetStep());
        series.recordRoundWinner(1);
        series.advanceRound();

        assertEquals(SelectionSort.SORT_ID, series.nextQuestion().sortId());
        series.recordRoundWinner(2);
        series.advanceRound();

        assertEquals(MergeSort.SORT_ID, series.nextQuestion().sortId());
        assertEquals(3, series.getCurrentRound());
    }

    @Test
    void mixedRoundsUseEveryAlgorithmBeforeRepeating() {
        SeriesController series = controllerAtRoundFour();
        Set<String> firstBag = new HashSet<>();
        Set<String> secondBag = new HashSet<>();

        for (int question = 0; question < 3; question++) {
            QuestionRequest request = series.nextQuestion();
            assertTrue(request.randomTargetStep());
            firstBag.add(request.sortId());
        }
        for (int question = 0; question < 3; question++) {
            secondBag.add(series.nextQuestion().sortId());
        }

        Set<String> allSorts = Set.of(
            InsertionSort.SORT_ID, SelectionSort.SORT_ID, MergeSort.SORT_ID);
        assertEquals(allSorts, firstBag);
        assertEquals(allSorts, secondBag);
    }

    @Test
    void thirdRoundWinEndsTheSeriesImmediately() {
        SeriesController series = new SeriesController(new Random(42));
        series.recordRoundWinner(1);
        series.advanceRound();
        series.recordRoundWinner(1);
        series.advanceRound();

        series.recordRoundWinner(1);

        assertEquals(3, series.getRoundWins(1));
        assertEquals(1, series.getSeriesWinner());
        assertTrue(series.isSeriesOver());
        assertTrue(series.isRoundComplete());
        assertThrows(IllegalStateException.class, series::advanceRound);
    }

    @Test
    void roundCannotAdvanceUntilAWinnerIsRecorded() {
        SeriesController series = new SeriesController(new Random(42));

        assertThrows(IllegalStateException.class, series::advanceRound);
        assertEquals(1, series.getCurrentRound());
    }

    private SeriesController controllerAtRoundFour() {
        SeriesController series = new SeriesController(new Random(42));
        for (int round = 1; round < 4; round++) {
            series.recordRoundWinner(round == 2 ? 2 : 1);
            series.advanceRound();
        }
        return series;
    }
}
