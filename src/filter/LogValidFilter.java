package filter;

import bean.LogSession;
import info.SystemInfo;
import util.LogSessionUtil;
import util.LogUtil;
import util.RequestUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class LogValidFilter implements Filter {
    private static ConcurrentHashMap<String, Long> gradeUpdateFilterMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Long> timeTableUpdateFilterMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Long> examUpdateFilterMap = new ConcurrentHashMap<>();

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        int processOutDateCnt = 0;
        if (!SystemInfo.CONNECTED_TO_VPN) {//跳转到VPN断开提示页面
            response.sendRedirect(request.getContextPath() + "/pages/notice/jump.jsp?ref=2&type=1&env=" + request.getParameter("env"));
            return;
        }
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("clientUid");
        String connected = (String) session.getAttribute("connected");
        if (uid == null || connected == null) {//未登录，尝试使用cookie登录
            String _uid, pwd;
            String cookiePwd = (String) session.getAttribute("cookiePwd");
            if (cookiePwd != null) {
                _uid = uid;
                pwd = "2" + cookiePwd;
            } else {
                String str[] = RequestUtil.getUidAndPwdFromCookie(request);
                _uid = str[0];
                pwd = str[1];
            }
            if (_uid != null && !"".equals(_uid) && pwd != null && !"".equals(pwd)) {
                String state = LogUtil.login(_uid, pwd, request, response);
                if (state.charAt(0) == '0' || Integer.valueOf(state) > 0) {
                    chain.doFilter(request, response);
                    if(processOutDateCnt < 2) {
                        processOutDate(request, response, chain);
                        ++processOutDateCnt;
                    }
                } else
                    LogUtil.dispatchToLogin(request, response, Integer.valueOf(state));
            } else {
                LogUtil.dispatchToLogin(request, response, 0);
            }
            return;
        } else {//已登录，过滤频繁更新请求
            String reqServlet = request.getServletPath();
            if (request.getAttribute("isFromEmpty") == null) {//不来自数据库为空的情况
                int type = 0;
                if (reqServlet.contains("newGrade"))
                    type = 1;
                else if (reqServlet.contains("newTimeTable"))
                    type = 2;
                else if (reqServlet.contains("newExam"))
                    type = 3;
                if (type != 0 && !userUpdateFreqFilter(type, request, response))
                    return;
            }
        }
        chain.doFilter(request, response);
        if(processOutDateCnt < 2) {
            processOutDate(request, response, chain);
            ++processOutDateCnt;
        }
    }

    private void processOutDate(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (response.getStatus() == LogSession.OUT_OF_DATE) {
            HttpSession session = request.getSession();
            String uid = (String) session.getAttribute("clientUid");
            String pwd = (String) session.getAttribute("clientPwd");
            LogSession logSession = LogSessionUtil.getLogSession(request, uid);
            int state = LogUtil.loginWithSession(uid, pwd, logSession);
            if (state < 0)
                LogUtil.dispatchToLogin(request, response, state);
            else {
                response.setStatus(200);
                chain.doFilter(request, response);
            }
        }
    }

    private boolean userUpdateFreqFilter(int type, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uid = (String) request.getSession().getAttribute("clientUid");
        //type == 1表示刷新成绩，2表示刷新课表，3表示刷新考试安排
        if (type == 1 && !gradeUpdateFilterMap.containsKey(uid)) {
            gradeUpdateFilterMap.put(uid, new Date().getTime());
            return true;
        } else if (type == 2 && !timeTableUpdateFilterMap.containsKey(uid)) {
            timeTableUpdateFilterMap.put(uid, new Date().getTime());
            return true;
        } else if (type == 3 && !examUpdateFilterMap.containsKey(uid)) {
            examUpdateFilterMap.put(uid, new Date().getTime());
            return true;
        }
        Long curTime = new Date().getTime();
        if (judgeInterval(uid, type)) {//跳转到更新频率过高提示页面
            response.sendRedirect(request.getContextPath() + "/pages/notice/jump.jsp?ref=4&type=" + type + "&env=" + request.getParameter("env"));
            return false;
        }
        if (type == 1)
            gradeUpdateFilterMap.put(uid, curTime);
        else if (type == 2)
            timeTableUpdateFilterMap.put(uid, curTime);
        else if (type == 3)
            examUpdateFilterMap.put(uid, curTime);
        return true;
    }

    private boolean judgeInterval(String uid, int type) {
        Long curTime = new Date().getTime();
        if (type == 1) {//成绩
            if (curTime - gradeUpdateFilterMap.get(uid) < SystemInfo.UPDATE_GRADE_INTERVAL)
                return true;
            else
                return false;
        } else if (type == 2) {//课表
            if (curTime - timeTableUpdateFilterMap.get(uid) < SystemInfo.UPDATE_TIMETABLE_INTERVAL)
                return true;
            else
                return false;
        } else if(type == 3){
            if (curTime - examUpdateFilterMap.get(uid) < SystemInfo.UPDATE_EXAM_INTERVAL)
                return true;
            else
                return false;
        }
        return false;
    }

}
