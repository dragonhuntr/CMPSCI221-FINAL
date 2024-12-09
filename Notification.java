public class Notification {
    private String sentDate;
    private String content;
    private boolean isRead;
    private boolean isDeleted;

    public Notification(String sentDate, String content) {
        this.sentDate = sentDate;
        this.content = content;
        this.isRead = false;
        this.isDeleted = false;
    }

    public String getSentDate() {
        return sentDate;
    }

    public String getContent() {
        return content;
    }

    public boolean isRead() {
        return isRead;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }
}