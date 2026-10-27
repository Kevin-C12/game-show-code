package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


/**
 * Main entry point for the JavaFX application.
 *
 * <p>Extending {@link Application} lets JavaFX manage the app's lifecycle:
 * it sets up the graphics toolkit, then calls {@link #start(Stage)} on the
 * JavaFX Application Thread to build the user interface.</p>
 */
public class App extends Application {

    /**
     * Builds and shows the main window. JavaFX calls this method automatically
     * after {@link #launch(String...)} runs.
     *
     * @param stage the main window JavaFX creates for this app
     */
    @Override
    public void start(Stage stage) {
        // Look up which Java and JavaFX versions the app is running on
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();

        // Create a text label that shows both version numbers
        var label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");

        // A StackPane centers its children, so the label sits in the middle.
        // The Scene holds that layout and sets the window's content size to 640 x 480 pixels.
        var scene = new Scene(new StackPane(label), 640, 480);

        // Put the scene in the window and make the window visible
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Starts the program. launch() starts JavaFX, which then calls start().
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch();
    }

}
