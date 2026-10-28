package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Loader {
/**
     * Loads all sorting questions from the JSON folder.
     * @return a QuestionReturn object containing all sorting questions
     */
    public static QuestionReturn loadQuestions() {

        // Creates a QuestionReturn object
        QuestionReturn data = new QuestionReturn();

        // Gets the JSON folder
        Path folder = Path.of("src/main/java/com/example/json");

        // Creates lists to temporarily store the questions
        List<String> insertion = new ArrayList<>();
        List<String> selection = new ArrayList<>();
        List<String> merge = new ArrayList<>();

        try {

            // Checks if the JSON folder exists
            if (!Files.exists(folder)) {
                System.out.println("JSON folder not found!");
                return data;
            }

            // Reads all files in the folder
            try (var files = Files.list(folder)) {

                files.filter(path -> path.toString().endsWith(".json"))
                     .sorted()
                     .forEach(path -> {

                        try {

                             // Reads the JSON file
                            String json = Files.readString(path);

                             // Converts the JSON string into a JSON object
                            JsonObject object =
                                JsonParser.parseString(json).getAsJsonObject();

                             // Gets the sorting algorithm type
                            String sortType =
                                object.get("sort").getAsString();

                             // Stores the JSON in the correct list
                            switch (sortType) {

                                 case "insertionSort":
                                    insertion.add(json);
                                    break;

                                 case "selectionSort":
                                    selection.add(json);
                                    break;

                                 case "MergeSort":
                                    merge.add(json);
                                    break;
                            }

                        } catch (Exception e) {
                            System.out.println("Error reading: " + path.getFileName());
                        }
                     });
            }

            // Converts the lists into arrays
            data.setInsertionSort(insertion.toArray(new String[0]));
            data.setSelectionSort(selection.toArray(new String[0]));
            data.setMergeSort(merge.toArray(new String[0]));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Returns the completed QuestionReturn object
        return data;
    }
}

