package com.studyplatform.dao;

import com.studyplatform.models.StudyPlan;
import com.studyplatform.models.Coursework;
import com.studyplatform.util.DatabaseUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

//here is our StudyPlan dao
public class StudyPlanDAO implements BaseDAO<StudyPlan> {
    private static final String TABLE_NAME = "STUDY_PLANS";
    private static final String COURSEWORK_TABLE_NAME = "COURSEWORKS";
    private static final String COURSES_TABLE_NAME = "COURSES";

    // this should be able to create table and put that in the db
    public void createTable() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            // this should create courses table
            String createCoursesTableSQL = "CREATE TABLE " + COURSES_TABLE_NAME + " (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "name VARCHAR(255) UNIQUE NOT NULL)";

            // this should create study plans table
            String createStudyPlanTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "course_id INT, " +
                    "name VARCHAR(255), " +
                    "FOREIGN KEY (course_id) REFERENCES " + COURSES_TABLE_NAME + "(id))";

            // this create coursework table
            String createCourseworkTableSQL = "CREATE TABLE " + COURSEWORK_TABLE_NAME + " (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "study_plan_id INT, " +
                    "name VARCHAR(255), " +
                    "details VARCHAR(500), " +
                    "due_date VARCHAR(50), " +
                    "status VARCHAR(50), " +
                    "FOREIGN KEY (study_plan_id) REFERENCES " + TABLE_NAME + "(id))";

            stmt.execute(createCoursesTableSQL);
            stmt.execute(createStudyPlanTableSQL);
            stmt.execute(createCourseworkTableSQL);
        }
    }

    // this would bacailly add courses
    public void addCourse(String name) throws SQLException {
        String sql = "INSERT INTO " + COURSES_TABLE_NAME + " (name) VALUES (?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return;
                }
            }
        }
    }

    // and this one would get the courseID
    public int getCourseId(String courseName) throws SQLException {
        String sql = "SELECT id FROM " + COURSES_TABLE_NAME + " WHERE name = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }

            throw new SQLException("Course not found: " + courseName);
        }
    }

    //this one would create the whole study plan
    // as it would get the info from the above two fucntions
    @Override
    public void create(StudyPlan studyPlan) throws SQLException {
        // first, we would need to get the course ID
        int courseId = getCourseId(studyPlan.getCourse());

        String sql = "INSERT INTO " + TABLE_NAME + " (course_id, name) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, courseId);
            pstmt.setString(2, studyPlan.getName());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    studyPlan.setId(generatedKeys.getInt(1));
                }
            }

            // insert associated courseworks
            insertCourseworks(studyPlan);
        }
    }

    // this would insert coursework into the study plan
    private void insertCourseworks(StudyPlan studyPlan) throws SQLException {
        if (studyPlan.getCourseworkList() == null || studyPlan.getCourseworkList().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO " + COURSEWORK_TABLE_NAME + " (study_plan_id, name, details, due_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (Coursework coursework : studyPlan.getCourseworkList()) {
                pstmt.setInt(1, studyPlan.getId());
                pstmt.setString(2, coursework.getName());
                pstmt.setString(3, coursework.getDetails());
                pstmt.setString(4, coursework.getDueDate());
                pstmt.setString(5, coursework.getStatus());

                pstmt.executeUpdate();

                // get the generated coursework ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        coursework.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }

    // this method would simply get the studyplan and read it
    // and apply some try and if statements to check connection and reablity
    @Override
    public StudyPlan read(int id) throws SQLException {
        String sql = "SELECT sp.id, sp.name, c.name AS course_name " +
                "FROM " + TABLE_NAME + " sp " +
                "JOIN " + COURSES_TABLE_NAME + " c ON sp.course_id = c.id " +
                "WHERE sp.id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    StudyPlan studyPlan = new StudyPlan();
                    studyPlan.setId(rs.getInt("id"));
                    studyPlan.setName(rs.getString("name"));
                    studyPlan.setCourse(rs.getString("course_name"));

                    // Fetch associated courseworks
                    fetchCourseworks(studyPlan);

                    return studyPlan;
                }
            }
        }
        return null;
    }

    //this would get the search for the coursework and get it so that it can be
    //put into the study plan
    private void fetchCourseworks(StudyPlan studyPlan) throws SQLException {
        String sql = "SELECT id, name, details, due_date, status " +
                "FROM " + COURSEWORK_TABLE_NAME + " " +
                "WHERE study_plan_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studyPlan.getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Coursework coursework = new Coursework();
                    coursework.setId(rs.getInt("id"));
                    coursework.setName(rs.getString("name"));
                    coursework.setDetails(rs.getString("details"));

                    // this would handle null date
                    String dueDate = rs.getString("due_date");
                    if (dueDate != null) {
                        coursework.setDueDate(dueDate);
                    }

                    coursework.setStatus(rs.getString("status"));

                    studyPlan.addCoursework(coursework);
                }
            } catch(SQLException ex) {
                System.err.println("Error fetching courseworks for study plan: " + studyPlan.getId());
                ex.printStackTrace();
            }
        }
    }

    //this would update the study plan after the last two methods
    @Override
    public void update(StudyPlan studyPlan) throws SQLException {
        // First, get the course ID
        int courseId = getCourseId(studyPlan.getCourse());

        String sql = "UPDATE " + TABLE_NAME + " SET course_id = ?, name = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.setString(2, studyPlan.getName());
            pstmt.setInt(3, studyPlan.getId());

            pstmt.executeUpdate();

            // also we would need to remove existing courseworks and put it back
            removeCourseworks(studyPlan.getId());
            insertCourseworks(studyPlan);
        }
    }

    // so now in this method we would need to remove coursework
    //and place the remaning
    private void removeCourseworks(int studyPlanId) throws SQLException {
        String sql = "DELETE FROM " + COURSEWORK_TABLE_NAME + " WHERE study_plan_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studyPlanId);
            pstmt.executeUpdate();
        }
    }

    // here we would need to delete the coursework ID
    // to reject all the links it has with the database
    @Override
    public void delete(int id) throws SQLException {
        // we would first remove associated courseworks
        removeCourseworks(id);

        // then after removeing the course work we would delete the study plan
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    //through this method we shoudl be able to find all study plan
    @Override
    public List<StudyPlan> findAll() throws SQLException {
        List<StudyPlan> studyPlans = new ArrayList<>();
        String sql = "SELECT sp.id, sp.name, c.name AS course_name " +
                "FROM " + TABLE_NAME + " sp " +
                "JOIN " + COURSES_TABLE_NAME + " c ON sp.course_id = c.id";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                StudyPlan studyPlan = new StudyPlan();
                studyPlan.setId(rs.getInt("id"));
                studyPlan.setCourse(rs.getString("course_name"));
                studyPlan.setName(rs.getString("name"));

                // this should get associated courseworks
                fetchCourseworks(studyPlan);

                studyPlans.add(studyPlan);
            }
        }

        return studyPlans;
    }

    // this should add the course work to the study plan
    public void addCourseworkToStudyPlan(int studyPlanId, Coursework coursework) throws SQLException {
        // now we should be able to add coursework directly to the COURSEWORKS table with the study plan ID
        String createCourseworkSQL = "INSERT INTO " + COURSEWORK_TABLE_NAME +
                " (study_plan_id, name, details, due_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(createCourseworkSQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, studyPlanId);
            pstmt.setString(2, coursework.getName());
            pstmt.setString(3, coursework.getDetails());
            pstmt.setString(4, coursework.getDueDate());
            pstmt.setString(5, coursework.getStatus());

            pstmt.executeUpdate();

            // obtain the generated coursework ID again
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int courseworkId = generatedKeys.getInt(1);
                    coursework.setId(courseworkId);
                } else {
                    throw new SQLException("Creating coursework failed, no ID obtained.");
                }
            }
        }
    }

    //this would update the course work after the addition of the previous method
    public void updateCoursework(int studyPlanId, Coursework coursework) throws SQLException {
        // Update the coursework in the COURSEWORKS table
        String updateCourseworkSQL = "UPDATE " + COURSEWORK_TABLE_NAME + " SET name = ?, details = ?, due_date = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateCourseworkSQL)) {

            pstmt.setString(1, coursework.getName());
            pstmt.setString(2, coursework.getDetails());
            pstmt.setString(3, coursework.getDueDate());
            pstmt.setString(4, coursework.getStatus());
            pstmt.setInt(5, coursework.getId());

            pstmt.executeUpdate();
        }
    }

    //this would remove the course from the study plan
    public void removeCourseworkFromStudyPlan(int studyPlanId, int courseworkId) throws SQLException {
        // this should delete the specific coursework from the coursework table
        String deleteCourseworkSQL = "DELETE FROM " + COURSEWORK_TABLE_NAME + " WHERE id = ? AND study_plan_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement deleteCourseworkStmt = conn.prepareStatement(deleteCourseworkSQL)) {

            deleteCourseworkStmt.setInt(1, courseworkId);
            deleteCourseworkStmt.setInt(2, studyPlanId);
            deleteCourseworkStmt.executeUpdate();
        }
    }

    // this method is supposed to get all courses
    public List<String> getAllCourses() throws SQLException {
        List<String> courses = new ArrayList<>();
        String sql = "SELECT name FROM " + COURSES_TABLE_NAME + " ORDER BY name";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                courses.add(rs.getString("name"));
            }
        }

        return courses;
    }

    // here we nned this method to get study plans for a specific course
    public List<StudyPlan> getStudyPlansForCourse(String courseName) throws SQLException {
        List<StudyPlan> studyPlans = new ArrayList<>();

        String sql = "SELECT sp.id, sp.name " +
                "FROM " + TABLE_NAME + " sp " +
                "JOIN " + COURSES_TABLE_NAME + " c ON sp.course_id = c.id " +
                "WHERE c.name = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseName);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    StudyPlan studyPlan = new StudyPlan();
                    studyPlan.setId(rs.getInt("id"));
                    studyPlan.setName(rs.getString("name"));
                    studyPlan.setCourse(courseName);

                    // Fetch associated courseworks
                    fetchCourseworks(studyPlan);

                    studyPlans.add(studyPlan);
                }
            }
        }

        return studyPlans;
    }

    // this method should be able to add a coursework to a study plan
    public void addCourseworkToStudyPlan(StudyPlan studyPlan, Coursework coursework) throws SQLException {
        String sql = "INSERT INTO " + COURSEWORK_TABLE_NAME + " (study_plan_id, name, details, due_date, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, studyPlan.getId());
            pstmt.setString(2, coursework.getName());
            pstmt.setString(3, coursework.getDetails());
            pstmt.setString(4, coursework.getDueDate());
            pstmt.setString(5, coursework.getStatus());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    coursework.setId(generatedKeys.getInt(1));
                }
            }

            // and add to study plan's coursework list
            studyPlan.addCoursework(coursework);
        }
    }

    // this method would also need to update the coursework
    public void updateCoursework(StudyPlan studyPlan, Coursework coursework) throws SQLException {
        String sql = "UPDATE " + COURSEWORK_TABLE_NAME + " SET name = ?, details = ?, due_date = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, coursework.getName());
            pstmt.setString(2, coursework.getDetails());
            pstmt.setString(3, coursework.getDueDate());
            pstmt.setString(4, coursework.getStatus());
            pstmt.setInt(5, coursework.getId());

            pstmt.executeUpdate();

            // also would need to update in study plan's coursework list
            studyPlan.updateCoursework(coursework);
        }
    }

    // here we would need the method to remove a coursework from a study plan
    public void removeCourseworkFromStudyPlan(StudyPlan studyPlan, Coursework coursework) throws SQLException {
        String sql = "DELETE FROM " + COURSEWORK_TABLE_NAME + " WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, coursework.getId());
            pstmt.executeUpdate();

            // then we would need to remove from study plan's coursework list
            studyPlan.removeCoursework(coursework);
        }
    }

    // now after all that we would need the method to delete a study plan
    //becuase you can't delete all of them at once
    public void deleteStudyPlan(String courseName, String studyPlanName) throws SQLException {
        // so first you get the course ID
        int courseId = getCourseId(courseName);

        // then you delete linked courseworks
        String deleteCourseworksSql = "DELETE FROM " + COURSEWORK_TABLE_NAME + " WHERE study_plan_id IN " +
                "(SELECT id FROM " + TABLE_NAME + " WHERE course_id = ? AND name = ?)";

        // after that step you need to delete the study plan
        String deleteStudyPlanSql = "DELETE FROM " + TABLE_NAME + " WHERE course_id = ? AND name = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmtCourseworks = conn.prepareStatement(deleteCourseworksSql);
             PreparedStatement pstmtStudyPlan = conn.prepareStatement(deleteStudyPlanSql)) {


            // just like the other ones we need to
            // delete coursework
            pstmtCourseworks.setInt(1, courseId);
            pstmtCourseworks.setString(2, studyPlanName);
            pstmtCourseworks.executeUpdate();

            // then after delete study plan
            pstmtStudyPlan.setInt(1, courseId);
            pstmtStudyPlan.setString(2, studyPlanName);
            pstmtStudyPlan.executeUpdate();

            // now we need to commit to the changes
            conn.commit();
        } catch (SQLException e) {
            // then rollback in case of error
            try (Connection conn = DatabaseUtil.getConnection()) {
                conn.rollback();
            }
            throw e;
        }
    }

    //here we would need to find course work and when it is due
    public List<Coursework> findCourseworkDueOnDate(Date today) throws SQLException {
        List<Coursework> dueCourseworks = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String todayString = dateFormat.format(today);

        String sql = "SELECT * FROM " + COURSEWORK_TABLE_NAME + " WHERE due_date = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, todayString);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Coursework coursework = new Coursework();
                    coursework.setId(rs.getInt("id"));
                    coursework.setName(rs.getString("name"));
                    coursework.setDetails(rs.getString("details"));
                    coursework.setDueDate(rs.getString("due_date"));
                    coursework.setStatus(rs.getString("status"));

                    dueCourseworks.add(coursework);
                }
            }
        }

        return dueCourseworks;
    }
}