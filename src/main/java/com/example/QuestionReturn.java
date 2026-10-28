
package com.example;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class QuestionReturn {

    // All of these sort might changed to different data types in the future, 
    // but for now they are all strings
    public String[] insertionSort = {};

    public String[] selectionSort = {};

    public String[] mergeSort = {};

    // Here all of the data is converted into a json file, so that it can be sent to the front end
    public String toJson() {

        Gson gson = new Gson();

        Map<String, Object> data = new LinkedHashMap<>();

        data.put("insertionSort", parseQuestions(insertionSort));
        data.put("selectionSort", parseQuestions(selectionSort));
        data.put("mergeSort", parseQuestions(mergeSort));

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

    // The getter and setter methods for the three arrays

    public String[] getInsertionSort() {
        // User Validation goes here
        return insertionSort;
    }

    public void setInsertionSort(String[] insertionSort) {
        // User Validation goes here
        this.insertionSort = insertionSort;
    }

    public String[] getSelectionSort() {
        // User Validation goes here
        return selectionSort;
    }

    public void setSelectionSort(String[] selectionSort) {
        // User Validation goes here
        this.selectionSort = selectionSort;
    }

    public String[] getMergeSort() {
        // User Validation goes here
        return mergeSort;
    }

    public void setMergeSort(String[] mergeSort) {
        // User Validation goes here
        this.mergeSort = mergeSort;
    }
}
