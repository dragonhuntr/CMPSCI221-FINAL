package com.studyplatform.models;

import java.util.ArrayList;
import java.util.List;

public class StudyPlan {
    private String course;
    private String name;
    private List<Coursework> courseworkList;

    public StudyPlan(String course, String name) {
        this.course = course;
        this.name = name;
        this.courseworkList = new ArrayList<>();
    }

    public String getCourse() { return course; }

    public String getName() { return name; }

    public String getDetails() { return name; }

    public List<Coursework> getCourseworkList() { return courseworkList; }

    public void addCoursework(Coursework coursework) { 
        courseworkList.add(coursework); 
    }
}
