public class Tutor {
    private String name;
    private String tutorClass;
    private String availableDate;
    private String location;
    private boolean isScheduled;

    public Tutor(String name, String tutorClass, String availableDate, String location, boolean isScheduled) {
        this.name = name;
        this.tutorClass = tutorClass;
        this.availableDate = availableDate;
        this.location = location;
        this.isScheduled = isScheduled;
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
}