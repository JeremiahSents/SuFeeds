package com.sentomero.sufeeds.javasufeeds.Controllers;

import com.sentomero.sufeeds.javasufeeds.Utilities.Db_connection;
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
    @FXML private TextField studentNumberField; // Changed from usernameField
    @FXML private PasswordField passwordField;  // Fixed typo in field name
    @FXML private Button loginButton;
    @FXML private Label registerLink;

    @FXML
    private void initialize() {
        // Set up event handlers
        loginButton.setOnAction(_ -> handleLogin());
        registerLink.setOnMouseClicked(_ -> handleRegisterLink());

        // Set prompt text for student number field
        studentNumberField.setPromptText("Enter Student Number");
    }

    private void handleLogin() {
        String studentNumber = studentNumberField.getText().trim();
        String password = passwordField.getText().trim();

        if (studentNumber.isEmpty() || password.isEmpty()) {
            showAlert("Please enter both student number and password.");
            return;
        }

        try (Connection conn = Db_connection.getConnection()) {
            String query = "SELECT user_id FROM tbl_users WHERE student_number = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, studentNumber);
                pstmt.setString(2, password); // Note: In production, use password hashing!

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    // Login successful
                    loadHomeFeed(rs.getInt("user_id"));
                } else {
                    showAlert("Invalid student number or password.");
                }
            }
        } catch (SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            showAlert("Utilities error: " + e.getMessage());
        }
    }

    private void handleRegisterLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sentomero/sufeeds/views/register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registerLink.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            showAlert("Could not load registration page.");
        }
    }

    private void loadHomeFeed(int userId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sentomero/sufeeds/views/home.fxml"));
            Parent root = loader.load();

            HomeController homeControllerController = loader.getController();
            homeControllerController.initData(userId);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            showAlert("Could not load home page.");
        }
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}