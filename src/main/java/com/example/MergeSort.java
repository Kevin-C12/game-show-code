package com.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import com.google.gson.GsonBuilder;

public class MergeSort {

    static final int listSize = 6;

    public static void run() throws Exception {
        Random rand = new Random();
        int[] arr = new int[listSize];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = rand.nextInt(10);
        }

        int[] original = arr.clone();
        Map<String, int[]> steps = new LinkedHashMap<>();

        // Record the whole array after each completed merge.
        mergeSort(arr, new int[arr.length], 0, arr.length, steps);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("sort", "mergeSort");
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
            file = folder.resolve("mergeSort" + fileNumber + ".json");
            fileNumber++;
        } while (Files.exists(file));

        Files.writeString(file, json);
        System.out.println(json);
        System.out.println("Saved to: " + file);
    }

    static int[] firstStep(int[] input) {
        int[] result = input.clone();
        Map<String, int[]> steps = new LinkedHashMap<>();
        mergeSort(result, new int[result.length], 0, result.length, steps);
        return steps.getOrDefault("step1", result);
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

        System.arraycopy(temp, start, arr, start, end - start);
        steps.put("step" + (steps.size() + 1), arr.clone());
    }
}
