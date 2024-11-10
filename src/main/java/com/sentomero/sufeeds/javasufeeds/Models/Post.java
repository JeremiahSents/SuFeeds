package com.sentomero.sufeeds.javasufeeds.Models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private int id;
    private User user;
    private String content;
    private LocalDateTime createdAt;
    private int likes;
    private int dislikes;
    private String classTag;
    private String userReaction; // to track current user's reaction
    private List<Comment> comments;
    private boolean isStandaloneComment;

    // Updated constructor with all necessary fields
    public Post(int id, User user, String content, LocalDateTime createdAt, int likes, int dislikes, String classTag,boolean isStandaloneComment) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
        this.likes = likes;
        this.dislikes = dislikes;
        this.classTag = classTag;
        this.comments = new ArrayList<>();
        this.isStandaloneComment = isStandaloneComment;
    }

    // Getters
    public int getId() {
        return id;
    }

    public boolean isStandaloneComment() {
        return isStandaloneComment;
    }

    public User getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public String getClassTag() {
        return classTag;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getUserReaction() {
        return userReaction;
    }

    // Setters
    public void setUserReaction(String userReaction) {
        this.userReaction = userReaction;
    }

    // Helper methods to check user reactions
    public boolean isLikedByCurrentUser() {
        return "like".equals(userReaction);
    }

    public boolean isDislikedByCurrentUser() {
        return "dislike".equals(userReaction);
    }
}