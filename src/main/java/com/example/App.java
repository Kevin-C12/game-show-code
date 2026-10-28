
package com.example;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class App extends Application {

    // Game state
    private final GameState game = new GameState();

    // Windows and layouts
    private BorderPane root;
    private VBox landingPage;
    private VBox gamePage;

    // Header components
    private Label teamLabel;
    private Label timerLabel;
    private Label strikeLabel;
    private Label landingMessage;

    // Game components
    private HBox numberBoxes;
    private HBox answerBoxes;
    private Button buzzer;
    private Button submitButton;
    private Label resultLabel;
    private Label feedbackSymbol;

    private final List<TextField> answerFields = new ArrayList<>();

    // Current question
    private JsonArray correctAnswer;

    // Timer
    private Timeline timer;
    private PauseTransition feedbackTimer;
    private int secondsLeft = 30;

    // Game flags
    private boolean buzzed = false;
    private boolean spaceHeld = false;
    private boolean playing = false;
    private boolean showingFeedback = false;
    private boolean devToolsOpen = false;

    // Developer demo arrays
    private static final int[] SORTED_DEMO = {
        7, 15, 29, 42, 61, 83
    };

    private static final int[] UNSORTED_DEMO = {
        42, 15, 83, 7, 29, 61
    };

    private static final int[] WORST_CASE_DEMO = {
        83, 61, 42, 29, 15, 7
    };

    // Timestamp formatter
    private final DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Override
    public void start(Stage stage) {

        root = new BorderPane();

        // Create header components
        teamLabel = new Label();
        timerLabel = new Label("00:30");
        strikeLabel = new Label();

        teamLabel.getStyleClass().add("team-label");
        timerLabel.getStyleClass().add("timer-label");
        strikeLabel.getStyleClass().add("strike-label");

        // Header layout
        BorderPane topBar = new BorderPane();
        topBar.setLeft(teamLabel);
        topBar.setCenter(timerLabel);
        topBar.setRight(strikeLabel);
        topBar.getStyleClass().add("game-header");

        root.setTop(topBar);

        // Create pages
        createLandingPage();
        createGamePage();

        // Add developer controls on right
        root.setRight(createDevTools());

        // Create countdown timer
        timer = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {

                if (!playing || showingFeedback) {
                    return;
                }

                secondsLeft--;
                updateHeader();

                if (secondsLeft <= 0) {
                    timer.stop();
                    wrongAnswer();
                }
            })
        );

        timer.setCycleCount(Timeline.INDEFINITE);

        // Feedback timer
        feedbackTimer = new PauseTransition(
            Duration.seconds(1.5)
        );

        // Feedback overlay
        feedbackSymbol = new Label();
        feedbackSymbol.getStyleClass().add("feedback-symbol");
        feedbackSymbol.setMouseTransparent(true);
        feedbackSymbol.setVisible(false);

        StackPane mainLayout = new StackPane(
            root, feedbackSymbol
        );

        // Start at landing screen
        showLanding("Team 1 is ready!");

        Scene scene = new Scene(mainLayout, 1000, 650);

        // Space bar activates buzzer
        scene.setOnKeyPressed(e -> {

            if (e.getCode() == KeyCode.SPACE
                    && playing
                    && !buzzed
                    && !spaceHeld
                    && !showingFeedback) {

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

        // Load stylesheet
        scene.getStylesheets().add(
            getClass().getResource("/styles.css")
                .toExternalForm()
        );

        stage.setTitle("Sort Feud");
        stage.setScene(scene);
        stage.show();
    }

    // Create landing page
    private void createLandingPage() {

        landingPage = new VBox(25);
        landingPage.setAlignment(Pos.CENTER);

        landingMessage = new Label();
        landingMessage.getStyleClass().add("question-label");

        Button startButton = new Button("Start Question");
        startButton.getStyleClass().add("submit-button");

        startButton.setOnAction(e -> startQuestion());

        landingPage.getChildren().addAll(
            landingMessage,
            startButton
        );
    }

    // Create gameplay page
    private void createGamePage() {

        gamePage = new VBox(20);
        gamePage.setAlignment(Pos.CENTER);

        Label instruction = new Label(
            "What is the array after Step 1 of insertion sort?"
        );
        instruction.getStyleClass().add("question-label");

        // Original number boxes
        numberBoxes = new HBox(10);
        numberBoxes.setAlignment(Pos.CENTER);

        // Buzzer
        buzzer = new Button("BUZZ!");
        buzzer.getStyleClass().add("buzzer");
        buzzer.setFocusTraversable(false);

        // Answer fields
        answerBoxes = new HBox(10);
        answerBoxes.setAlignment(Pos.CENTER);
        setShown(answerBoxes, false);

        // Submit button
        submitButton = new Button("Submit Answer");
        submitButton.getStyleClass().add("submit-button");
        setShown(submitButton, false);

        // Feedback text
        resultLabel = new Label();
        resultLabel.getStyleClass().add("result-label");

        // Pass button
        Button passButton = new Button("Pass to Other Team");
        passButton.getStyleClass().add("pass-button");

        // Buzzer action
        buzzer.setOnAction(e -> {

            if (buzzed || !playing || showingFeedback) {
                return;
            }

            buzzed = true;

            System.out.println(
                "TEAM " + game.getCurrentTeam() +
                " BUZZED AT: " +
                LocalTime.now().format(formatter)
            );

            buzzer.setText("BUZZED!");
            buzzer.getStyleClass().add("buzzer-activated");
            buzzer.setScaleX(0.94);
            buzzer.setScaleY(0.94);

            // Reveal answer inputs
            setShown(answerBoxes, true);
            setShown(submitButton, true);

            if (!answerFields.isEmpty()) {
                answerFields.get(0).requestFocus();
            }
        });

        // Check submitted answer
        submitButton.setOnAction(e -> {

            if (!playing || !buzzed ||
                showingFeedback || correctAnswer == null) {
                return;
            }

            boolean correct = AnswerChecker.checkAnswer(
                answerFields, correctAnswer
            );

            showingFeedback = true;
            timer.stop();
            submitButton.setDisable(true);

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

            System.out.println(
                correct ? "ANSWER CORRECT!" : "ANSWER WRONG!"
            );

            feedbackTimer.setOnFinished(event -> {

                feedbackSymbol.setVisible(false);
                showingFeedback = false;

                if (correct) {
                    correctAnswer();
                } else {
                    wrongAnswer();
                }
            });

            feedbackTimer.playFromStart();
        });

        // Pass turn to other team
        passButton.setOnAction(e -> {

            if (!playing || showingFeedback) {
                return;
            }

            endQuestion();
            game.switchTeam();

            showLanding(
                "Question passed to Team " +
                game.getCurrentTeam()
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

    // Create expandable DevTools panel
    private VBox createDevTools() {

        VBox devToolsPanel = new VBox(10);
        devToolsPanel.setAlignment(Pos.TOP_CENTER);
        devToolsPanel.getStyleClass().add("devtools-panel");

        Button devButton = new Button("⚙ DevTools");
        Button sortedButton = new Button("Sorted List");
        Button unsortedButton = new Button("Unsorted List");
        Button worstCaseButton = new Button("Worst Case");

        devButton.getStyleClass().add("devtools-button");
        sortedButton.getStyleClass().add("devtools-button");
        unsortedButton.getStyleClass().add("devtools-button");
        worstCaseButton.getStyleClass().add("devtools-button");

        // Hide options initially
        setShown(sortedButton, false);
        setShown(unsortedButton, false);
        setShown(worstCaseButton, false);

        // Expand or collapse DevTools
        devButton.setOnAction(e -> {

            devToolsOpen = !devToolsOpen;

            setShown(sortedButton, devToolsOpen);
            setShown(unsortedButton, devToolsOpen);
            setShown(worstCaseButton, devToolsOpen);
        });

        // Load predefined lists
        sortedButton.setOnAction(e ->
            loadDemoQuestion(SORTED_DEMO)
        );

        unsortedButton.setOnAction(e ->
            loadDemoQuestion(UNSORTED_DEMO)
        );

        worstCaseButton.setOnAction(e ->
            loadDemoQuestion(WORST_CASE_DEMO)
        );

        devToolsPanel.getChildren().addAll(
            devButton,
            sortedButton,
            unsortedButton,
            worstCaseButton
        );

        return devToolsPanel;
    }

    // Start a new insertion sort question
    private void startQuestion() {

        try {

            // Generate question JSON
            InsertionSort.run();

            Path folder = Path.of(
                "src/main/java/com/example/json"
            );

            // Find highest numbered question
            Path newest;

            try (Stream<Path> files = Files.list(folder)) {

                newest = files
                    .filter(p -> p.getFileName().toString()
                        .matches("insertionSort\\d+\\.json"))
                    .max(Comparator.comparingInt(
                        this::getQuestionNumber
                    ))
                    .orElseThrow(() ->
                        new IOException("No insertion sort questions found")
                    );
            }

            // Load question data
            JsonObject question = JsonParser
                .parseString(Files.readString(newest))
                .getAsJsonObject();

            JsonArray original =
                question.getAsJsonArray("unOrderedList");

            correctAnswer = question
                .getAsJsonObject("sortSteps")
                .getAsJsonArray("step1");

            displayNumbers(original);

            // Start fresh after previous 3 strikes
            if (game.getStrikes() >= 3) {
                game.resetStrikes();
            }

            resetBuzzer();

            playing = true;
            secondsLeft = 30;

            updateHeader();
            root.setCenter(gamePage);

            timer.playFromStart();

        } catch (Exception e) {
            showLanding("Could not generate question");
            e.printStackTrace();
        }
    }

    // Display original array and create matching inputs
    private void displayNumbers(JsonArray numbers) {

        numberBoxes.getChildren().clear();
        answerFields.clear();
        answerBoxes.getChildren().clear();

        for (int i = 0; i < numbers.size(); i++) {

            Label number = new Label(
                numbers.get(i).getAsString()
            );
            number.getStyleClass().add("number-box");
            numberBoxes.getChildren().add(number);

            TextField field = new TextField();
            field.setPromptText("?");
            field.setPrefWidth(65);
            field.setAlignment(Pos.CENTER);
            field.getStyleClass().add("answer-box");

            answerFields.add(field);
            answerBoxes.getChildren().add(field);
        }
    }

    // Developer testing question
    private void loadDemoQuestion(int[] demoArray) {

        if (!playing || showingFeedback) {
            return;
        }

        int[] arr = demoArray.clone();

        // Calculate first insertion sort step
        int key = arr[1];
        int j = 0;

        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }

        arr[j + 1] = key;

        // Build expected answer
        JsonArray expected = new JsonArray();

        for (int number : arr) {
            expected.add(number);
        }

        correctAnswer = expected;

        // Build original test question
        JsonArray original = new JsonArray();

        for (int number : demoArray) {
            original.add(number);
        }

        displayNumbers(original);

        resetBuzzer();

        secondsLeft = 30;
        updateHeader();
        timer.playFromStart();

        String demoType;

        if (Arrays.equals(demoArray, SORTED_DEMO)) {
            demoType = "Sorted";
        } else if (Arrays.equals(demoArray, WORST_CASE_DEMO)) {
            demoType = "Worst Case";
        } else {
            demoType = "Unsorted";
        }

        System.out.println(
            "DEVTOOLS: Loaded " + demoType + " list"
        );
    }

    // Restore buzzer and answer UI
    private void resetBuzzer() {

        buzzed = false;
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

    // Show or hide a component
    private void setShown(
            javafx.scene.Node node, boolean visible) {

        node.setVisible(visible);
        node.setManaged(visible);
    }

    // Get numeric suffix in insertionSortN.json
    private int getQuestionNumber(Path path) {

        String name = path.getFileName().toString();

        return Integer.parseInt(
            name.replace("insertionSort", "")
                .replace(".json", "")
        );
    }

    // Handle correct answer
    private void correctAnswer() {

        if (!playing) {
            return;
        }

        game.addPoints(10);
        game.resetStrikes();

        System.out.println(
            "Team " + game.getCurrentTeam() +
            " scored 10 points!"
        );

        endQuestion();

        showLanding(
            "CORRECT! Team " +
            game.getCurrentTeam() +
            " earns 10 points and plays again!"
        );
    }

    // Handle incorrect answer or timeout
    private void wrongAnswer() {

        if (!playing) {
            return;
        }

        game.addStrike();

        System.out.println(
            "Team " + game.getCurrentTeam() +
            " strike " + game.getStrikes()
        );

        updateHeader();

        if (game.getStrikes() >= 3) {

            endQuestion();
            game.switchTeam();

            showLanding(
                "Three strikes! Team " +
                game.getCurrentTeam() +
                "'s turn."
            );

        } else {

            // Same team retries the same question
            resetBuzzer();

            secondsLeft = 30;
            updateHeader();

            timer.playFromStart();
        }
    }

    // Stop the current question
    private void endQuestion() {

        playing = false;
        timer.stop();
        feedbackTimer.stop();

        feedbackSymbol.setVisible(false);
        showingFeedback = false;
    }

    // Show landing page between questions
    private void showLanding(String message) {

        playing = false;
        secondsLeft = 30;

        landingMessage.setText(message);
        root.setCenter(landingPage);

        updateHeader();
    }

    // Refresh team, scores, timer and strikes
    private void updateHeader() {

        teamLabel.setText(
            "TEAM " + game.getCurrentTeam() +
            "\nTeam 1: " + game.getTeam1Points() +
            " pts | Team 2: " + game.getTeam2Points() +
            " pts"
        );

        timerLabel.setText(
            String.format("00:%02d", secondsLeft)
        );

        StringBuilder strikes = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            strikes.append(
                i < game.getStrikes() ? "✕ " : "○ "
            );
        }

        strikeLabel.setText(strikes.toString());
    }
}
