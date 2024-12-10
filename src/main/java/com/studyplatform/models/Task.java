
package com.studyplatform.models;

public class Task {
    // these are some private fields for task properties
    private int id;
    private String title;
    private String description;
    private String type;
    private String dueDate;
    private String status;

    // default constructor for task
    public Task() {}

    // parameterize constructor to initialize task properties
    public Task(String title, String description, String type, String dueDate, String status) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.dueDate = dueDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
