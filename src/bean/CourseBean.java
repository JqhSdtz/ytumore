package bean;

import java.util.ArrayList;
import java.util.List;

public class CourseBean {
    private int cUid;
    private String cNo;
    private String cName;
    private int cSeq;
    private String cTerm;
    private String cCredit;
    private String cAttr;
    private String cTeacher;
    private List<CourseScheduleBean> courseScheduleList;

    public CourseBean(CourseBean ori) {
        this.cNo = ori.cNo;
        this.cName = ori.cName;
        this.cSeq = ori.cSeq;
        this.cTerm = ori.cTerm;
        this.cCredit = ori.cCredit;
        this.cAttr = ori.cAttr;
        this.cTeacher = ori.cTeacher;
        this.courseScheduleList = ori.courseScheduleList;
    }

    public CourseBean() {
        courseScheduleList = new ArrayList<>();
    }

    public int getcUid() {
        return cUid;
    }

    public void setcUid(int cUid) {
        this.cUid = cUid;
    }

    public String getcTerm() {
        return cTerm;
    }

    public void setcTerm(String cTerm) {
        this.cTerm = cTerm;
    }

    public String getcNo() {
        return cNo;
    }

    public void setcNo(String cNo) {
        this.cNo = cNo;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public int getcSeq() {
        return cSeq;
    }

    public void setcSeq(int cSeq) {
        this.cSeq = cSeq;
    }

    public String getcCredit() {
        return cCredit;
    }

    public void setcCredit(String cCredit) {
        this.cCredit = cCredit;
    }

    public String getcAttr() {
        return cAttr;
    }

    public void setcAttr(String cAttr) {
        this.cAttr = cAttr;
    }

    public String getcTeacher() {
        return cTeacher;
    }

    public void setcTeacher(String cTeacher) {
        this.cTeacher = cTeacher;
    }

    public List<CourseScheduleBean> getCourseScheduleList() {
        return courseScheduleList;
    }

    public void setCourseScheduleList(List<CourseScheduleBean> courseScheduleList) {
        this.courseScheduleList = courseScheduleList;
    }
}
