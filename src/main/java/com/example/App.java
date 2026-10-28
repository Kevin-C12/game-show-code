
package com.example;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    // Game logic
    private final GameEngine engine = new GameEngine();

    // Layouts
    private BorderPane root;
    private VBox landingPage;
    private VBox gamePage;

    // Header
    private Label teamLabel;
    private Label timerLabel;
    private Label strikeLabel;
    private Label landingMessage;

    // Game controls
    private HBox numberBoxes;
    private HBox answerBoxes;
    private Button buzzer;
    private Button submitButton;
    private Label resultLabel;
    private Label feedbackSymbol;

    private final List<TextField> answerFields =
        new ArrayList<>();

    // Animation and timing
    private Timeline timer;
    private PauseTransition feedbackTimer;

    private boolean spaceHeld = false;
    private boolean showingFeedback = false;
    private boolean devToolsOpen = false;

    private final DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Override
    public void start(Stage stage) {

        root = new BorderPane();

        // Header
        teamLabel = new Label();
        timerLabel = new Label();
        strikeLabel = new Label();

        teamLabel.getStyleClass().add("team-label");
        timerLabel.getStyleClass().add("timer-label");
        strikeLabel.getStyleClass().add("strike-label");

        BorderPane header = new BorderPane();
        header.setLeft(teamLabel);
        header.setCenter(timerLabel);
        header.setRight(strikeLabel);
        header.getStyleClass().add("game-header");

        root.setTop(header);

        // Create pages
        createLandingPage();
        createGamePage();

        // Developer tools
        root.setRight(createDevTools());

        // Countdown timer
        timer = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {

                if (showingFeedback || !engine.isPlaying()) {
                    return;
                }

                if (engine.tick()) {
                    timer.stop();

                    GameEngine.Result result =
                        engine.timeExpired();

                    showFeedback(result);
                }

                updateHeader();
            })
        );

        timer.setCycleCount(Timeline.INDEFINITE);

        // Feedback animation
        feedbackTimer = new PauseTransition(
            Duration.seconds(1.5)
        );

        feedbackSymbol = new Label();
        feedbackSymbol.getStyleClass().add("feedback-symbol");
        feedbackSymbol.setMouseTransparent(true);
        feedbackSymbol.setVisible(false);

        StackPane layout = new StackPane(
            root, feedbackSymbol
        );

        // Show landing screen
        showLanding("Team 1 is ready!");

        Scene scene = new Scene(layout, 1000, 650);

        // Space activates buzzer
        scene.setOnKeyPressed(e -> {

            if (e.getCode() == KeyCode.SPACE
                    && !spaceHeld
                    && !showingFeedback
                    && engine.isPlaying()
                    && !engine.isBuzzed()) {

                spaceHeld = true;
                buzzer.fire();
                e.consume();
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                spaceHeld = false;
            }
        });

        scene.getStylesheets().add(
            getClass().getResource("/styles.css")
                .toExternalForm()
        );

        stage.setTitle("Sort Feud");
        stage.setScene(scene);
        stage.show();
    }

    // Landing page
    private void createLandingPage() {

        landingPage = new VBox(25);
        landingPage.setAlignment(Pos.CENTER);

        landingMessage = new Label();
        landingMessage.getStyleClass().add("question-label");

        Button startButton = new Button("Start Question");
        startButton.getStyleClass().add("submit-button");

        startButton.setOnAction(e -> startQuestion());

        landingPage.getChildren().addAll(
            landingMessage, startButton
        );
    }

    // Main game page
    private void createGamePage() {

        gamePage = new VBox(20);
        gamePage.setAlignment(Pos.CENTER);

        Label instruction = new Label(
            "What is the array after Step 1 of insertion sort?"
        );
        instruction.getStyleClass().add("question-label");

        numberBoxes = new HBox(10);
        numberBoxes.setAlignment(Pos.CENTER);

        // Buzzer
        buzzer = new Button("BUZZ!");
        buzzer.getStyleClass().add("buzzer");
        buzzer.setFocusTraversable(false);

        buzzer.setOnAction(e -> {

            if (showingFeedback || !engine.buzz()) {
                return;
            }

            System.out.println(
                "TEAM " + engine.getCurrentTeam() +
                " BUZZED AT: " +
                LocalTime.now().format(formatter)
            );

            buzzer.setText("BUZZED!");
            buzzer.getStyleClass().add("buzzer-activated");
            buzzer.setScaleX(0.94);
            buzzer.setScaleY(0.94);

            setShown(answerBoxes, true);
            setShown(submitButton, true);

            if (!answerFields.isEmpty()) {
                answerFields.get(0).requestFocus();
            }
        });

        // Answer inputs
        answerBoxes = new HBox(10);
        answerBoxes.setAlignment(Pos.CENTER);
        setShown(answerBoxes, false);

        // Submit button
        submitButton = new Button("Submit Answer");
        submitButton.getStyleClass().add("submit-button");
        setShown(submitButton, false);

        submitButton.setOnAction(e -> submitAnswer());

        resultLabel = new Label();
        resultLabel.getStyleClass().add("result-label");

        // Pass button
        Button passButton = new Button("Pass to Other Team");
        passButton.getStyleClass().add("pass-button");

        passButton.setOnAction(e -> {

            if (!engine.isPlaying() || showingFeedback) {
                return;
            }

            timer.stop();
            engine.passTurn();

            showLanding(
                "Question passed to Team " +
                engine.getCurrentTeam()
            );
        });

        gamePage.getChildren().addAll(
            instruction,
            numberBoxes,
            buzzer,
            answerBoxes,
            submitButton,
            resultLabel,
            passButton
        );
    }

    // Start question using GameEngine
    private void startQuestion() {

        try {
            engine.startQuestion();

            displayNumbers();
            resetAnswerUI();

            root.setCenter(gamePage);
            updateHeader();
            timer.playFromStart();

        } catch (Exception e) {
            showLanding("Could not generate question");
            e.printStackTrace();
        }
    }

    // Display numbers and create inputs
    private void displayNumbers() {

        numberBoxes.getChildren().clear();
        answerBoxes.getChildren().clear();
        answerFields.clear();

        for (int number : engine.getNumbers()) {

            Label label = new Label(
                String.valueOf(number)
            );
            label.getStyleClass().add("number-box");

            numberBoxes.getChildren().add(label);

            TextField field = new TextField();
            field.setPromptText("?");
            field.setPrefWidth(65);
            field.setAlignment(Pos.CENTER);
            field.getStyleClass().add("answer-box");

            answerFields.add(field);
            answerBoxes.getChildren().add(field);
        }
    }

    // Submit player's answer
    private void submitAnswer() {

        if (!engine.isPlaying() ||
                !engine.isBuzzed() || showingFeedback) {
            return;
        }

        int[] answer = new int[answerFields.size()];

        // Convert text fields into integers
        try {
            for (int i = 0; i < answer.length; i++) {
                answer[i] = Integer.parseInt(
                    answerFields.get(i).getText().trim()
                );
            }
        } catch (NumberFormatException e) {
            resultLabel.setText(
                "Please enter valid numbers in every box."
            );
            resultLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        // Let GameEngine check the answer
        GameEngine.Result result =
            engine.submitAnswer(answer);

        showFeedback(result);
    }

    // Display correct or incorrect feedback
    private void showFeedback(GameEngine.Result result) {

        showingFeedback = true;
        timer.stop();
        submitButton.setDisable(true);

        boolean correct =
            result == GameEngine.Result.CORRECT;

        feedbackSymbol.setText(correct ? "✓" : "✕");
        feedbackSymbol.setStyle(
            "-fx-text-fill: " +
            (correct ? "#22c55e;" : "#ef2929;")
        );
        feedbackSymbol.setVisible(true);

        resultLabel.setText(
            correct ? "CORRECT!" : "INCORRECT!"
        );
        resultLabel.setStyle(
            "-fx-text-fill: " +
            (correct ? "#22c55e;" : "#ef2929;")
        );

        updateHeader();

        feedbackTimer.setOnFinished(e -> {

            feedbackSymbol.setVisible(false);
            showingFeedback = false;

            switch (result) {

                case CORRECT:
                    showLanding(
                        "CORRECT! Team " +
                        engine.getCurrentTeam() +
                        " earns 10 points and plays again!"
                    );
                    break;

                case THREE_STRIKES:
                    showLanding(
                        "Three strikes! Team " +
                        engine.getCurrentTeam() +
                        "'s turn."
                    );
                    break;

                case WRONG:
                    resetAnswerUI();
                    updateHeader();
                    timer.playFromStart();
                    break;
            }
        });

        feedbackTimer.playFromStart();
    }

    // Reset buzzer and answer fields
    private void resetAnswerUI() {

        spaceHeld = false;

        buzzer.setText("BUZZ!");
        buzzer.getStyleClass().remove("buzzer-activated");
        buzzer.setScaleX(1);
        buzzer.setScaleY(1);

        setShown(answerBoxes, false);
        setShown(submitButton, false);

        submitButton.setDisable(false);
        resultLabel.setText("");

        for (TextField field : answerFields) {
            field.clear();
        }
    }

    // Developer tools
    private VBox createDevTools() {

        VBox panel = new VBox(10);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.getStyleClass().add("devtools-panel");

        Button devButton = new Button("⚙ DevTools");
        Button sortedButton = new Button("Sorted List");
        Button unsortedButton = new Button("Unsorted List");
        Button worstButton = new Button("Worst Case");

        Button[] options = {
            sortedButton, unsortedButton, worstButton
        };

        devButton.getStyleClass().add("devtools-button");

        for (Button button : options) {
            button.getStyleClass().add("devtools-button");
            setShown(button, false);
        }

        // Open and close developer options
        devButton.setOnAction(e -> {

            devToolsOpen = !devToolsOpen;

            for (Button button : options) {
                setShown(button, devToolsOpen);
            }
        });

        sortedButton.setOnAction(e ->
            loadDemo(GameEngine.SORTED_DEMO)
        );

        unsortedButton.setOnAction(e ->
            loadDemo(GameEngine.UNSORTED_DEMO)
        );

        worstButton.setOnAction(e ->
            loadDemo(GameEngine.WORST_CASE_DEMO)
        );

        panel.getChildren().addAll(
            devButton, sortedButton,
            unsortedButton, worstButton
        );

        return panel;
    }

    // Load developer question
    private void loadDemo(int[] array) {

        if (!engine.isPlaying() || showingFeedback) {
            return;
        }

        engine.loadDemoQuestion(array);

        displayNumbers();
        resetAnswerUI();
        updateHeader();

        timer.playFromStart();

        System.out.println("DEVTOOLS: Demo question loaded");
    }

    // Show landing screen
    private void showLanding(String message) {

        timer.stop();

        landingMessage.setText(message);
        root.setCenter(landingPage);

        updateHeader();
    }

    // Update header
    private void updateHeader() {

        teamLabel.setText(
            "TEAM " + engine.getCurrentTeam() +
            "\nTeam 1: " + engine.getTeam1Points() +
            " pts | Team 2: " + engine.getTeam2Points() +
            " pts"
        );

        timerLabel.setText(
            String.format(
                "00:%02d", engine.getSecondsLeft()
            )
        );

        StringBuilder strikes = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            strikes.append(
                i < engine.getStrikes() ? "✕ " : "○ "
            );
        }

        strikeLabel.setText(strikes.toString());
    }

    // Show or hide GUI component
    private void setShown(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }
}
