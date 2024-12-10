package com.studyplatform.dao;

import com.studyplatform.models.Group;
import com.studyplatform.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO implements BaseDAO<Group> {
    // constant for the table name used in database queries
    private static final String TABLE_NAME = "GROUPS";

    // this is a method to create the groups table in the database
    // this includes columns for id, name, meeting time, members, and files
    public void createTable() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "name VARCHAR(255), " +
                    "meeting_time TIMESTAMP, " +
                    "members VARCHAR(1000), " +
                    "files VARCHAR(1000))";
            stmt.execute(createTableSQL);
        }
    }

    // here this is a method to insert a new group into the database
    // we need to use a prepared statement to handle arangments and generate a new id for the group
    @Override
    public void create(Group group) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (name, meeting_time, members, files) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, group.getName());
            pstmt.setTimestamp(2, group.getMeetingTime() != null ? new Timestamp(group.getMeetingTime().getTime()) : null);
            pstmt.setString(3, String.join(",", group.getMembers()));
            pstmt.setString(4, String.join(",", group.getFiles()));

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    group.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // here we have a method to read a group from the database based on its id
    // it shoudl get group details like name, meeting time, members, and files from the result set
    @Override
    public Group read(int id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Group group = new Group();
                    group.setId(rs.getInt("id"));
                    group.setName(rs.getString("name"));

                    Timestamp meetingTime = rs.getTimestamp("meeting_time");
                    if (meetingTime != null) {
                        group.setMeetingTime(new java.util.Date(meetingTime.getTime()));
                    }

                    String membersStr = rs.getString("members");
                    if (membersStr != null && !membersStr.isEmpty()) {
                        String[] members = membersStr.split(",");
                        for (String member : members) {
                            group.addMember(member);
                        }
                    }

                    String filesStr = rs.getString("files");
                    if (filesStr != null && !filesStr.isEmpty()) {
                        String[] files = filesStr.split(",");
                        for (String file : files) {
                            group.uploadFile(file);
                        }
                    }

                    return group;
                }
            }
        }
        return null;
    }

    // this method should update an existing group's details in the database
    // and also updates name, meeting time, members, and files using the group's id
    @Override
    public void update(Group group) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET name = ?, meeting_time = ?, members = ?, files = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, group.getName());
            pstmt.setTimestamp(2, group.getMeetingTime() != null ? new Timestamp(group.getMeetingTime().getTime()) : null);
            pstmt.setString(3, String.join(",", group.getMembers()));
            pstmt.setString(4, String.join(",", group.getFiles()));
            pstmt.setInt(5, group.getId());

            pstmt.executeUpdate();
        }
    }

    // this should also use this method to delete a group
    // from the database using its id
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // this method should be able to get all groups from the database
    // and use theseloops through the result set to create and add group objects to a list
    @Override
    public List<Group> findAll() throws SQLException {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Group group = new Group();
                group.setId(rs.getInt("id"));
                group.setName(rs.getString("name"));

                Timestamp meetingTime = rs.getTimestamp("meeting_time");
                if (meetingTime != null) {
                    group.setMeetingTime(new java.util.Date(meetingTime.getTime()));
                }

                String membersStr = rs.getString("members");
                if (membersStr != null && !membersStr.isEmpty()) {
                    String[] members = membersStr.split(",");
                    for (String member : members) {
                        group.addMember(member);
                    }
                }

                String filesStr = rs.getString("files");
                if (filesStr != null && !filesStr.isEmpty()) {
                    String[] files = filesStr.split(",");
                    for (String file : files) {
                        group.uploadFile(file);
                    }
                }

                groups.add(group);
            }
        }

        return groups;
    }
}
