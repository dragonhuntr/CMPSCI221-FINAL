package com.studyplatform.models;

import java.util.Date;

public class Notification {
    private String title;
    private String description;
    private Date timestamp;
    private boolean isRead;

    public Notification(String title, String description) {
        this.title = title;
        this.description = description;
        this.timestamp = new Date();
        this.isRead = false;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getTimestamp() { return timestamp; }

    public boolean isRead() { return isRead; }
    public void markAsRead() { this.isRead = true; }
}
