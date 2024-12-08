import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Group {
    private String name;
    private List<String> members = new ArrayList<>();
    private List<String> files = new ArrayList<>();
    private Date meetingTime;

    public Group(String name) {
        this.name = name;
    }

    public String getName() { 
        return name; 
    }

    public List<String> getMembers() {
        return members;
    }

    public List<String> getFiles() { 
        return files; 
    }

    public void addMember(String member) { 
        members.add(member); 
    }

    public void removeMember(String member) { 
        members.remove(member); 
    }

    public void uploadFile(String file) { 
        files.add(file); 
    }

    public void removeFile(String file) { 
        files.remove(file); 
    }

    public Date getMeetingTime() { 
        return meetingTime; 
    }

    public void setMeetingTime(Date meetingTime) { 
        this.meetingTime = meetingTime; 
    }
}
