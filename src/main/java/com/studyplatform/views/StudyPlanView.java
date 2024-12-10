package com.studyplatform.views;

import com.studyplatform.controllers.StudyPlanController;
import com.studyplatform.models.Coursework;
import com.studyplatform.models.StudyPlan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

// this is the class definition for the study plan view
public class StudyPlanView extends JPanel {
    private StudyPlanController studyPlanController;
    private DefaultListModel<String> courseListModel;
    private DefaultListModel<String> studyPlanListModel;
    private JList<String> courseList;
    private JList<String> studyPlanList;
    private JTable courseworkTable;
    private DefaultTableModel courseworkTableModel;

    // this is the study plan view contoller
    public StudyPlanView(StudyPlanController studyPlanController) {
        this.studyPlanController = studyPlanController;
        initializeComponents();
        loadExistingCourses();
    }

    // here we are initializing/creating the components for the class
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Study Plan Management"));

        // these are all the course list
        courseListModel = new DefaultListModel<>();
        courseList = new JList<>(courseListModel);
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane courseScrollPane = new JScrollPane(courseList);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Courses"));
        leftPanel.add(courseScrollPane, BorderLayout.CENTER);

        // and here we are making the course management buttons
        JPanel courseButtonPanel = new JPanel(new GridLayout(1, 2));
        JButton addCourseButton = new JButton("Add Course");
        addCourseButton.addActionListener(e -> addCourse());
        JButton deleteCourseButton = new JButton("Delete Course");
        deleteCourseButton.addActionListener(e -> deleteCourse());
        courseButtonPanel.add(addCourseButton);
        courseButtonPanel.add(deleteCourseButton);
        leftPanel.add(courseButtonPanel, BorderLayout.SOUTH);

        // here we added the study plan list
        studyPlanListModel = new DefaultListModel<>();
        studyPlanList = new JList<>(studyPlanListModel);
        JScrollPane studyPlanScrollPane = new JScrollPane(studyPlanList);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Study Plans"));
        rightPanel.add(studyPlanScrollPane, BorderLayout.CENTER);

        // and here we need the study plan management buttons
        JPanel studyPlanButtonPanel = new JPanel(new GridLayout(1, 3));
        JButton addStudyPlanButton = new JButton("Add Study Plan");
        addStudyPlanButton.addActionListener(e -> addStudyPlan());
        JButton viewStudyPlanButton = new JButton("View Study Plan");
        viewStudyPlanButton.addActionListener(e -> viewStudyPlan());
        JButton deleteStudyPlanButton = new JButton("Delete Study Plan");
        deleteStudyPlanButton.addActionListener(e -> deleteStudyPlan());
        studyPlanButtonPanel.add(addStudyPlanButton);
        studyPlanButtonPanel.add(viewStudyPlanButton);
        studyPlanButtonPanel.add(deleteStudyPlanButton);
        rightPanel.add(studyPlanButtonPanel, BorderLayout.SOUTH);

