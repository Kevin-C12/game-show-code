package com.example;

public class QuestionReturn {

    // All of these sort might changed to different data types in the future, 
    // but for now they are all strings
    public String[] insertionSort = {};

    public String[] selectionSort = {};

    public String[] bubbleSort = {};

    // Here all of the data is converted into a json file, so that it can be sent to the front end
    public String toJson() {
        return "{ \"insertionSort\": " + arrayToJson(insertionSort) + ", \"selectionSort\": " + arrayToJson(selectionSort) + ", \"bubbleSort\": " + arrayToJson(bubbleSort) + " }";
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
        return insertionSort;
    }

    public void setInsertionSort(String[] insertionSort) {
        this.insertionSort = insertionSort;
    }

    public String[] getSelectionSort() {
        return selectionSort;
    }

    public void setSelectionSort(String[] selectionSort) {
        this.selectionSort = selectionSort;
    }

    public String[] getBubbleSort() {
        return bubbleSort;
    }

    public void setBubbleSort(String[] bubbleSort) {
        this.bubbleSort = bubbleSort;
    }


}
