
package com.example;

import com.google.gson.JsonArray;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AnswerPopup {

    public static void show(Stage parent, JsonArray correctAnswer) {

        // Create popup window
        Stage popup = new Stage();
        popup.setTitle("Answer Question");
        popup.initOwner(parent);
        popup.initModality(Modality.WINDOW_MODAL);

        // Create components
        Label title = new Label("Enter the next insertion sort step");
        Button submit = new Button("Submit Answer");
        Label result = new Label();

        HBox answerBoxes = new HBox(10);
        answerBoxes.setAlignment(Pos.CENTER);

        List<TextField> fields = new ArrayList<>();

        // Create one answer box for each number
        for (int i = 0; i < correctAnswer.size(); i++) {

            TextField field = new TextField();
            field.setPromptText("?");
            field.setPrefWidth(65);
            field.setAlignment(Pos.CENTER);
            field.getStyleClass().add("answer-box");

            fields.add(field);
            answerBoxes.getChildren().add(field);
        }

        // Submit answer
        submit.setOnAction(e -> {

            boolean correct = AnswerChecker.checkAnswer(
                fields, correctAnswer
            );

            if (correct) {
                result.setText("✓ CORRECT!");
                result.setStyle(
                    "-fx-text-fill: #22c55e;" +
                    "-fx-font-size: 40px;" +
                    "-fx-font-weight: bold;"
                );

                submit.setDisable(true);

            } else {
                result.setText("✕ INCORRECT!");
                result.setStyle(
                    "-fx-text-fill: #ef4444;" +
                    "-fx-font-size: 40px;" +
                    "-fx-font-weight: bold;"
                );
            }
        });

        // Create layout
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
            title,
            answerBoxes,
            submit,
            result
        );

        // Apply CSS classes
        title.getStyleClass().add("question-label");
        submit.getStyleClass().add("submit-button");

        // Create scene
        Scene scene = new Scene(root, 650, 350);

        // Load existing CSS
        scene.getStylesheets().add(
            AnswerPopup.class
                .getResource("/styles.css")
                .toExternalForm()
        );

        popup.setScene(scene);
        popup.showAndWait();
    }
}
