package com.filipal.skyforecast;

// Import statements for JavaFX classes
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for SkyForecast.
 * This class extends the JavaFX Application class and serves as the entry point for the JavaFX application.
 */
public class MainApp extends Application {

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned, and after the system is ready for the application to begin running.
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set. Applications may create other stages, if needed, but they will not be primary stages.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file for the main view using FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/filipal/skyforecast/MainView.fxml"));
            // Load the FXML file into a Parent object
            Parent root = loader.load();

            // Create a new Scene with the loaded Parent (root)
            Scene scene = new Scene(root);
            // Add an external CSS stylesheet to the scene
            scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

            // Set the scene to the primary stage
            primaryStage.setScene(scene);
            // Set the title of the primary stage
            primaryStage.setTitle("SkyForecast");
            // Display the primary stage
            primaryStage.show();
        } catch (Exception e) {
            // Print the stack trace if an exception occurs
            e.printStackTrace();
        }
    }

    /**
     * The main() method is the entry point of the application.
     * It launches the JavaFX application by calling the static launch method, which internally calls the start method.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
