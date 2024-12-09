package com.studyplatform.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class DatabaseUtil {
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String DB_URL = "jdbc:derby:studydb;";
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

            System.out.println("Connected to database: " + DB_URL);
        }
        return connection;
    }
}
