package com.sentomero.sufeeds.javasufeeds.Controllers;

import com.sentomero.sufeeds.javasufeeds.Utilities.Db_connection;
import com.sentomero.sufeeds.javasufeeds.Models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileContoller {
    @FXML private Label nameLabel;
    @FXML private Label studentNumberLabel;
    @FXML private Label courseLabel;
    @FXML private Label yearLabel;

    private User currentUser;

    public void initData(int userId) {
        loadUserData(userId);
    }

    private void loadUserData(int userId) {
        String query = "SELECT u.*, c.course_name FROM Users u JOIN Courses c ON u.course_id = c.course_id WHERE u.user_id = ?";

        try (Connection conn = Db_connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                currentUser = new User(
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("student_number"),
                        rs.getString("course_name"),
                        rs.getString("year_module")
                );
                updateUI();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateUI() {
        nameLabel.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        studentNumberLabel.setText(currentUser.getStudentNumber());
        courseLabel.setText(currentUser.getCourse());
        yearLabel.setText(currentUser.getYearModule());
    }
}