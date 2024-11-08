package com.sentomero.sufeeds.javasufeeds.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.application.Platform;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.control.Label;

public class HomeFeed {
    @FXML
    private BorderPane mainContainer;

    @FXML
    private VBox leftSidebar;

    @FXML
    private VBox feedContainer;

    @FXML
    private VBox postsContainer;

    @FXML
    private VBox rightSidebar;

    @FXML
    private TextArea postInput;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            String cssPath = "/com/sentomero/sufeeds/javasufeeds/styles.css";
            if (mainContainer.getScene() != null) {
                mainContainer.getScene().getStylesheets().add(
                        getClass().getResource(cssPath).toExternalForm()
                );
            }
        });
    }

    @FXML
    private void handlePostButton() {
        String content = postInput.getText().trim();
        if (!content.isEmpty()) {
            addPost(content);
            postInput.clear();
        }
    }

    private void addPost(String content) {
        VBox post = createPostNode(content);
        postsContainer.getChildren().add(0, post); // Add at the top
    }

    private VBox createPostNode(String content) {
        VBox post = new VBox(10);
        post.getStyleClass().add("post");

        // User info section
        HBox userInfo = new HBox(10);
        userInfo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        FontIcon userIcon = new FontIcon("fas-user-circle");
        userIcon.setIconSize(32);
        userIcon.setIconColor(javafx.scene.paint.Color.web("#3E7BFA"));

        VBox userDetails = new VBox(2);
        Label userName = new Label("User Name");
        Label timestamp = new Label("Just now");
        userName.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold;");
        timestamp.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        userDetails.getChildren().addAll(userName, timestamp);
        userInfo.getChildren().addAll(userIcon, userDetails);

        // Post content
        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-text-fill: #FFFFFF;");

        // Action buttons
        HBox actions = new HBox(15);
        actions.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Button likeBtn = createActionButton("fas-heart", "Like");
        Button commentBtn = createActionButton("fas-comment", "Comment");
        Button shareBtn = createActionButton("fas-share", "Share");

        actions.getChildren().addAll(likeBtn, commentBtn, shareBtn);

        post.getChildren().addAll(userInfo, contentLabel, actions);
        return post;
    }

    private Button createActionButton(String iconLiteral, String text) {
        Button button = new Button();
        button.getStyleClass().add("nav-button");

        HBox content = new HBox(5);
        content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconLiteral);
        icon.getStyleClass().add("nav-icon");

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #CCCCCC;");

        content.getChildren().addAll(icon, label);
        button.setGraphic(content);

        return button;
    }

    @FXML
    private void handleHomeButton() {
        // Implement home navigation logic
    }

    @FXML
    private void handleProfileButton() {
        // Implement profile navigation logic
    }

    @FXML
    private void handleSettingsButton() {
        // Implement settings navigation logic
    }
}