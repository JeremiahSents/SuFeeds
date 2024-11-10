package com.sentomero.sufeeds.javasufeeds.Controllers;

import com.sentomero.sufeeds.javasufeeds.Database.Db_connection;
import com.sentomero.sufeeds.javasufeeds.Models.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.application.Platform;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.*;
import java.util.Optional;
import java.io.IOException;

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
    private TextArea postInput;
    @FXML
    private ComboBox<String> classTagSelect;

    private User currentUser;
    private int currentUserId;

    @FXML
    private void initialize() {
        Platform.runLater(this::loadClassTags);
    }

    public void initData(int userId) {
        this.currentUserId = userId;
        loadCurrentUser();
        loadPosts();
    }

    private void loadCurrentUser() {
        String query = "SELECT u.*, c.course_name FROM Users u JOIN Courses c ON u.course_id = c.course_id WHERE u.user_id = ?";

        try (Connection conn = Db_connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, currentUserId);
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load user data: " + e.getMessage());
        }
    }
    @FXML
    private void handleAddCommentButton() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add New Comment");
        dialog.setHeaderText("Create a new standalone comment");

        // Create the comment input and class tag selection
        VBox content = new VBox(10);

        // Class tag selection
        ComboBox<String> classTagSelect = new ComboBox<>();
        classTagSelect.getItems().addAll(this.classTagSelect.getItems()); // Copy items from main class tag selector
        classTagSelect.setValue("General");
        HBox classTagBox = new HBox(10);
        classTagBox.getChildren().addAll(new Label("Class Tag:"), classTagSelect);

        // Comment input
        TextArea commentInput = new TextArea();
        commentInput.setPromptText("Write your comment here...");
        commentInput.setPrefRowCount(5);

        content.getChildren().addAll(classTagBox, commentInput);
        dialog.getDialogPane().setContent(content);

        // Add buttons
        ButtonType submitButton = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButton, ButtonType.CANCEL);

        // Enable/Disable submit button depending on whether there's text in the input field
        Node submitNode = dialog.getDialogPane().lookupButton(submitButton);
        submitNode.setDisable(true);
        commentInput.textProperty().addListener((observable, oldValue, newValue) -> {
            submitNode.setDisable(newValue.trim().isEmpty());
        });

        // Convert the result to comment text when the submit button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButton) {
                return commentInput.getText() + "|" + classTagSelect.getValue(); // Combine comment and class tag
            }
            return null;
        });

        // Handle the result
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(commentData -> {
            String[] parts = commentData.split("\\|");
            String commentText = parts[0];
            String classTag = parts[1];
            if (!commentText.trim().isEmpty()) {
                saveStandaloneComment(commentText.trim(), classTag);
            }
        });
    }

    private void saveStandaloneComment(String content, String classTag) {
        // Using the Posts table since it's already set up for this type of content
        String query = "INSERT INTO Posts (user_id, content, class_tag, created_at, is_standalone_comment) VALUES (?, ?, ?, ?, TRUE)";

        try (Connection conn = Db_connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, currentUserId);
            pstmt.setString(2, content);
            pstmt.setString(3, classTag);
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                loadPosts(); // Refresh the feed to show the new standalone comment
                showAlert(Alert.AlertType.INFORMATION, "Success", "Your comment has been posted!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not save comment: " + e.getMessage());
        }
    }
    @FXML
    private void handleHomeButton() {
        loadPosts();  // Refresh the feed
    }

    // You can remove handleHomeButton() since we're not using it anymore
    private void loadPosts() {
        String query = """
            SELECT p.*, u.first_name, u.last_name, u.student_number, u.year_module,
                   c.course_name,
                   (SELECT COUNT(*) FROM Reactions r WHERE r.post_id = p.post_id AND r.type = 'like') as likes_count,
                   (SELECT COUNT(*) FROM Reactions r WHERE r.post_id = p.post_id AND r.type = 'dislike') as dislikes_count,
                   (SELECT type FROM Reactions r WHERE r.post_id = p.post_id AND r.user_id = ?) as user_reaction,
                   p.class_tag,
                   COALESCE(p.is_standalone_comment, FALSE) as is_standalone_comment
            FROM Posts p
            JOIN Users u ON p.user_id = u.user_id
            JOIN Courses c ON u.course_id = c.course_id
            ORDER BY p.created_at DESC
            """;

        try (Connection conn = Db_connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();

            // Clear existing posts
            postsContainer.getChildren().clear();

            while (rs.next()) {
                // Create User object
                User postUser = new User(
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("student_number"),
                        rs.getString("course_name"),
                        rs.getString("year_module")
                );

                // Create Post object with the isStandaloneComment parameter
                Post post = new Post(
                        rs.getInt("post_id"),
                        postUser,
                        rs.getString("content"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getInt("likes_count"),
                        rs.getInt("dislikes_count"),
                        rs.getString("class_tag"),
                        rs.getBoolean("is_standalone_comment")  // Added this parameter
                );

                // Set user reaction
                String userReaction = rs.getString("user_reaction");
                if (userReaction != null) {
                    post.setUserReaction(userReaction);
                }

                // Load comments for this post
                loadCommentsForPost(post);

                // Create and add the post node to the container
                VBox postNode = createPostNode(post);
                postsContainer.getChildren().add(postNode);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load posts: " + e.getMessage());
        }
    }

    private void loadClassTags() {
        String query = "SELECT class_code FROM Classes";

        try (Connection conn = Db_connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();
            classTagSelect.getItems().clear();
            classTagSelect.getItems().add("General"); // Default option

            while (rs.next()) {
                classTagSelect.getItems().add(rs.getString("class_code"));
            }
            classTagSelect.setValue("General");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load class tags: " + e.getMessage());
        }
    }

    String query = """
            SELECT p.*, u.first_name, u.last_name, u.student_number, u.year_module,
                   c.course_name,
                   (SELECT COUNT(*) FROM Reactions r WHERE r.post_id = p.post_id AND r.type = 'like') as likes_count,
                   (SELECT COUNT(*) FROM Reactions r WHERE r.post_id = p.post_id AND r.type = 'dislike') as dislikes_count,
                   (SELECT type FROM Reactions r WHERE r.post_id = p.post_id AND r.user_id = ?) as user_reaction,
                   p.class_tag
            FROM Posts p
            JOIN Users u ON p.user_id = u.user_id
            JOIN Courses c ON u.course_id = c.course_id
            ORDER BY p.created_at DESC
            """;

    private void loadCommentsForPost(Post post) {
        String query = """
                SELECT c.*, u.first_name, u.last_name, u.student_number, u.year_module, co.course_name
                FROM Comments c
                JOIN Users u ON c.user_id = u.user_id
                JOIN Courses co ON u.course_id = co.course_id
                WHERE c.post_id = ?
                ORDER BY c.created_at
                """;

        try (Connection conn = Db_connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, post.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Comment comment = new Comment(
                        rs.getInt("comment_id"),
                        new User(
                                rs.getInt("user_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("student_number"),
                                rs.getString("course_name"),
                                rs.getString("year_module")
                        ),
                        rs.getString("content"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                post.getComments().add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePostButton() {
        String content = postInput.getText().trim();
        String classTag = classTagSelect.getValue();

        if (content.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter some content for your post.");
            return;
        }

        String query = "INSERT INTO Posts (user_id, content, class_tag, created_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = Db_connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, currentUserId);
            pstmt.setString(2, content);
            pstmt.setString(3, classTag);
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                postInput.clear();
                loadPosts(); // Refresh the feed
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not create post: " + e.getMessage());
        }
    }

    private void handleReaction(Post post, String reactionType) {
        try (Connection conn = Db_connection.getConnection()) {
            // First, check if user already has a reaction
            String checkQuery = "SELECT type FROM Reactions WHERE post_id = ? AND user_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, post.getId());
                checkStmt.setInt(2, currentUserId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // User has an existing reaction
                    String existingType = rs.getString("type");
                    if (existingType.equals(reactionType)) {
                        // Remove the reaction if it's the same type
                        String deleteQuery = "DELETE FROM Reactions WHERE post_id = ? AND user_id = ?";
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                            deleteStmt.setInt(1, post.getId());
                            deleteStmt.setInt(2, currentUserId);
                            deleteStmt.executeUpdate();
                        }
                    } else {
                        // Update to new reaction type
                        String updateQuery = "UPDATE Reactions SET type = ? WHERE post_id = ? AND user_id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setString(1, reactionType);
                            updateStmt.setInt(2, post.getId());
                            updateStmt.setInt(3, currentUserId);
                            updateStmt.executeUpdate();
                        }
                    }
                } else {
                    // Insert new reaction
                    String insertQuery = "INSERT INTO Reactions (post_id, user_id, type) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, post.getId());
                        insertStmt.setInt(2, currentUserId);
                        insertStmt.setString(3, reactionType);
                        insertStmt.executeUpdate();
                    }
                }
            }

            loadPosts(); // Refresh the feed

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not process reaction: " + e.getMessage());
        }
    }

    @FXML
    private void handleProfileButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sentomero/sufeeds/javasufeeds/profile.fxml"));
            Parent root = loader.load();

            ProfileContoller controller = loader.getController();
            controller.initData(currentUserId);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Profile");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load profile view: " + e.getMessage());
        }
    }

    @FXML
    private void handleUsersButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sentomero/sufeeds/javasufeeds/users.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("All Users");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load users view: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogoutButton() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sentomero/sufeeds/javasufeeds/login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) mainContainer.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Could not load login view: " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private VBox createPostNode(Post post) {
        VBox postBox = new VBox(10);
        postBox.getStyleClass().add("post-box");
        postBox.setPadding(new javafx.geometry.Insets(15));
        postBox.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");

        // Header with user info and class tag
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label userInfo = new Label(String.format("%s %s (%s)\n%s - %s",
                post.getUser().getFirstName(),
                post.getUser().getLastName(),
                post.getUser().getStudentNumber(),
                post.getUser().getCourse(),
                post.getUser().getYearModule()));
        userInfo.setStyle("-fx-font-weight: bold;");

        Label classTag = new Label(post.getClassTag());
        classTag.setStyle("-fx-background-color: #e3f2fd; -fx-padding: 5; -fx-background-radius: 3;");

        Label timestamp = new Label(post.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm")));
        timestamp.setStyle("-fx-text-fill: #757575;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(userInfo, spacer, classTag, timestamp);

        // Post content
        Label content = new Label(post.getContent());
        content.setWrapText(true);
        content.setStyle("-fx-font-size: 14px;");

        // Reactions bar
        HBox reactionsBar = new HBox(15);
        reactionsBar.setAlignment(Pos.CENTER_LEFT);

        // Like button
        Button likeButton = new Button();
        FontIcon likeIcon = new FontIcon("fas-thumbs-up");
        likeIcon.setIconColor(post.isLikedByCurrentUser() ? Color.BLUE : Color.BLACK);
        likeButton.setGraphic(likeIcon);
        likeButton.setText(String.valueOf(post.getLikes()));
        likeButton.setOnAction(e -> handleReaction(post, "like"));

        // Dislike button
        Button dislikeButton = new Button();
        FontIcon dislikeIcon = new FontIcon("fas-thumbs-down");
        dislikeIcon.setIconColor(post.isDislikedByCurrentUser() ? Color.RED : Color.BLACK);
        dislikeButton.setGraphic(dislikeIcon);
        dislikeButton.setText(String.valueOf(post.getDislikes()));
        dislikeButton.setOnAction(e -> handleReaction(post, "dislike"));

        // Comment button
        Button commentButton = new Button("Comment");
        FontIcon commentIcon = new FontIcon("fas-comment");
        commentButton.setGraphic(commentIcon);
        commentButton.setOnAction(e -> showCommentDialog(post));

        reactionsBar.getChildren().addAll(likeButton, dislikeButton, commentButton);

        // Comments section
        VBox commentsBox = new VBox(5);
        commentsBox.setStyle("-fx-padding: 10 0 0 20;");

        for (Comment comment : post.getComments()) {
            HBox commentBox = new HBox(10);
            commentBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10; -fx-background-radius: 5;");

            VBox commentContent = new VBox(5);
            Label commentHeader = new Label(String.format("%s %s - %s",
                    comment.getUser().getFirstName(),
                    comment.getUser().getLastName(),
                    comment.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM d, HH:mm"))));
            commentHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

            Label commentText = new Label(comment.getContent());
            commentText.setWrapText(true);

            commentContent.getChildren().addAll(commentHeader, commentText);
            commentBox.getChildren().add(commentContent);
            commentsBox.getChildren().add(commentBox);
        }

        postBox.getChildren().addAll(header, content, reactionsBar, commentsBox);
        return postBox;
    }

    private void showCommentDialog(Post post) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Comment");
        dialog.setHeaderText(null);

        // Create the comment input field
        TextArea commentInput = new TextArea();
        commentInput.setPromptText("Write your comment here...");
        commentInput.setPrefRowCount(3);

        dialog.getDialogPane().setContent(commentInput);

        // Add buttons
        ButtonType submitButton = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButton, ButtonType.CANCEL);

        // Enable/Disable submit button depending on whether there's text in the input field
        Node submitNode = dialog.getDialogPane().lookupButton(submitButton);
        submitNode.setDisable(true);
        commentInput.textProperty().addListener((observable, oldValue, newValue) -> {
            submitNode.setDisable(newValue.trim().isEmpty());
        });

        // Convert the result to comment text when the submit button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButton) {
                return commentInput.getText();
            }
            return null;
        });

        // Handle the result
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(commentText -> {
            if (!commentText.trim().isEmpty()) {
                saveComment(post.getId(), commentText.trim());
            }
        });
    }

    private void saveComment(int postId, String content) {
        String query = "INSERT INTO Comments (post_id, user_id, content, created_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = Db_connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, postId);
            pstmt.setInt(2, currentUserId);
            pstmt.setString(3, content);
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                loadPosts(); // Refresh the feed to show the new comment
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not save comment: " + e.getMessage());
        }
    }
}