package com.studyplatform.controllers;

import com.studyplatform.models.Coursework;
import com.studyplatform.models.StudyPlan;
import com.studyplatform.dao.StudyPlanDAO;
import com.studyplatform.dao.CourseworkDAO;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class StudyPlanController {
    private StudyPlanDAO studyPlanDAO;
    private CourseworkDAO courseworkDAO;

    public StudyPlanController() {
        this.studyPlanDAO = new StudyPlanDAO();
        this.courseworkDAO = new CourseworkDAO();
        try {
            this.studyPlanDAO.createTable();
            this.courseworkDAO.createTable();
        } catch (SQLException e) {
            System.err.println("Error creating study plans table: " + e.getMessage());
        }
    }

    public StudyPlan createStudyPlan(String course, String name) {
        if (course == null || course.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Course and name cannot be empty.");
        }

        try {
            StudyPlan studyPlan = new StudyPlan(course, name);
            studyPlanDAO.create(studyPlan);
            return studyPlan;
        } catch (SQLException e) {
            System.err.println("Error creating study plan: " + e.getMessage());
            return null;
        }
    }

    public void deleteStudyPlan(String course, String studyPlanName) {
        try {
            List<StudyPlan> coursePlans = getStudyPlansForCourse(course);
            StudyPlan targetPlan = coursePlans.stream()
                    .filter(sp -> sp.getName().equals(studyPlanName))
                    .findFirst()
                    .orElse(null);
            
            if (targetPlan != null) {
                studyPlanDAO.delete(targetPlan.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error deleting study plan: " + e.getMessage());
        }
    }

    public List<StudyPlan> getStudyPlansForCourse(String course) {
        try {
            return studyPlanDAO.findAll().stream()
                    .filter(sp -> sp.getCourse().equals(course))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error retrieving study plans for course: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    public void addCourseworkToStudyPlan(String course, String studyPlanName, 
                                         String courseworkName, String details, 
                                         Date dueDate, String status) {
        try {
            List<StudyPlan> coursePlans = getStudyPlansForCourse(course);
            StudyPlan targetPlan = coursePlans.stream()
                    .filter(sp -> sp.getName().equals(studyPlanName))
                    .findFirst()
                    .orElse(null);
            
            if (targetPlan != null) {
                Coursework coursework = new Coursework(courseworkName, details, dueDate, status);
                courseworkDAO.create(coursework);
                
                targetPlan.addCoursework(coursework);
                studyPlanDAO.update(targetPlan);
            }
        } catch (SQLException e) {
            System.err.println("Error adding coursework to study plan: " + e.getMessage());
        }
    }

    public void updateCoursework(String course, String studyPlanName, 
                                 String courseworkName, String newName, 
                                 String newDetails, Date newDueDate, 
                                 String newStatus) {
        try {
            List<StudyPlan> coursePlans = getStudyPlansForCourse(course);
            StudyPlan targetPlan = coursePlans.stream()
                    .filter(sp -> sp.getName().equals(studyPlanName))
                    .findFirst()
                    .orElse(null);
            
            if (targetPlan != null) {
                List<Coursework> courseworkList = targetPlan.getCourseworkList();
                Coursework targetCoursework = courseworkList.stream()
                        .filter(cw -> cw.getName().equals(courseworkName))
                        .findFirst()
                        .orElse(null);
                
                if (targetCoursework != null) {
                    targetCoursework.setName(newName);
                    targetCoursework.setDetails(newDetails);
                    targetCoursework.setDueDate(newDueDate);
                    targetCoursework.setStatus(newStatus);
                    
                    courseworkDAO.update(targetCoursework);
                    studyPlanDAO.update(targetPlan);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating coursework: " + e.getMessage());
        }
    }

    public void deleteCoursework(String course, String studyPlanName, String courseworkName) {
        try {
            List<StudyPlan> coursePlans = getStudyPlansForCourse(course);
            StudyPlan targetPlan = coursePlans.stream()
                    .filter(sp -> sp.getName().equals(studyPlanName))
                    .findFirst()
                    .orElse(null);
            
            if (targetPlan != null) {
                Coursework targetCoursework = targetPlan.getCourseworkList().stream()
                        .filter(cw -> cw.getName().equals(courseworkName))
                        .findFirst()
                        .orElse(null);
                
                if (targetCoursework != null) {
                    courseworkDAO.delete(targetCoursework.getId());
                    targetPlan.getCourseworkList().remove(targetCoursework);
                    studyPlanDAO.update(targetPlan);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error deleting coursework: " + e.getMessage());
        }
    }

    public Map<String, List<StudyPlan>> getAllStudyPlans() {
        try {
            List<StudyPlan> allPlans = studyPlanDAO.findAll();
            Map<String, List<StudyPlan>> groupedPlans = new HashMap<>();
            
            for (StudyPlan plan : allPlans) {
                groupedPlans.computeIfAbsent(plan.getCourse(), k -> new java.util.ArrayList<>()).add(plan);
            }
            
            return groupedPlans;
        } catch (SQLException e) {
            System.err.println("Error retrieving all study plans: " + e.getMessage());
            return new HashMap<>();
        }
    }
}
