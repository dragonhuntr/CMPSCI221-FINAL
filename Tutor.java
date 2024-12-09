public class Tutor {
    private String name;
    private String tutorClass;
    private String availableDate;
    private String location;
    private boolean isScheduled;
    private String scheduleDate;

    public Tutor(String name, String tutorClass, String availableDate, String location, boolean isScheduled, String scheduleDate) {
        this.name = name;
        this.tutorClass = tutorClass;
        this.availableDate = availableDate;
        this.location = location;
        this.isScheduled = isScheduled;
        this.scheduleDate = scheduleDate;
    }

    public String getName() {
        return name;
    }

    public String getTutorClass() {
        return tutorClass;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public String getLocation() {
        return location;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }
}