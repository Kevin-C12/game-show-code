
package com.example;

import com.google.gson.JsonArray;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AnswerPopup {

    public static Stage show(
            Stage parent,
            JsonArray correctAnswer,
            Consumer<Boolean> onAnswer,
            Runnable onSubmit) {

        // Create popup
        Stage popup = new Stage();
        popup.initOwner(parent);
        popup.initModality(Modality.WINDOW_MODAL);
        popup.setTitle("Answer Question");

        //Th
        Label title = new Label("Enter the next insertion sort step");
        title.getStyleClass().add("question-label");

        HBox answerBoxes = new HBox(10);
        answerBoxes.setAlignment(Pos.CENTER);

        List<TextField> fields = new ArrayList<>();

        // Create an input for each array element
        for (int i = 0; i < correctAnswer.size(); i++) {
            TextField field = new TextField();
            field.setPromptText("?");
            field.setPrefWidth(65);
            field.setAlignment(Pos.CENTER);
            field.getStyleClass().add("answer-box");

            fields.add(field);
            answerBoxes.getChildren().add(field);
        }

        Button submit = new Button("Submit Answer");
        submit.getStyleClass().add("submit-button");

        // Large correct / incorrect symbol
        Label feedback = new Label();
        feedback.setVisible(false);
        feedback.setMouseTransparent(true);
        feedback.getStyleClass().add("feedback-symbol");

        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(
            title, answerBoxes, submit
        );

        StackPane root = new StackPane(content, feedback);

        Scene scene = new Scene(root, 650, 350);
        scene.getStylesheets().add(
            AnswerPopup.class
                .getResource("/styles.css")
                .toExternalForm()
        );

        PauseTransition delay =
            new PauseTransition(Duration.seconds(1));

        // Check answer
        submit.setOnAction(e -> {

            boolean correct = AnswerChecker.checkAnswer(
                fields, correctAnswer
            );

            // Prevent repeated submissions
            submit.setDisable(true);
            onSubmit.run();

            feedback.setText(correct ? "✓" : "✕");
            feedback.setStyle(
                "-fx-text-fill: " +
                (correct ? "#22c55e;" : "#ef2929;")
            );

            feedback.setVisible(true);
            content.setOpacity(0.15);

            System.out.println(
                correct ? "CORRECT ANSWER!" : "WRONG ANSWER!"
            );

            // Show result, then return to main window
            delay.setOnFinished(event -> {
                popup.close();
                onAnswer.accept(correct);
            });

            delay.play();
        });

        // Prevent a pending result from firing after forced close
        popup.setOnHidden(e -> delay.stop());

        popup.setScene(scene);
        popup.show();

        return popup;
    }
}
