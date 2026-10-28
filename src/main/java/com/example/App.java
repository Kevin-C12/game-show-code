
package com.example;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class App extends Application {

    private boolean buzzed = false;
    private boolean spaceHeld = false;

    @Override
    public void start(Stage stage) {

        // Create components
        Label title = new Label("Welcome to Sort Feud!");
        Button buzzer = new Button("BUZZ!");

        TextField answerInput = new TextField();
        answerInput.setPromptText("Enter your answer");
        answerInput.setMaxWidth(250);

        // Hide answer input initially
        answerInput.setVisible(false);
        answerInput.setManaged(false);

        // Apply CSS classes
        title.getStyleClass().add("title");
        buzzer.getStyleClass().add("buzzer");
        buzzer.setFocusTraversable(false);

        // Format timestamp
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        // Buzzer behavior
        
        buzzer.setOnAction(event -> {

            if (buzzed) {
                return;
            }

            buzzed = true;

            // Print timestamp
            LocalTime pressTime = LocalTime.now();

            System.out.println(
                "BUZZER PRESSED AT: " + pressTime.format(formatter)
            );

            // Permanently change button appearance
            buzzer.setText("BUZZED!");

            buzzer.setStyle(
                "-fx-background-color: #7f1d1d;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: #450a0a;" +
                "-fx-border-width: 5px;" +
                "-fx-border-radius: 1000px;" +
                "-fx-background-radius: 1000px;" +
                "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.8), 15, 0.5, 0, 5);"
            );

            buzzer.setScaleX(0.92);
            buzzer.setScaleY(0.92);

            // Reveal answer input
            answerInput.setVisible(true);
            answerInput.setManaged(true);
            answerInput.requestFocus();
        });


        // Create layout
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
            title,
            buzzer,
            answerInput
        );

        Scene scene = new Scene(root, 640, 480);

        // Space bar activation
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE
                    && !spaceHeld
                    && !buzzed
                    && !answerInput.isFocused()) {

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

        // Display window
        stage.setTitle("Sort Feud");
        stage.setScene(scene);
        stage.show();
    }
}