        // this will include the course Selection Listener
        courseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateStudyPlanList();
            }
        });

        // this will be the split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);
    }

    // this will add the courses
    private void addCourse() {
        String courseName = JOptionPane.showInputDialog(this, "Enter course name:");
        if (courseName != null && !courseName.trim().isEmpty()) {
            studyPlanController.addCourse(courseName);
            courseListModel.addElement(courseName);
        }
    }

    // this will delete those courses
    private void deleteCourse() {
        String selectedCourse = courseList.getSelectedValue();
        if (selectedCourse != null) {
            courseListModel.removeElement(selectedCourse);
            studyPlanController.getStudyPlansForCourse(selectedCourse).clear();
        }
    }

    // this will add the selteched course to the study plan
    private void addStudyPlan() {
        String selectedCourse = courseList.getSelectedValue();
        if (selectedCourse != null) {
            String studyPlanName = JOptionPane.showInputDialog(this, "Enter study plan name:");
            if (studyPlanName != null && !studyPlanName.trim().isEmpty()) {
                studyPlanController.createStudyPlan(selectedCourse, studyPlanName);
                updateStudyPlanList();
            }
        }
    }

    // here using this function you can view the study plan
    private void viewStudyPlan() {
        String selectedCourse = courseList.getSelectedValue();
        String selectedStudyPlan = studyPlanList.getSelectedValue();
        
        if (selectedCourse != null && selectedStudyPlan != null) {
            List<StudyPlan> coursePlans = studyPlanController.getStudyPlansForCourse(selectedCourse);
            StudyPlan targetPlan = coursePlans.stream()
                    .filter(sp -> sp.getName().equals(selectedStudyPlan))
                    .findFirst()
                    .orElse(null);
            
            if (targetPlan != null) {
                showCourseworkManagementDialog(targetPlan);
            }
        }
    }

    // here you can delete the same study plan
    private void deleteStudyPlan() {
        String selectedCourse = courseList.getSelectedValue();
        String selectedStudyPlan = studyPlanList.getSelectedValue();

        if (selectedCourse != null && selectedStudyPlan != null) {
            studyPlanController.deleteStudyPlan(selectedCourse, selectedStudyPlan);
            updateStudyPlanList();
        }
    }

    // here we can update that same study plan
    private void updateStudyPlanList() {
        String selectedCourse = courseList.getSelectedValue();
        studyPlanListModel.clear();
        
        if (selectedCourse != null) {
            List<StudyPlan> plans = studyPlanController.getStudyPlansForCourse(selectedCourse);
            for (StudyPlan sp : plans) {
                studyPlanListModel.addElement(sp.getName());
            }
        }
    }

    // now we can show all the course work using this fucntion
    private void showCourseworkManagementDialog(StudyPlan studyPlan) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                     "Manage Coursework for " + studyPlan.getName(), 
                                     true);
        dialog.setSize(600, 400);

        // this is all the coursework table
        String[] columnNames = {"Name", "Details", "Due Date", "Status"};
        courseworkTableModel = new DefaultTableModel(columnNames, 0);
        courseworkTable = new JTable(courseworkTableModel);
        JScrollPane tableScrollPane = new JScrollPane(courseworkTable);

        // this will add things to the table
        refreshCourseworkTable(studyPlan);

        // these are just the button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        JButton addButton = new JButton("Add Coursework");
        addButton.addActionListener(e -> addCoursework(studyPlan));
        JButton editButton = new JButton("Edit Coursework");
        editButton.addActionListener(e -> editCoursework(studyPlan));
        JButton deleteButton = new JButton("Delete Coursework");
        deleteButton.addActionListener(e -> deleteCoursework(studyPlan));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // this is for the layout
        dialog.setLayout(new BorderLayout());
        dialog.add(tableScrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // this will refresh the coursework table from the study plan
    private void refreshCourseworkTable(StudyPlan studyPlan) {
        courseworkTableModel.setRowCount(0);

        for (Coursework cw : studyPlan.getCourseworkList()) {
            courseworkTableModel.addRow(new Object[]{
                cw.getName(),
                cw.getDetails() == null ? "Not set" : cw.getDetails(),
                cw.getDueDate() == null ? "Not set" : cw.getDueDate(),
                cw.getStatus() == null ? "Not set" : cw.getStatus()
            });
        }
    }

    // this function should allow me to add the course work
    private void addCoursework(StudyPlan studyPlan) {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        JTextField nameField = new JTextField();
        JTextField detailsField = new JTextField();
        JTextField dueDateField = new JTextField();
        String[] statusTypes = {"Not Started", "In Progress", "Completed"};
        JComboBox<String> statusComboBox = new JComboBox<>(statusTypes);

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Details:"));
        inputPanel.add(detailsField);
        inputPanel.add(new JLabel("Due Date (MM-dd-yyyy):"));
        inputPanel.add(dueDateField);
        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(statusComboBox);

        int result = JOptionPane.showConfirmDialog(
                this, inputPanel, "Add Coursework", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // this is for Validate inputs
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Coursework name cannot be empty.");
                    return;
                }

                String dueDate = dueDateField.getText().trim();

                if (!dueDate.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                        sdf.setLenient(false);  // Strict date parsing
                        sdf.parse(dueDate);
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM-dd-yyyy.");
                        return;
                    }
                }

                // this should allow me to create coursework
                Coursework coursework = new Coursework();
                coursework.setName(name);
                coursework.setDetails(detailsField.getText().trim());
                coursework.setDueDate(dueDate);
                coursework.setStatus((String) statusComboBox.getSelectedItem());

                // and her we can add to study plan via controller
                studyPlanController.addCourseworkToStudyPlan(studyPlan, coursework);

                // this should simply refresh table
                refreshCourseworkTable(studyPlan);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding coursework: " + ex.getMessage());
            }
        }
    }

    // here we should be able to edit the coursework
    private void editCoursework(StudyPlan studyPlan) {
        int selectedRow = courseworkTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a coursework to edit.");
            return;
        }

        // we should ne able to get the selected coursework
        Coursework selectedCoursework = studyPlan.getCourseworkList().get(selectedRow);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        JTextField nameField = new JTextField(selectedCoursework.getName());
        JTextField detailsField = new JTextField(selectedCoursework.getDetails() == null ? "" : selectedCoursework.getDetails());
        
        // and then format due date if exists
        JTextField dueDateField = new JTextField();
        if (selectedCoursework.getDueDate() != null) {
            dueDateField.setText(new SimpleDateFormat("MM-dd-yyyy").format(selectedCoursework.getDueDate()));
        }

        String[] statusTypes = {"Not Started", "In Progress", "Completed"};
        JComboBox<String> statusComboBox = new JComboBox<>(statusTypes);
        statusComboBox.setSelectedItem(selectedCoursework.getStatus());

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Details:"));
        inputPanel.add(detailsField);
        inputPanel.add(new JLabel("Due Date (MM-dd-yyyy):"));
        inputPanel.add(dueDateField);
        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(statusComboBox);

        int result = JOptionPane.showConfirmDialog(
                this, inputPanel, "Edit Coursework", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // this will validate inputs
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Coursework name cannot be empty.");
                    return;
                }

                // and then parse due date
                String dueDate = dueDateField.getText().trim();

                if (!dueDate.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                        sdf.setLenient(false);  // Strict date parsing
                        sdf.parse(dueDate);
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM-dd-yyyy.");
                        return;
                    }
                }

                // this should update coursework
                selectedCoursework.setName(name);
                selectedCoursework.setDetails(detailsField.getText().trim());
                selectedCoursework.setDueDate(dueDate);
                selectedCoursework.setStatus((String) statusComboBox.getSelectedItem());

                // and then update via controller
                studyPlanController.updateCoursework(studyPlan, selectedCoursework);

                // then Refresh table entierly
                refreshCourseworkTable(studyPlan);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating coursework: " + ex.getMessage());
            }
        }
    }

    // this should delete the course work
    private void deleteCoursework(StudyPlan studyPlan) {
        int selectedRow = courseworkTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a coursework to delete.");
            return;
        }

        // and here we get the selected coursework
        Coursework selectedCoursework = studyPlan.getCourseworkList().get(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(
                this, 
                "Are you sure you want to delete this coursework?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Delete via controller
                studyPlanController.removeCourseworkFromStudyPlan(studyPlan, selectedCoursework);

                // Refresh table
                refreshCourseworkTable(studyPlan);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting coursework: " + ex.getMessage());
            }
        }
    }

    // and after we add this method to load existing courses when the view is created
    public void loadExistingCourses() {
        try {
            // then fetch courses from the database
            List<String> courses = studyPlanController.getAllCourses();
            courseListModel.clear();
            for (String course : courses) {
                courseListModel.addElement(course);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + ex.getMessage());
        }
    }
}
