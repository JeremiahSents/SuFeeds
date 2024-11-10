package com.sentomero.sufeeds.javasufeeds.Controllers;

import com.sentomero.sufeeds.javasufeeds.Database.Db_connection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordFeild;
    @FXML private Button loginButton;
    @FXML private Label registerLink;

    @FXML
    private void initialize() {
        // Set up event handlers
        loginButton.setOnAction(event -> handleLogin());
        registerLink.setOnMouseClicked(event -> handleRegisterLink());
    }

    private void handleLogin() {
        String studentNumber = usernameField.getText().trim();
        String password = passwordFeild.getText().trim();

        if (studentNumber.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        try (Connection conn = Db_connection.getConnection()) {
            String query = "SELECT * FROM users WHERE student_number = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, studentNumber);
                pstmt.setString(2, password); // Note: In production, use password hashing!

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    // Login successful
                    loadHomeFeed(rs.getInt("id"));
                } else {
                    showAlert("Error", "Invalid credentials.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    private void handleRegisterLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sentomero/sufeeds/javasufeeds/register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registerLink.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load register page.");
        }
    }

    private void loadHomeFeed(int userId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sentomero/sufeeds/javasufeeds/homeFeed.fxml"));
            Parent root = loader.load();

            HomeFeed homeFeedController = loader.getController();
            homeFeedController.initData(userId); // You'll need to add this method to HomeFeed

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load home page.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}