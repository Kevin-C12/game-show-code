
package com.example;

import java.nio.file.*;
import java.util.*;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class QuestionReturn {

    // All of these sort might changed to different data types in the future, 
    // but for now they are all strings
    public String[] insertionSort = {};

    public String[] selectionSort = {};

    public String[] bubbleSort = {};

    // Constructor: automatically loads JSON files
    public QuestionReturn() {
        loadQuestions();
    }

    // Checks the JSON folder and loads all sorting questions
    public void loadQuestions() {

        Path folder = Path.of("src/main/java/com/example/json");

        List<String> insertion = new ArrayList<>();
        List<String> selection = new ArrayList<>();
        List<String> bubble = new ArrayList<>();

        try {
            // Check if the JSON folder exists
            if (!Files.exists(folder)) {
                System.out.println("JSON folder not found!");
                return;
            }

            // Read all files in the folder
            try (var files = Files.list(folder)) {

                files.filter(path -> path.toString().endsWith(".json"))
                     .sorted()
                     .forEach(path -> {
                         try {
                             // Read JSON file
                             String json = Files.readString(path);

                             // Get the sort type
                             JsonObject object =
                                 JsonParser.parseString(json).getAsJsonObject();

                             String sortType =
                                 object.get("sort").getAsString();

                             // Store JSON in the correct array
                             switch (sortType) {
                                 case "insertionSort":
                                     insertion.add(json);
                                     break;

                                 case "selectionSort":
                                     selection.add(json);
                                     break;

                                 case "bubbleSort":
                                     bubble.add(json);
                                     break;
                             }

                         } catch (Exception e) {
                             System.out.println(
                                 "Error reading: " + path.getFileName()
                             );
                         }
                     });
            }

            // Convert lists into arrays
            insertionSort = insertion.toArray(new String[0]);
            selectionSort = selection.toArray(new String[0]);
            bubbleSort = bubble.toArray(new String[0]);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Here all of the data is converted into a json file, so that it can be sent to the front end
    public String toJson() {

        Gson gson = new Gson();

        Map<String, Object> data = new LinkedHashMap<>();

        data.put("insertionSort", parseQuestions(insertionSort));
        data.put("selectionSort", parseQuestions(selectionSort));
        data.put("bubbleSort", parseQuestions(bubbleSort));

        return gson.toJson(data);
    }

    // Convert stored JSON strings into JSON objects
    private Object[] parseQuestions(String[] array) {

        Object[] objects = new Object[array.length];

        for (int i = 0; i < array.length; i++) {
            objects[i] = JsonParser.parseString(array[i]);
        }

        return objects;
    }

    // This method converts an array of strings into a json array
    private String arrayToJson(String[] array) {
        // This method converts an array of strings into a json array
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < array.length; i++) {
            // This line appends the string to the json array, and adds quotes around it
            json.append("\"").append(array[i]).append("\"");

            if (i < array.length - 1) {
                // This line adds a comma between the strings, but not after the last one
                json.append(", ");
            }
        }

        // This line closes the json array
        json.append("]");
        return json.toString();
    }

    // The getter and setter methods for the three arrays

    public String[] getInsertionSort() {
        // User validation goes here
        return insertionSort;
    }

    public void setInsertionSort(String[] insertionSort) {
        // User validation goes here
        this.insertionSort = insertionSort;
    }

    public String[] getSelectionSort() {
        // User validation goes here
        return selectionSort;
    }

    public void setSelectionSort(String[] selectionSort) {
        // User validation goes here
        this.selectionSort = selectionSort;
    }

    public String[] getBubbleSort() {
        // User validation goes here
        return bubbleSort;
    }

    public void setBubbleSort(String[] bubbleSort) {
        // User validation goes here
        this.bubbleSort = bubbleSort;
    }
}
