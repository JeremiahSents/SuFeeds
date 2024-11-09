package com.sentomero.sufeeds.javasufeeds.Models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private int postId;
    private User user;
    private String content;
    private LocalDateTime createdAt;
    private List<Comment> comments;
    private int likes;
    private int dislikes;
    private boolean isLikedByCurrentUser;
    private boolean isDislikedByCurrentUser;

    public Post(int postId, User user, String content, LocalDateTime createdAt) {
        this.postId = postId;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
        this.comments = new ArrayList<>();
        this.likes = 0;
        this.dislikes = 0;
    }

    // Getters and setters
    public int getPostId() { return postId; }
    public User getUser() { return user; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Comment> getComments() { return comments; }
    public int getLikes() { return likes; }
    public int getDislikes() { return dislikes; }
    public boolean isLikedByCurrentUser() { return isLikedByCurrentUser; }
    public boolean isDislikedByCurrentUser() { return isDislikedByCurrentUser; }

    public void setLikedByCurrentUser(boolean liked) { this.isLikedByCurrentUser = liked; }
    public void setDislikedByCurrentUser(boolean disliked) { this.isDislikedByCurrentUser = disliked; }
    public void setLikes(int likes) { this.likes = likes; }
    public void setDislikes(int dislikes) { this.dislikes = dislikes; }
}