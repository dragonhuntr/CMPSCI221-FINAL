package com.studyplatform.dao;

import com.studyplatform.models.Task;
import com.studyplatform.util.DatabaseUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskDAO implements BaseDAO<Task> {
    private static final String TABLE_NAME = "TASKS";

    public void createTable() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            // here we are using create table to prevent errors if table already exists
            String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "title VARCHAR(255), " +
                    "description VARCHAR(1000), " +
                    "type VARCHAR(100), " +
                    "due_date VARCHAR(50), " +
                    "status VARCHAR(50))";
            stmt.execute(createTableSQL);
        }
    }

    @Override
    public void create(Task task) throws SQLException {
        // this is the sql/apache query to insert a new task into the table
        String sql = "INSERT INTO " + TABLE_NAME + " (title, description, type, due_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // setting the parameters for the prepared statement
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getType());
            pstmt.setString(4, task.getDueDate());
            pstmt.setString(5, task.getStatus());

            // this should execut the update
            pstmt.executeUpdate();

            // and this should retrive the generated id for the new task
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    task.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Task read(int id) throws SQLException {
        // this is a sql/apache query to read a task by its id
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // this shoudl set the id parameter for the prepared statement
            pstmt.setInt(1, id);

            // this should excute the query and processing the result set
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setType(rs.getString("type"));
                    task.setDueDate(rs.getString("due_date"));
                    task.setStatus(rs.getString("status"));

                    return task;
                }
            }
        }
        return null;
    }

    // here I am updating the task and then allowing the database to do the rest
    @Override
    public void update(Task task) throws SQLException {
        // this is the sql/apache query to update an existing task
        String sql = "UPDATE " + TABLE_NAME + " SET title = ?, description = ?, type = ?, due_date = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // this is for setting the parameters for the prepared statement
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getType());
            pstmt.setString(4, task.getDueDate());
            pstmt.setString(5, task.getStatus());
            pstmt.setInt(6, task.getId());

            // here I am executing the update
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        // this should be the sql/apache query to delete a task by its id
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // here I am going to set the id parameter for the prepared statement
            pstmt.setInt(1, id);
            // here I am executing the update
            pstmt.executeUpdate();
        }
    }

    // here we are going to findALL of the tasks in the module
    @Override
    public List<Task> findAll() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        // this should be for the sql query to select all tasks
        String sql = "SELECT * FROM " + TABLE_NAME;

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // while we do that
            // we should be processing the result set and adding tasks to the list
            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setType(rs.getString("type"));
                task.setDueDate(rs.getString("due_date"));
                task.setStatus(rs.getString("status"));

                tasks.add(task);
            }
        }

        return tasks;
    }

    //also we need to add find task and then set the due date and let database do the rest
    public List<Task> findTasksDueToday(Date today) throws SQLException {
        List<Task> dueTasks = new ArrayList<>();
        // here I am formatting the date to match the due_date format in the database
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String todayString = dateFormat.format(today);

        // this would allow me to use sql/apache query to find tasks due today and not completed
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE due_date = ? AND status != 'COMPLETED'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // this shoudl set the date parameter for the prepared statement
            pstmt.setString(1, todayString);

            // and now we need to execute the query and processing the result set
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setType(rs.getString("type"));
                    task.setDueDate(rs.getString("due_date"));
                    task.setStatus(rs.getString("status"));

                    dueTasks.add(task);
                }
            }
        }

        return dueTasks;
    }
}