import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Platform extends JFrame {
    private JTextField taskTitleField;
    private JTextField taskDescriptionField;
    private JComboBox<String> taskTypeComboBox;
    private JTextField taskDueDateField;
    private DefaultTableModel taskTableModel;
    private JTable taskTable;
    private JPanel groupListPanel;
    private JComboBox<String> classDropdown;
    private DefaultTableModel sessionTableModel;
    private JTable sessionTable;
    private List<Notification> notifications = new ArrayList<>();
    private DefaultTableModel notificationTableModel;
    private JTabbedPane tabbedPane;
    private List<Task> tasks = new ArrayList<>();
    private Map<String, Group> groups = new HashMap<>();
    private Map<String, List<StudyPlan>> studyPlans = new HashMap<>();
    private List<Tutor> tutors = new ArrayList<>();
    private String[] taskTypes = { "Assignment", "Project", "Homework" };
    private String[] statusTypes = { "Not Started", "In Progress", "Completed" };

    public Platform() {
        setTitle("Study Management Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // Create tabs
        tabbedPane = new JTabbedPane();

        JPanel taskPanel = createTaskPanel();
        tabbedPane.addTab("Task Manager", taskPanel);

        JPanel taskListTab = createTaskListTab();
        tabbedPane.addTab("Task List", taskListTab);

        JPanel groupPanel = createGroupPanel();
        tabbedPane.addTab("Group Management", groupPanel);

        JPanel studyPlanPanel = createStudyPlanPanel();
        tabbedPane.addTab("Study Plans", studyPlanPanel);

        JPanel tutoringPanel = createTutoringPanel();
        tabbedPane.addTab("Tutoring", tutoringPanel);

        JPanel notificationPanel = createNotificationsTab();
        tabbedPane.addTab("Notifications", notificationPanel);

        populateTutors();
        getContentPane().add(tabbedPane);
        startNotificationChecker();
    }

    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Task Management"));

        // Task form using GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Title:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(titleLabel, gbc);

        taskTitleField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(taskTitleField, gbc);

        JLabel descriptionLabel = new JLabel("Description:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(descriptionLabel, gbc);

        taskDescriptionField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(taskDescriptionField, gbc);

        JLabel typeLabel = new JLabel("Type:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(typeLabel, gbc);

        taskTypeComboBox = new JComboBox<>(taskTypes);
        gbc.gridx = 1;
        formPanel.add(taskTypeComboBox, gbc);

        JLabel dueDateLabel = new JLabel("Due Date:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(dueDateLabel, gbc);

        taskDueDateField = new JTextField(10);
        taskDueDateField.setToolTipText("MM-dd-yyyy");
        gbc.gridx = 1;
        formPanel.add(taskDueDateField, gbc);

        JButton createTaskButton = new JButton("Create Task");
        createTaskButton.addActionListener(e -> createTask());
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(createTaskButton, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        return panel;
    }

    private void createTask() {
        String title = taskTitleField.getText();
        String description = taskDescriptionField.getText();
        String type = (String) taskTypeComboBox.getSelectedItem();
        String status = "Incomplete";
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        try {
            Date dueDate = sdf.parse(taskDueDateField.getText());
            Task task = new Task(title, description, type, taskDueDateField.getText(), status);
            tasks.add(task);

            // Add task to the table model
            taskTableModel.addRow(new Object[] { title, description, type, sdf.format(dueDate), status });

            // Clear input fields
            taskTitleField.setText("");
            taskDescriptionField.setText("");
            taskDueDateField.setText("");
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM-dd-yyyy.");
        }
    }

    private void updateTask(Task task, int rowIndex) {
        // Input fields
        JTextField titleField = new JTextField(task.getTitle());
        JTextField descriptionField = new JTextField(task.getDescription());
        JComboBox<String> typeComboBox = new JComboBox<>(taskTypes);
        typeComboBox.setSelectedItem(task.getType());

        String dueDateString = task.getDueDate();
        JTextField dueDateField = new JTextField(dueDateString);

        JComboBox<String> statusComboBox = new JComboBox<>(statusTypes);
        statusComboBox.setSelectedItem(task.getStatus());

        // Create input panel
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
                // Parse due date if not empty
                Date dueDate = null;
                if (!dueDateField.getText().trim().isEmpty()) {
                    dueDate = new SimpleDateFormat("MM-dd-yyyy").parse(dueDateField.getText());
                }

                // Update task properties
                task.setTitle(titleField.getText().trim());
                task.setDescription(descriptionField.getText().trim());
                task.setType((String) typeComboBox.getSelectedItem());
                task.setDueDate(dueDate.toString());
                task.setStatus((String) statusComboBox.getSelectedItem());

                // Update table model
                taskTableModel.setValueAt(task.getTitle(), rowIndex, 0);
                taskTableModel.setValueAt(task.getDescription(), rowIndex, 1);
                taskTableModel.setValueAt(task.getType(), rowIndex, 2);
                taskTableModel.setValueAt(
                        dueDate != null ? new SimpleDateFormat("MM-dd-yyyy").format(dueDate) : "Not set", rowIndex, 3);
                taskTableModel.setValueAt(task.getStatus(), rowIndex, 4);

                JOptionPane.showMessageDialog(this, "Task updated successfully!");
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM-dd-yyyy.");
            }
        }
    }

    private JPanel createTaskListTab() {
        // Panel to hold task table and buttons
        JPanel taskListPanel = new JPanel(new BorderLayout());

        // Task table
        taskTableModel = new DefaultTableModel(new String[] { "Title", "Description", "Type", "Due Date", "Status" },
                0);
        taskTable = new JTable(taskTableModel);
        JScrollPane taskScrollPane = new JScrollPane(taskTable);

        // Buttons for task management
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

        // Edit Task Button
        JButton editTaskButton = new JButton("Edit Task");
        editTaskButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow >= 0) {
                Task selectedTask = tasks.get(selectedRow);
                updateTask(selectedTask, selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "No task selected.");
            }
        });

        // Delete Task Button
        JButton deleteTaskButton = new JButton("Delete Task");
        deleteTaskButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow >= 0) {
                tasks.remove(selectedRow);
                taskTableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Task deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "No task selected.");
            }
        });

        // Add buttons to the panel
        buttonPanel.add(editTaskButton);
        buttonPanel.add(deleteTaskButton);

        // Add table and buttons to the main panel
        taskListPanel.add(taskScrollPane, BorderLayout.CENTER);
        taskListPanel.add(buttonPanel, BorderLayout.SOUTH);

        return taskListPanel;
    }

    private JPanel createGroupPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Group Management"));

        // Group form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel groupNameLabel = new JLabel("Group Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(groupNameLabel, gbc);

        JTextField groupNameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(groupNameField, gbc);

        JButton createGroupButton = new JButton("Create Group");
        createGroupButton.addActionListener(e -> createGroup(groupNameField.getText()));
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(createGroupButton, gbc);

        JButton deleteGroupButton = new JButton("Delete Group");
        deleteGroupButton.addActionListener(e -> deleteGroup(groupNameField.getText()));
        gbc.gridx = 1;
        gbc.gridy = 2; // Correctly add deleteGroupButton here
        formPanel.add(deleteGroupButton, gbc);
        panel.add(formPanel, BorderLayout.NORTH);

        // Group List
        groupListPanel = new JPanel(new GridLayout(0, 1));
        JScrollPane groupScrollPane = new JScrollPane(groupListPanel);
        panel.add(groupScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void createGroup(String groupName) {
        if (groupName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Group name cannot be empty.");
            return;
        }

        if (groups.containsKey(groupName)) {
            JOptionPane.showMessageDialog(this, "Group already exists.");
            return;
        }

        Group group = new Group(groupName);
        groups.put(groupName, group);
        addGroupToList(group);
    }

    private void deleteGroup(String groupName) {
        if (groupName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Group name cannot be empty.");
            return;
        }

        if (!groups.containsKey(groupName)) {
            JOptionPane.showMessageDialog(this, "Group does not exist.");
            return;
        }

        // Remove the group from the map
        groups.remove(groupName);

        // Remove the group's panel from the UI
        for (Component component : groupListPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel groupPanel = (JPanel) component;
                if (groupPanel.getBorder() instanceof javax.swing.border.TitledBorder) {
                    javax.swing.border.TitledBorder border = (javax.swing.border.TitledBorder) groupPanel.getBorder();
                    if (border.getTitle().equals(groupName)) {
                        groupListPanel.remove(groupPanel);
                        break;
                    }
                }
            }
        }

        // Refresh the group list panel
        groupListPanel.revalidate();
        groupListPanel.repaint();

        JOptionPane.showMessageDialog(this, "Group deleted successfully!");
    }

    private void addGroupToList(Group group) {
        JPanel groupPanel = new JPanel(new BorderLayout());
        groupPanel.setBorder(BorderFactory.createTitledBorder(group.getName()));

        JTextArea groupDetails = new JTextArea();
        groupDetails.setEditable(false);
        groupPanel.add(groupDetails, BorderLayout.CENTER);

        // Initial update for group details
        updateGroup(group, groupDetails);

        JButton addMemberButton = new JButton("Add Member");
        addMemberButton.addActionListener(e -> addMember(group, groupDetails));
        JButton removeMemberButton = new JButton("Remove Member");
        removeMemberButton.addActionListener(e -> removeMember(group, groupDetails));
        JButton scheduleMeetingButton = new JButton("Schedule Meeting");
        scheduleMeetingButton.addActionListener(e -> scheduleMeeting(group, groupDetails));
        JButton uploadFileButton = new JButton("Upload File");
        uploadFileButton.addActionListener(e -> uploadFile(group, groupDetails));
        JButton downloadFileButton = new JButton("Download File");
        downloadFileButton.addActionListener(e -> downloadFile(group));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5));
        buttonPanel.add(addMemberButton);
        buttonPanel.add(removeMemberButton);
        buttonPanel.add(scheduleMeetingButton);
        buttonPanel.add(uploadFileButton);
        buttonPanel.add(downloadFileButton);

        groupPanel.add(buttonPanel, BorderLayout.SOUTH);

        groupListPanel.add(groupPanel);
        groupListPanel.revalidate();
        groupListPanel.repaint();
    }

    private void addMember(Group group, JTextArea groupDetails) {
        String memberName = JOptionPane.showInputDialog(this, "Enter member name:");
        if (memberName != null && !memberName.isEmpty()) {
            group.addMember(memberName);
            updateGroup(group, groupDetails);
        }
    }

    private void removeMember(Group group, JTextArea groupDetails) {
        String memberName = JOptionPane.showInputDialog(this, "Enter member name to remove:");
        if (memberName != null && !memberName.isEmpty() && group.getMembers().contains(memberName)) {
            group.removeMember(memberName);
            updateGroup(group, groupDetails);
        }
    }

    private void scheduleMeeting(Group group, JTextArea groupDetails) {
        // Create input fields for meeting details
        JTextField dateField = new JTextField();
        dateField.setToolTipText("MM-dd-yyyy");
        JTextField timeField = new JTextField();
        timeField.setToolTipText("HH:mm");

        // Create a panel to hold the input fields
        JPanel schedulePanel = new JPanel(new GridLayout(2, 2));
        schedulePanel.add(new JLabel("Meeting Date (MM-dd-yyyy):"));
        schedulePanel.add(dateField);
        schedulePanel.add(new JLabel("Meeting Time (HH:mm):"));
        schedulePanel.add(timeField);

        // Show input dialog
        int result = JOptionPane.showConfirmDialog(
                this, schedulePanel, "Schedule Meeting", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Date meetingTime = new SimpleDateFormat("MM-dd-yyyy HH:mm").parse(
                        dateField.getText() + " " + timeField.getText());
                group.setMeetingTime(meetingTime);
                updateGroup(group, groupDetails);
                JOptionPane.showMessageDialog(this, "Meeting scheduled successfully!");
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date/time format. Please use MM-dd-yyyy and HH:mm.");
            }
        }
    }

    private void uploadFile(Group group, JTextArea groupDetails) {
        String fileName = JOptionPane.showInputDialog(this, "Enter file name to upload:");
        if (fileName != null && !fileName.isEmpty()) {
            group.uploadFile(fileName);
            updateGroup(group, groupDetails);
        }
    }

    private void updateGroup(Group group, JTextArea groupDetails) {
        // Format meeting time
        String formattedMeetingTime = (group.getMeetingTime() != null)
                ? new SimpleDateFormat("MM-dd-yyyy HH:mm").format(group.getMeetingTime())
                : "No meeting scheduled.";

        // Prepare member details
        StringBuilder membersBuilder = new StringBuilder("Members:\n");
        for (String member : group.getMembers()) {
            membersBuilder.append("- ").append(member).append("\n");
        }

        // Prepare file details
        StringBuilder filesBuilder = new StringBuilder("Files:\n");
        for (String file : group.getFiles()) {
            filesBuilder.append("- ").append(file).append("\n");
        }

        // Update group details text area
        groupDetails.setText(
                membersBuilder.toString() + "\n" +
                        filesBuilder.toString() + "\n" +
                        "Meetings: \n" + formattedMeetingTime);
    }

    private void downloadFile(Group group) {
        JOptionPane.showMessageDialog(this, "Downloading file for group: " + group.getName());
    }

    private JPanel createStudyPlanPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Study Plan Management"));

        // Left side: Course management
        DefaultListModel<String> courseListModel = new DefaultListModel<>();
        JList<String> courseList = new JList<>(courseListModel);
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane courseScrollPane = new JScrollPane(courseList);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Courses"));
        leftPanel.add(courseScrollPane, BorderLayout.CENTER);

        JButton addCourseButton = new JButton("Add Course");
        addCourseButton.addActionListener(e -> {
            String courseName = JOptionPane.showInputDialog(this, "Enter course name:");
            if (courseName != null && !courseName.trim().isEmpty()) {
                courseListModel.addElement(courseName);
            } else {
                JOptionPane.showMessageDialog(this, "Course name cannot be empty.");
            }
        });

        JButton deleteCourseButton = new JButton("Delete Course");
        deleteCourseButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            if (selectedCourse != null) {
                courseListModel.removeElement(selectedCourse);
                studyPlans.remove(selectedCourse);
            } else {
                JOptionPane.showMessageDialog(this, "No course selected.");
            }
        });

        JPanel courseButtonPanel = new JPanel(new GridLayout(1, 2));
        courseButtonPanel.add(addCourseButton);
        courseButtonPanel.add(deleteCourseButton);
        leftPanel.add(courseButtonPanel, BorderLayout.SOUTH);

        // Right side: Study plan management
        DefaultListModel<String> studyPlanListModel = new DefaultListModel<>();
        JList<String> studyPlanList = new JList<>(studyPlanListModel);
        JScrollPane studyPlanScrollPane = new JScrollPane(studyPlanList);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Study Plans"));
        rightPanel.add(studyPlanScrollPane, BorderLayout.CENTER);

        JButton addStudyPlanButton = new JButton("Add Study Plan");
        addStudyPlanButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            if (selectedCourse != null) {
                String studyPlanName = JOptionPane.showInputDialog(this, "Enter study plan name:");
                if (studyPlanName != null && !studyPlanName.trim().isEmpty()) {
                    StudyPlan studyPlan = new StudyPlan(selectedCourse, studyPlanName);
                    studyPlans.computeIfAbsent(selectedCourse, k -> new ArrayList<>()).add(studyPlan);
                    studyPlanListModel.addElement(studyPlan.getName());
                } else {
                    JOptionPane.showMessageDialog(this, "Study plan name cannot be empty.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No course selected.");
            }
        });

        JButton viewStudyPlanButton = new JButton("View Study Plan");
        viewStudyPlanButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            String selectedStudyPlanName = studyPlanList.getSelectedValue();
            if (selectedCourse != null && selectedStudyPlanName != null) {
                StudyPlan selectedStudyPlan = studyPlans.get(selectedCourse).stream()
                        .filter(sp -> sp.getName().equals(selectedStudyPlanName))
                        .findFirst()
                        .orElse(null);
                if (selectedStudyPlan != null) {
                    // Table for coursework details
                    String[] columnNames = { "Name", "Details", "Due Date", "Status" };
                    List<Coursework> studyPlanCoursework = selectedStudyPlan.getCourseworkList();
                    String[][] tableData = new String[studyPlanCoursework.size()][4];

                    for (int i = 0; i < studyPlanCoursework.size(); i++) {
                        Coursework cw = studyPlanCoursework.get(i);
                        tableData[i][0] = cw.getName();
                        tableData[i][1] = cw.getDetails() == null ? "Not set" : cw.getDetails();
                        tableData[i][2] = cw.getDueDate() == null ? "Not set"
                                : new SimpleDateFormat("MM-dd-yyyy").format(cw.getDueDate());
                        tableData[i][3] = cw.getStatus() == null ? "Not set" : cw.getStatus();
                    }

                    JTable courseworkTable = new JTable(tableData, columnNames);
                    courseworkTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                    // Coursework Management Buttons
                    JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
                    JButton addCourseworkButton = new JButton("Add Coursework");
                    JButton editCourseworkButton = new JButton("Edit Coursework");
                    JButton deleteCourseworkButton = new JButton("Delete Coursework");

                    // Add Coursework
                    addCourseworkButton.addActionListener(ae -> {
                        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
                        JTextField nameField = new JTextField();
                        JTextField detailsField = new JTextField();
                        JTextField dueDateField = new JTextField();
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
                                this, inputPanel, "Add Coursework", JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.PLAIN_MESSAGE);

                        if (result == JOptionPane.OK_OPTION) {
                            try {
                                Date dueDate = null;
                                if (!dueDateField.getText().trim().isEmpty()) {
                                    dueDate = new SimpleDateFormat("MM-dd-yyyy").parse(dueDateField.getText());
                                }

                                String name = nameField.getText().trim();
                                String details = detailsField.getText().trim();
                                String status = (String) statusComboBox.getSelectedItem();

                                if (name.isEmpty()) {
                                    JOptionPane.showMessageDialog(this, "Coursework name cannot be empty.");
                                    return;
                                }

                                Coursework coursework = new Coursework(name, details, dueDate, status);
                                selectedStudyPlan.addCoursework(coursework);

                                // Refresh table
                                refreshCourseworkTable(selectedStudyPlan, courseworkTable);
                            } catch (ParseException ex) {
                                JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM-dd-yyyy.");
                            }
                        }
                    });

                    // Edit Coursework
                    editCourseworkButton.addActionListener(ae -> {
                        int selectedRow = courseworkTable.getSelectedRow();
                        if (selectedRow >= 0) {
                            Coursework selectedCoursework = studyPlanCoursework.get(selectedRow);

                            JPanel inputPanel = new JPanel(new GridLayout(4, 2));
                            JTextField nameField = new JTextField(selectedCoursework.getName());
                            JTextField detailsField = new JTextField(selectedCoursework.getDetails());
                            JTextField dueDateField = new JTextField(selectedCoursework.getDueDate() != null
                                    ? new SimpleDateFormat("MM-dd-yyyy").format(selectedCoursework.getDueDate())
                                    : "");
                            JComboBox<String> statusComboBox = new JComboBox<>(
                                    new String[] { "Not Started", "In Progress", "Completed" });
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
                                    this, inputPanel, "Edit Coursework", JOptionPane.OK_CANCEL_OPTION,
                                    JOptionPane.PLAIN_MESSAGE);

                            if (result == JOptionPane.OK_OPTION) {
                                try {
                                    Date dueDate = null;
                                    if (!dueDateField.getText().trim().isEmpty()) {
                                        dueDate = new SimpleDateFormat("MM-dd-yyyy").parse(dueDateField.getText());
                                    }

                                    selectedCoursework.setName(nameField.getText().trim());
                                    selectedCoursework.setDetails(detailsField.getText().trim());
                                    selectedCoursework.setDueDate(dueDate);
                                    selectedCoursework.setStatus((String) statusComboBox.getSelectedItem());

                                    // Refresh table
                                    refreshCourseworkTable(selectedStudyPlan, courseworkTable);
                                } catch (ParseException ex) {
                                    JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM-dd-yyyy.");
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "No coursework selected.");
                        }
                    });

                    // Delete Coursework
                    deleteCourseworkButton.addActionListener(ae -> {
                        int selectedRow = courseworkTable.getSelectedRow();
                        if (selectedRow >= 0) {
                            studyPlanCoursework.remove(selectedRow);

                            // Refresh table
                            refreshCourseworkTable(selectedStudyPlan, courseworkTable);
                            JOptionPane.showMessageDialog(this, "Coursework deleted successfully.");
                        } else {
                            JOptionPane.showMessageDialog(this, "No coursework selected.");
                        }
                    });

                    buttonPanel.add(addCourseworkButton);
                    buttonPanel.add(editCourseworkButton);
                    buttonPanel.add(deleteCourseworkButton);

                    // Display table and buttons in a dialog
                    JPanel courseworkPanel = new JPanel(new BorderLayout());
                    courseworkPanel.add(new JScrollPane(courseworkTable), BorderLayout.CENTER);
                    courseworkPanel.add(buttonPanel, BorderLayout.SOUTH);

                    JOptionPane.showMessageDialog(this, courseworkPanel, "Manage Coursework for Study Plan",
                            JOptionPane.PLAIN_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No study plan selected.");
            }
        });

        JButton deleteStudyPlanButton = new JButton("Delete Study Plan");
        deleteStudyPlanButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            String selectedStudyPlanName = studyPlanList.getSelectedValue();

            if (selectedCourse != null && selectedStudyPlanName != null) {
                List<StudyPlan> plans = studyPlans.get(selectedCourse);
                if (plans != null) {
                    plans.removeIf(sp -> sp.getName().equals(selectedStudyPlanName));
                }
                studyPlanListModel.removeElement(selectedStudyPlanName);
                JOptionPane.showMessageDialog(this, "Study plan deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "No study plan or course selected.");
            }
        });

        JPanel studyPlanButtonPanel = new JPanel(new GridLayout(1, 3));
        studyPlanButtonPanel.add(addStudyPlanButton);
        studyPlanButtonPanel.add(viewStudyPlanButton);
        studyPlanButtonPanel.add(deleteStudyPlanButton);
        rightPanel.add(studyPlanButtonPanel, BorderLayout.SOUTH);

        // Update study plans on course selection
        courseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedCourse = courseList.getSelectedValue();
                studyPlanListModel.clear();
                if (selectedCourse != null && studyPlans.containsKey(selectedCourse)) {
                    for (StudyPlan sp : studyPlans.get(selectedCourse)) {
                        studyPlanListModel.addElement(sp.getDetails()); // Add only the details to the list
                    }
                }
            }
        });

        // Combine left and right panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private void refreshCourseworkTable(StudyPlan studyPlan, JTable courseworkTable) {
        List<Coursework> studyPlanCoursework = studyPlan.getCourseworkList();
        String[][] tableData = new String[studyPlanCoursework.size()][4];

        for (int i = 0; i < studyPlanCoursework.size(); i++) {
            Coursework cw = studyPlanCoursework.get(i);
            tableData[i][0] = cw.getName();
            tableData[i][1] = cw.getDetails() == null ? "Not set" : cw.getDetails();
            tableData[i][2] = cw.getDueDate() == null ? "Not set"
                    : new SimpleDateFormat("MM-dd-yyyy").format(cw.getDueDate());
            tableData[i][3] = cw.getStatus() == null ? "Not set" : cw.getStatus();
        }

        courseworkTable.setModel(
                new javax.swing.table.DefaultTableModel(tableData,
                        new String[] { "Name", "Details", "Due Date", "Status" }));
    }

    private JPanel createTutoringPanel() {
        JPanel tutoringPanel = new JPanel(new BorderLayout());

        // Top Panel: Dropdown for classes
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        classDropdown = new JComboBox<>();
        classDropdown.addItem("Select a Class"); // Default option
        classDropdown.addActionListener(e -> updateSessionTable());
        topPanel.add(new JLabel("Class:"));
        topPanel.add(classDropdown);

        // Bottom Panel: Table for sessions
        sessionTableModel = new DefaultTableModel(new String[] { "Name", "Available Date", "Location", "Scheduled" },
                0);
        sessionTable = new JTable(sessionTableModel);
        JScrollPane tableScrollPane = new JScrollPane(sessionTable);

        // Button to schedule session
        JButton scheduleButton = new JButton("Schedule Session");
        scheduleButton.addActionListener(e -> scheduleSession());

        // Button to volunteer as a tutor
        JButton volunteerButton = new JButton("Volunteer as Tutor");
        volunteerButton.addActionListener(e -> volunteerAsTutor());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(scheduleButton);
        buttonPanel.add(volunteerButton);

        tutoringPanel.add(topPanel, BorderLayout.NORTH);
        tutoringPanel.add(tableScrollPane, BorderLayout.CENTER);
        tutoringPanel.add(buttonPanel, BorderLayout.SOUTH);

        return tutoringPanel;
    }

    private void populateTutors() {
        tutors.add(new Tutor("Tutor A", "Math", "12-10-2024", "Room 101", false));
        tutors.add(new Tutor("Tutor B", "Science", "12-11-2024", "Room 202", false));
        tutors.add(new Tutor("Tutor C", "Math", "12-12-2024", "Room 101", false));
        tutors.add(new Tutor("Tutor D", "English", "12-13-2024", "Room 303", false));

        // Populate the dropdown with unique classes
        tutors.stream()
                .map(Tutor::getTutorClass)
                .distinct()
                .forEach(classDropdown::addItem);
    }

    private void updateSessionTable() {
        String selectedClass = (String) classDropdown.getSelectedItem();
        if (selectedClass == null || selectedClass.equals("Select a Class")) {
            sessionTableModel.setRowCount(0); // Clear table
            return;
        }

        sessionTableModel.setRowCount(0); // Clear existing rows

        for (Tutor tutor : tutors) {
            if (tutor.getTutorClass().equals(selectedClass)) {
                sessionTableModel.addRow(new Object[] {
                        tutor.getName(),
                        tutor.getAvailableDate(),
                        tutor.getLocation(),
                        tutor.isScheduled() ? "Yes" : "No"
                });
            }
        }
    }

    private void scheduleSession() {
        int selectedRow = sessionTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "No session selected.");
            return;
        }

        String tutorName = (String) sessionTableModel.getValueAt(selectedRow, 0);

        for (Tutor tutor : tutors) {
            if (tutor.getName().equals(tutorName)) {
                if (tutor.isScheduled()) {
                    JOptionPane.showMessageDialog(this, "This session is already scheduled.");
                    return;
                }

                tutor.setScheduled(true);
                updateSessionTable();
                JOptionPane.showMessageDialog(this, "Session scheduled successfully.");
                return;
            }
        }
    }

    private void volunteerAsTutor() {
        // Create input fields
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
                this, inputPanel, "Volunteer as Tutor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String tutorClass = classField.getText().trim();
                String availableDate = availableDateField.getText().trim();
                String location = locationField.getText().trim();

                if (name.isEmpty() || tutorClass.isEmpty() || availableDate.isEmpty() || location.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.");
                    return;
                }

                // Validate the date format
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                sdf.setLenient(false);
                sdf.parse(availableDate);

                // Add new tutor to the list
                Tutor newTutor = new Tutor(name, tutorClass, availableDate, location, false);
                tutors.add(newTutor);

                // Update dropdown if the class is new
                if (!classDropdown.getItemAt(0).equals(tutorClass)) {
                    boolean exists = false;
                    for (int i = 0; i < classDropdown.getItemCount(); i++) {
                        if (classDropdown.getItemAt(i).equals(tutorClass)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        classDropdown.addItem(tutorClass);
                    }
                }

                JOptionPane.showMessageDialog(this, "Your volunteer tutor application has been submitted.");
                updateSessionTable(); // Refresh the table to include the new tutor
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM-dd-yyyy.");
            }
        }
    }

    private JPanel createNotificationsTab() {
        JPanel notificationPanel = new JPanel(new BorderLayout());

        // Notifications Table
        notificationTableModel = new DefaultTableModel(new String[] { "Date", "Content", "Read" }, 0);

        // Populate table with existing notifications
        for (Notification notification : notifications) {
            notificationTableModel.addRow(new Object[] {
                    new SimpleDateFormat("MM-dd-yyyy").format(notification.getSentDate()),
                    notification.getContent(),
                    notification.isRead() ? "Yes" : "No"
            });
        }

        JTable notificationTable = new JTable(notificationTableModel);
        notificationTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        JScrollPane tableScrollPane = new JScrollPane(notificationTable);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton deleteNotificationButton = new JButton("Delete Notification");
        deleteNotificationButton.addActionListener(e -> deleteSelectedNotification(notificationTable));

        JButton markAllReadButton = new JButton("Mark All as Read");
        markAllReadButton.addActionListener(e -> markAllNotificationsAsRead());

        buttonPanel.add(deleteNotificationButton);
        buttonPanel.add(markAllReadButton);

        notificationPanel.add(tableScrollPane, BorderLayout.CENTER);
        notificationPanel.add(buttonPanel, BorderLayout.SOUTH);

        return notificationPanel;
    }

    private void startNotificationChecker() {
        Timer timer = new Timer(5000, e -> checkForNotifications());
        timer.start();
    }

    private void checkForNotifications() {
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

        // Check task due dates
        for (Task task : tasks) {
            if (!task.getStatus().equals("Completed") &&
                    task.getDueDate().equals(sdf.format(currentDate))) {
                String content = "Task due today: " + task.getTitle();
                addNotification(content);
            }
        }

        // Check tutor availability
        for (Tutor tutor : tutors) {
            if (tutor.isScheduled() &&
                    tutor.getAvailableDate().equals(sdf.format(currentDate))) {
                String content = "Scheduled tutor session today: " + tutor.getName();
                addNotification(content);
            }
        }

        updateNotificationsTab();
    }

    private void addNotification(String content) {
        // Get the current date formatted as MM-dd-yyyy
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String currentDateString = sdf.format(currentDate);
    
        // Check if a notification with the same content and date already exists, regardless of deletion state
        boolean exists = notifications.stream()
                .anyMatch(notification -> notification.getContent().equals(content) &&
                        sdf.format(notification.getSentDate()).equals(currentDateString) &&
                        !notification.isDeleted());
    
        if (exists) {
            // If a duplicate notification exists, skip adding
            return;
        }
    
        // Add the new notification if it's not a duplicate
        Notification notification = new Notification(currentDate, content);
        notifications.add(notification);
    
        // Add the notification to the table model
        notificationTableModel.addRow(new Object[] {
                currentDateString,
                notification.getContent(),
                notification.isRead() ? "Yes" : "No"
        });
    
        // Update the tab title
        updateNotificationsTab();
    }
    
    private void updateNotificationsTab() {
        // Find the Notifications tab index
        int notificationsTabIndex = -1;
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            String tabTitle = tabbedPane.getTitleAt(i);
            if (tabTitle.startsWith("Notifications")) {
                notificationsTabIndex = i;
                break;
            }
        }
    
        // Ensure the tab exists
        if (notificationsTabIndex != -1) {
            // Count unread notifications that are not deleted
            long unreadCount = notifications.stream()
                    .filter(notification -> !notification.isRead() && !notification.isDeleted())
                    .count();
    
            // Update the tab title to show unread notifications count
            String newTitle = unreadCount > 0 ? "Notifications (" + unreadCount + ")" : "Notifications";
            tabbedPane.setTitleAt(notificationsTabIndex, newTitle);
        } else {
            // Log an error if the Notifications tab is not found
            System.err.println("Notifications tab not found in JTabbedPane.");
        }
    }
    
    
    private void deleteSelectedNotification(JTable notificationTable) {
        int selectedRow = notificationTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Mark the notification as deleted
            notifications.get(selectedRow).markAsDeleted();
    
            // Remove the row from the table model
            notificationTableModel.removeRow(selectedRow);
    
            // Update the notifications tab title to reflect the unread count
            updateNotificationsTab();
        } else {
            JOptionPane.showMessageDialog(this, "No notification selected.");
        }
    }
    
    private void markAllNotificationsAsRead() {
        notifications.forEach(Notification::markAsRead);
        updateNotificationsTab();
        refreshNotificationTable();
    }

    private void refreshNotificationTable() {
        notificationTableModel.setRowCount(0); // Clear the table model
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
    
        // Re-populate the table with updated notifications
        for (Notification notification : notifications) {
            notificationTableModel.addRow(new Object[] {
                sdf.format(notification.getSentDate()),
                notification.getContent(),
                notification.isRead() ? "Yes" : "No"
            });
        }
    }    

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Platform platform = new Platform();
            platform.setVisible(true);
        });
    }
}