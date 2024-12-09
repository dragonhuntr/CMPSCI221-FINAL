package com.studyplatform.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class DatabaseUtil {
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String DB_URL = "jdbc:derby:studyplatformDB;create=true";
    private static Connection connection;

    // Static initializer to load Derby driver
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Derby JDBC Driver not found: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    // Private constructor to prevent instantiation
    private DatabaseUtil() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            // Create database directory if it doesn't exist
            File dbDirectory = new File("studyplatformDB");
            if (!dbDirectory.exists()) {
                dbDirectory.mkdir();
            }
            
            // Establish connection
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    // Shutdown the Derby database
    public static void shutdownDatabase() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            // Derby throws an exception on successful shutdown, so we ignore it
            if (!e.getSQLState().equals("XJ015")) {
                System.err.println("Database shutdown error: " + e.getMessage());
            }
        }
    }
}
