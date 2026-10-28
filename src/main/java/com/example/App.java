package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


/**
 * JavaFX App
 * To Run this print this in terminal "mvn clean javafx:run"
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        // Loads the questions from the JSON file
        QuestionReturn data = Loader.loadQuestions("/sorts.json");

        // Gets the first bubble sort question
        String question = "No questions found";

        if (data != null && data.getBubbleSort() != null && data.getBubbleSort().length > 0) {
            question = data.getBubbleSort()[0];
        }

        // Displays the loaded question
        var label = new Label(question);

        var scene = new Scene(new StackPane(label), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}