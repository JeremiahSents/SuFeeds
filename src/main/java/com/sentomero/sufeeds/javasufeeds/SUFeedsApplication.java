package com.sentomero.sufeeds.javasufeeds;

import com.sentomero.sufeeds.javasufeeds.Utilities.CSSLoader;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sentomero/sufeeds/views/Login.fxml"));
            Parent root = loader.load();

            // Create the scene
            Scene scene = new Scene(root);

            // Load CSS file
            // In the start method, after creating the scene
            CSSLoader.loadCSS(scene, "styles.css");

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