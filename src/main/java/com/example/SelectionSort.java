package com.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import com.google.gson.GsonBuilder;

public class SelectionSort {

    static final int listSize = 6;

    public static void run() throws Exception {
        Random rand = new Random();
        int[] arr = new int[listSize];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = rand.nextInt(10);
        }

        int[] original = arr.clone();
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

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("sort", "selectionSort");
        data.put("unOrderedList", original);
        data.put("sortSteps", steps);

        String json = new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(data);

        Path folder = Path.of("src/main/java/com/example/json");
        Files.createDirectories(folder);

        int fileNumber = 1;
        Path file;
        do {
            file = folder.resolve("selectionSort" + fileNumber + ".json");
            fileNumber++;
        } while (Files.exists(file));

        Files.writeString(file, json);
        System.out.println(json);
        System.out.println("Saved to: " + file);
    }

    static int[] firstStep(int[] input) {
        int[] result = input.clone();
        if (result.length < 2) {
            return result;
        }
        int smallest = 0;
        for (int i = 1; i < result.length; i++) {
            if (result[i] < result[smallest]) {
                smallest = i;
            }
        }
        int temp = result[0];
        result[0] = result[smallest];
        result[smallest] = temp;
        return result;
    }
}
