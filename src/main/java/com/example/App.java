package com.example;

import javafx.animation.KeyFrame;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App extends Application {

    private final GameEngine engine = new GameEngine();
    private final BuzzerInput buzzerInput = new BuzzerInput();
    private final List<TextField> answerFields = new ArrayList<>();
    private final List<Label> scoreDots = new ArrayList<>();
    private final List<TextField> teamOneMemberFields = new ArrayList<>();
    private final List<TextField> teamTwoMemberFields = new ArrayList<>();
    private final DateTimeFormatter timestampFormat =
        DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private BorderPane root;
    private Node gameHeader;
    private Node setupHeader;
    private ScrollPane setupViewport;
    private ScrollPane gameViewport;
    private Node devTools;
    private VBox teamOneCard;
    private VBox teamTwoCard;
    private TextField teamOneNameField;
    private TextField teamTwoNameField;
    private VBox teamOneMembersBox;
    private VBox teamTwoMembersBox;
    private Label setupError;
    private Label teamOneName;
    private Label teamTwoName;
    private Label teamOneWins;
    private Label teamTwoWins;
    private Label teamOneStrikes;
    private Label teamTwoStrikes;
    private Label teamOneCompetitor;
    private Label teamTwoCompetitor;
    private Label matchStatus;
    private Label timerLabel;
    private Label questionLabel;
    private Label versusLabel;
    private Label currentCaption;
    private HBox numberBoxes;
    private HBox answerBoxes;
    private VBox answerArea;
    private Button teamOneBuzzer;
    private Button teamTwoBuzzer;
    private Button submitButton;
    private Button continueButton;
    private Label resultLabel;
    private Label feedbackBanner;
    private Timeline timer;
    private ScaleTransition timerPulse;
    private PauseTransition feedbackTimer;
    private boolean showingFeedback;
    private boolean timerPulsing;
    private boolean holdingFeedbackMatchup;
    private List<Integer> renderedDotOwners = List.of();
    private Matchup displayedMatchup;
    private long questionRevealedAtNanos;

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        root.getStyleClass().add("app-shell");
        gameHeader = createHeader();
        setupHeader = createSetupHeader();
        root.setTop(setupHeader);

        createSetupPage();
        createGamePage();
        devTools = createDevTools();

        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (showingFeedback || !engine.isPlaying()) {
                return;
            }
            if (engine.tick()) {
                timer.stop();
                showFeedback(engine.timeExpired());
            }
            updateScoreboard();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);

        timerPulse = new ScaleTransition(Duration.millis(360), timerLabel);
        timerPulse.setFromX(1);
        timerPulse.setFromY(1);
        timerPulse.setToX(1.24);
        timerPulse.setToY(1.24);
        timerPulse.setAutoReverse(true);
        timerPulse.setCycleCount(Timeline.INDEFINITE);

        feedbackTimer = new PauseTransition();
        feedbackBanner = new Label();
        feedbackBanner.getStyleClass().add("feedback-banner");
        feedbackBanner.setMouseTransparent(true);
        feedbackBanner.setVisible(false);

        StackPane layout = new StackPane(root, feedbackBanner);
        StackPane.setAlignment(feedbackBanner, Pos.TOP_CENTER);
        StackPane.setMargin(feedbackBanner, new Insets(154, 20, 0, 20));

        Scene scene = new Scene(layout, 1180, 760);
        scene.getStylesheets().add(
            getClass().getResource("/styles.css").toExternalForm());
        registerBuzzerKeys(scene);
        showSetup();

        stage.setTitle("Sort Feud — Team Sorting Quiz");
        stage.setMinWidth(1040);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();
    }

    private Node createHeader() {
        teamOneCard = createTeamCard(1);
        teamTwoCard = createTeamCard(2);

        Label brand = new Label("SORT FEUD");
        brand.getStyleClass().add("brand-mark");
        matchStatus = new Label("BEST OF 9  ·  FIRST TO 5");
        matchStatus.getStyleClass().add("match-status");

        HBox dots = new HBox(8);
        dots.setAlignment(Pos.CENTER);
        dots.getStyleClass().add("score-dots");
        for (int index = 0; index < 9; index++) {
            Label dot = new Label();
            dot.getStyleClass().add("score-dot");
            scoreDots.add(dot);
            dots.getChildren().add(dot);
        }

        timerLabel = new Label("20");
        timerLabel.getStyleClass().add("timer-label");
        Label timerCaption = new Label("SECONDS");
        timerCaption.getStyleClass().add("micro-label");
        VBox clock = new VBox(0, timerLabel, timerCaption);
        clock.setAlignment(Pos.CENTER);

        VBox center = new VBox(5, brand, matchStatus, dots, clock);
        center.setAlignment(Pos.CENTER);
        center.getStyleClass().add("show-center");
        HBox.setHgrow(center, Priority.ALWAYS);

        HBox header = new HBox(24, teamOneCard, center, teamTwoCard);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("game-header");
        return header;
    }

    private Node createSetupHeader() {
        Label brand = new Label("SORT FEUD");
        brand.getStyleClass().add("brand-mark");
        HBox header = new HBox(brand);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().addAll("setup-header", "game-header");
        return header;
    }

    private VBox createTeamCard(int team) {
        Label name = new Label("TEAM " + team);
        name.getStyleClass().add("team-name");
        Label key = new Label(team == 1 ? "A" : "L");
        key.getStyleClass().add("header-key");
        HBox identity = new HBox(9, name, key);
        identity.setAlignment(Pos.CENTER_LEFT);

        Label wins = new Label("0");
        wins.getStyleClass().add("wins-label");
        Label winsCaption = new Label("ROUNDS WON");
        winsCaption.getStyleClass().add("micro-label");
        Label competitor = new Label("Roster not set");
        competitor.getStyleClass().add("competitor-label");
        Label strikes = new Label("—  —  —");
        strikes.getStyleClass().add("strikes-label");

        VBox card = new VBox(4, identity, wins, winsCaption, competitor, strikes);
        card.getStyleClass().addAll(
            "team-card", team == 1 ? "team-one-card" : "team-two-card");
        if (team == 1) {
            teamOneName = name;
            teamOneWins = wins;
            teamOneCompetitor = competitor;
            teamOneStrikes = strikes;
        } else {
            teamTwoName = name;
            teamTwoWins = wins;
            teamTwoCompetitor = competitor;
            teamTwoStrikes = strikes;
        }
        return card;
    }

    private void createSetupPage() {
        Label sectionNumber = new Label("01 / TEAM SETUP");
        sectionNumber.getStyleClass().add("eyebrow");
        Label title = new Label("Build the matchup");
        title.getStyleClass().add("setup-title");
        Label intro = new Label(
            "Name both teams and list every player. The draw rotates through "
            + "the full roster before anyone can be selected again.");
        intro.getStyleClass().add("setup-intro");
        intro.setWrapText(true);
        intro.setMaxWidth(720);

        VBox panelOne = createTeamSetupPanel(1);
        VBox panelTwo = createTeamSetupPanel(2);
        HBox teams = new HBox(20, panelOne, panelTwo);
        HBox.setHgrow(panelOne, Priority.ALWAYS);
        HBox.setHgrow(panelTwo, Priority.ALWAYS);

        setupError = new Label();
        setupError.getStyleClass().add("setup-error");
        setupError.setWrapText(true);
        setShown(setupError, false);

        Button start = new Button("START THE MATCH");
        start.getStyleClass().addAll("primary-button", "start-button");
        start.setOnAction(event -> startConfiguredMatch());

        VBox content = new VBox(
            12, sectionNumber, title, intro, teams, setupError, start);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(42, 54, 56, 54));
        content.getStyleClass().add("setup-page");
        setupViewport = createGameViewport(content);
    }

    private VBox createTeamSetupPanel(int team) {
        Label number = new Label(team == 1 ? "TEAM 01" : "TEAM 02");
        number.getStyleClass().add("panel-number");
        TextField teamNameField = new TextField();
        teamNameField.setPromptText("Team name");
        teamNameField.getStyleClass().add("setup-field");

        Label rosterLabel = new Label("How many teammates?");
        rosterLabel.getStyleClass().add("count-question");
        ComboBox<Integer> size = new ComboBox<>();
        size.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8);
        size.setValue(2);
        size.getStyleClass().add("roster-count");
        HBox countRow = new HBox(12, rosterLabel, size);
        countRow.setAlignment(Pos.CENTER_LEFT);
        VBox memberBox = new VBox(9);

        VBox panel = new VBox(12, number, teamNameField, countRow, memberBox);
        panel.getStyleClass().addAll(
            "team-setup-panel", team == 1 ? "setup-team-one" : "setup-team-two");
        panel.setMaxWidth(Double.MAX_VALUE);

        if (team == 1) {
            teamOneNameField = teamNameField;
            teamOneMembersBox = memberBox;
            size.valueProperty().addListener((observable, oldValue, newValue) ->
                rebuildMemberFields(1, newValue));
            rebuildMemberFields(1, size.getValue());
        } else {
            teamTwoNameField = teamNameField;
            teamTwoMembersBox = memberBox;
            size.valueProperty().addListener((observable, oldValue, newValue) ->
                rebuildMemberFields(2, newValue));
            rebuildMemberFields(2, size.getValue());
        }
        return panel;
    }

    private void rebuildMemberFields(int team, int count) {
        List<TextField> fields = team == 1
            ? teamOneMemberFields : teamTwoMemberFields;
        VBox box = team == 1 ? teamOneMembersBox : teamTwoMembersBox;
        List<String> savedNames = fields.stream().map(TextField::getText).toList();
        fields.clear();
        box.getChildren().clear();
        for (int index = 0; index < count; index++) {
            TextField field = new TextField(
                index < savedNames.size() ? savedNames.get(index) : "");
            field.setPromptText("Player " + (index + 1));
            field.getStyleClass().add("setup-field");
            fields.add(field);
            box.getChildren().add(field);
        }
    }

    private void startConfiguredMatch() {
        try {
            TeamRoster first = new TeamRoster(
                teamOneNameField.getText(), memberNames(teamOneMemberFields));
            TeamRoster second = new TeamRoster(
                teamTwoNameField.getText(), memberNames(teamTwoMemberFields));
            engine.configureMatch(first, second, new Random());
            setShown(setupError, false);
            showRoundIntro();
        } catch (IllegalArgumentException exception) {
            setupError.setText(exception.getMessage());
            setShown(setupError, true);
        }
    }

    private List<String> memberNames(List<TextField> fields) {
        return fields.stream().map(TextField::getText).toList();
    }

    private void createGamePage() {
        versusLabel = new Label();
        versusLabel.getStyleClass().add("versus-label");
        questionLabel = new Label();
        questionLabel.getStyleClass().add("question-label");

        currentCaption = new Label("ORIGINAL LIST:");
        currentCaption.getStyleClass().addAll("section-label", "array-stage-label");
        numberBoxes = new HBox(12);
        numberBoxes.setAlignment(Pos.CENTER);
        HBox arrayRow = new HBox(18, currentCaption, numberBoxes);
        arrayRow.setAlignment(Pos.CENTER);
        arrayRow.getStyleClass().add("array-row");
        Separator separator = new Separator();
        separator.getStyleClass().add("challenge-separator");

        Label answerCaption = new Label("ANSWER");
        answerCaption.getStyleClass().add("section-label");
        answerBoxes = new HBox(12);
        answerBoxes.setAlignment(Pos.CENTER);
        answerArea = new VBox(10, answerCaption, answerBoxes);
        answerArea.setAlignment(Pos.CENTER);
        setShown(answerArea, false);

        submitButton = new Button("SUBMIT");
        submitButton.getStyleClass().add("primary-button");
        submitButton.setOnAction(event -> submitAnswer());
        setShown(submitButton, false);

        continueButton = new Button("NEXT QUESTION");
        continueButton.getStyleClass().add("primary-button");
        continueButton.setOnAction(event -> finishFeedback());
        setShown(continueButton, false);

        resultLabel = new Label("Buzz when ready.");
        resultLabel.getStyleClass().add("result-label");
        resultLabel.setWrapText(true);

        VBox challenge = new VBox(
            12, versusLabel, questionLabel, arrayRow,
            separator, answerArea, submitButton,
            continueButton, resultLabel);
        challenge.setAlignment(Pos.CENTER);
        challenge.getStyleClass().add("challenge-board");
        challenge.setMaxWidth(940);

        teamOneBuzzer = createBuzzer(1);
        teamTwoBuzzer = createBuzzer(2);
        HBox buzzers = new HBox(24, teamOneBuzzer, teamTwoBuzzer);
        buzzers.setAlignment(Pos.CENTER);

        VBox gamePage = new VBox(18, challenge, buzzers);
        gamePage.setAlignment(Pos.CENTER);
        gamePage.setPadding(new Insets(24, 38, 30, 38));
        gamePage.getStyleClass().add("game-page");
        gameViewport = createGameViewport(gamePage);
    }

    private Button createBuzzer(int team) {
        Button button = new Button();
        button.getStyleClass().addAll(
            "buzzer", team == 1 ? "team-one" : "team-two");
        button.setFocusTraversable(false);
        button.setOnAction(event -> buzz(team));
        return button;
    }

    private void registerBuzzerKeys(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            boolean typing = scene.getFocusOwner() instanceof TextInputControl;
            int team = buzzerInput.press(event.getCode(), typing);
            if (team != 0) {
                buzz(team);
                event.consume();
            }
        });
        scene.addEventFilter(KeyEvent.KEY_RELEASED, event ->
            buzzerInput.release(event.getCode()));
    }

    private void buzz(int team) {
        if (showingFeedback || !engine.buzz(team)) {
            return;
        }
        String member = team == 1
            ? engine.getCurrentMatchup().team1Member()
            : engine.getCurrentMatchup().team2Member();
        long reactionMillis = BuzzTiming.elapsedMillis(
            questionRevealedAtNanos, System.nanoTime());
        System.out.println("BUZZ | " + LocalTime.now().format(timestampFormat)
            + " | " + engine.getTeam(team).name() + " / " + member
            + " | " + reactionMillis + " ms after reveal");

        Button selected = team == 1 ? teamOneBuzzer : teamTwoBuzzer;
        selected.getStyleClass().add("buzzer-activated");
        teamOneBuzzer.setDisable(true);
        teamTwoBuzzer.setDisable(true);
        setShown(answerArea, true);
        setShown(submitButton, true);
        resultLabel.setText(member + " answering.");
        resultLabel.getStyleClass().setAll("result-label");
        updateScoreboard();
        timer.playFromStart();
        if (!answerFields.isEmpty()) {
            answerFields.get(0).requestFocus();
        }
    }

    private void startQuestion() {
        try {
            engine.startQuestion();
            showOpenQuestion();
        } catch (Exception exception) {
            showSetupError("The next question could not be generated.");
            exception.printStackTrace();
        }
    }

    private void showOpenQuestion() {
        displayedMatchup = engine.getCurrentMatchup();
        displayNumbers();
        resetAnswerUI();
        updateQuestionMetadata();
        root.setCenter(gameViewport);
        root.setTop(gameHeader);
        root.setBottom(devTools);
        updateScoreboard();
        questionRevealedAtNanos = System.nanoTime();
    }

    private void updateQuestionMetadata() {
        questionLabel.setText(QuestionPrompt.forStep(
            engine.getCurrentStepNumber(), engine.getSortName()));
        currentCaption.setText(engine.getArrayStageLabel());
        Matchup matchup = engine.getCurrentMatchup();
        versusLabel.setText(matchup.team1Member() + "  vs  "
            + matchup.team2Member());
    }

    private void displayNumbers() {
        numberBoxes.getChildren().clear();
        answerBoxes.getChildren().clear();
        answerFields.clear();
        for (int number : engine.getNumbers()) {
            Label label = new Label(String.valueOf(number));
            label.getStyleClass().add("number-box");
            numberBoxes.getChildren().add(label);
            TextField field = new TextField();
            field.setPromptText("—");
            field.setAlignment(Pos.CENTER);
            field.getStyleClass().add("answer-box");
            answerFields.add(field);
            answerBoxes.getChildren().add(field);
        }
        for (int index = 0; index < answerFields.size(); index++) {
            setupAutoAdvance(answerFields.get(index), index);
        }
    }

    private void setupAutoAdvance(TextField field, int index) {
        field.setOnAction(event -> {
            if (index < answerFields.size() - 1) {
                answerFields.get(index + 1).requestFocus();
            } else {
                submitAnswer();
            }
        });
        field.setTextFormatter(new TextFormatter<String>(change ->
            AnswerInput.isPotentialInteger(change.getControlNewText())
                ? change : null));
        field.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE
                    && field.getText().isEmpty() && index > 0) {
                answerFields.get(index - 1).requestFocus();
                event.consume();
            }
        });
    }

    private void submitAnswer() {
        if (!engine.isPlaying() || !engine.isBuzzed() || showingFeedback) {
            return;
        }
        int[] answer = new int[answerFields.size()];
        try {
            for (int index = 0; index < answer.length; index++) {
                answer[index] = Integer.parseInt(
                    answerFields.get(index).getText().trim());
            }
        } catch (NumberFormatException exception) {
            resultLabel.setText("Fill every box.");
            resultLabel.getStyleClass().setAll("result-label", "result-error");
            return;
        }
        showFeedback(engine.submitAnswer(answer));
    }

    private void showFeedback(GameEngine.Result result) {
        showingFeedback = true;
        holdingFeedbackMatchup = true;
        feedbackTimer.stop();
        timer.stop();
        submitButton.setDisable(true);
        displayNumbers();
        setShown(answerArea, false);
        setShown(submitButton, false);
        setShown(continueButton, false);

        boolean correct = result != GameEngine.Result.WRONG
            && result != GameEngine.Result.MATCH_FORFEITED;
        boolean manualContinue = FeedbackFlow.requiresManualContinue(result);
        currentCaption.setText(FeedbackFlow.answerCaption(result));
        String answeringName = engine.getTeam(engine.getLastAnswerTeam()).name();
        boolean roundComplete = engine.isRoundComplete();
        feedbackBanner.setText(roundComplete
            ? engine.getTeam(engine.getWinner()).name() + " WINS THE ROUND"
            : correct ? answeringName + " CLAIMS A DOT"
            : "CORRECT ANSWER");
        feedbackBanner.getStyleClass().setAll(
            "feedback-banner", correct ? "feedback-correct" : "feedback-wrong");
        feedbackBanner.setVisible(true);
        resultLabel.setText(roundComplete
            ? "ROUND " + engine.getRoundNumber() + " COMPLETE"
            : correct ? "CORRECT" : "STRIKE");
        resultLabel.getStyleClass().setAll(
            "result-label", correct ? "result-success" : "result-error");
        updateScoreboard();

        if (manualContinue) {
            continueButton.setText(FeedbackFlow.continueLabel(roundComplete));
            setShown(continueButton, true);
        } else {
            feedbackTimer.setDuration(Duration.seconds(1.2));
            feedbackTimer.setOnFinished(event -> finishFeedback());
            feedbackTimer.playFromStart();
        }
        animateFeedbackBanner();
    }

    private void finishFeedback() {
        feedbackBanner.setVisible(false);
        setShown(continueButton, false);
        showingFeedback = false;
        holdingFeedbackMatchup = false;
        if (engine.isRoundComplete()) {
            if (engine.isSeriesOver()) {
                showSeriesEnd();
            } else {
                engine.advanceRound();
                showRoundIntro();
            }
        } else if (engine.isPlaying()) {
            showOpenQuestion();
        } else {
            startQuestion();
        }
    }

    private void showSeriesEnd() {
        timer.stop();
        int winner = engine.getSeriesWinner();
        Label eyebrow = new Label("BEST OF FIVE SERIES COMPLETE");
        eyebrow.getStyleClass().add("eyebrow");
        Label title = new Label(engine.getTeam(winner).name() + " wins the series");
        title.getStyleClass().add("match-end-title");
        Label summary = new Label(engine.getRoundWins(1) + " — "
            + engine.getRoundWins(2) + " after " + engine.getRoundNumber()
            + " round" + (engine.getRoundNumber() == 1 ? "" : "s") + ".");
        summary.getStyleClass().add("match-end-summary");

        Button rematch = new Button("REMATCH");
        rematch.getStyleClass().add("primary-button");
        rematch.setOnAction(event -> {
            engine.configureMatch(engine.getTeam(1), engine.getTeam(2), new Random());
            showRoundIntro();
        });
        Button editTeams = new Button("EDIT TEAMS");
        editTeams.getStyleClass().add("secondary-button");
        editTeams.setOnAction(event -> showSetup());
        HBox actions = new HBox(12, rematch, editTeams);
        actions.setAlignment(Pos.CENTER);

        VBox panel = new VBox(14, eyebrow, title, summary, actions);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("match-end-panel");
        VBox page = new VBox(panel);
        page.setAlignment(Pos.CENTER);
        page.setPadding(new Insets(50));
        root.setCenter(page);
        root.setTop(gameHeader);
        root.setBottom(null);
        matchStatus.setText("FINAL  ·  " + engine.getRoundWins(1)
            + "–" + engine.getRoundWins(2));
        updateScoreboard();

        panel.setOpacity(0);
        panel.setScaleX(0.94);
        panel.setScaleY(0.94);
        FadeTransition fade = new FadeTransition(Duration.millis(420), panel);
        fade.setToValue(1);
        ScaleTransition scale = new ScaleTransition(Duration.millis(520), panel);
        scale.setToX(1);
        scale.setToY(1);
        new ParallelTransition(fade, scale).play();
    }

    private void showRoundIntro() {
        showingFeedback = true;
        timer.stop();
        root.setTop(gameHeader);
        root.setBottom(null);

        Label eyebrow = new Label("ROUND " + engine.getRoundNumber() + " OF 5");
        eyebrow.getStyleClass().add("eyebrow");
        Label title = new Label(engine.getRoundPolicyName());
        title.getStyleClass().add("round-intro-title");
        Label score = new Label(engine.getTeam(1).name() + "  "
            + engine.getRoundWins(1) + " — " + engine.getRoundWins(2) + "  "
            + engine.getTeam(2).name());
        score.getStyleClass().add("round-intro-score");
        Label rule = new Label("FIRST TO 5 DOTS  ·  3 STRIKES FORFEITS THE ROUND");
        rule.getStyleClass().add("match-status");

        VBox panel = new VBox(14, eyebrow, title, score, rule);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("round-intro");
        VBox page = new VBox(panel);
        page.setAlignment(Pos.CENTER);
        page.setPadding(new Insets(50));
        root.setCenter(page);
        updateScoreboard();

        panel.setOpacity(0);
        panel.setTranslateY(24);
        panel.setScaleX(0.97);
        panel.setScaleY(0.97);
        FadeTransition fade = new FadeTransition(Duration.millis(360), panel);
        fade.setToValue(1);
        TranslateTransition rise = new TranslateTransition(Duration.millis(480), panel);
        rise.setToY(0);
        ScaleTransition scale = new ScaleTransition(Duration.millis(480), panel);
        scale.setToX(1);
        scale.setToY(1);
        ParallelTransition entrance = new ParallelTransition(fade, rise, scale);
        PauseTransition hold = new PauseTransition(Duration.seconds(1.35));
        FadeTransition exit = new FadeTransition(Duration.millis(240), panel);
        exit.setToValue(0);
        SequentialTransition sequence = new SequentialTransition(entrance, hold, exit);
        sequence.setOnFinished(event -> {
            showingFeedback = false;
            startQuestion();
        });
        sequence.play();
    }

    private void animateFeedbackBanner() {
        feedbackBanner.setOpacity(0);
        feedbackBanner.setTranslateY(-18);
        FadeTransition fade = new FadeTransition(Duration.millis(180), feedbackBanner);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(
            Duration.millis(260), feedbackBanner);
        slide.setToY(0);
        new ParallelTransition(fade, slide).play();
    }

    private void resetAnswerUI() {
        teamOneBuzzer.getStyleClass().remove("buzzer-activated");
        teamTwoBuzzer.getStyleClass().remove("buzzer-activated");
        teamOneBuzzer.setDisable(false);
        teamTwoBuzzer.setDisable(false);
        setShown(answerArea, false);
        setShown(submitButton, false);
        submitButton.setDisable(false);
        resultLabel.setText("Buzz when ready.");
        resultLabel.getStyleClass().setAll("result-label");
    }

    private HBox createDevTools() {
        Label label = new Label("DEMO ARRAYS");
        label.getStyleClass().add("devtools-label");
        Button sorted = createDemoButton("SORTED", GameEngine.SORTED_DEMO);
        Button random = createDemoButton("RANDOM", GameEngine.UNSORTED_DEMO);
        Button reverse = createDemoButton("REVERSE", GameEngine.WORST_CASE_DEMO);
        HBox tools = new HBox(10, label, sorted, random, reverse);
        tools.setAlignment(Pos.CENTER_RIGHT);
        tools.getStyleClass().add("devtools-bar");
        return tools;
    }

    private Button createDemoButton(String text, int[] values) {
        Button button = new Button(text);
        button.getStyleClass().add("devtools-button");
        button.setOnAction(event -> loadDemo(values));
        return button;
    }

    private void loadDemo(int[] array) {
        if (!engine.isPlaying() || showingFeedback) {
            return;
        }
        if (!engine.loadDemoQuestion(array)) {
            resultLabel.setText("This array is already sorted; there is no changed step to answer.");
            resultLabel.getStyleClass().setAll("result-label");
            return;
        }
        showOpenQuestion();
        timer.stop();
        System.out.println("DEVTOOLS | demo question loaded");
    }

    private void showSetup() {
        if (timer != null) {
            timer.stop();
        }
        root.setCenter(setupViewport);
        root.setTop(setupHeader);
        root.setBottom(null);
        matchStatus.setText("BEST OF 9  ·  FIRST TO 5");
        updateScoreboard();
    }

    private void showSetupError(String message) {
        showSetup();
        setupError.setText(message);
        setShown(setupError, true);
    }

    private void updateScoreboard() {
        teamOneName.setText(engine.getTeam(1).name().toUpperCase());
        teamTwoName.setText(engine.getTeam(2).name().toUpperCase());
        teamOneWins.setText(String.valueOf(engine.getRoundWins(1)));
        teamTwoWins.setText(String.valueOf(engine.getRoundWins(2)));
        teamOneStrikes.setText(strikeText(engine.getTeamStrikes(1)));
        teamTwoStrikes.setText(strikeText(engine.getTeamStrikes(2)));
        timerLabel.setText(String.format("%02d", engine.getSecondsLeft()));
        updateTimerWarning();

        Matchup matchup = holdingFeedbackMatchup && displayedMatchup != null
            ? displayedMatchup : engine.getCurrentMatchup();
        teamOneCompetitor.setText(matchup == null
            ? "Roster not set" : matchup.team1Member());
        teamTwoCompetitor.setText(matchup == null
            ? "Roster not set" : matchup.team2Member());
        if (teamOneBuzzer != null) {
            teamOneBuzzer.setText(matchup == null
                ? "TEAM 1  ·  A" : matchup.team1Member() + "  ·  A");
            teamTwoBuzzer.setText(matchup == null
                ? "TEAM 2  ·  L" : matchup.team2Member() + "  ·  L");
        }
        updateDots();
        toggleClass(teamOneCard, "team-active", engine.getCurrentPlayer() == 1);
        toggleClass(teamTwoCard, "team-active", engine.getCurrentPlayer() == 2);
        if (!engine.isSeriesOver()) {
            matchStatus.setText("ROUND " + engine.getRoundNumber()
                + " OF 5  ·  SERIES " + engine.getRoundWins(1)
                + "–" + engine.getRoundWins(2));
        }
    }

    private void updateDots() {
        List<Integer> owners = ScoreDotLayout.owners(
            scoreDots.size(), engine.getTeamWins(1), engine.getTeamWins(2));
        for (int index = 0; index < scoreDots.size(); index++) {
            Label dot = scoreDots.get(index);
            dot.getStyleClass().setAll("score-dot");
            int owner = owners.get(index);
            if (owner != 0) {
                dot.getStyleClass().add(
                    owner == 1 ? "dot-team-one" : "dot-team-two");
                int previousOwner = index < renderedDotOwners.size()
                    ? renderedDotOwners.get(index) : 0;
                if (owner != previousOwner) {
                    dot.setScaleX(0.55);
                    dot.setScaleY(0.55);
                    ScaleTransition claim = new ScaleTransition(
                        Duration.millis(280), dot);
                    claim.setToX(1);
                    claim.setToY(1);
                    claim.play();
                }
            }
        }
        renderedDotOwners = owners;
    }

    private void updateTimerWarning() {
        boolean critical = TimerUrgency.isCritical(
            engine.getSecondsLeft(), engine.isPlaying(), engine.isBuzzed());
        toggleClass(timerLabel, "timer-critical", critical);
        if (critical && !timerPulsing) {
            timerPulsing = true;
            timerPulse.playFromStart();
        } else if (!critical && timerPulsing) {
            timerPulsing = false;
            timerPulse.stop();
            timerLabel.setScaleX(1);
            timerLabel.setScaleY(1);
        }
    }

    private String strikeText(int strikes) {
        return (strikes > 0 ? "✕" : "—") + "  "
            + (strikes > 1 ? "✕" : "—") + "  "
            + (strikes > 2 ? "✕" : "—");
    }

    private void toggleClass(Node node, String styleClass, boolean enabled) {
        node.getStyleClass().remove(styleClass);
        if (enabled) {
            node.getStyleClass().add(styleClass);
        }
    }

    private void setShown(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    static ScrollPane createGameViewport(Node content) {
        ScrollPane viewport = new ScrollPane(content);
        viewport.setFitToWidth(true);
        viewport.setFitToHeight(false);
        viewport.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        viewport.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        viewport.setPannable(true);
        viewport.getStyleClass().add("game-viewport");
        return viewport;
    }
}
