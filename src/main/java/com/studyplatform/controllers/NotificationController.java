package com.studyplatform.controllers;

import com.studyplatform.models.Notification;
import com.studyplatform.dao.NotificationDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationController {
    private NotificationDAO notificationDAO;

    public NotificationController() {
        this.notificationDAO = new NotificationDAO();
        try {
            this.notificationDAO.createTable();
        } catch (SQLException e) {
            System.err.println("Error creating notifications table: " + e.getMessage());
        }
    }

    public Notification createNotification(String title, String description) {
        try {
            Notification notification = new Notification(title, description);
            notificationDAO.create(notification);
            return notification;
        } catch (SQLException e) {
            System.err.println("Error creating notification: " + e.getMessage());
            return null;
        }
    }

    public List<Notification> getAllNotifications() {
        try {
            return notificationDAO.findAll();
        } catch (SQLException e) {
            System.err.println("Error retrieving notifications: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Notification> getUnreadNotifications() {
        try {
            return notificationDAO.findAll().stream()
                    .filter(notification -> !notification.isRead())
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error retrieving unread notifications: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void markNotificationAsRead(Notification notification) {
        try {
            notification.markAsRead();
            notificationDAO.update(notification);
        } catch (SQLException e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
        }
    }

    public void clearAllNotifications() {
        try {
            List<Notification> notifications = notificationDAO.findAll();
            for (Notification notification : notifications) {
                notificationDAO.delete(notification.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error clearing notifications: " + e.getMessage());
        }
    }

    public void removeNotification(Notification notification) {
        try {
            notificationDAO.delete(notification.getId());
        } catch (SQLException e) {
            System.err.println("Error removing notification: " + e.getMessage());
        }
    }

    public int getUnreadNotificationCount() {
        try {
            return (int) notificationDAO.findAll().stream()
                    .filter(notification -> !notification.isRead())
                    .count();
        } catch (SQLException e) {
            System.err.println("Error counting unread notifications: " + e.getMessage());
            return 0;
        }
    }
}
