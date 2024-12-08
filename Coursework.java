import java.util.Date;
import java.text.SimpleDateFormat;

public class Coursework {
    private String name;
    private String details;
    private Date dueDate;
    private String status; // New status field

    public Coursework(String name, String details, Date dueDate, String status) {
        this.name = name;
        this.details = details;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        StringBuilder sb = new StringBuilder();
        sb.append("Coursework Name: ").append(name);
        sb.append("\nDetails: ").append(details == null ? "Not set" : details);
        sb.append("\nDue Date: ").append(dueDate == null ? "Not set" : new SimpleDateFormat("MM-dd-yyyy").format(dueDate));
        sb.append("\nStatus: ").append(status == null ? "Not set" : status);
        return sb.toString();
    }
}