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
import java.sql.SQLException;

public class RegisterController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField studentNumberField;
    @FXML private ComboBox<String> courseSelect;
    @FXML private ComboBox<String> yearSelect;
    @FXML private PasswordField passwordSelect;
    @FXML private PasswordField confirmpasswordSelect;
    @FXML private Button registerButton;

    @FXML
    private void initialize() {
        // Initialize ComboBox items
        courseSelect.getItems().addAll(
                "Computer Science",
                "Information Technology",
                "Software Engineering",
                "Cybersecurity"
        );

        yearSelect.getItems().addAll(
                "Year 1",
                "Year 2",
                "Year 3",
                "Year 4"
        );

        registerButton.setOnAction(event -> handleRegistration());
    }

    private void handleRegistration() {
        // Validate input fields
        if (!validateFields()) {
            return;
        }

        // Get values from fields
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String studentNumber = studentNumberField.getText().trim();
        String course = courseSelect.getValue();
        String year = yearSelect.getValue();
        String password = passwordSelect.getText();

        try (Connection conn = Db_connection.getConnection()) {
            String query = "INSERT INTO users (first_name, last_name, student_number, course, year, password) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, studentNumber);
                pstmt.setString(4, course);
                pstmt.setString(5, year);
                pstmt.setString(6, password); // Note: In production, use password hashing!

                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful!");
                    loadLoginPage();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Registration failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        if (firstNameField.getText().trim().isEmpty() ||
                lastNameField.getText().trim().isEmpty() ||
                studentNumberField.getText().trim().isEmpty() ||
                courseSelect.getValue() == null ||
                yearSelect.getValue() == null ||
                passwordSelect.getText().isEmpty() ||
                confirmpasswordSelect.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return false;
        }

        if (!passwordSelect.getText().equals(confirmpasswordSelect.getText())) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
            return false;
        }

        return true;
    }

    private void loadLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sentomero/sufeeds/javasufeeds/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load login page.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}