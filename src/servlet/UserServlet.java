package servlet;

import info.SystemInfo;
import util.AESUtil;
import util.LogUtil;
import util.ThreadUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("1".equals(request.getParameter("login"))) {
            login(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("1".equals(request.getParameter("logout"))) {
            logout(request, response);
        } else if ("1".equals(request.getParameter("show"))) {
            LogUtil.dispatchToLogin(request, response, 0);
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SystemInfo.CONNECTED_TO_VPN) {//跳转到VPN断开提示页面
            response.sendRedirect(request.getContextPath() + "/pages/notice/jump.jsp?ref=2&env=" + request.getParameter("env"));
            ThreadUtil.completeAsyncTask(request);
            return;
        }
        String uid = request.getParameter("uid");
        String pwd = request.getParameter("pwd");
        pwd = AESUtil.decrypt(pwd, (String) request.getSession().getAttribute("aesKey"));
        request.getSession().setAttribute("aesKey", "");
        String state = LogUtil.login(uid, pwd, request, response);
        if (state.charAt(0) != '0' && Integer.valueOf(state) < 0) {
            LogUtil.dispatchToLogin(request, response, Integer.valueOf(state));
        } else {//登录成功，state表示sid
            String oriUrl = request.getParameter("oriUrl");
            if (oriUrl != null && !"".equals(oriUrl) && !"login".equals(oriUrl)) {//登录页面是从其他页面跳转过来的
                response.sendRedirect(oriUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/jump.jsp?env=" + request.getParameter("env"));
            }
        }
        ThreadUtil.completeAsyncTask(request);
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LogUtil.dispatchToLogin(request, response, -5);
    }
}
