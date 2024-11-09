package com.sentomero.sufeeds.javasufeeds.Controllers;

import com.sentomero.sufeeds.javasufeeds.Models.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.application.Platform;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class HomeFeed {
    @FXML private BorderPane mainContainer;
    @FXML private VBox leftSidebar;
    @FXML private VBox feedContainer;
    @FXML private VBox postsContainer;
    @FXML private TextArea postInput;

    private List<Post> posts;
    private User currentUser; // This would normally be set during login

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            setupMockData();
            loadPosts();
        });
    }

    private void setupMockData() {
        // Set up a mock current user
        currentUser = new User(1, "John", "Doe", "CS001", "Computer Science", "Year 1");

        // Create mock posts list
        posts = new ArrayList<>();

        // Create some mock users
        User user1 = new User(2, "Jane", "Smith", "CS002", "Computer Science", "Year 2");
        User user2 = new User(3, "Mike", "Johnson", "CS003", "Computer Science", "Year 3");

        // Create mock posts
        Post post1 = new Post(1, user1, "Anyone struggling with the latest programming assignment? #CSC101",
                LocalDateTime.now().minusHours(2));
        Post post2 = new Post(2, user2, "Great lecture today on Binary Trees! #CSC201",
                LocalDateTime.now().minusHours(1));

        // Add comments to posts
        post1.getComments().add(new Comment(1, user2, "Yes, particularly with the recursion part!",
                LocalDateTime.now().minusHours(1)));
        post2.getComments().add(new Comment(2, user1, "The visualization really helped understand the concept",
                LocalDateTime.now().minusMinutes(30)));

        // Set likes/dislikes
        post1.setLikes(5);
        post1.setDislikes(1);
        post2.setLikes(3);
        post2.setDislikes(0);

        // Add posts to list
        posts.add(post1);
        posts.add(post2);
    }

    private void loadPosts() {
        postsContainer.getChildren().clear();
        for (Post post : posts) {
            postsContainer.getChildren().add(createPostNode(post));
        }
    }

    private VBox createPostNode(Post post) {
        VBox postBox = new VBox(10);
        postBox.getStyleClass().add("post");

        // User info section
        HBox userInfo = new HBox(10);
        userInfo.setAlignment(Pos.CENTER_LEFT);

        FontIcon userIcon = new FontIcon("fas-user-circle");
        userIcon.setIconSize(32);
        userIcon.setIconColor(Color.web("#3E7BFA"));

        VBox userDetails = new VBox(2);
        Label userName = new Label(post.getUser().getFullName());
        Label timestamp = new Label(formatDateTime(post.getCreatedAt()));
        userName.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold;");
        timestamp.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        userDetails.getChildren().addAll(userName, timestamp);
        userInfo.getChildren().addAll(userIcon, userDetails);

        // Post content
        Label contentLabel = new Label(post.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-text-fill: #FFFFFF;");

        // Reaction counts
        HBox reactionCounts = new HBox(15);
        reactionCounts.setAlignment(Pos.CENTER_LEFT);
        Label likesCount = new Label(post.getLikes() + " likes");
        Label dislikesCount = new Label(post.getDislikes() + " dislikes");
        likesCount.setStyle("-fx-text-fill: #888888;");
        dislikesCount.setStyle("-fx-text-fill: #888888;");
        reactionCounts.getChildren().addAll(likesCount, dislikesCount);

        // Action buttons
        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button likeBtn = createActionButton("fas-thumbs-up", "Like");
        Button dislikeBtn = createActionButton("fas-thumbs-down", "Dislike");
        Button commentBtn = createActionButton("fas-comment", "Comment");
        Button viewCommentsBtn = createActionButton("fas-comments",
                post.getComments().size() + " Comments");

        // Set up button actions
        likeBtn.setOnAction(e -> handleLike(post, likesCount));
        dislikeBtn.setOnAction(e -> handleDislike(post, dislikesCount));
        commentBtn.setOnAction(e -> showCommentDialog(post));
        viewCommentsBtn.setOnAction(e -> toggleComments(post, postBox));

        actions.getChildren().addAll(likeBtn, dislikeBtn, commentBtn, viewCommentsBtn);

        // Add all components to post box
        postBox.getChildren().addAll(userInfo, contentLabel, reactionCounts, actions);
        return postBox;
    }

    private void handleLike(Post post, Label likesCount) {
        if (!post.isLikedByCurrentUser()) {
            post.setLikes(post.getLikes() + 1);
            if (post.isDislikedByCurrentUser()) {
                post.setDislikes(post.getDislikes() - 1);
                post.setDislikedByCurrentUser(false);
            }
            post.setLikedByCurrentUser(true);
        } else {
            post.setLikes(post.getLikes() - 1);
            post.setLikedByCurrentUser(false);
        }
        likesCount.setText(post.getLikes() + " likes");
    }

    private void handleDislike(Post post, Label dislikesCount) {
        if (!post.isDislikedByCurrentUser()) {
            post.setDislikes(post.getDislikes() + 1);
            if (post.isLikedByCurrentUser()) {
                post.setLikes(post.getLikes() - 1);
                post.setLikedByCurrentUser(false);
            }
            post.setDislikedByCurrentUser(true);
        } else {
            post.setDislikes(post.getDislikes() - 1);
            post.setDislikedByCurrentUser(false);
        }
        dislikesCount.setText(post.getDislikes() + " dislikes");
    }

    private void showCommentDialog(Post post) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Comment");
        dialog.setHeaderText(null);

        // Create the comment input field
        TextArea commentInput = new TextArea();
        commentInput.setPromptText("Write your comment...");
        commentInput.setPrefRowCount(3);

        dialog.getDialogPane().setContent(commentInput);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return commentInput.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(content -> {
            if (!content.trim().isEmpty()) {
                Comment newComment = new Comment(
                        post.getComments().size() + 1,
                        currentUser,
                        content,
                        LocalDateTime.now()
                );
                post.getComments().add(newComment);
                loadPosts(); // Refresh the posts to show new comment
            }
        });
    }

    private void toggleComments(Post post, VBox postBox) {
        // Remove existing comments section if it exists
        if (postBox.getChildren().size() > 4) {
            postBox.getChildren().remove(4);
            return;
        }

        // Create comments section
        VBox commentsSection = new VBox(5);
        commentsSection.setStyle("-fx-padding: 10 0 0 20;");

        for (Comment comment : post.getComments()) {
            HBox commentBox = new HBox(10);

            FontIcon userIcon = new FontIcon("fas-user-circle");
            userIcon.setIconSize(24);
            userIcon.setIconColor(Color.web("#3E7BFA"));

            VBox commentContent = new VBox(2);
            Label commentUser = new Label(comment.getUser().getFullName());
            Label commentText = new Label(comment.getContent());
            Label commentTime = new Label(formatDateTime(comment.getCreatedAt()));

            commentUser.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold;");
            commentText.setStyle("-fx-text-fill: #FFFFFF;");
            commentTime.setStyle("-fx-text-fill: #888888; -fx-font-size: 11px;");

            commentContent.getChildren().addAll(commentUser, commentText, commentTime);
            commentBox.getChildren().addAll(userIcon, commentContent);

            commentsSection.getChildren().add(commentBox);
        }

        postBox.getChildren().add(commentsSection);
    }

    private Button createActionButton(String iconLiteral, String text) {
        Button button = new Button();
        button.getStyleClass().add("nav-button");

        HBox content = new HBox(5);
        content.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconLiteral);
        icon.getStyleClass().add("nav-icon");

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #CCCCCC;");

        content.getChildren().addAll(icon, label);
        button.setGraphic(content);

        return button;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' HH:mm");
        return dateTime.format(formatter);
    }

    @FXML
    private void handlePostButton() {
        String content = postInput.getText().trim();
        if (!content.isEmpty()) {
            Post newPost = new Post(
                    posts.size() + 1,
                    currentUser,
                    content,
                    LocalDateTime.now()
            );
            posts.add(0, newPost);
            loadPosts();
            postInput.clear();
        }
    }
    @FXML
    private void handleHomeButton() {
        // Add your home button logic here
        System.out.println("Home button clicked");
    }

    @FXML
    private void handleProfileButton() {
        // Add your profile button logic here
        System.out.println("Profile button clicked");
    }

    public void handleUsersButton(ActionEvent event) {
    }

    public void handleAddCommentButton(ActionEvent event) {
    }

    public void handleLogoutButton(ActionEvent event) {
    }
}