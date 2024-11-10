package com.sentomero.sufeeds.javasufeeds.Models;

import java.time.LocalDateTime;

public class Comment {
    private int id;
    private User user;
    private String content;
    private LocalDateTime createdAt;

    public Comment(int id, User user, String content, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() { return id; }
    public User getUser() { return user; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}