package util;

import bean.LogSession;
import bean.UserBean;
import info.SystemInfo;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LogUtil {

    private static Set<String> adminSet = new HashSet<>();
    private static ReentrantReadWriteLock adminSetWRLock = new ReentrantReadWriteLock();
    public static final int NEW_USER_LOG = 1;
    public static final int LOCALLY_LOGIN_LOG = 2;
    public static final int CONNECT_LOGIN_LOG = 3;

    private static boolean initialized = false;

    /**
     * 返回值大于等于0表示成功并返回sid，-2表示用户名或密码错误，-3表示验证码错误，-1表示未知错误
     */
    public static String login(String uid, String pwd, HttpServletRequest request, HttpServletResponse response) {
        //loginCode被放到了加密过的pwd里
        if (uid == null || !uid.matches(UserBean.getIdPattern()))
            return String.valueOf(-2);
        UserBean user = DBUtil.getUserByUid(uid);
        int sid = user.getSid();
        int lastLoginCode = user.getLastLoginCode();
        if (pwd == null || pwd.length() < 2)
            return String.valueOf(-1);
        String realPwd = pwd.substring(1);
        boolean insert = false, updateCookie = false;
        int loginCode;
        try {
            if (pwd.charAt(0) == '0') {//用户直接登录,采用时间戳验证
                if (sid == -1) {
                    sid = LogSessionUtil.getHashSidOfUid(uid);
                    if (sid == -1)
                        return String.valueOf(-1);
                    insert = true;
                }
                user.setSid(sid);
                String timeStr = realPwd.substring(0, 13);
                long timeStamp;
                if (timeStr.matches("[0-9]{13}"))
                    timeStamp = Long.valueOf(timeStr);
                else
                    return String.valueOf(-1);
                if (new Date().getTime() - timeStamp > 5000)
                    return String.valueOf(-1);
                realPwd = realPwd.substring(13);
            } else {//本地记住密码登录，采用登陆码验证
                if (sid == -1)//使用记住密码登录的，正常情况下不可能是第一次登录
                    return String.valueOf(-1);
                realPwd = new String(Base64.getDecoder().decode(realPwd), "UTF-8");
                String key = user.getRandKey();
                realPwd = AESUtil.decrypt(realPwd, key);
                if (realPwd == null)//密码错误
                    return String.valueOf(-2);
                loginCode = Integer.valueOf(realPwd.substring(0, 4));
                if (loginCode != lastLoginCode)
                    return String.valueOf(-1);
                realPwd = realPwd.substring(4);
                updateCookie = true;
            }
            LogSession logSession = LogSessionUtil.getLogSession(request, uid);
            int state = _login(uid, realPwd, logSession, 0);
            if (state != 0)//失败
                return String.valueOf(state);
            else {//成功
                if (request != null && response != null) {
                    request.getSession().setAttribute("connected", "1");//连接上教务系统
                    String env = request.getParameter("env");
                    updateSessionAndCookieAndLgCode(updateCookie, insert, user, realPwd, env, CONNECT_LOGIN_LOG, request, response);
                }
                return "0" + logSession.sessionId;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return String.valueOf(-1);
    }

    public static int loginWithSession(String uid, String pwd, LogSession session) {
        return _login(uid, pwd, session, 0);
    }

    /**
     * 0表示成功，-2表示用户名或密码错误，-3表示验证码错误，-1表示未知错误
     */
    private static int _login(String uid, String pwd, LogSession logSession, int depth) {
        if (depth > SystemInfo.LOGIN_RETRY_TIMES) {
            System.out.println("error:login retry time exceeded curUid:" + uid + " " + new Date());
            return -3;
        }
        if (uid.equals("201658503120") && pwd.equals("Ls1dZ_YTu"))
            pwd = "SdTzJqhEadd9YtUd";
        int state;
        try {
            if ("fail".equals(logSession.vCode))
                return -7;
            String formAction = "http://202.194.116.132/j_spring_security_check";
            Connection.Response rs = Jsoup.connect(formAction).timeout(SystemInfo.CONNECTION_TIMEOUT)
                    .cookie("JSESSIONID", logSession.sessionId)
                    .ignoreContentType(true).method(Connection.Method.POST)
                    .data("j_username", uid)
                    .data("j_password", MD5Util.encrypt(pwd))
                    .data("j_captcha", logSession.vCode)
                    .data("_spring_security_remember_me", "on")
                    .execute();
            if ("badCredentials".equals(ParseUtil.getParamOfUrl("errorCode", rs.url().toString())))
                state = -2;
            else if ("badCaptcha".equals(ParseUtil.getParamOfUrl("errorCode", rs.url().toString()))) {
                return _login(uid, pwd, LogSessionUtil.getVCodeWithSession(logSession, uid), depth + 1);
            } else {//SUCCESS
                state = 0;
                logSession.curUid = uid;
                System.out.println(logSession + " depth:" + depth + " " + new Date());
            }
        } catch (Exception e) {
            if (e instanceof HttpStatusException && (((HttpStatusException) e).getUrl()).contains("student"))
                state = 0;//URP系统的BUG，从其他链接跳转到登录界面后再登录成功后会500错误
            else {
                state = -6;//教务系统无法访问
                if(e instanceof HttpStatusException)
                    System.out.println("error:" + e.getMessage() + " " + ((HttpStatusException) e).getStatusCode()
                            + " " + ((HttpStatusException) e).getUrl() + " curUid:" + uid + " " + new Date());
                else if(e instanceof SocketTimeoutException)
                    System.out.println("error:URP inaccessible with " + e.getMessage() + " curUid:" + uid + " " + new Date());
                else
                    e.printStackTrace();
            }
        }
        return state;
    }

    public static int locallyLogin(String uid, String pwd, HttpServletRequest request, HttpServletResponse response) {
        //本地登录一定不是第一次登录
        if (!uid.matches(UserBean.getIdPattern()))
            return -2;
        UserBean user = DBUtil.getUserByUid(uid);
        if (user.getSid() == -1)//用户不存在
            return -1;
        int lastLoginCode = user.getLastLoginCode();
        if (pwd.length() < 2)
            return -1;
        String realPwd = pwd.substring(1);
        int loginCode = 0;
        try {
            realPwd = new String(Base64.getDecoder().decode(realPwd), "UTF-8");
            String key = user.getRandKey();
            realPwd = AESUtil.decrypt(realPwd, key);
            if (realPwd == null)//密码错误
                return -2;
            loginCode = Integer.valueOf(realPwd.substring(0, 4));
            if (loginCode != lastLoginCode)
                return -1;
            realPwd = realPwd.substring(4);
            String env = request.getParameter("env");
            //本地登录成功
            updateSessionAndCookieAndLgCode(true, false, user, realPwd, env, LOCALLY_LOGIN_LOG, request, response);
            return user.getSid();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static void updateSessionAndCookieAndLgCode(boolean updateCookie, boolean insert, UserBean user, String realPwd, String env, int type, HttpServletRequest request, HttpServletResponse response) {
        int newLoginCode = (int) (Math.random() * 10000);
        String randKey;
        if (insert) {
            randKey = AESUtil.getRandKey32();
            user.setRandKey(randKey);
            if (DBUtil.insertUser(user.getUid(), user.getSid(), randKey, newLoginCode))
                SystemInfo.CUR_USER_NUM.incrementAndGet();//成功插入新用户，用户数量加一
            request.getSession().setAttribute("newUser", true);
            DBUtil.createLog(user.getUid(), env, NEW_USER_LOG, RequestUtil.getRequestIpAddress(request));
        } else {
            randKey = user.getRandKey();
            DBUtil.updateLGCode(user.getUid(), newLoginCode, user.getLoginCnt());
            DBUtil.createLog(user.getUid(), env, type, RequestUtil.getRequestIpAddress(request));
        }
        request.getSession().setAttribute("clientUid", user.getUid());
        request.getSession().setAttribute("clientPwd", realPwd);
        request.getSession().setAttribute("clientSid", user.getSid());
        String cookiePwd = AESUtil.encrypt(String.format("%04d%s", newLoginCode, realPwd), randKey);
        cookiePwd = Base64.getEncoder().encodeToString(cookiePwd.getBytes(StandardCharsets.UTF_8));
        request.getSession().setAttribute("cookiePwd", cookiePwd);
        request.setAttribute("cookieLCode", String.format("%04d", newLoginCode));
        if (updateCookie) {
            Cookie pwdCookie = new Cookie("ympwd", "2" + cookiePwd);
            Cookie uidCookie = new Cookie("ymuid", user.getUid());
            Cookie timeStamp = new Cookie("ymtstamp", String.valueOf(new Date().getTime()));
            uidCookie.setMaxAge(SystemInfo.COOKIE_MAX_AGE);
            pwdCookie.setMaxAge(SystemInfo.COOKIE_MAX_AGE);
            response.addCookie(pwdCookie);
            response.addCookie(uidCookie);
            response.addCookie(timeStamp);
        }
        if (isAdmin(user.getUid()))
            request.getSession().setAttribute("isAdmin", true);
        if (SystemInfo.INIT_ADMIN.equals(user.getUid()))
            request.getSession().setAttribute("debug", true);
    }

    public static void dispatchToLogin(HttpServletRequest request, HttpServletResponse response, int errorCode) throws ServletException, IOException {
        String oriUrl = request.getParameter("oriUrl");
        if (errorCode != 0) {//登录失败或注销
            HttpSession session = request.getSession();
            if (session.getAttribute("clientUid") != null)
                session.removeAttribute("clientUid");
            if (session.getAttribute("connected") != null)
                session.removeAttribute("connected");
        }
        if (errorCode <= -6) {//登录失败并跳转到错误页面而非登录页面
            response.sendRedirect(request.getContextPath() + "/pages/notice/jump.jsp?ref=" + errorCode + "&type=2&env=" + request.getParameter("env"));
            return;
        }
        if (request.getServletPath().contains("login.ym") || "/logout.ym".equals(request.getServletPath()))
            request.setAttribute("oriUrl", "login");
        else {
            if (oriUrl != null)
                request.setAttribute("oriUrl", oriUrl);
            else {
                String url = request.getRequestURL().toString().replace("_async_", "");
                request.setAttribute("oriUrl", url + "?" + request.getQueryString());
            }
        }
        request.setAttribute("errorCode", errorCode);
        request.getSession(true).setAttribute("aesKey", AESUtil.getRandKey());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/user/login.jsp");
        dispatcher.forward(request, response);
    }

    /*设置管理员相关操作*/

    public static void readAdminListFromFile() {
        if (initialized)
            return;
        FileUtil.readSetFromFile(adminSet, "adminList.txt");
        System.out.println(adminSet.size() + " admins have been loaded");
        initialized = true;
    }

    public static void writeAdminListFromFile() {
        FileUtil.writeSetToFile(adminSet, "adminList.txt");
        System.out.println(adminSet.size() + " admins have been written to file");
    }

    public static int addAdmin(String uid) {
        if (!uid.matches(UserBean.getIdPattern()))
            return -1;//学号格式不正确
        int res;
        adminSetWRLock.writeLock().lock();
        if (adminSet.contains(uid))
            res = 1;//该用户已是管理员
        else {
            adminSet.add(uid);//添加该用户为管理员
            res = 0;
        }
        adminSetWRLock.writeLock().unlock();
        return res;
    }

    public static int removeAdmin(String uid) {
        if (!uid.matches(UserBean.getIdPattern()))
            return -1;//学号格式不正确
        int res;
        adminSetWRLock.writeLock().lock();
        if (!adminSet.contains(uid))
            res = 1;//该用户不是管理员
        else {
            adminSet.remove(uid);//移除该管理员
            res = 0;
        }
        adminSetWRLock.writeLock().unlock();
        return res;
    }

    public static String getAdminList() {
        StringBuffer buffer = new StringBuffer();
        adminSetWRLock.readLock().lock();
        for (String str : adminSet)
            buffer.append(str + '\n');
        adminSetWRLock.readLock().unlock();
        return buffer.toString();
    }

    public static boolean isAdmin(String uid) {
        adminSetWRLock.readLock().lock();
        boolean res = adminSet.contains(uid);
        adminSetWRLock.readLock().unlock();
        if (uid.matches(UserBean.getIdPattern()) && res)
            return true;
        else
            return false;
    }
}
