package com.studyplatform.controllers;

import com.studyplatform.models.Task;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TaskController {
    private List<Task> tasks;

    public TaskController() {
        this.tasks = new ArrayList<>();
    }

    public Task createTask(String title, String description, String type, String dueDateStr, String status) throws ParseException {
        Task task = new Task(title, description, type, dueDateStr, status);
        tasks.add(task);
        return task;
    }

    public void updateTask(Task task, String title, String description, String type, String dueDateStr, String status) throws ParseException {
        task.setTitle(title);
        task.setDescription(description);
        task.setType(type);
        task.setDueDate(dueDateStr);
        task.setStatus(status);
    }

    public void deleteTask(Task task) {
        tasks.remove(task);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public Task getTaskByTitle(String title) {
        return tasks.stream()
                .filter(task -> task.getTitle().equals(title))
                .findFirst()
                .orElse(null);
    }
}
