package com.sentomero.sufeeds.javasufeeds.Controllers;

import com.sentomero.sufeeds.javasufeeds.Utilities.Db_connection;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersViewController {
    @FXML
    private VBox usersContainer;

    @FXML
    private void initialize() {
        loadUsers();
    }

    private void loadUsers() {
        String query = """
            SELECT u.first_name, u.last_name, c.course_name 
            FROM Users u 
            JOIN Courses c ON u.course_id = c.course_id 
            ORDER BY u.first_name, u.last_name
            """;

        try (Connection conn = Db_connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                HBox userBox = createUserBox(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("course_name")
                );
                usersContainer.getChildren().add(userBox);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // You might want to show an alert here
        }
    }

    private HBox createUserBox(String firstName, String lastName, String course) {
        HBox userBox = new HBox(10);
        userBox.getStyleClass().add("user-box");
        userBox.setPadding(new Insets(10));

        // User icon
        FontIcon userIcon = new FontIcon("fas-user-circle");
        userIcon.setIconSize(32);
        userIcon.setIconColor(Color.web("#3E7BFA"));

        // User name and course
        VBox userInfo = new VBox(5);
        Label nameLabel = new Label(firstName + " " + lastName);
        Label courseLabel = new Label(course);

        nameLabel.getStyleClass().add("user-name");
        courseLabel.getStyleClass().add("user-course");

        userInfo.getChildren().addAll(nameLabel, courseLabel);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        userBox.getChildren().addAll(userIcon, userInfo, spacer);
        return userBox;
    }
}