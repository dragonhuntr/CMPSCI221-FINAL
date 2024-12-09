import java.util.Date;

public class Notification {
    private Date sentDate;
    private String content;
    private boolean isRead;

    public Notification(Date sentDate, String content) {
        this.sentDate = sentDate;
        this.content = content;
        this.isRead = false;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public String getContent() {
        return content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
