package com.example;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

class SelectionSortTest {

    @Test
    void recordsEverySelectionPassIncludingUnchangedOnes() {
        Map<String, int[]> steps = SelectionSort.stepsFor(
            new int[] {35, 37, 79, 10, 45, 33});

        assertEquals(5, steps.size());
        assertArrayEquals(
            new int[] {10, 37, 79, 35, 45, 33}, steps.get("step1"));
        assertArrayEquals(
            new int[] {10, 33, 79, 35, 45, 37}, steps.get("step2"));
        assertArrayEquals(
            new int[] {10, 33, 35, 79, 45, 37}, steps.get("step3"));
        assertArrayEquals(
            new int[] {10, 33, 35, 37, 45, 79}, steps.get("step4"));
        assertArrayEquals(
            new int[] {10, 33, 35, 37, 45, 79}, steps.get("step5"));
    }

    @Test
    void sortedInputStillRecordsEverySelectionPass() {
        Map<String, int[]> steps = SelectionSort.stepsFor(
            new int[] {-2, 0, 0, 4});

        assertEquals(3, steps.size());
        assertArrayEquals(new int[] {-2, 0, 0, 4}, steps.get("step1"));
        assertArrayEquals(new int[] {-2, 0, 0, 4}, steps.get("step2"));
        assertArrayEquals(new int[] {-2, 0, 0, 4}, steps.get("step3"));
    }

    @Test
    void handlesDuplicatesAndNegativeValues() {
        Map<String, int[]> steps = SelectionSort.stepsFor(
            new int[] {2, 2, -1});

        assertEquals(2, steps.size());
        assertArrayEquals(new int[] {-1, 2, 2}, steps.get("step1"));
        assertArrayEquals(new int[] {-1, 2, 2}, steps.get("step2"));
    }
}
