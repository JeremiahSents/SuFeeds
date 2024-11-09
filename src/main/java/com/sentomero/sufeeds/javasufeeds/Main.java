package com.sentomero.sufeeds.javasufeeds;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/sentomero/sufeeds/javasufeeds/homeFeed.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);

        // Add the stylesheet directly
        URL cssUrl = Main.class.getResource("/com/sentomero/sufeeds/javasufeeds/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.out.println("Error: styles.css not found at the specified path.");
        }

        // Set up the stage
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
