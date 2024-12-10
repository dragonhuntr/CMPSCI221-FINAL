
package com.studyplatform.views;

import com.studyplatform.controllers.TutorController;
import com.studyplatform.models.Tutor;

import javax.swing.*;
        import javax.swing.table.DefaultTableModel;
import java.awt.*;
        import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class TutorView extends JPanel {
    // these are the private fields for tutor view components
    private TutorController tutorController;
    private JComboBox<String> classDropdown;
    private DefaultTableModel sessionTableModel;
    private JTable sessionTable;

    // and these are the constructor to initialize tutor controller and components
    public TutorView(TutorController tutorController) {
        this.tutorController = tutorController;
        initializeComponents();
    }

    // and this is the method to initialize components
    private void initializeComponents() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        classDropdown = new JComboBox<>();
        classDropdown.addItem("Select a Class");

        // here we can add class dropdown with unique tutor classes
        List<String> uniqueClasses = tutorController.getUniqueTutorClasses();
        uniqueClasses.forEach(classDropdown::addItem);

        classDropdown.addActionListener(e -> updateSessionTable());
        topPanel.add(new JLabel("Class:"));
        topPanel.add(classDropdown);
        add(topPanel, BorderLayout.NORTH);

        // this is to set up session table with column names
        String[] columnNames = {"Name", "Available Date", "Location", "Scheduled"};
        sessionTableModel = new DefaultTableModel(columnNames, 0);
        sessionTable = new JTable(sessionTableModel);
        JScrollPane tableScrollPane = new JScrollPane(sessionTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // this will create button panel with schedule and volunteer buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton scheduleButton = new JButton("Schedule Session");
        scheduleButton.addActionListener(e -> scheduleSession());
        buttonPanel.add(scheduleButton);

        JButton volunteerButton = new JButton("Volunteer as Tutor");
        volunteerButton.addActionListener(e -> volunteerAsTutor());
        buttonPanel.add(volunteerButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // and this method will update session table based on selected class
    private void updateSessionTable() {
        sessionTableModel.setRowCount(0);

        String selectedClass = (String) classDropdown.getSelectedItem();
        if (selectedClass == null || selectedClass.equals("Select a Class")) {
            return;
        }

        // through that we can add session table with tutors of the selected class
        List<Tutor> tutors = tutorController.getTutorsByClass(selectedClass);
        for (Tutor tutor : tutors) {
            sessionTableModel.addRow(new Object[]{
                    tutor.getName(),
                    tutor.getAvailableDate(),
                    tutor.getLocation(),
                    tutor.isScheduled() ? "Yes" : "No"
            });
        }
    }

    // and this method  should schedule a session with a selected tutor
    private void scheduleSession() {
        int selectedRow = sessionTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "No session selected.");
            return;
        }

        String tutorName = (String) sessionTableModel.getValueAt(selectedRow, 0);

        try {
            Tutor tutor = tutorController.findTutorByName(tutorName);
            if (tutor != null) {
                if (tutor.isScheduled()) {
                    JOptionPane.showMessageDialog(this, "This session is already scheduled.");
                    return;
                }

                tutorController.scheduleTutorSession(tutorName);

                updateSessionTable();

                JOptionPane.showMessageDialog(this, "Session scheduled successfully.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error scheduling session: " + e.getMessage());
        }
    }

    // here we use this method to volunteer as a tutor
    private void volunteerAsTutor() {
        // create input panel for tutor details
        JTextField nameField = new JTextField();
        JTextField classField = new JTextField();
        JTextField availableDateField = new JTextField();
        availableDateField.setToolTipText("MM-dd-yyyy");
        JTextField locationField = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Class:"));
        inputPanel.add(classField);
        inputPanel.add(new JLabel("Available Date (MM-dd-yyyy):"));
        inputPanel.add(availableDateField);
        inputPanel.add(new JLabel("Location:"));
        inputPanel.add(locationField);

        int result = JOptionPane.showConfirmDialog(
                this, inputPanel, "Volunteer as Tutor",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String tutorClass = classField.getText().trim();
                String availableDate = availableDateField.getText().trim();
                String location = locationField.getText().trim();

                if (name.isEmpty() || tutorClass.isEmpty() || availableDate.isEmpty() || location.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields must be filled.");
                    return;
                }

                // this is to check date format
                new SimpleDateFormat("MM-dd-yyyy").parse(availableDate);

                tutorController.addTutor(name, tutorClass, availableDate, location);

                // and then update class dropdown with new tutor classes
                List<String> classes = tutorController.getUniqueTutorClasses();
                classDropdown.removeAllItems();
                classDropdown.addItem("Select a Class");
                classes.forEach(classDropdown::addItem);

                JOptionPane.showMessageDialog(this, "Tutor added successfully!");
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM-dd-yyyy.");
            }
        }
    }
}