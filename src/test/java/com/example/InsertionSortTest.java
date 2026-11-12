package com.example;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

class InsertionSortTest {

    @Test
    void recordsEveryInsertionPassIncludingUnchangedOnes() {
        Map<String, int[]> steps = InsertionSort.stepsFor(
            new int[] {1, 4, 3, 2, 15});

        assertEquals(4, steps.size());
        assertArrayEquals(
            new int[] {1, 4, 3, 2, 15}, steps.get("step1"));
        assertArrayEquals(
            new int[] {1, 3, 4, 2, 15}, steps.get("step2"));
        assertArrayEquals(
            new int[] {1, 2, 3, 4, 15}, steps.get("step3"));
        assertArrayEquals(
            new int[] {1, 2, 3, 4, 15}, steps.get("step4"));
    }

    @Test
    void sortedInputStillRecordsEveryInsertionPass() {
        Map<String, int[]> steps = InsertionSort.stepsFor(
            new int[] {-2, 0, 0, 4});

        assertEquals(3, steps.size());
        assertArrayEquals(new int[] {-2, 0, 0, 4}, steps.get("step1"));
        assertArrayEquals(new int[] {-2, 0, 0, 4}, steps.get("step2"));
        assertArrayEquals(new int[] {-2, 0, 0, 4}, steps.get("step3"));
    }

    @Test
    void handlesDuplicatesAndNegativeValues() {
        Map<String, int[]> steps = InsertionSort.stepsFor(
            new int[] {2, 2, -1});

        assertEquals(2, steps.size());
        assertArrayEquals(new int[] {2, 2, -1}, steps.get("step1"));
        assertArrayEquals(new int[] {-1, 2, 2}, steps.get("step2"));
    }
}
