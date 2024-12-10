package com.studyplatform.controllers;

import com.studyplatform.models.Task;
import com.studyplatform.dao.TaskDAO;
import java.text.ParseException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// this should be able to manage operations for tasks.
public class TaskController {
    private TaskDAO taskDAO;

    // this should be able to initialize dao and
    // create the tasks table during initialization.
    public TaskController() {
        this.taskDAO = new TaskDAO();
        try {
            this.taskDAO.createTable();
        } catch (SQLException e) {
            System.err.println("Error creating tasks table: " + e.getMessage());
        }
    }

    // this should be able to create a new task and
    // store it in the database.
    public Task createTask(String title, String description, String type, String dueDateStr, String status) throws ParseException {
        try {
            Task task = new Task(title, description, type, dueDateStr, status);
            taskDAO.create(task);
            return task;
        } catch (SQLException e) {
            System.err.println("Error creating task: " + e.getMessage());
            return null;
        }
    }

    //this should be able to update an
    // existing task with new details.
    public void updateTask(Task task, String title, String description, String type, String dueDateStr, String status) throws ParseException {
        try {
            task.setTitle(title);
            task.setDescription(description);
            task.setType(type);
            task.setDueDate(dueDateStr);
            task.setStatus(status);
            
            taskDAO.update(task);
        } catch (SQLException e) {
            System.err.println("Error updating task: " + e.getMessage());
        }
    }

    // delete a task by its ID.
    public void deleteTask(Task task) {
        try {
            taskDAO.delete(task.getId());
        } catch (SQLException e) {
            System.err.println("Error deleting task: " + e.getMessage());
        }
    }

    // this should just get all tasks from the database
    public List<Task> getAllTasks() {
        try {
            return taskDAO.findAll();
        } catch (SQLException e) {
            System.err.println("Error retrieving tasks: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // find a task by its title.
    public Task getTaskByTitle(String title) {
        try {
            return taskDAO.findAll().stream()
                    .filter(task -> task.getTitle().equals(title))
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            System.err.println("Error finding task by title: " + e.getMessage());
            return null;
        }
    }
}
