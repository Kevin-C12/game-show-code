package com.example;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class MergeSort {

    public static final String SORT_ID = "mergeSort";

    static final int listSize = 6;

    public static Path run() throws Exception {
        Random rand = new Random();
        int[] arr;
        int[] original;
        Map<String, int[]> steps;
        do {
            arr = new int[listSize];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = rand.nextInt(120) - 20;
            }
            original = arr.clone();
            steps = stepsFor(arr);
        } while (steps.isEmpty());

        Path folder = Path.of("src/main/java/com/example/json");
        Path file = QuestionWriter.writeNext(
            folder, SORT_ID, original, steps);
        System.out.println("Saved to: " + file);
        return file;
    }

    public static int[] firstStep(int[] input) {
        return stepsFor(input).getOrDefault("step1", input.clone());
    }

    public static Map<String, int[]> stepsFor(int[] input) {
        int[] result = input.clone();
        Map<String, int[]> steps = new LinkedHashMap<>();
        mergeSort(result, new int[result.length], 0, result.length, steps);
        return steps;
    }

    private static void mergeSort(int[] arr, int[] temp, int start, int end,
                                  Map<String, int[]> steps) {
        if (end - start <= 1) {
            return;
        }

        int middle = start + (end - start) / 2;
        mergeSort(arr, temp, start, middle, steps);
        mergeSort(arr, temp, middle, end, steps);

        int left = start;
        int right = middle;
        for (int i = start; i < end; i++) {
            if (right >= end || (left < middle && arr[left] <= arr[right])) {
                temp[i] = arr[left++];
            } else {
                temp[i] = arr[right++];
            }
        }

        int[] beforeMerge = arr.clone();
        System.arraycopy(temp, start, arr, start, end - start);
        if (!Arrays.equals(beforeMerge, arr)) {
            steps.put("step" + (steps.size() + 1), arr.clone());
        }
    }
}
