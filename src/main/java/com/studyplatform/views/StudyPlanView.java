package com.studyplatform.views;

import com.studyplatform.controllers.StudyPlanController;
import com.studyplatform.models.Coursework;
import com.studyplatform.models.StudyPlan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StudyPlanView extends JPanel {
    private StudyPlanController studyPlanController;
    private DefaultListModel<String> courseListModel;
    private DefaultListModel<String> studyPlanListModel;
    private JList<String> courseList;
    private JList<String> studyPlanList;
    private JTable courseworkTable;
    private DefaultTableModel courseworkTableModel;

    public StudyPlanView(StudyPlanController studyPlanController) {
        this.studyPlanController = studyPlanController;
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Study Plan Management"));

        // Course List
        courseListModel = new DefaultListModel<>();
        courseList = new JList<>(courseListModel);
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane courseScrollPane = new JScrollPane(courseList);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Courses"));
        leftPanel.add(courseScrollPane, BorderLayout.CENTER);

        // Course Management Buttons
        JPanel courseButtonPanel = new JPanel(new GridLayout(1, 2));
        JButton addCourseButton = new JButton("Add Course");
        addCourseButton.addActionListener(e -> addCourse());
        JButton deleteCourseButton = new JButton("Delete Course");
        deleteCourseButton.addActionListener(e -> deleteCourse());
        courseButtonPanel.add(addCourseButton);
        courseButtonPanel.add(deleteCourseButton);
        leftPanel.add(courseButtonPanel, BorderLayout.SOUTH);

        // Study Plan List
        studyPlanListModel = new DefaultListModel<>();
        studyPlanList = new JList<>(studyPlanListModel);
        JScrollPane studyPlanScrollPane = new JScrollPane(studyPlanList);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Study Plans"));
        rightPanel.add(studyPlanScrollPane, BorderLayout.CENTER);

        // Study Plan Management Buttons
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

        // Course Selection Listener
        courseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateStudyPlanList();
            }
        });

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);
    }

    private void addCourse() {
        String courseName = JOptionPane.showInputDialog(this, "Enter course name:");
        if (courseName != null && !courseName.trim().isEmpty()) {
            courseListModel.addElement(courseName);
        }
    }

    private void deleteCourse() {
        String selectedCourse = courseList.getSelectedValue();
        if (selectedCourse != null) {
            courseListModel.removeElement(selectedCourse);
            studyPlanController.getStudyPlansForCourse(selectedCourse).clear();
        }
    }

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

    private void deleteStudyPlan() {
        String selectedCourse = courseList.getSelectedValue();
        String selectedStudyPlan = studyPlanList.getSelectedValue();

        if (selectedCourse != null && selectedStudyPlan != null) {
            studyPlanController.deleteStudyPlan(selectedCourse, selectedStudyPlan);
            updateStudyPlanList();
        }
    }

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

    private void showCourseworkManagementDialog(StudyPlan studyPlan) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                     "Manage Coursework for " + studyPlan.getName(), 
                                     true);
        dialog.setSize(600, 400);

        // Coursework Table
        String[] columnNames = {"Name", "Details", "Due Date", "Status"};
        courseworkTableModel = new DefaultTableModel(columnNames, 0);
        courseworkTable = new JTable(courseworkTableModel);
        JScrollPane tableScrollPane = new JScrollPane(courseworkTable);

        // Populate table
        refreshCourseworkTable(studyPlan);

        // Button Panel
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

        // Layout
        dialog.setLayout(new BorderLayout());
        dialog.add(tableScrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void refreshCourseworkTable(StudyPlan studyPlan) {
        courseworkTableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        
        for (Coursework cw : studyPlan.getCourseworkList()) {
            courseworkTableModel.addRow(new Object[]{
                cw.getName(),
                cw.getDetails() == null ? "Not set" : cw.getDetails(),
                cw.getDueDate() == null ? "Not set" : sdf.format(cw.getDueDate()),
                cw.getStatus() == null ? "Not set" : cw.getStatus()
            });
        }
    }

    private void addCoursework(StudyPlan studyPlan) {
        JPanel inputPanel = createCourseworkInputPanel(null);
        
        int result = JOptionPane.showConfirmDialog(
                this, inputPanel, "Add Coursework", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = ((JTextField)inputPanel.getComponent(1)).getText().trim();
                String details = ((JTextField)inputPanel.getComponent(3)).getText().trim();
                String dueDateStr = ((JTextField)inputPanel.getComponent(5)).getText().trim();
                String status = (String)((JComboBox<?>)inputPanel.getComponent(7)).getSelectedItem();

                Date dueDate = null;
                if (!dueDateStr.isEmpty()) {
                    dueDate = new SimpleDateFormat("MM-dd-yyyy").parse(dueDateStr);
                }

                studyPlanController.addCourseworkToStudyPlan(
                    studyPlan.getCourse(), 
                    studyPlan.getName(), 
                    name, 
                    details, 
                    dueDate, 
                    status
                );

                refreshCourseworkTable(studyPlan);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM-dd-yyyy.");
            }
        }
    }

    private void editCoursework(StudyPlan studyPlan) {
        int selectedRow = courseworkTable.getSelectedRow();
        if (selectedRow >= 0) {
            Coursework selectedCoursework = studyPlan.getCourseworkList().get(selectedRow);

            JPanel inputPanel = createCourseworkInputPanel(selectedCoursework);
            
            int result = JOptionPane.showConfirmDialog(
                    this, inputPanel, "Edit Coursework", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String name = ((JTextField)inputPanel.getComponent(1)).getText().trim();
                    String details = ((JTextField)inputPanel.getComponent(3)).getText().trim();
                    String dueDateStr = ((JTextField)inputPanel.getComponent(5)).getText().trim();
                    String status = (String)((JComboBox<?>)inputPanel.getComponent(7)).getSelectedItem();

                    Date dueDate = null;
                    if (!dueDateStr.isEmpty()) {
                        dueDate = new SimpleDateFormat("MM-dd-yyyy").parse(dueDateStr);
                    }

                    studyPlanController.updateCoursework(
                        studyPlan.getCourse(), 
                        studyPlan.getName(), 
                        selectedCoursework.getName(), 
                        name, 
                        details, 
                        dueDate, 
                        status
                    );

                    refreshCourseworkTable(studyPlan);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM-dd-yyyy.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No coursework selected.");
        }
    }

    private void deleteCoursework(StudyPlan studyPlan) {
        int selectedRow = courseworkTable.getSelectedRow();
        if (selectedRow >= 0) {
            Coursework selectedCoursework = studyPlan.getCourseworkList().get(selectedRow);
            
            studyPlanController.deleteCoursework(
                studyPlan.getCourse(), 
                studyPlan.getName(), 
                selectedCoursework.getName()
            );

            refreshCourseworkTable(studyPlan);
        } else {
            JOptionPane.showMessageDialog(this, "No coursework selected.");
        }
    }

    private JPanel createCourseworkInputPanel(Coursework coursework) {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        
        // Name
        inputPanel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField(coursework != null ? coursework.getName() : "");
        inputPanel.add(nameField);

        // Details
        inputPanel.add(new JLabel("Details:"));
        JTextField detailsField = new JTextField(coursework != null ? 
                (coursework.getDetails() != null ? coursework.getDetails() : "") : "");
        inputPanel.add(detailsField);

        // Due Date
        inputPanel.add(new JLabel("Due Date (MM-dd-yyyy):"));
        JTextField dueDateField = new JTextField(coursework != null && coursework.getDueDate() != null ? 
                new SimpleDateFormat("MM-dd-yyyy").format(coursework.getDueDate()) : "");
        inputPanel.add(dueDateField);

        // Status
        inputPanel.add(new JLabel("Status:"));
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Not Started", "In Progress", "Completed"});
        if (coursework != null && coursework.getStatus() != null) {
            statusComboBox.setSelectedItem(coursework.getStatus());
        }
        inputPanel.add(statusComboBox);

        return inputPanel;
    }
}
