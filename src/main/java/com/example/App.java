
package com.example;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

        // Create components
        Label title = new Label("Welcome to Sort Feud!");
        Label instruction = new Label(
            "What is the array after Step 1 of insertion sort?"
        );

        Button buzzer = new Button("BUZZ!");
        Button submitButton = new Button("Submit Answer");

        Label resultLabel = new Label("");

        // Containers
        HBox numberBoxes = new HBox(10);
        HBox answerBoxes = new HBox(10);

        numberBoxes.setAlignment(Pos.CENTER);
        answerBoxes.setAlignment(Pos.CENTER);

        // Store text fields and correct answers
        List<TextField> answerFields = new ArrayList<>();
        JsonArray correctAnswer = new JsonArray();

        // Load insertion sort question
        if (questions != null &&
                questions.getInsertionSort().length > 0) {

            String json = questions.getInsertionSort()[0];

            JsonObject question = JsonParser
                .parseString(json)
                .getAsJsonObject();

            JsonArray list =
                question.getAsJsonArray("unOrderedList");

            correctAnswer = question
                .getAsJsonObject("sortSteps")
                .getAsJsonArray("step1");

            // Create number boxes and answer fields
            for (int i = 0; i < list.size(); i++) {

                int number = list.get(i).getAsInt();

                Label numberLabel = new Label(
                    String.valueOf(number)
                );

                numberLabel.getStyleClass().add("number-box");
                numberBoxes.getChildren().add(numberLabel);

                TextField field = new TextField();
                field.setPromptText("?");
                field.setPrefWidth(65);
                field.setAlignment(Pos.CENTER);
                field.getStyleClass().add("answer-box");

                answerFields.add(field);
                answerBoxes.getChildren().add(field);
            }

        } else {
            instruction.setText(
                "No insertion sort questions found"
            );
            buzzer.setDisable(true);
        }

        // Store correct answer for event handler
        final JsonArray expectedAnswer = correctAnswer;

        // Hide answer controls initially
        answerBoxes.setVisible(false);
        answerBoxes.setManaged(false);

        submitButton.setVisible(false);
        submitButton.setManaged(false);

        // Apply CSS classes
        title.getStyleClass().add("title");
        instruction.getStyleClass().add("question-label");
        buzzer.getStyleClass().add("buzzer");
        submitButton.getStyleClass().add("submit-button");
        resultLabel.getStyleClass().add("result-label");

        buzzer.setFocusTraversable(false);

        // Timestamp formatter
        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        // Buzzer behavior
        buzzer.setOnAction(event -> {

            if (buzzed) {
                return;
            }

            buzzed = true;

            LocalTime pressTime = LocalTime.now();

            System.out.println(
                "BUZZER PRESSED AT: "
                + pressTime.format(formatter)
            );

            // Permanently change buzzer
            buzzer.setText("BUZZED!");

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

            // Reveal answer controls
            answerBoxes.setVisible(true);
            answerBoxes.setManaged(true);

            submitButton.setVisible(true);
            submitButton.setManaged(true);

            if (!answerFields.isEmpty()) {
                answerFields.get(0).requestFocus();
            }
        });

        // Create overlay for correct/wrong animation
        Label feedbackSymbol = new Label();
        feedbackSymbol.getStyleClass().add("feedback-symbol");
        feedbackSymbol.setMouseTransparent(true);
        feedbackSymbol.setVisible(false);

        // Automatically hide feedback
        PauseTransition feedbackTimer =
            new PauseTransition(Duration.seconds(1.5));

        feedbackTimer.setOnFinished(e -> {
            feedbackSymbol.setVisible(false);
        });

        // Submit button behavior
        submitButton.setOnAction(event -> {

            boolean correct = AnswerChecker.checkAnswer(
                answerFields,
                expectedAnswer
            );

            feedbackTimer.stop();

            if (correct) {

                // Correct answer
                feedbackSymbol.setText("✓");
                feedbackSymbol.setStyle(
                    "-fx-text-fill: #22c55e;"
                );

                resultLabel.setText("CORRECT!");
                resultLabel.setStyle(
                    "-fx-text-fill: #22c55e;"
                );

                System.out.println("ANSWER CORRECT!");

                // Prevent another submission
                submitButton.setDisable(true);

            } else {

                // Incorrect answer
                feedbackSymbol.setText("✕");
                feedbackSymbol.setStyle(
                    "-fx-text-fill: #ef2929;"
                );

                resultLabel.setText(
                    "INCORRECT! Try again."
                );

                resultLabel.setStyle(
                    "-fx-text-fill: #ef2929;"
                );

                System.out.println("ANSWER INCORRECT!");
            }

            // Display giant feedback symbol
            feedbackSymbol.setVisible(true);
            feedbackTimer.playFromStart();
        });

        // Main layout
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
            title,
            instruction,
            numberBoxes,
            buzzer,
            answerBoxes,
            submitButton,
            resultLabel
        );

        // StackPane allows feedback to overlay GUI
        StackPane mainLayout = new StackPane();

        mainLayout.getChildren().addAll(
            root,
            feedbackSymbol
        );

        Scene scene = new Scene(mainLayout, 900, 650);

        // Space activates buzzer
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE
                    && !spaceHeld
                    && !buzzed) {

                spaceHeld = true;
                buzzer.fire();
                event.consume();
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                spaceHeld = false;
            }
        });

        // Load stylesheet
        scene.getStylesheets().add(
            getClass().getResource("/styles.css").toExternalForm()
        );

        // Delete generated JSON when window closes
        stage.setOnCloseRequest(event -> {
            deleteGeneratedJson();
        });

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
