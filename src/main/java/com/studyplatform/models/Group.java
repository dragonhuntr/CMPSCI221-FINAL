
package com.studyplatform.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Group {
    // there are some private fields for group properties
    private int id;
    private String name;
    private List<String> members;
    private List<String> files;
    private Date meetingTime;

    // this is the default constructor to initialize members and files lists
    public Group() {
        this.members = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    // here we are parameterizing constructor to create group name and lists
    public Group(String name) {
        this.name = name;
        this.members = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    // getter for id
    public int getId() { return id; }
    // setter for id
    public void setId(int id) { this.id = id; }

    // getter for name
    public String getName() { return name; }
    // setter for name
    public void setName(String name) { this.name = name; }

    // getter for members list
    public List<String> getMembers() { return members; }

    // this method shoulbe be able to add a member to the group
    public void addMember(String memberName) {
        if (!members.contains(memberName)) {
            members.add(memberName);
        }
    }

    // also this method should be able remove a member from the group
    public void removeMember(String memberName) {
        members.remove(memberName);
    }

    // getter for files list
    public List<String> getFiles() { return files; }

    // this method should upload a file to the group
    public void uploadFile(String fileName) {
        if (!files.contains(fileName)) {
            files.add(fileName);
        }
    }

    // getter for meeting time
    public Date getMeetingTime() { return meetingTime; }

    // setter for meeting time
    public void setMeetingTime(Date meetingTime) { this.meetingTime = meetingTime; }
}