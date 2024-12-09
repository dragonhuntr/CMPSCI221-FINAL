package com.studyplatform.dao;

import com.studyplatform.models.Notification;
import com.studyplatform.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO implements BaseDAO<Notification> {
    private static final String TABLE_NAME = "NOTIFICATIONS";

    public void createTable() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "title VARCHAR(255), " +
                    "description VARCHAR(1000), " +
                    "timestamp TIMESTAMP, " +
                    "is_read BOOLEAN)";
            stmt.execute(createTableSQL);
        }
    }

    @Override
    public void create(Notification notification) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (title, description, timestamp, is_read) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, notification.getTitle());
            pstmt.setString(2, notification.getDescription());
            pstmt.setTimestamp(3, new Timestamp(notification.getTimestamp().getTime()));
            pstmt.setBoolean(4, notification.isRead());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    notification.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Notification read(int id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Notification notification = new Notification();
                    notification.setId(rs.getInt("id"));
                    notification.setTitle(rs.getString("title"));
                    notification.setDescription(rs.getString("description"));
                    notification.getTimestamp().setTime(rs.getTimestamp("timestamp").getTime());
                    
                    if (rs.getBoolean("is_read")) {
                        notification.markAsRead();
                    }
                    
                    return notification;
                }
            }
        }
        return null;
    }

    @Override
    public void update(Notification notification) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET title = ?, description = ?, timestamp = ?, is_read = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, notification.getTitle());
            pstmt.setString(2, notification.getDescription());
            pstmt.setTimestamp(3, new Timestamp(notification.getTimestamp().getTime()));
            pstmt.setBoolean(4, notification.isRead());
            pstmt.setInt(5, notification.getId());
            
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Notification> findAll() throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setId(rs.getInt("id"));
                notification.setTitle(rs.getString("title"));
                notification.setDescription(rs.getString("description"));
                notification.getTimestamp().setTime(rs.getTimestamp("timestamp").getTime());
                
                if (rs.getBoolean("is_read")) {
                    notification.markAsRead();
                }
                
                notifications.add(notification);
            }
        }
        
        return notifications;
    }
}
