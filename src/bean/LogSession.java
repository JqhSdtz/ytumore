package bean;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LogSession {
    public String sessionId;
    public String vCode;
    public String curUid;
    public Lock lock;
    public static final int OUT_OF_DATE = 999;
    public LogSession(){
        sessionId = null;
        vCode = null;
        lock = new ReentrantLock(false);
    }
    public LogSession(String sessionId, String vCode) {
        this.sessionId = sessionId;
        this.vCode = vCode;
        lock = new ReentrantLock(false);
    }

    public String toString(){
        return "sessionId:" + sessionId + ' ' + "vCode:" + vCode + ' ' + "curUid:" + curUid;
    }
}
