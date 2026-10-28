package com.example;

import javafx.application.Application;

public class Main {

    public static void main(String[] args) throws Exception {

        // Generate a new insertion sort question
        InsertionSort.run();

        // Load the saved questions
        QuestionReturn questions = new QuestionReturn();

        // Pass questions to JavaFX
        App.setQuestions(questions);

        // Launch GUI
        Application.launch(App.class, args);
    }
}
