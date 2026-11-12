package com.example;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class SelectionSort {

    public static final String SORT_ID = "selectionSort";

    static final int listSize = 6;

    public static Path run() throws Exception {
        Random rand = new Random();
        int[] arr = new int[listSize];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = rand.nextInt(120) - 20;
        }
        int[] original = arr.clone();
        Map<String, int[]> steps = stepsFor(arr);

        Path folder = Path.of("src/main/java/com/example/json");
        Path file = QuestionWriter.writeNext(
            folder, SORT_ID, original, steps);
        System.out.println("Saved to: " + file);
        return file;
    }

    public static Map<String, int[]> stepsFor(int[] input) {
        int[] arr = input.clone();
        Map<String, int[]> steps = new LinkedHashMap<>();

        // Each pass selects the smallest remaining value and puts it in place.
        for (int i = 0; i < arr.length - 1; i++) {
            int smallest = i;

            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[smallest]) {
                    smallest = j;
                }
            }

            int temp = arr[i];
            arr[i] = arr[smallest];
            arr[smallest] = temp;
            steps.put("step" + (i + 1), arr.clone());
        }

        return steps;
    }

    static int[] firstStep(int[] input) {
        return stepsFor(input).getOrDefault("step1", input.clone());
    }
}
