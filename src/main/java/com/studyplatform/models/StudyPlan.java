
package com.studyplatform.models;

import java.util.ArrayList;
import java.util.List;

public class StudyPlan {
    // these are some private fields for study plan properties
    private int id;
    private String course;
    private String name;
    private List<Coursework> courseworkList;

    // here is my default constructor initializing coursework list
    public StudyPlan() {
        this.courseworkList = new ArrayList<>();
    }

    // and like the others we would need to parameterize constructor to initialize
    // course, name, and coursework list
    public StudyPlan(String course, String name) {
        this.course = course;
        this.name = name;
        this.courseworkList = new ArrayList<>();
    }

    // getter for id
    public int getId() { return id; }
    // setter for id
    public void setId(int id) { this.id = id; }

    // getter for course
    public String getCourse() { return course; }
    // setter for course
    public void setCourse(String course) { this.course = course; }

    // getter for name
    public String getName() { return name; }
    // setter for name
    public void setName(String name) { this.name = name; }

    // getter for details (returns name)
    public String getDetails() { return name; }

    // getter for coursework list
    public List<Coursework> getCourseworkList() { return courseworkList; }

    // this method should be able to add coursework to the list
    public void addCoursework(Coursework coursework) {
        courseworkList.add(coursework);
    }

    // and right here we would need a method to remove coursework from the list
    public void removeCoursework(Coursework coursework) {
        courseworkList.remove(coursework);
    }

    // this is a really simple but import method
    // it would be used for updating existing coursework in the list
    public void updateCoursework(Coursework coursework) {
        // this would be to find the existing coursework and update it
        for (int i = 0; i < courseworkList.size(); i++) {
            if (courseworkList.get(i).getId() == coursework.getId()) {
                courseworkList.set(i, coursework);
                break;
            }
        }
    }
}