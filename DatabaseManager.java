import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseManager {
    // Database connection parameters
    private static final String DB_URL = "jdbc:derby:StudyManagementDB; create=true";
    private Connection connection;

    // Constructor to establish database connection
    public DatabaseManager() {
        try {
            // Load Derby JDBC driver
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

            // Establish connection
            connection = DriverManager.getConnection(DB_URL);

            // Initialize database tables if they don't exist
            initializeTables();
        } catch (ClassNotFoundException e) {
            System.err.println("Derby JDBC Driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection error");
            e.printStackTrace();
        }
    }

    // Initialize database tables
    private void initializeTables() throws SQLException {
        Statement stmt = connection.createStatement();

        // Create Tasks table
        stmt.execute("CREATE TABLE IF NOT EXISTS tasks (" +
                "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                "title VARCHAR(255), " +
                "description VARCHAR(500), " +
                "type VARCHAR(50), " +
                "due_date DATE, " +
                "status VARCHAR(50))");

        // Create Groups table
        stmt.execute("CREATE TABLE IF NOT EXISTS groups (" +
                "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                "name VARCHAR(255), " +
                "meeting_time TIMESTAMP)");

        // Create Group Members table
        stmt.execute("CREATE TABLE IF NOT EXISTS group_members (" +
                "group_id INT, " +
                "member_name VARCHAR(255), " +
                "FOREIGN KEY (group_id) REFERENCES groups(id))");

        // Create Group Files table
        stmt.execute("CREATE TABLE IF NOT EXISTS group_files (" +
                "group_id INT, " +
                "file_name VARCHAR(255), " +
                "FOREIGN KEY (group_id) REFERENCES groups(id))");

        // Create Study Plans table
        stmt.execute("CREATE TABLE IF NOT EXISTS study_plans (" +
                "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                "course_name VARCHAR(255), " +
                "details VARCHAR(500), " +
                "due_date DATE, " +
                "priority VARCHAR(50))");
    }

    // Tasks CRUD Operations
    public void addTask(Task task) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO tasks (title, description, type, due_date, status) VALUES (?, ?, ?, ?, ?)");

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getType());

            // Use the Date object directly, converting to java.sql.Date
            pstmt.setDate(4, task.getDueDate() != null ?
                    new java.sql.Date(task.getDueDate().getTime()) : null);

            pstmt.setString(5, task.getStatus());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tasks");

            while (rs.next()) {
                Task task = new Task(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("type"),
                        rs.getDate("due_date"),
                        rs.getString("status")
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // Groups CRUD Operations
    public void addGroup(Group group) {
        try {
            // Insert group
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO groups (name, meeting_time) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, group.getName());
            pstmt.setTimestamp(2, group.getMeetingTime() != null ?
                    new Timestamp(group.getMeetingTime().getTime()) : null);

            pstmt.executeUpdate();

            // Get the generated group ID
            ResultSet rs = pstmt.getGeneratedKeys();
            int groupId = -1;
            if (rs.next()) {
                groupId = rs.getInt(1);
            }

            // Insert members
            PreparedStatement memberStmt = connection.prepareStatement(
                    "INSERT INTO group_members (group_id, member_name) VALUES (?, ?)");
            for (String member : group.getMembers()) {
                memberStmt.setInt(1, groupId);
                memberStmt.setString(2, member);
                memberStmt.executeUpdate();
            }

            // Insert files
            PreparedStatement fileStmt = connection.prepareStatement(
                    "INSERT INTO group_files (group_id, file_name) VALUES (?, ?)");
            for (String file : group.getFiles()) {
                fileStmt.setInt(1, groupId);
                fileStmt.setString(2, file);
                fileStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Study Plan CRUD Operations
    public void addStudyPlan(StudyPlan studyPlan) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO study_plans (course_name, details, due_date, priority) VALUES (?, ?, ?, ?)");

            pstmt.setString(1, studyPlan.getCourseName());
            pstmt.setString(2, studyPlan.getDetails());
            pstmt.setDate(3, studyPlan.getDueDate() != null ?
                    new java.sql.Date(studyPlan.getDueDate().getTime()) : null);
            pstmt.setString(4, studyPlan.getPriority());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Close database connection
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}