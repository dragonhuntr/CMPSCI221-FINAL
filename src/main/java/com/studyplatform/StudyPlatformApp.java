package com.studyplatform;

import com.studyplatform.controllers.TaskController;
import com.studyplatform.controllers.GroupController;
import com.studyplatform.controllers.StudyPlanController;
import com.studyplatform.controllers.TutorController;
import com.studyplatform.controllers.NotificationController;

import com.studyplatform.views.TaskView;
import com.studyplatform.views.GroupView;
import com.studyplatform.views.StudyPlanView;
import com.studyplatform.views.TutorView;
import com.studyplatform.views.NotificationView;

import javax.swing.*;

public class StudyPlatformApp extends JFrame {
    // these are the controllers
    private TaskController taskController;
    private GroupController groupController;
    private StudyPlanController studyPlanController;
    private TutorController tutorController;
    private NotificationController notificationController;

    // these are all of the views
    private TaskView taskView;
    private GroupView groupView;
    private StudyPlanView studyPlanView;
    private TutorView tutorView;
    private NotificationView notificationView;

    //this is the most important study platform app
    // which has to contol a lot of details
    public StudyPlatformApp() {
        // here we would need to initialize controllers
        taskController = new TaskController();
        groupController = new GroupController();
        studyPlanController = new StudyPlanController();
        tutorController = new TutorController();
        notificationController = new NotificationController();

        // and here we need to initialize views
        taskView = new TaskView(taskController);
        groupView = new GroupView(groupController);
        studyPlanView = new StudyPlanView(studyPlanController);
        tutorView = new TutorView(tutorController);
        notificationView = new NotificationView(notificationController);

        // then we would need to setup main frame
        setTitle("Study Management Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // finally create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Task Manager", taskView);
        tabbedPane.addTab("Group Management", groupView);
        tabbedPane.addTab("Study Plans", studyPlanView);
        tabbedPane.addTab("Tutoring", tutorView);
        tabbedPane.addTab("Notifications", notificationView);

        // then add that to the tabbed pane to frame
        getContentPane().add(tabbedPane);

        // then center the frame on screen
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        // this will ensure GUI is created on event dispatch thread
        SwingUtilities.invokeLater(() -> {
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }

            // and we would need to create and display the application
            StudyPlatformApp app = new StudyPlatformApp();
            app.setVisible(true);
        });
    }
}