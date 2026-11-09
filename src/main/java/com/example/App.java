
package com.example;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
    private Label rulesLabel;

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

    private final Set<Integer> heldShiftKeys = ConcurrentHashMap.newKeySet();
    private Stage gameStage;
    private boolean nativeHookRegistered = false;
    private boolean leftShiftDown = false;
    private boolean rightShiftDown = false;
    private boolean showingFeedback = false;
    private boolean devToolsOpen = false;

    private final DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Override
    public void start(Stage stage) {

        gameStage = stage;

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

        scene.getStylesheets().add(
            getClass().getResource("/styles.css")
                .toExternalForm()
        );

        stage.setTitle("Sort Feud");
        stage.setScene(scene);
        stage.show();
        registerShiftBuzzers(scene);
    }

    private final NativeKeyListener shiftListener = new NativeKeyListener() {
        @Override
        public void nativeKeyPressed(NativeKeyEvent event) {
            if (event.getKeyCode() != NativeKeyEvent.VC_SHIFT) {
                return;
            }

            int location = event.getKeyLocation();
            int team = location == NativeKeyEvent.KEY_LOCATION_LEFT ? 1
                : location == NativeKeyEvent.KEY_LOCATION_RIGHT ? 2 : 0;
            if (team == 0 || !heldShiftKeys.add(location)) {
                return;
            }

            Platform.runLater(() -> {
                if (gameStage.isFocused() && root.getCenter() == gamePage) {
                    buzz(team);
                }
            });
        }

        @Override
        public void nativeKeyReleased(NativeKeyEvent event) {
            if (event.getKeyCode() == NativeKeyEvent.VC_SHIFT) {
                heldShiftKeys.remove(event.getKeyLocation());
            }
        }
    };

    private void registerShiftBuzzers(Scene scene) {
        if (System.getProperty("os.name", "").startsWith("Mac")) {
            try {
                leftShiftDown = MacShiftKeys.leftDown();
                rightShiftDown = MacShiftKeys.rightDown();
                scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.SHIFT) {
                        handleMacShiftPressed();
                    }
                });
                scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
                    if (event.getCode() == KeyCode.SHIFT) {
                        leftShiftDown = MacShiftKeys.leftDown();
                        rightShiftDown = MacShiftKeys.rightDown();
                    }
                });
                gameStage.focusedProperty().addListener((observable, oldValue, focused) -> {
                    if (!focused) {
                        leftShiftDown = false;
                        rightShiftDown = false;
                    }
                });
                return;
            } catch (LinkageError | RuntimeException e) {
                System.err.println("Mac Shift buzzers unavailable: " + e.getMessage());
                showKeyboardFallback();
                return;
            }
        }

        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(shiftListener);
            nativeHookRegistered = true;
        } catch (NativeHookException | UnsatisfiedLinkError e) {
            System.err.println("Shift buzzers unavailable; use the on-screen buttons: "
                + e.getMessage());
            showKeyboardFallback();
        }
    }

    private void handleMacShiftPressed() {
        boolean leftNow = MacShiftKeys.leftDown();
        boolean rightNow = MacShiftKeys.rightDown();
        int team = leftNow && !leftShiftDown ? 1
            : rightNow && !rightShiftDown ? 2 : 0;
        leftShiftDown = leftNow;
        rightShiftDown = rightNow;
        if (team != 0 && gameStage.isFocused() && root.getCenter() == gamePage) {
            buzz(team);
        }
    }

    private void showKeyboardFallback() {
        rulesLabel.setText("Keyboard buzzers are unavailable. Click a team buzzer. "
            + "Three strikes give the other team one steal chance. "
            + "Complete the sort to earn 10 points.");
    }

    @Override
    public void stop() {
        if (nativeHookRegistered) {
            GlobalScreen.removeNativeKeyListener(shiftListener);
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e) {
                System.err.println("Could not release keyboard listener: "
                    + e.getMessage());
            }
        }
    }

    // Landing page
    private void createLandingPage() {

        landingPage = new VBox(25);
        landingPage.setAlignment(Pos.CENTER);

        landingMessage = new Label();
        landingMessage.getStyleClass().add("question-label");

        rulesLabel = new Label(
            "Left Shift buzzes for Team 1; Right Shift buzzes for Team 2. "
            + "You can also click a buzzer. Three strikes give the other team one steal chance. "
            + "Complete the sort to earn 10 points."
        );
        rulesLabel.getStyleClass().add("rules-label");
        rulesLabel.setWrapText(true);
        rulesLabel.setMaxWidth(650);

        Button startButton = new Button("Start Round");
        startButton.getStyleClass().add("submit-button");

        startButton.setOnAction(e -> startQuestion());

        landingPage.getChildren().addAll(
            landingMessage, rulesLabel, startButton
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

        team1Buzzer = new Button("Team 1");
        team2Buzzer = new Button("Team 2");
        team1Buzzer.getStyleClass().add("team-one");
        team2Buzzer.getStyleClass().add("team-two");
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
        selected.getStyleClass().add("buzzer-activated");
        team1Buzzer.setDisable(true);
        team2Buzzer.setDisable(true);

        setShown(answerBoxes, true);
        setShown(submitButton, true);
        resultLabel.setText("Team " + team + ", enter step "
            + engine.getCurrentStepNumber() + " of " + engine.getTotalSteps() + ".");
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
            instruction.setText(
                "Round " + engine.getRoundNumber() +
                " • " + engine.getSortName().toUpperCase() +
                "\nWhat is the next step?"
            );

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

        int[] numbers = engine.getNumbers();

        for (int i = 0; i < numbers.length; i++) {

            Label label = new Label(
                String.valueOf(numbers[i])
            );
            label.getStyleClass().add("number-box");

            numberBoxes.getChildren().add(label);

            TextField field = new TextField();
            field.setPromptText("–");
            field.setPrefWidth(68);
            field.setAlignment(Pos.CENTER);
            field.getStyleClass().add("answer-box");

            answerFields.add(field);
            answerBoxes.getChildren().add(field);
        }

        for (int i = 0; i < answerFields.size(); i++) {
            setupAutoAdvance(answerFields.get(i), i);
        }
    }

    private void setupAutoAdvance(TextField field, int index) {
        field.setOnAction(e -> submitAnswer());

        field.setTextFormatter(new TextFormatter<String>(change ->
            change.getControlNewText().matches("\\d{0,2}") ? change : null
        ));

        PauseTransition advanceDelay = new PauseTransition(Duration.millis(450));
        advanceDelay.setOnFinished(e -> {
            if (field.isFocused() && !field.getText().isEmpty()
                    && index < answerFields.size() - 1) {
                answerFields.get(index + 1).requestFocus();
            }
        });

        field.textProperty().addListener((observable, oldValue, newValue) -> {
            advanceDelay.stop();
            if (newValue.isEmpty() || index == answerFields.size() - 1) {
                return;
            }
            if (newValue.length() == 2) {
                if (field.isFocused()) {
                    answerFields.get(index + 1).requestFocus();
                }
            } else {
                advanceDelay.playFromStart();
            }
        });

        field.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.BACK_SPACE
                    && field.getText().isEmpty() && index > 0) {
                answerFields.get(index - 1).requestFocus();
                e.consume();
            }
        });
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

        boolean correct = result == GameEngine.Result.CORRECT
            || result == GameEngine.Result.STEP_CORRECT;

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

                case STEP_CORRECT:
                    displayNumbers();
                    resetAnswerUI();
                    resultLabel.setText("Correct! Step "
                        + engine.getCurrentStepNumber() + " of "
                        + engine.getTotalSteps() + " is next.");
                    updateHeader();
                    break;

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

        team1Buzzer.setText("Team 1");
        team2Buzzer.setText("Team 2");
        team1Buzzer.getStyleClass().remove("buzzer-activated");
        team2Buzzer.getStyleClass().remove("buzzer-activated");
        team1Buzzer.setDisable(engine.getCurrentTeam() == 2);
        team2Buzzer.setDisable(engine.getCurrentTeam() == 1);

        setShown(answerBoxes, false);
        setShown(submitButton, false);

        submitButton.setDisable(false);
        resultLabel.setText("");
        resultLabel.setStyle("");

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
