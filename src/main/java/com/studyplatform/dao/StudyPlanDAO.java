package com.studyplatform.dao;

import com.studyplatform.models.StudyPlan;
import com.studyplatform.models.Coursework;
import com.studyplatform.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudyPlanDAO implements BaseDAO<StudyPlan> {
    private static final String TABLE_NAME = "STUDY_PLANS";
    private static final String COURSEWORK_TABLE_NAME = "STUDY_PLAN_COURSEWORKS";

    public void createTable() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            String createStudyPlanTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "course VARCHAR(255), " +
                    "name VARCHAR(255))";
            
            String createCourseworkTableSQL = "CREATE TABLE " + COURSEWORK_TABLE_NAME + " (" +
                    "study_plan_id INT, " +
                    "coursework_id INT, " +
                    "PRIMARY KEY (study_plan_id, coursework_id), " +
                    "FOREIGN KEY (study_plan_id) REFERENCES " + TABLE_NAME + "(id), " +
                    "FOREIGN KEY (coursework_id) REFERENCES COURSEWORKS(id))";
            
            stmt.execute(createStudyPlanTableSQL);
            stmt.execute(createCourseworkTableSQL);
        }
    }

    @Override
    public void create(StudyPlan studyPlan) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (course, name) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, studyPlan.getCourse());
            pstmt.setString(2, studyPlan.getName());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    studyPlan.setId(generatedKeys.getInt(1));
                }
            }
            
            // Insert associated courseworks
            insertCourseworks(studyPlan);
        }
    }

    private void insertCourseworks(StudyPlan studyPlan) throws SQLException {
        if (studyPlan.getCourseworkList() == null || studyPlan.getCourseworkList().isEmpty()) {
            return;
        }
        
        String sql = "INSERT INTO " + COURSEWORK_TABLE_NAME + " (study_plan_id, coursework_id) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (Coursework coursework : studyPlan.getCourseworkList()) {
                pstmt.setInt(1, studyPlan.getId());
                pstmt.setInt(2, coursework.getId());
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
        }
    }

    @Override
    public StudyPlan read(int id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    StudyPlan studyPlan = new StudyPlan();
                    studyPlan.setId(rs.getInt("id"));
                    studyPlan.setCourse(rs.getString("course"));
                    studyPlan.setName(rs.getString("name"));
                    
                    // Fetch associated courseworks
                    fetchCourseworks(studyPlan);
                    
                    return studyPlan;
                }
            }
        }
        return null;
    }

    private void fetchCourseworks(StudyPlan studyPlan) throws SQLException {
        String sql = "SELECT coursework_id FROM " + COURSEWORK_TABLE_NAME + " WHERE study_plan_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studyPlan.getId());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                CourseworkDAO courseworkDAO = new CourseworkDAO();
                while (rs.next()) {
                    int courseworkId = rs.getInt("coursework_id");
                    Coursework coursework = courseworkDAO.read(courseworkId);
                    if (coursework != null) {
                        studyPlan.addCoursework(coursework);
                    }
                }
            }
        }
    }

    @Override
    public void update(StudyPlan studyPlan) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET course = ?, name = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studyPlan.getCourse());
            pstmt.setString(2, studyPlan.getName());
            pstmt.setInt(3, studyPlan.getId());
            
            pstmt.executeUpdate();
            
            // Remove existing courseworks and re-insert
            removeCourseworks(studyPlan.getId());
            insertCourseworks(studyPlan);
        }
    }

    private void removeCourseworks(int studyPlanId) throws SQLException {
        String sql = "DELETE FROM " + COURSEWORK_TABLE_NAME + " WHERE study_plan_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studyPlanId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        // First remove associated courseworks
        removeCourseworks(id);
        
        // Then delete the study plan
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<StudyPlan> findAll() throws SQLException {
        List<StudyPlan> studyPlans = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                StudyPlan studyPlan = new StudyPlan();
                studyPlan.setId(rs.getInt("id"));
                studyPlan.setCourse(rs.getString("course"));
                studyPlan.setName(rs.getString("name"));
                
                // Fetch associated courseworks
                fetchCourseworks(studyPlan);
                
                studyPlans.add(studyPlan);
            }
        }
        
        return studyPlans;
    }
}
