
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
    private Button team1Buzzer;
    private Button team2Buzzer;
    private Label instruction;
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
        showLanding("Round 1: insertion sort. Teams, get ready to buzz!");

        Scene scene = new Scene(layout, 1000, 650);

        // Teams can buzz with the number keys.
        scene.setOnKeyPressed(e -> {

            if ((e.getCode() == KeyCode.DIGIT1 || e.getCode() == KeyCode.DIGIT2)
                    && !spaceHeld
                    && !showingFeedback
                    && engine.isPlaying()
                    && !engine.isBuzzed()) {

                spaceHeld = true;
                buzz(e.getCode() == KeyCode.DIGIT1 ? 1 : 2);
                e.consume();
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.DIGIT1 || e.getCode() == KeyCode.DIGIT2) {
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

        Label rules = new Label(
            "Press 1 or 2 to buzz. Three strikes give the other team one steal chance. "
            + "A correct answer earns 10 points."
        );
        rules.getStyleClass().add("rules-label");
        rules.setWrapText(true);
        rules.setMaxWidth(650);

        Button startButton = new Button("Start Round");
        startButton.getStyleClass().add("submit-button");

        startButton.setOnAction(e -> startQuestion());

        landingPage.getChildren().addAll(
            landingMessage, rules, startButton
        );
    }

    // Main game page
    private void createGamePage() {

        gamePage = new VBox(20);
        gamePage.setAlignment(Pos.CENTER);

        instruction = new Label();
        instruction.getStyleClass().add("question-label");

        numberBoxes = new HBox(10);
        numberBoxes.setAlignment(Pos.CENTER);

        team1Buzzer = new Button("TEAM 1\nBUZZ (1)");
        team2Buzzer = new Button("TEAM 2\nBUZZ (2)");
        for (Button button : new Button[] {team1Buzzer, team2Buzzer}) {
            button.getStyleClass().add("buzzer");
            button.setFocusTraversable(false);
            button.setWrapText(true);
        }
        team1Buzzer.setOnAction(e -> buzz(1));
        team2Buzzer.setOnAction(e -> buzz(2));
        HBox buzzers = new HBox(20, team1Buzzer, team2Buzzer);
        buzzers.setAlignment(Pos.CENTER);

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

        gamePage.getChildren().addAll(
            instruction,
            numberBoxes,
            buzzers,
            answerBoxes,
            submitButton,
            resultLabel
        );
    }

    private void buzz(int team) {
        if (showingFeedback || !engine.buzz(team)) {
            return;
        }

        System.out.println("TEAM " + team + " BUZZED AT: "
            + LocalTime.now().format(formatter));

        Button selected = team == 1 ? team1Buzzer : team2Buzzer;
        selected.setText("BUZZED!");
        selected.getStyleClass().add("buzzer-activated");
        team1Buzzer.setDisable(true);
        team2Buzzer.setDisable(true);

        setShown(answerBoxes, true);
        setShown(submitButton, true);
        resultLabel.setText("Team " + team + ", enter your answer.");
        updateHeader();
        timer.playFromStart();

        if (!answerFields.isEmpty()) {
            answerFields.get(0).requestFocus();
        }
    }

    // Start question using GameEngine
    private void startQuestion() {

        try {
            engine.startQuestion();

            displayNumbers();
            resetAnswerUI();
            instruction.setText("Round " + engine.getRoundNumber()
                + ": What is the array after Step 1 of "
                + engine.getSortName() + "?");

            root.setCenter(gamePage);
            updateHeader();

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

        resultLabel.setText(correct ? "CORRECT!" : "INCORRECT!");
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
                        " earns 10 points. Round " + (engine.getRoundNumber() + 1)
                        + " is " + engine.getNextSortName() + "."
                    );
                    break;

                case THREE_STRIKES:
                    resetAnswerUI();
                    resultLabel.setText("Three strikes! Team "
                        + engine.getCurrentTeam() + " has one chance to steal.");
                    updateHeader();
                    break;

                case STEAL_FAILED:
                    showLanding("Steal missed. No points this round. Round "
                        + (engine.getRoundNumber() + 1) + " is "
                        + engine.getNextSortName() + ".");
                    break;

                case WRONG:
                    resetAnswerUI();
                    updateHeader();
                    break;
            }
        });

        feedbackTimer.playFromStart();
    }

    // Reset buzzer and answer fields
    private void resetAnswerUI() {

        spaceHeld = false;

        team1Buzzer.setText("TEAM 1\nBUZZ (1)");
        team2Buzzer.setText("TEAM 2\nBUZZ (2)");
        team1Buzzer.getStyleClass().remove("buzzer-activated");
        team2Buzzer.getStyleClass().remove("buzzer-activated");
        team1Buzzer.setDisable(engine.getCurrentTeam() == 2);
        team2Buzzer.setDisable(engine.getCurrentTeam() == 1);

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

        timer.stop();

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
            (engine.getCurrentTeam() == 0 ? "BUZZ TO PLAY" :
                "TEAM " + engine.getCurrentTeam()
                + (engine.isStealAttempt() ? " - STEAL" : "")) +
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
                i < (engine.isPlaying() ? engine.getRoundStrikes() : 0)
                    ? "✕ " : "○ "
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
