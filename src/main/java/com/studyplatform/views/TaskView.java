
package com.studyplatform.views;

import com.studyplatform.controllers.TaskController;
import com.studyplatform.models.Task;

import javax.swing.*;
        import javax.swing.table.DefaultTableModel;
import java.awt.*;
        import java.text.ParseException;

public class TaskView extends JPanel {
    // this is the private fields for task view components
    private TaskController taskController;
    private JTextField taskTitleField;
    private JTextField taskDescriptionField;
    private JComboBox<String> taskTypeComboBox;
    private JTextField taskDueDateField;
    private DefaultTableModel taskTableModel;
    private JTable taskTable;

    // these are the constants for task types and status types
    private static final String[] TASK_TYPES = { "Assignment", "Project", "Homework" };
    private static final String[] STATUS_TYPES = { "Not Started", "In Progress", "Completed" };

    // and this is the constructor to create task controller and components
    public TaskView(TaskController taskController) {
        this.taskController = taskController;
        initializeComponents();
    }

    // now this method is to create components
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Task Management"));

        // and here we use task form using GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // these are just for the title
        JLabel titleLabel = new JLabel("Title:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(titleLabel, gbc);

        taskTitleField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(taskTitleField, gbc);

        // these are for the description
        JLabel descriptionLabel = new JLabel("Description:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(descriptionLabel, gbc);

        taskDescriptionField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(taskDescriptionField, gbc);

        // this is for the type
        JLabel typeLabel = new JLabel("Type:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(typeLabel, gbc);

        taskTypeComboBox = new JComboBox<>(TASK_TYPES);
        gbc.gridx = 1;
        formPanel.add(taskTypeComboBox, gbc);

        // now finlly the due date
        JLabel dueDateLabel = new JLabel("Due Date:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(dueDateLabel, gbc);

        taskDueDateField = new JTextField(10);
        taskDueDateField.setToolTipText("MM-dd-yyyy");
        gbc.gridx = 1;
        formPanel.add(taskDueDateField, gbc);

        // this should be to create task button
        JButton createTaskButton = new JButton("Create Task");
        createTaskButton.addActionListener(e -> createTask());
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(createTaskButton, gbc);

        add(formPanel, BorderLayout.NORTH);

        // and then make the task table
        String[] columnNames = { "Title", "Description", "Type", "Due Date", "Status" };
        taskTableModel = new DefaultTableModel(columnNames, 0);
        taskTable = new JTable(taskTableModel);
        JScrollPane taskScrollPane = new JScrollPane(taskTable);

        // these are the task management buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

        JButton editTaskButton = new JButton("Edit Task");
        editTaskButton.addActionListener(e -> editTask());
        buttonPanel.add(editTaskButton);

        JButton deleteTaskButton = new JButton("Delete Task");
        deleteTaskButton.addActionListener(e -> deleteTask());
        buttonPanel.add(deleteTaskButton);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(taskScrollPane, BorderLayout.CENTER);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        add(tablePanel, BorderLayout.CENTER);

        // and the input initial tasks
        refreshTaskTable();
    }

    // this method is to create a new task
    private void createTask() {
        try {
            String title = taskTitleField.getText();
            String description = taskDescriptionField.getText();
            String type = (String) taskTypeComboBox.getSelectedItem();
            String dueDateStr = taskDueDateField.getText();
            String status = "Not Started";

            taskController.createTask(title, description, type, dueDateStr, status);

            // this is to clear input fields
            taskTitleField.setText("");
            taskDescriptionField.setText("");
            taskDueDateField.setText("");

            // and then refresh table
            refreshTaskTable();
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM-dd-yyyy.");
        }
    }

    // this method is to edit an existing task
    private void editTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow >= 0) {
            Task selectedTask = taskController.getAllTasks().get(selectedRow);

            // and then create input panel for editing
            JTextField titleField = new JTextField(selectedTask.getTitle());
            JTextField descriptionField = new JTextField(selectedTask.getDescription());
            JComboBox<String> typeComboBox = new JComboBox<>(TASK_TYPES);
            typeComboBox.setSelectedItem(selectedTask.getType());
            JTextField dueDateField = new JTextField(selectedTask.getDueDate());
            JComboBox<String> statusComboBox = new JComboBox<>(STATUS_TYPES);
            statusComboBox.setSelectedItem(selectedTask.getStatus());

            JPanel editPanel = new JPanel(new GridLayout(5, 2));
            editPanel.add(new JLabel("Title:"));
            editPanel.add(titleField);
            editPanel.add(new JLabel("Description:"));
            editPanel.add(descriptionField);
            editPanel.add(new JLabel("Type:"));
            editPanel.add(typeComboBox);
            editPanel.add(new JLabel("Due Date (MM-dd-yyyy):"));
            editPanel.add(dueDateField);
            editPanel.add(new JLabel("Status:"));
            editPanel.add(statusComboBox);

            int result = JOptionPane.showConfirmDialog(
                    this, editPanel, "Update Task", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    taskController.updateTask(
                            selectedTask,
                            titleField.getText(),
                            descriptionField.getText(),
                            (String) typeComboBox.getSelectedItem(),
                            dueDateField.getText(),
                            (String) statusComboBox.getSelectedItem()
                    );

                    refreshTaskTable();
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM-dd-yyyy.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No task selected.");
        }
    }

    // now this method is to delete a task
    private void deleteTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow >= 0) {
            Task selectedTask = taskController.getAllTasks().get(selectedRow);
            taskController.deleteTask(selectedTask);
            refreshTaskTable();
            JOptionPane.showMessageDialog(this, "Task deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "No task selected.");
        }
    }

    //  and this method is to refresh the task table
    private void refreshTaskTable() {
        // clear existing rows
        taskTableModel.setRowCount(0);

        // and then this for loop adds tasks to the table model
        for (Task task : taskController.getAllTasks()) {
            taskTableModel.addRow(new Object[] {
                    task.getTitle(),
                    task.getDescription(),
                    task.getType(),
                    task.getDueDate(),
                    task.getStatus()
            });
        }
    }
}