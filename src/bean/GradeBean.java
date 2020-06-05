package bean;

public class GradeBean {
    private CourseBean course;
    private String courseGrade;
    private String examTime;
    private int level;
    private String term;
    private int status;

    public GradeBean() {
    }

    public GradeBean(CourseBean course, String courseGrade, String term) {
        this.course = course;
        this.term = term;
        this.courseGrade = courseGrade;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    public String getCourseGrade() {
        return courseGrade;
    }

    public void setCourseGrade(String courseGrade) {
        this.courseGrade = courseGrade;
    }

    public CourseBean getCourse() {
        return course;
    }

    public void setCourse(CourseBean course) {
        this.course = course;
    }
}
