package com.studyplatform.models;

import java.util.Date;

public class Coursework {
    private int id;
    private String name;
    private String details;
    private Date dueDate;
    private String status;

    public Coursework() {}

    public Coursework(String name, String details, Date dueDate, String status) {
        this.name = name;
        this.details = details;
        this.dueDate = dueDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
