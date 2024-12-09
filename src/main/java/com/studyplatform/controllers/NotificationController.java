package com.studyplatform.controllers;

import com.studyplatform.models.Notification;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationController {
    private List<Notification> notifications;

    public NotificationController() {
        this.notifications = new ArrayList<>();
    }

    public Notification createNotification(String title, String description) {
        Notification notification = new Notification(title, description);
        notifications.add(notification);
        return notification;
    }

    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notifications);
    }

    public List<Notification> getUnreadNotifications() {
        return notifications.stream()
                .filter(notification -> !notification.isRead())
                .collect(Collectors.toList());
    }

    public void markNotificationAsRead(Notification notification) {
        notification.markAsRead();
    }

    public void clearAllNotifications() {
        notifications.clear();
    }

    public void removeNotification(Notification notification) {
        notifications.remove(notification);
    }

    public int getUnreadNotificationCount() {
        return (int) notifications.stream()
                .filter(notification -> !notification.isRead())
                .count();
    }
}
