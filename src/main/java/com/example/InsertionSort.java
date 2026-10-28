
package com.example;

import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.GsonBuilder;

public class InsertionSort {

    // Change this to control the list size
    static final int listSize = 6;

    public static void run() throws Exception {

        Random rand = new Random();
        int[] arr = new int[listSize];

        // Generate random list (0-99)
        for (int i = 0; i < arr.length; i++) {
            arr[i] = rand.nextInt(100);
        }

        // Save original unordered list
        int[] original = arr.clone();

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

            // Save the array after each insertion
            steps.put("step" + i, arr.clone());
        }

        // Build JSON structure
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("sort", "insertionSort");
        data.put("unOrderedList", original);
        data.put("sortSteps", steps);

        // Convert data to JSON
        String json = new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(data);

        // Folder for JSON files
        Path folder = Path.of("src/main/java/com/example/json");

        // Create folder if it doesn't exist
        Files.createDirectories(folder);

        // Find next available filename
        int fileNumber = 1;
        Path file;

        do {
            file = folder.resolve(
                "insertionSort" + fileNumber + ".json"
            );
            fileNumber++;
        } while (Files.exists(file));

        // Save JSON to file
        Files.writeString(file, json);

        // Print JSON and file location to terminal
        System.out.println(json);
        System.out.println("Saved to: " + file);
    }
}
