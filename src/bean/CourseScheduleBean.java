package bean;

public class CourseScheduleBean {
    private String weeks;
    private int weekDay;
    private int startSec;
    private int secNum;
    private String building;
    private String room;
    private CourseBean course;

    public CourseScheduleBean() {
    }

    public CourseScheduleBean(String weeks, int weekDay, int startSec, int secNum, String building, String room) {
        this.weeks = weeks;
        this.weekDay = weekDay;
        this.startSec = startSec;
        this.secNum = secNum;
        this.building = building;
        this.room = room;
    }

    public CourseBean getCourse() {
        return course;
    }

    public void setCourse(CourseBean course) {
        this.course = course;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public int getStartSec() {
        return startSec;
    }

    public void setStartSec(int startSec) {
        this.startSec = startSec;
    }

    public int getSecNum() {
        return secNum;
    }

    public void setSecNum(int secNum) {
        this.secNum = secNum;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
