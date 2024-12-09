package com.studyplatform.controllers;

import com.studyplatform.models.Group;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GroupController {
    private Map<String, Group> groups;

    public GroupController() {
        this.groups = new HashMap<>();
    }

    public Group createGroup(String groupName) {
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be empty.");
        }

        if (groups.containsKey(groupName)) {
            throw new IllegalStateException("Group already exists.");
        }

        Group group = new Group(groupName);
        groups.put(groupName, group);
        return group;
    }

    public void deleteGroup(String groupName) {
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be empty.");
        }

        if (!groups.containsKey(groupName)) {
            throw new IllegalStateException("Group does not exist.");
        }

        groups.remove(groupName);
    }

    public Group getGroup(String groupName) {
        return groups.get(groupName);
    }

    public void addMemberToGroup(String groupName, String memberName) {
        Group group = groups.get(groupName);
        if (group != null) {
            group.addMember(memberName);
        }
    }

    public void removeMemberFromGroup(String groupName, String memberName) {
        Group group = groups.get(groupName);
        if (group != null) {
            group.removeMember(memberName);
        }
    }

    public void uploadFileToGroup(String groupName, String fileName) {
        Group group = groups.get(groupName);
        if (group != null) {
            group.uploadFile(fileName);
        }
    }

    public void scheduleMeeting(String groupName, Date meetingTime) {
        Group group = groups.get(groupName);
        if (group != null) {
            group.setMeetingTime(meetingTime);
        }
    }

    public Map<String, Group> getAllGroups() {
        return new HashMap<>(groups);
    }
}
