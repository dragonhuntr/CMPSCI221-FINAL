package com.studyplatform.views;

import com.studyplatform.controllers.NotificationController;
import com.studyplatform.models.Notification;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class NotificationView extends JPanel {
    private NotificationController notificationController;
    private DefaultTableModel notificationTableModel;
    private JTable notificationTable;
    private JLabel unreadCountLabel;

    public NotificationView(NotificationController notificationController) {
        this.notificationController = notificationController;
        this.notificationController.registerView(this);
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Notifications"));

        // Unread Notifications Count
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        unreadCountLabel = new JLabel("Unread Notifications: 0");
        topPanel.add(unreadCountLabel);
        add(topPanel, BorderLayout.NORTH);

        // Notification Table
        String[] columnNames = {"Title", "Description", "Timestamp", "Status"};
        notificationTableModel = new DefaultTableModel(columnNames, 0);
        notificationTable = new JTable(notificationTableModel);
        JScrollPane tableScrollPane = new JScrollPane(notificationTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton markReadButton = new JButton("Mark as Read");
        markReadButton.addActionListener(e -> markSelectedNotificationAsRead());
        buttonPanel.add(markReadButton);

        JButton clearAllButton = new JButton("Clear All");
        clearAllButton.addActionListener(e -> clearAllNotifications());
        buttonPanel.add(clearAllButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Initial refresh
        refreshView();
    }

    private void markSelectedNotificationAsRead() {
        int selectedRow = notificationTable.getSelectedRow();
        if (selectedRow >= 0) {
            List<Notification> notifications = notificationController.getAllNotifications();
            Notification selectedNotification = notifications.get(selectedRow);
            
            notificationController.markNotificationAsRead(selectedNotification);
            refreshView();
        } else {
            JOptionPane.showMessageDialog(this, "No notification selected.");
        }
    }

    private void clearAllNotifications() {
        int confirmDialog = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to clear all notifications?", 
            "Confirm Clear", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirmDialog == JOptionPane.YES_OPTION) {
            notificationController.clearAllNotifications();
            refreshView();
        }
    }

    public void refreshView() {
        try {
            // Clear existing rows
            notificationTableModel.setRowCount(0);
            
            // Get all non-deleted notifications
            List<Notification> notifications = notificationController.getAllNotifications();
            
            // Date formatter
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
            
            // Track unread count
            int unreadCount = 0;
            
            // Populate table
            for (Notification notification : notifications) {
                // Skip deleted notifications
                if (notification.isDeleted()) continue;
                
                // Check if notification is unread
                if (!notification.isRead()) {
                    unreadCount++;
                }
                
                // Add to table
                notificationTableModel.addRow(new Object[]{
                    notification.getTitle(),
                    notification.getDescription(),
                    sdf.format(notification.getTimestamp()),
                    notification.isRead() ? "Read" : "Unread"
                });
            }
            
            // Update unread count label
            unreadCountLabel.setText("Unread Notifications: " + unreadCount);
            
        } catch (Exception e) {
            System.err.println("Error refreshing notifications: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
