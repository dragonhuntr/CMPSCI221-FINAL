import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class StudyPlan {
    private String courseName;
    private String name; // Study plan name
    private List<Coursework> courseworkList; // List of coursework items
    private String details;
    private Date dueDate;
    private String status;

    public StudyPlan(String courseName, String name) {
        this.courseName = courseName;
        this.name = name;
        this.courseworkList = new ArrayList<>();
    }

    public String getCourseName() {
        return courseName;
    }

    public String getName() {
        return name;
    }

    public List<Coursework> getCourseworkList() {
        return courseworkList;
    }

    public void addCoursework(Coursework coursework) {
        courseworkList.add(coursework);
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Study Plan Details:");
        sb.append("\nCourse Name: ").append(courseName);
        sb.append("\nStudy Plan Name: ").append(name);
        sb.append("\nDetails: ").append(details == null ? "Not set" : details);
        sb.append("\nCoursework:");
        if (courseworkList.isEmpty()) {
            sb.append(" None");
        } else {
            for (Coursework cw : courseworkList) {
                sb.append("\n- ").append(cw.getName());
            }
        }
        sb.append("\nDue Date: ").append(dueDate == null ? "Not set" : new SimpleDateFormat("MM-dd-yyyy").format(dueDate));
        sb.append("\nStatus: ").append(status == null ? "Not set" : status);
        return sb.toString();
    }
}
