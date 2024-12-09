package com.studyplatform.controllers;

import com.studyplatform.models.Coursework;
import com.studyplatform.models.StudyPlan;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyPlanController {
    private Map<String, List<StudyPlan>> studyPlans;

    public StudyPlanController() {
        this.studyPlans = new HashMap<>();
    }

    public StudyPlan createStudyPlan(String course, String name) {
        if (course == null || course.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Course and name cannot be empty.");
        }

        StudyPlan studyPlan = new StudyPlan(course, name);
        studyPlans.computeIfAbsent(course, k -> new java.util.ArrayList<>()).add(studyPlan);
        return studyPlan;
    }

    public void deleteStudyPlan(String course, String studyPlanName) {
        if (studyPlans.containsKey(course)) {
            studyPlans.get(course).removeIf(sp -> sp.getName().equals(studyPlanName));
        }
    }

    public List<StudyPlan> getStudyPlansForCourse(String course) {
        return studyPlans.getOrDefault(course, java.util.Collections.emptyList());
    }

    public void addCourseworkToStudyPlan(String course, String studyPlanName, 
                                         String courseworkName, String details, 
                                         Date dueDate, String status) {
        List<StudyPlan> coursePlans = studyPlans.get(course);
        if (coursePlans != null) {
            StudyPlan targetPlan = coursePlans.stream()
                    .filter(sp -> sp.getName().equals(studyPlanName))
                    .findFirst()
                    .orElse(null);
            
            if (targetPlan != null) {
                Coursework coursework = new Coursework(courseworkName, details, dueDate, status);
                targetPlan.addCoursework(coursework);
            }
        }
    }

    public void updateCoursework(String course, String studyPlanName, 
                                 String courseworkName, String newName, 
                                 String newDetails, Date newDueDate, 
                                 String newStatus) {
        List<StudyPlan> coursePlans = studyPlans.get(course);
        if (coursePlans != null) {
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
                }
            }
        }
    }

    public void deleteCoursework(String course, String studyPlanName, String courseworkName) {
        List<StudyPlan> coursePlans = studyPlans.get(course);
        if (coursePlans != null) {
            StudyPlan targetPlan = coursePlans.stream()
                    .filter(sp -> sp.getName().equals(studyPlanName))
                    .findFirst()
                    .orElse(null);
            
            if (targetPlan != null) {
                targetPlan.getCourseworkList().removeIf(cw -> cw.getName().equals(courseworkName));
            }
        }
    }

    public Map<String, List<StudyPlan>> getAllStudyPlans() {
        return new HashMap<>(studyPlans);
    }
}
