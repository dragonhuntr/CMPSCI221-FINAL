import java.util.Date;
import java.text.SimpleDateFormat;

public class StudyPlan {
    private String courseName; // The course associated with the study plan
    private String coursework;
    private String details;   // The details of the study plan
    private Date dueDate;     // Optional: due date for the study plan
    private String priority;  // Optional: priority level (e.g., High, Medium, Low)

    public StudyPlan(String courseName, String details, Date dueDate, String priority) {
        this.courseName = courseName;
        this.details = details;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Study Plan Details:");
        sb.append("\nCourse Name: ").append(courseName);
        sb.append("\nDetails: ").append(details);
        if (dueDate != null) {
            sb.append("\nDue Date: ").append(new SimpleDateFormat("MM-dd-yyyy").format(dueDate));
        }
        if (priority != null) {
            sb.append("\nPriority: ").append(priority);
        }
        return sb.toString();
    }
}
