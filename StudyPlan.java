import java.util.Date;
import java.text.SimpleDateFormat;

public class StudyPlan {
    private String courseName; // The course associated with the study plan
    private String coursework;
    private String details;   // The details of the study plan
    private Date dueDate;     // Optional: due date for the study plan
    private String status;  // Optional: priority level (e.g., High, Medium, Low)

    public StudyPlan(String courseName, String coursework, String details, Date dueDate, String status) {
        this.courseName = courseName;
        this.coursework = coursework;
        this.details = details;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCoursework() {
        return coursework;
    }

    public void setCoursework(String coursework) {
        this.coursework = coursework;
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
        sb.append("\nDetails: ").append(details);
        if (dueDate != null) {
            sb.append("\nDue Date: ").append(new SimpleDateFormat("MM-dd-yyyy").format(dueDate));
        }
        if (status != null) {
            sb.append("\nStatus: ").append(status);
        }
        return sb.toString();
    }
}
