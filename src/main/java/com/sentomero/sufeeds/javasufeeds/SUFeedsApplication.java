package com.sentomero.sufeeds.javasufeeds;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SUFeedsApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the login view as the initial view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sentomero/sufeeds/javasufeeds/login.fxml"));
            Parent root = loader.load();

            // Create the scene
            Scene scene = new Scene(root);

            // Load CSS file
            URL cssUrl = getClass().getResource("/com/sentomero/sufeeds/javasufeeds/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Warning: Could not load CSS file");
            }

            // Set up the primary stage
            primaryStage.setTitle("SU Feeds");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);  // Set minimum window width
            primaryStage.setMinHeight(600); // Set minimum window height
            primaryStage.show();

        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            System.err.println("Error loading application: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        // Cleanup code when application closes
        // You might want to close any open database connections here
        System.out.println("Application is closing...");
    }
}