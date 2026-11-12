package com.example;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/** Controls the best-of-five round series and its question schedule. */
public final class SeriesController {

    private static final int ROUNDS_TO_WIN_SERIES = 3;
    private static final int MAX_ROUNDS = 5;
    private static final List<String> SORTS = List.of(
        InsertionSort.SORT_ID,
        SelectionSort.SORT_ID,
        MergeSort.SORT_ID
    );

    private final Random random;
    private final int[] roundWins = new int[2];
    private final Deque<String> mixedBag = new ArrayDeque<>();
    private int currentRound = 1;
    private int seriesWinner;
    private boolean roundComplete;

    public SeriesController(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    public QuestionRequest nextQuestion() {
        if (seriesWinner != 0) {
            throw new IllegalStateException("The series is already over");
        }
        if (roundComplete) {
            throw new IllegalStateException("Advance to the next round first");
        }
        return switch (currentRound) {
            case 1 -> new QuestionRequest(InsertionSort.SORT_ID, false);
            case 2 -> new QuestionRequest(SelectionSort.SORT_ID, false);
            case 3 -> new QuestionRequest(MergeSort.SORT_ID, false);
            case 4, 5 -> new QuestionRequest(nextMixedSort(), true);
            default -> throw new IllegalStateException("Invalid series round");
        };
    }

    public void recordRoundWinner(int team) {
        int index = indexFor(team);
        if (seriesWinner != 0 || roundComplete) {
            throw new IllegalStateException("The current round is already complete");
        }
        roundWins[index]++;
        roundComplete = true;
        if (roundWins[index] == ROUNDS_TO_WIN_SERIES) {
            seriesWinner = team;
        }
    }

    public void advanceRound() {
        if (!roundComplete) {
            throw new IllegalStateException("The current round is not complete");
        }
        if (seriesWinner != 0) {
            throw new IllegalStateException("The series is already over");
        }
        if (currentRound >= MAX_ROUNDS) {
            throw new IllegalStateException("There are no rounds remaining");
        }
        currentRound++;
        roundComplete = false;
        mixedBag.clear();
    }

    private String nextMixedSort() {
        if (mixedBag.isEmpty()) {
            List<String> shuffled = new ArrayList<>(SORTS);
            Collections.shuffle(shuffled, random);
            mixedBag.addAll(shuffled);
        }
        return mixedBag.removeFirst();
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getRoundWins(int team) {
        return roundWins[indexFor(team)];
    }

    public int getSeriesWinner() {
        return seriesWinner;
    }

    public boolean isSeriesOver() {
        return seriesWinner != 0;
    }

    public boolean isRoundComplete() {
        return roundComplete;
    }

    private int indexFor(int team) {
        if (team != 1 && team != 2) {
            throw new IllegalArgumentException("Team must be 1 or 2");
        }
        return team - 1;
    }
}
