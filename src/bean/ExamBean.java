package bean;

/**
 * @author JQH
 * @since 下午 10:22 19/12/10
 */
public class ExamBean {
    private int eUid;
    private String eName;
    private String eBuilding;
    private String eRoom;
    private int eWeekNum;
    private int eWeekDay;
    private String eTerm;
    private String eTime;
    public ExamBean() {}
    public ExamBean(String eName, String eBuilding, String eRoom, int eWeekNum, int eWeekDay, String eTime, String eTerm) {
        this.eName = eName;
        this.eBuilding = eBuilding;
        this.eRoom = eRoom;
        this.eWeekNum = eWeekNum;
        this.eWeekDay = eWeekDay;
        this.eTime = eTime;
        this.eTerm = eTerm;
    }

    public int geteUid() {
        return eUid;
    }

    public void seteUid(int eUid) {
        this.eUid = eUid;
    }

    public String geteTerm() {
        return eTerm;
    }

    public void seteTerm(String eTerm) {
        this.eTerm = eTerm;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public String geteBuilding() {
        return eBuilding;
    }

    public void seteBuilding(String eBuilding) {
        this.eBuilding = eBuilding;
    }

    public String geteRoom() {
        return eRoom;
    }

    public void seteRoom(String eRoom) {
        this.eRoom = eRoom;
    }

    public int geteWeekNum() {
        return eWeekNum;
    }

    public void seteWeekNum(int eWeekNum) {
        this.eWeekNum = eWeekNum;
    }

    public int geteWeekDay() {
        return eWeekDay;
    }

    public void seteWeekDay(int eWeekDay) {
        this.eWeekDay = eWeekDay;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }
}
