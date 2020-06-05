package util;

import bean.LogSession;
import info.SystemInfo;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;

/**
 * @author JQH
 * @since 下午 7:44 19/09/15
 */
public class LogSessionUtil {
    private static boolean initialized = false;

    public static int getHashSidOfUid(String uid) {
        if (!initialized)
            return -1;
        int[] a = new int[5];
        a[0] = Integer.valueOf(uid.substring(2, 4));
        a[1] = Integer.valueOf(uid.substring(4, 7));
        a[2] = Integer.valueOf(uid.substring(7, 9));
        a[3] = Integer.valueOf(uid.substring(9, 10));
        a[4] = Integer.valueOf(uid.substring(10, 12));
        return getHashValue(a);
    }

    private static int getHashValue(int a[]) {
        int hash = a[4], prime = SystemInfo.SID_RANGE_PRIME;
        for (int i = 0; i < a.length; ++i)
            hash = (hash << 4) ^ (hash >> 28) ^ a[i];
        hash = hash < 0 ? hash * -1 : hash;
        return hash % prime;
    }

    public static boolean init() {
        if (initialized)
            return true;
        initialized = true;
        SystemInfo.setState(SystemInfo.UNPREPARED);
        return true;
    }


    private static LogSession createSession(HttpServletRequest request, String uid) {
        LogSession logSession = getVCodeWithSession(new LogSession(), uid);
        if (request != null)
            request.getSession().setAttribute("logSession", logSession);
        //logSession.curUid = uid;
        return logSession;
    }

    public static LogSession getLogSession(HttpServletRequest request, String uid) {
        if (request == null)
            return createSession(null, uid);
        HttpSession session = request.getSession();
        if (session.getAttribute("logSession") == null)
            return createSession(request, uid);
        LogSession logSession = (LogSession) session.getAttribute("logSession");
        if ("fail".equals(logSession.vCode))
            return getVCodeWithSession(logSession, uid);
        return logSession;
    }

    public static LogSession getVCodeWithSession(LogSession logSession, String uid) {
        getVCodeWithSession(logSession, uid, 0);
        return logSession;
    }

    private static void getVCodeWithSession(LogSession logSession, String uid, int depth) {
        if (depth > SystemInfo.LOGIN_RETRY_TIMES) {
            logSession.vCode = "fail";
            System.out.println("error:vCode failed with retry time exceeded curUid:" + uid + " " + new Date());
            return;
        }
        try {
            String url = "http://202.194.116.132/img/captcha.jpg";
            logSession.vCode = getVCodeRes(url, logSession);
            if (logSession.vCode == null || logSession.sessionId == null)
                getVCodeWithSession(logSession, uid, depth + 1);
        } catch (IOException e) {
            logSession.vCode = "fail";
            System.out.println("error:vCode failed with " + e.getMessage() + " curUid:" + uid + " " + new Date());
        }
    }

    private static String getVCodeRes(String url, LogSession logSession) throws IOException {
        Connection.Response rs;
        if (logSession.sessionId == null) {
            rs = Jsoup.connect(url).ignoreContentType(true).timeout(SystemInfo.CONNECTION_TIMEOUT)
                    .ignoreHttpErrors(true).execute();
            logSession.sessionId = rs.cookie("JSESSIONID");
        } else {
            rs = Jsoup.connect(url).cookie("JSESSIONID", logSession.sessionId).timeout(SystemInfo.CONNECTION_TIMEOUT)
                    .ignoreContentType(true).execute();
        }
        return VCodeUtil.getVCodeRes(rs.bodyStream());
    }

}
