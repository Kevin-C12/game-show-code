
package com.example;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.stream.Stream;

public class App extends Application {

    private static QuestionReturn questions;

    private boolean buzzed = false;
    private boolean spaceHeld = false;

    // Receive questions from Main
    public static void setQuestions(QuestionReturn data) {
        questions = data;
    }

    @Override
    public void start(Stage stage) {

        // Create GUI components
        Label title = new Label("Welcome to Sort Feud!");
        Label instruction = new Label(
            "What is the array after Step 1 of insertion sort?"
        );

        Button buzzer = new Button("BUZZ!");
        Button answerButton = new Button("Answer Question");

        // Create container for original array
        HBox numberBoxes = new HBox(10);
        numberBoxes.setAlignment(Pos.CENTER);

        // Hide answer button until buzzer is pressed
        answerButton.setVisible(false);
        answerButton.setManaged(false);

        // Store correct answer from JSON
        JsonArray correctAnswer = new JsonArray();

        // Load insertion sort question
        if (questions != null &&
                questions.getInsertionSort().length > 0) {

            String json = questions.getInsertionSort()[0];

            JsonObject question = JsonParser
                .parseString(json)
                .getAsJsonObject();

            // Get original unordered list
            JsonArray list =
                question.getAsJsonArray("unOrderedList");

            // Get correct answer for Step 1
            correctAnswer = question
                .getAsJsonObject("sortSteps")
                .getAsJsonArray("step1");

            // Create an individual box for each number
            for (int i = 0; i < list.size(); i++) {

                int number = list.get(i).getAsInt();

                Label numberLabel = new Label(
                    String.valueOf(number)
                );

                numberLabel.getStyleClass().add("number-box");

                numberBoxes.getChildren().add(numberLabel);
            }

        } else {
            instruction.setText(
                "No insertion sort questions found"
            );

            buzzer.setDisable(true);
        }

        // Make correct answer available to popup
        final JsonArray expectedAnswer = correctAnswer;

        // Apply CSS classes
        title.getStyleClass().add("title");
        instruction.getStyleClass().add("question-label");
        buzzer.getStyleClass().add("buzzer");
        answerButton.getStyleClass().add("submit-button");

        // Prevent buzzer keyboard focus
        buzzer.setFocusTraversable(false);

        // Format timestamp with milliseconds
        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        // Buzzer action
        buzzer.setOnAction(event -> {

            // Only allow the first buzz
            if (buzzed) {
                return;
            }

            buzzed = true;

            // Record and print timestamp
            LocalTime pressTime = LocalTime.now();

            System.out.println(
                "BUZZER PRESSED AT: "
                + pressTime.format(formatter)
            );

            // Permanently change buzzer appearance
            buzzer.setText("BUZZED!");
            buzzer.getStyleClass().add("buzzer-activated");

            // Make button look pressed in 3D
            buzzer.setStyle(
                "-fx-background-color: #7f1d1d;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: #450a0a;" +
                "-fx-border-width: 5px;" +
                "-fx-border-radius: 1000px;" +
                "-fx-background-radius: 1000px;" +
                "-fx-effect: innershadow(gaussian, black, 15, 0.5, 0, 5);"
            );

            buzzer.setScaleX(0.94);
            buzzer.setScaleY(0.94);

            // Reveal Answer Question button
            answerButton.setVisible(true);
            answerButton.setManaged(true);
        });

        // Open answer popup
        answerButton.setOnAction(event -> {

            AnswerPopup.show(stage, expectedAnswer);

        });

        // Create main layout
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
            title,
            instruction,
            numberBoxes,
            buzzer,
            answerButton
        );

        // Create scene
        Scene scene = new Scene(root, 800, 600);

        // Activate buzzer using Space
        scene.setOnKeyPressed(event -> {

            if (event.getCode() == KeyCode.SPACE
                    && !spaceHeld
                    && !buzzed
                    && !buzzer.isDisabled()) {

                spaceHeld = true;
                buzzer.fire();
                event.consume();
            }
        });

        // Reset Space key state on release
        scene.setOnKeyReleased(event -> {

            if (event.getCode() == KeyCode.SPACE) {
                spaceHeld = false;
            }
        });

        // Load stylesheet
        scene.getStylesheets().add(
            getClass()
                .getResource("/styles.css")
                .toExternalForm()
        );

        // Delete generated JSON files when window closes
        stage.setOnCloseRequest(event -> {
            deleteGeneratedJson();
        });

        // Display GUI
        stage.setTitle("Sort Feud");
        stage.setScene(scene);
        stage.show();
    }

    // Delete generated sorting JSON files
    private void deleteGeneratedJson() {

        Path folder = Path.of(
            "src/main/java/com/example/json"
        );

        if (!Files.isDirectory(folder)) {
            return;
        }

        try (Stream<Path> files = Files.list(folder)) {

            files.filter(path -> {

                String name = path.getFileName().toString();

                return name.matches(
                    "(insertionSort|selectionSort|bubbleSort)\\d+\\.json"
                );

            }).forEach(path -> {

                try {
                    Files.deleteIfExists(path);

                    System.out.println(
                        "Deleted: " + path.getFileName()
                    );

                } catch (IOException e) {
                    System.out.println(
                        "Could not delete: " + path
                    );
                }
            });

        } catch (IOException e) {
            System.out.println(
                "JSON cleanup error: " + e.getMessage()
            );
        }
    }
}
