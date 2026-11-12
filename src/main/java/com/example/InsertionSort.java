
package com.example;

import java.util.*;
import java.nio.file.Path;

public class InsertionSort {

    public static final String SORT_ID = "insertionSort";

    // Change this to control the list size
    static final int listSize = 6;

    public static Path run() throws Exception {

        Random rand = new Random();
        int[] arr = new int[listSize];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = rand.nextInt(120) - 20;
        }
        int[] original = arr.clone();
        Map<String, int[]> steps = stepsFor(arr);

        // Folder for JSON files
        Path folder = Path.of("src/main/java/com/example/json");
        Path file = QuestionWriter.writeNext(
            folder, SORT_ID, original, steps);

        // Print JSON and file location to terminal
        System.out.println("Saved to: " + file);
        return file;
    }

    public static Map<String, int[]> stepsFor(int[] input) {
        int[] arr = input.clone();

        // Store sorting steps
        Map<String, int[]> steps = new LinkedHashMap<>();

        // Insertion sort algorithm
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;

            // Shift larger elements to the right
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }

            // Insert the element
            arr[j + 1] = key;

            steps.put("step" + i, arr.clone());
        }

        return steps;
    }
}
