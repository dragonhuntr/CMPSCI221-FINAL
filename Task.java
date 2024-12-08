import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {
    private String title;
    private String description;
    private String type;
    private Date dueDate;
    private String status;

    public Task(String title, String description, String type, Date dueDate, String status) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.dueDate = dueDate;
        this.status = status;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDueDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        return sdf.format(dueDate);
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
}