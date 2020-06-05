package servlet;

import bean.ManageOperationInter;
import info.SystemInfo;
import org.json.JSONObject;
import util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageServlet extends HttpServlet {
    private static String lastTimeStamp = "";
    private static int curManagerId = 21483;
    private static FileWriter logWriter;

    private static Map<String, ManageOperationInter> operationMap = new HashMap<>();

    public void destroy() {
        try {
            logWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        File file = new File(ManageServlet.class.getResource("/").getPath() + "manageLog.txt");
        try {
            if (!file.exists())
                file.createNewFile();
            logWriter = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        operationMap.put("test", value -> value);/**测试管理功能*/
        operationMap.put("setTermStartWeekNum", value -> {/**设置学期开始周次*/
            SystemInfo.TERM_START_WEEK_NUM = Integer.valueOf(value);
            return "curWeekNum=" + CalendarUtil.getCurWeekNum();
        });
        operationMap.put("updatePwd", value -> {/**更新后台管理密码*/
            if (value.length() % 16 != 0)
                return "Password length should be multiple of 16!";
            else {
                SystemInfo.manageKey = value;
                return "Succeed! Don't forget new password!";
            }
        });
        operationMap.put("startSystem", value -> {/**启动系统*/
            SystemInfo.setState(SystemInfo.PREPARED);
            return "System started successfully!";
        });
        operationMap.put("shutDownSystem", value -> {/**关闭系统*/
            SystemInfo.setState(SystemInfo.UNPREPARED);
            return "System has shut down!";
        });
        operationMap.put("setCookieMaxAge", value -> {/**设置Cookie生存周期*/
            SystemInfo.COOKIE_MAX_AGE = Integer.valueOf(value);
            return "CookieMaxAge=" + value;
        });
        operationMap.put("setTryLockTime", value -> {/**设置尝试获取锁的等待时间*/
            SystemInfo.TRY_LOCK_TIME = Integer.valueOf(value);
            return "curTryLockTime=" + value;
        });
        operationMap.put("setLoginRetryTimes", value -> {/**设置登录失败重试次数*/
            SystemInfo.LOGIN_RETRY_TIMES = Integer.valueOf(value);
            return "curLoginRetryTimes=" + value;
        });
        operationMap.put("setConnectionTimeout", value -> { /**设置获取数据时连接超时时间*/
            SystemInfo.CONNECTION_TIMEOUT = Integer.valueOf(value);
            return "curConnectionTimeout=" + value;
        });
        operationMap.put("setAsyncServletTimeout", value -> {/**设置异步操作超时时间*/
            SystemInfo.ASYNC_SERVLET_TIMEOUT = Integer.valueOf(value);
            return "curAsyncServletTimeout=" + value;
        });
        operationMap.put("setAsync", value -> {
            int n = Integer.valueOf(value);
            if (n != 0 && n != 1)
                return "Wrong Command!";
            SystemInfo.IS_ASYNC = n;
            return n == 1 ? "System is async!" : "System is sync!";
        });
        operationMap.put("setCurTerm", value -> {/**设置当前学期*/
            SystemInfo.CUR_TERM = value;
            return "curTerm=" + value;
        });
        operationMap.put("setUpdateTimeTableInterval", value -> {/**设置用户更新课表最低间隔*/
            SystemInfo.UPDATE_TIMETABLE_INTERVAL = Integer.valueOf(value);
            return "curUpdateTimeTableInterval=" + value;
        });
        operationMap.put("setUpdateGradeInterval", value -> {/**设置用户更新成绩最低间隔*/
            SystemInfo.UPDATE_GRADE_INTERVAL = Integer.valueOf(value);
            return "curUpdateGradeInterval=" + value;
        });
        operationMap.put("setUpdateExamInterval", value -> {
            SystemInfo.UPDATE_EXAM_INTERVAL = Integer.valueOf(value);
            return "curUpdateExamInterval=" + value;
        });
        operationMap.put("closeDataSource", value -> {/**关闭数据源*/
            if (C3P0Util.getMysqlDS() == null && C3P0Util.getSQLiteDS() == null)
                return "All data source has already been closed!";
            else {
                if (C3P0Util.getMysqlDS() != null)
                    C3P0Util.getMysqlDS().close();
                if (C3P0Util.getSQLiteDS() != null)
                    C3P0Util.getSQLiteDS().close();
                return "Close data source success!";
            }
        });
        operationMap.put("setConnectedToVPN", value -> {
            if ("connected".equals(value)) {
                SystemInfo.CONNECTED_TO_VPN = true;
                return "curConnectedState=connected";
            } else if ("unconnected".equals(value)) {
                SystemInfo.CONNECTED_TO_VPN = false;
                return "curConnectedState=unconnected";
            } else
                return "wrong command!";
        });
        operationMap.put("addAdmin", value -> {
            int res = LogUtil.addAdmin(value);
            if (res == -1)
                return "wrong uid";
            return res == 1 ? "this user is already an admin!" : "add admin succeed!";
        });
        operationMap.put("removeAdmin", value -> {
            int res = LogUtil.removeAdmin(value);
            if (res == -1)
                return "wrong uid";
            return res == 1 ? "this user is not an admin!" : "remove admin succeed!";
        });
        operationMap.put("isAdmin", value -> LogUtil.isAdmin(value) ? "this user is admin" : "this user is not admin");
        operationMap.put("showAdminList", value -> LogUtil.getAdminList());

        operationMap.put("modServerSessionNum", value -> {
            SystemInfo.CUR_SESSION_NUM.set(Integer.valueOf(value));
            return "curServerSessionNum=" + value;
        });
        operationMap.put("modOnlineUserNum", value -> {
            SystemInfo.CUR_ONLINE_USER_NUM.set(Integer.valueOf(value));
            return "curOnlineUserNum=" + value;
        });
        operationMap.put("modConnectedUserNum", value -> {
            SystemInfo.CUR_CONNECTED_USER_NUM.set(Integer.valueOf(value));
            return "curConnectedUserNum=" + value;
        });
        operationMap.put("backstageLogin", value -> {
            String[] items = value.split("[)]");
            String uid = items[0], pwd = items[1];
            return LogUtil.login(uid, pwd, null, null);
        });
        operationMap.put("addIPBlackList", value -> {
            int res = RequestUtil.addToBlackList(value);
            return res == 1 ? "this ip is already on the blacklist!" : "add ip succeed!";
        });
        operationMap.put("removeIPBlackList", value -> {
            int res = RequestUtil.removeFromBlackList(value);
            return res == 1 ? "this ip is not in the blacklist!" : "remove ip succeed!";
        });
        operationMap.put("showIPBlackList", value -> RequestUtil.getBlackList());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getServletPath().contains("manage") && "1".equals(request.getParameter("set"))) {
            setOption(request, response);
        } else if (request.getServletPath().contains("monitor") && "1".equals(request.getParameter("login"))) {
            monitorLogin(request, response);
        } else
            response.setStatus(404);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getServletPath().contains("manage") && "1".equals(request.getParameter("show"))) {
            showManagePage(request, response);
        } else if (request.getServletPath().contains("monitor") && "1".equals(request.getParameter("show"))) {
            showMonitorPage(request, response);
        } else
            response.setStatus(404);
    }

    private void showManagePage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("managerId", curManagerId);
        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/manage/manage.jsp");
        dispatcher.forward(request, response);
    }

    private void setOption(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String data = request.getParameter("data");
        data = AESUtil.decrypt(data, SystemInfo.manageKey);
        Map<String, String> datas = paramParse(data);
        if (!permissionVerify(request, response, datas))
            return;
        String operation = datas.get("operation");
        String value = datas.get("value");
        String res;
        if (operationMap.containsKey(operation))
            res = operationMap.get(operation).operate(value);
        else
            res = "Wrong Command!";
        String logStr = "Time:" + new Date() + "\nIP:" + RequestUtil.getRequestIpAddress(request) + "\n";
        logStr += "Data:\n" + data + "\n" + "Result:\n" + res + "\n\n";
        logWriter.write(logStr);
        logWriter.flush();
        JSONObject json = new JSONObject();
        json.put("msg", res);
        json.put("manageId", curManagerId);
        String str = AESUtil.encrypt(json.toString(), SystemInfo.manageKey);
        response.getWriter().write(str);
    }

    private Map<String, String> paramParse(String data) {
        Map<String, String> map = new HashMap<>();
        String[] items = data.split("[&]");
        String[] entry;
        for (String item : items) {
            entry = item.split("[=]");
            if (entry.length == 2)
                map.put(entry[0], entry[1]);
            else if (entry.length == 1)
                map.put(entry[0], "");
        }
        return map;
    }

    private boolean permissionVerify(HttpServletRequest request, HttpServletResponse response, Map<String, String> datas) throws IOException {
        /*来自本地的请求*/
        if ("10086".equals(request.getParameter("fromLocal"))
                && (request.getAttribute("ymLocal") != null || SystemInfo.LOCAL_IP_ADDRESS.equals(RequestUtil.getRequestIpAddress(request)))) {
            String timeStamp = datas.get("timeStamp");
            if (new Date().getTime() - Long.valueOf(timeStamp) > 5000)
                return false;
            return true;
        }

        String timeStamp = datas.get("timeStamp");
        if (!timeStamp.matches("[0-9]{13}"))
            return false;
        int managerId = Integer.valueOf(datas.get("managerId"));
        JSONObject json = new JSONObject();
        if (lastTimeStamp.equals(timeStamp) || new Date().getTime() - Long.valueOf(timeStamp) > 5000) {
            response.setStatus(404);
            return false;
        } else if (managerId != curManagerId) {
            json.put("error", "ManagerId is invalid!");
            response.getWriter().write(AESUtil.encrypt(json.toString(), SystemInfo.manageKey));
            return false;
        } else if (request.getAttribute("ConcurrentError") != null
                && 1 == (int) request.getAttribute("ConcurrentError")) {
            json.put("error", "Concurrent Error! Someone else is operating!");
            response.getWriter().write(AESUtil.encrypt(json.toString(), SystemInfo.manageKey));
            return false;
        }
        curManagerId = (int) (Math.random() * 1000000);
        lastTimeStamp = timeStamp;
        return true;
    }

    private void showMonitorPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession().getAttribute("isAdmin") == null) {
            response.setStatus(404);
            return;
        }
        request.getSession().setAttribute("monitorAccess", true);
        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/manage/monitor.jsp");
        dispatcher.forward(request, response);
    }

    private void monitorLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pwd = request.getParameter("pwd");
        String aesKey = (String) request.getSession().getAttribute("aesKey");
        if (aesKey == null || pwd == null)
            return;
        pwd = AESUtil.decrypt(pwd, aesKey);
        if (pwd == null || !isNumeric(pwd.substring(0, 13)) || new Date().getTime() - Long.valueOf(pwd.substring(0, 13)) > 5000
                || !pwd.substring(13).equals(SystemInfo.manageKey))
            return;//验证失败
        request.getSession().setAttribute("monitorAccess", true);
        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/manage/monitor.jsp");
        dispatcher.forward(request, response);
    }

    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches())
            return false;
        return true;
    }

}
