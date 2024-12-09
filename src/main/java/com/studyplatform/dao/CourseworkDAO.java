package com.studyplatform.dao;

import com.studyplatform.models.Coursework;
import com.studyplatform.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseworkDAO implements BaseDAO<Coursework> {
    private static final String TABLE_NAME = "COURSEWORKS";

    // Create table if not exists
    public void createTable() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "name VARCHAR(255), " +
                    "details VARCHAR(1000), " +
                    "due_date DATE, " +
                    "status VARCHAR(50))";
            stmt.execute(createTableSQL);
        }
    }

    @Override
    public void create(Coursework coursework) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (name, details, due_date, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, coursework.getName());
            pstmt.setString(2, coursework.getDetails());
            pstmt.setDate(3, new Date(coursework.getDueDate().getTime()));
            pstmt.setString(4, coursework.getStatus());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    coursework.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Coursework read(int id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Coursework coursework = new Coursework();
                    coursework.setId(rs.getInt("id"));
                    coursework.setName(rs.getString("name"));
                    coursework.setDetails(rs.getString("details"));
                    coursework.setDueDate(rs.getDate("due_date"));
                    coursework.setStatus(rs.getString("status"));
                    return coursework;
                }
            }
        }
        return null;
    }

    @Override
    public void update(Coursework coursework) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET name = ?, details = ?, due_date = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, coursework.getName());
            pstmt.setString(2, coursework.getDetails());
            pstmt.setDate(3, new Date(coursework.getDueDate().getTime()));
            pstmt.setString(4, coursework.getStatus());
            pstmt.setInt(5, coursework.getId());
            
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
    public List<Coursework> findAll() throws SQLException {
        List<Coursework> courseworks = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Coursework coursework = new Coursework();
                coursework.setId(rs.getInt("id"));
                coursework.setName(rs.getString("name"));
                coursework.setDetails(rs.getString("details"));
                coursework.setDueDate(rs.getDate("due_date"));
                coursework.setStatus(rs.getString("status"));
                courseworks.add(coursework);
            }
        }
        
        return courseworks;
    }
}
