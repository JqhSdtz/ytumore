package bean;

public class UserBean {
    private static String idPattern = "^[0-9]{12}$";
    private String uid;
    private int sid;
    private String randKey;
    private int lastLoginCode;
    private int loginCnt;

    public UserBean(String uid, int sid, String randKey, int lastLoginCode, int loginCnt) {
        this.uid = uid;
        this.sid = sid;
        this.randKey = randKey;
        this.lastLoginCode = lastLoginCode;
        this.loginCnt = loginCnt;
    }

    public UserBean() {
    }

    public static String getIdPattern() {
        return idPattern;
    }

    public int getLoginCnt() {
        return loginCnt;
    }

    public void setLoginCnt(int loginCnt) {
        this.loginCnt = loginCnt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getLastLoginCode() {
        return lastLoginCode;
    }

    public void setLastLoginCode(int lastLoginCode) {
        this.lastLoginCode = lastLoginCode;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getRandKey() {
        return randKey;
    }

    public void setRandKey(String randKey) {
        this.randKey = randKey;
    }
}
