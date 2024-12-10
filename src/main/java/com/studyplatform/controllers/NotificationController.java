package com.studyplatform.controllers;

import com.studyplatform.models.Notification;
import com.studyplatform.models.Task;
import com.studyplatform.models.Tutor;
import com.studyplatform.dao.NotificationDAO;
import com.studyplatform.dao.TaskDAO;
import com.studyplatform.dao.TutorDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class NotificationController {
    private NotificationDAO notificationDAO;
    private TaskDAO taskDAO;
    private TutorDAO tutorDAO;
    private ScheduledExecutorService scheduler;

    public NotificationController() {
        this.notificationDAO = new NotificationDAO();
        this.taskDAO = new TaskDAO();
        this.tutorDAO = new TutorDAO();
        try {
            this.notificationDAO.createTable();
        } catch (SQLException e) {
            System.err.println("Error creating notifications table: " + e.getMessage());
        }

        // Start background task to check for due tasks and available tutors every 10 seconds
        startDueTaskNotificationScheduler();
    }

    private void checkAndCreateDueTaskNotifications() {
        try {
            Date today = new Date();
            List<Task> dueTasks = taskDAO.findTasksDueToday(today);
            
            for (Task task : dueTasks) {
                // Create a notification for each due task
                Notification notification = createNotification(
                    "Task Due Today", 
                    "Task '" + task.getTitle() + "' is due today!"
                );
                
                // Optional: Log if notification creation failed
                if (notification == null) {
                    System.err.println("Failed to create notification for task: " + task.getTitle());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking for due tasks: " + e.getMessage());
        }
    }

    private void checkAndCreateTutorAvailabilityNotifications() {
        try {
            Date today = new Date();
            List<Tutor> availableTutors = tutorDAO.findTutorsAvailableToday(today);
            
            for (Tutor tutor : availableTutors) {
                // Create a notification for each available tutor
                Notification notification = createNotification(
                    "Tutor Available Today", 
                    "Tutor '" + tutor.getName() + "' is available today in " + tutor.getLocation() + 
                    " for " + tutor.getTutorClass() + " class."
                );
                
                // Optional: Log if notification creation failed
                if (notification == null) {
                    System.err.println("Failed to create notification for tutor: " + tutor.getName());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking for available tutors: " + e.getMessage());
        }
    }

    private void startDueTaskNotificationScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            checkAndCreateDueTaskNotifications();
            checkAndCreateTutorAvailabilityNotifications();
        }, 0, 10, TimeUnit.SECONDS);
    }

    public Notification createNotification(String title, String description) {
        try {
            Notification notification = new Notification(title, description);
            notificationDAO.create(notification);
            System.out.println("Created Notification: " + title + " - " + description);
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

    @Override
    protected void finalize() throws Throwable {
        // Ensure the scheduler is shut down when the object is garbage collected
        if (scheduler != null) {
            scheduler.shutdown();
        }
        super.finalize();
    }
}
