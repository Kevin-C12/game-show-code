package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;

public class Loader {

    /**
     * Loads questions from a JSON file.
     * @param filePath the location of the JSON file
     * @return a QuestionReturn object containing the questions
     */
    public static QuestionReturn loadQuestions(String filePath) {

        //Creates a Gson object
        Gson gson = new Gson();

        //Finds the JSON file in the resources folder
        InputStream input = Loader.class.getResourceAsStream(filePath);

        //Checks if the file exists
        if (input == null) {
            System.out.println("Could not find JSON file: " + filePath);
            return null;
        }

        try {

            //Creates a reader for the JSON file
            InputStreamReader reader = new InputStreamReader(input);

            //Converts the JSON file into the SortData class
            QuestionReturn data = gson.fromJson(reader, QuestionReturn.class);

            //Closes the reader
            reader.close();

            //Returns the loaded data
            return data;

        } catch (IOException e) {

            //Displays an error if loading fails
            System.out.println("Error loading questions: " + e.getMessage());

            return null;
        }
    }
}

