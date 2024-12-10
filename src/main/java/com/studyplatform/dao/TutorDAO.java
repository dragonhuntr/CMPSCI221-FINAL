package com.studyplatform.dao;

import com.studyplatform.models.Tutor;
import com.studyplatform.util.DatabaseUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TutorDAO implements BaseDAO<Tutor> {
    private static final String TABLE_NAME = "TUTORS";

    public void createTable() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "name VARCHAR(255), " +
                    "tutor_class VARCHAR(255), " +
                    "available_date VARCHAR(50), " +
                    "location VARCHAR(255), " +
                    "scheduled BOOLEAN)";
            stmt.execute(createTableSQL);
        }
    }

    @Override
    public void create(Tutor tutor) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (name, tutor_class, available_date, location, scheduled) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, tutor.getName());
            pstmt.setString(2, tutor.getTutorClass());
            pstmt.setString(3, tutor.getAvailableDate());
            pstmt.setString(4, tutor.getLocation());
            pstmt.setBoolean(5, tutor.isScheduled());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tutor.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Tutor read(int id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Tutor tutor = new Tutor();
                    tutor.setId(rs.getInt("id"));
                    tutor.setName(rs.getString("name"));
                    tutor.setTutorClass(rs.getString("tutor_class"));
                    tutor.setAvailableDate(rs.getString("available_date"));
                    tutor.setLocation(rs.getString("location"));
                    tutor.setScheduled(rs.getBoolean("scheduled"));
                    
                    return tutor;
                }
            }
        }
        return null;
    }

    @Override
    public void update(Tutor tutor) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET name = ?, tutor_class = ?, available_date = ?, location = ?, scheduled = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tutor.getName());
            pstmt.setString(2, tutor.getTutorClass());
            pstmt.setString(3, tutor.getAvailableDate());
            pstmt.setString(4, tutor.getLocation());
            pstmt.setBoolean(5, tutor.isScheduled());
            pstmt.setInt(6, tutor.getId());
            
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
    public List<Tutor> findAll() throws SQLException {
        List<Tutor> tutors = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Tutor tutor = new Tutor();
                tutor.setId(rs.getInt("id"));
                tutor.setName(rs.getString("name"));
                tutor.setTutorClass(rs.getString("tutor_class"));
                tutor.setAvailableDate(rs.getString("available_date"));
                tutor.setLocation(rs.getString("location"));
                tutor.setScheduled(rs.getBoolean("scheduled"));
                
                tutors.add(tutor);
            }
        }
        
        return tutors;
    }

    public List<Tutor> findScheduledTutorsForToday(Date date) throws SQLException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String todayString = dateFormat.format(date);
        
        List<Tutor> scheduledTutors = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE scheduled = true AND available_date = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, todayString);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Tutor tutor = new Tutor();
                    tutor.setId(rs.getInt("id"));
                    tutor.setName(rs.getString("name"));
                    tutor.setTutorClass(rs.getString("tutor_class"));
                    tutor.setAvailableDate(rs.getString("available_date"));
                    tutor.setLocation(rs.getString("location"));
                    tutor.setScheduled(rs.getBoolean("scheduled"));
                    
                    scheduledTutors.add(tutor);
                }
            }
        }
        
        return scheduledTutors;
    }
}
