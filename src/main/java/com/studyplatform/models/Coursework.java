
package com.studyplatform.models;

public class Coursework {
    // private fields for coursework properties
    private int id;
    private String name;
    private String details;
    private String dueDate;
    private String status;

    // default constructor
    public Coursework() {}

    // here we are parametrizing constructor to create coursework properties
    public Coursework(String name, String details, String dueDate, String status) {
        this.name = name;
        this.details = details;
        this.dueDate = dueDate;
        this.status = status;
    }

    // getter for id
    public int getId() { return id; }
    // setter for id
    public void setId(int id) { this.id = id; }

    // getter for name
    public String getName() { return name; }
    // setter for name
    public void setName(String name) { this.name = name; }

    // getter for details
    public String getDetails() { return details; }
    // setter for details
    public void setDetails(String details) { this.details = details; }

    // getter for dueDate
    public String getDueDate() { return dueDate; }
    // setter for dueDate
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    // getter for status
    public String getStatus() { return status; }
    // setter for status
    public void setStatus(String status) { this.status = status; }
}