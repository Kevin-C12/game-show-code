package com.example;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.Test;

class MergeSortTest {

    @Test
    void exposesTheCanonicalMergeSortIdentifier() throws Exception {
        Field field = assertDoesNotThrow(
            () -> MergeSort.class.getField("SORT_ID"));

        assertEquals("mergeSort", field.get(null));
    }

    @Test
    void recordsEveryMergeForAReverseSortedArray() {
        Map<String, int[]> steps = MergeSort.stepsFor(
            new int[] {6, 5, 4, 3, 2, 1});

        assertEquals(5, steps.size());
        assertArrayEquals(new int[] {6, 4, 5, 3, 2, 1}, steps.get("step1"));
        assertArrayEquals(new int[] {4, 5, 6, 3, 2, 1}, steps.get("step2"));
        assertArrayEquals(new int[] {4, 5, 6, 3, 1, 2}, steps.get("step3"));
        assertArrayEquals(new int[] {4, 5, 6, 1, 2, 3}, steps.get("step4"));
        assertArrayEquals(new int[] {1, 2, 3, 4, 5, 6}, steps.get("step5"));
    }

    @Test
    void omitsNoOpMergesForAnAlreadySortedArray() {
        int[] sorted = {1, 2, 3, 4, 5, 6};
        Map<String, int[]> steps = MergeSort.stepsFor(sorted);

        assertEquals(0, steps.size());
    }

    @Test
    void sortsDuplicateAndNegativeValuesWithoutLosingMergeOrder() {
        Map<String, int[]> steps = MergeSort.stepsFor(
            new int[] {-3, 7, -1, 0, -3, 2});
        Iterator<int[]> iterator = steps.values().iterator();

        assertEquals(3, steps.size());
        assertArrayEquals(new int[] {-3, -1, 7, 0, -3, 2}, iterator.next());
        assertArrayEquals(new int[] {-3, -1, 7, -3, 0, 2}, iterator.next());
        assertArrayEquals(new int[] {-3, -3, -1, 0, 2, 7}, iterator.next());
    }
}
