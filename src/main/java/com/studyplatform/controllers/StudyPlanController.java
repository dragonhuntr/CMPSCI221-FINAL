
package com.studyplatform.controllers;

import com.studyplatform.models.Coursework;
import com.studyplatform.models.StudyPlan;
import com.studyplatform.dao.StudyPlanDAO;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;

public class StudyPlanController {
    private StudyPlanDAO studyPlanDAO;

    // here I am going to have a constructor initializes the StudyPlanDAO
    // and creates the study plans table if it doesn't exist
    public StudyPlanController() {
        this.studyPlanDAO = new StudyPlanDAO();
        try {
            this.studyPlanDAO.createTable();
        } catch (SQLException e) {
            System.err.println("Error creating study plans table: " + e.getMessage());
        }
    }

    // this should add a new course to the study plan
    public void addCourse(String name) {
        try {
            studyPlanDAO.addCourse(name);
        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
        }
    }

    // here I should be able to retrieves all courses from the study plan
    public List<String> getAllCourses() {
        try {
            return studyPlanDAO.getAllCourses();
        } catch (SQLException e) {
            System.err.println("Error retrieving courses: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    // here we should be able to creates a new study plan
    // for a given course and name
    public StudyPlan createStudyPlan(String course, String name) {
        if (course == null || course.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Course and name cannot be empty.");
        }

        try {
            // this should create the study plan
            StudyPlan studyPlan = new StudyPlan(course, name);
            studyPlanDAO.create(studyPlan);
            return studyPlan;
        } catch (SQLException e) {
            System.err.println("Error creating study plan: " + e.getMessage());
            return null;
        }
    }

    // We would also need a feature to delete the study plan
    // So here, we should be able to deletes a study plan
    // for a given course and study plan name
    public void deleteStudyPlan(String course, String studyPlanName) {
        try {
            studyPlanDAO.deleteStudyPlan(course, studyPlanName);
        } catch (SQLException e) {
            System.err.println("Error deleting study plan: " + e.getMessage());
        }
    }

    // this function should retrieve all study plans for a given course
    public List<StudyPlan> getStudyPlansForCourse(String course) {
        try {
            return studyPlanDAO.getStudyPlansForCourse(course);
        } catch (SQLException e) {
            System.err.println("Error retrieving study plans for course: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    // this is a simple function to add coursework
    // adds coursework to a specific study plan
    public void addCourseworkToStudyPlan(StudyPlan studyPlan, Coursework coursework) {
        try {
            studyPlanDAO.addCourseworkToStudyPlan(studyPlan, coursework);
        } catch (SQLException e) {
            System.err.println("Error adding coursework to study plan: " + e.getMessage());
        }
    }

    // this should be able to updates coursework in a specific study plan
    public void updateCoursework(StudyPlan studyPlan, Coursework coursework) {
        try {
            studyPlanDAO.updateCoursework(studyPlan, coursework);
        } catch (SQLException e) {
            System.err.println("Error updating coursework: " + e.getMessage());
        }
    }

    // we should also be able to remove coursework
    // so here we remove coursework from a specific study plan
    public void removeCourseworkFromStudyPlan(StudyPlan studyPlan, Coursework coursework) {
        try {
            studyPlanDAO.removeCourseworkFromStudyPlan(studyPlan, coursework);
        } catch (SQLException e) {
            System.err.println("Error removing coursework from study plan: " + e.getMessage());
        }
    }

    // this function should be able to deny method to
    // add coursework to a study plan using string parameters
    public void addCourseworkToStudyPlan(String course, String studyPlanName,
                                         String courseworkName, String details,
                                         String dueDate, String status) {
        try {
            List<StudyPlan> coursePlans = getStudyPlansForCourse(course);
            StudyPlan targetPlan = coursePlans.stream()
                    .filter(sp -> sp.getName().equals(studyPlanName))
                    .findFirst()
                    .orElse(null);

            if (targetPlan != null) {
                Coursework coursework = new Coursework(courseworkName, details, dueDate, status);
                addCourseworkToStudyPlan(targetPlan, coursework);
            }
        } catch (Exception e) {
            System.err.println("Error adding coursework to study plan: " + e.getMessage());
        }
    }

    // this should be able to update that denied method to
    // update coursework in a study plan using string parameters
    public void updateCoursework(String course, String studyPlanName,
                                 String courseworkName, String newName,
                                 String newDetails, String newDueDate,
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

                    updateCoursework(targetPlan, targetCoursework);
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating coursework: " + e.getMessage());
        }
    }

    // this function should  be able to deny method to
    // delete coursework from a study plan using string parameters
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
                    removeCourseworkFromStudyPlan(targetPlan, targetCoursework);
                }
            }
        } catch (Exception e) {
            System.err.println("Error deleting coursework: " + e.getMessage());
        }
    }

    // this should retrieve all study plans grouped by course
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
