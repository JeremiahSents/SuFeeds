package com.sentomero.sufeeds.javasufeeds.Models;


import java.time.LocalDateTime;

public class Comment {
    private int commentId;
    private User user;
    private String content;
    private LocalDateTime createdAt;

    public Comment(int commentId, User user, String content, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getters
    public int getCommentId() { return commentId; }
    public User getUser() { return user; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
