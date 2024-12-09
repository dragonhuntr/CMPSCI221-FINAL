package com.studyplatform.controllers;

import com.studyplatform.models.Group;
import com.studyplatform.dao.GroupDAO;
import java.util.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupController {
    private GroupDAO groupDAO;

    public GroupController() {
        this.groupDAO = new GroupDAO();
        try {
            this.groupDAO.createTable();
        } catch (SQLException e) {
            System.err.println("Error creating groups table: " + e.getMessage());
        }
    }

    public Group createGroup(String groupName) {
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be empty.");
        }

        try {
            // Check if group already exists
            List<Group> existingGroups = groupDAO.findAll();
            boolean groupExists = existingGroups.stream()
                    .anyMatch(g -> g.getName().equals(groupName));
            
            if (groupExists) {
                throw new IllegalStateException("Group already exists.");
            }

            Group group = new Group(groupName);
            groupDAO.create(group);
            return group;
        } catch (SQLException e) {
            System.err.println("Error creating group: " + e.getMessage());
            return null;
        }
    }

    public void deleteGroup(String groupName) {
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be empty.");
        }

        try {
            Group group = getGroup(groupName);
            if (group == null) {
                throw new IllegalStateException("Group does not exist.");
            }

            groupDAO.delete(group.getId());
        } catch (SQLException e) {
            System.err.println("Error deleting group: " + e.getMessage());
        }
    }

    public Group getGroup(String groupName) {
        try {
            return groupDAO.findAll().stream()
                    .filter(g -> g.getName().equals(groupName))
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            System.err.println("Error finding group: " + e.getMessage());
            return null;
        }
    }

    public void addMemberToGroup(String groupName, String memberName) {
        try {
            Group group = getGroup(groupName);
            if (group != null) {
                group.addMember(memberName);
                groupDAO.update(group);
            }
        } catch (SQLException e) {
            System.err.println("Error adding member to group: " + e.getMessage());
        }
    }

    public void removeMemberFromGroup(String groupName, String memberName) {
        try {
            Group group = getGroup(groupName);
            if (group != null) {
                group.removeMember(memberName);
                groupDAO.update(group);
            }
        } catch (SQLException e) {
            System.err.println("Error removing member from group: " + e.getMessage());
        }
    }

    public void uploadFileToGroup(String groupName, String fileName) {
        try {
            Group group = getGroup(groupName);
            if (group != null) {
                group.uploadFile(fileName);
                groupDAO.update(group);
            }
        } catch (SQLException e) {
            System.err.println("Error uploading file to group: " + e.getMessage());
        }
    }

    public void scheduleMeeting(String groupName, Date meetingTime) {
        try {
            Group group = getGroup(groupName);
            if (group != null) {
                group.setMeetingTime(meetingTime);
                groupDAO.update(group);
            }
        } catch (SQLException e) {
            System.err.println("Error scheduling meeting: " + e.getMessage());
        }
    }

    public Map<String, Group> getAllGroups() {
        try {
            List<Group> groups = groupDAO.findAll();
            return groups.stream()
                    .collect(Collectors.toMap(Group::getName, g -> g));
        } catch (SQLException e) {
            System.err.println("Error retrieving all groups: " + e.getMessage());
            return new HashMap<>();
        }
    }
}
