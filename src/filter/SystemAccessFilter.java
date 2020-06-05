package filter;

import info.SystemInfo;
import util.RequestUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SystemAccessFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        //System.out.println("SystemAccessFilter:" + ((HttpServletRequest)req).getRequestURL());
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        //response.setHeader("Strict-Transport-Security", "max-age=31622400; includeSubDomains");
        if(RequestUtil.isOnBlackList(request))
            return;
        if("/_async_".equals(request.getServletPath().substring(0, 8))) {//防止跳过AsyncDispatcherServlet直接请求异步资源
            response.setStatus(404);
            return;
        }
        if(request.getServletPath().equals("/manage.ym")
                && "1".equals(request.getParameter("set"))){//如果是管理员操作请求
            if(!SystemInfo.isManageAvailable()){//并且当前正在处理其他管理员操作请求
                request.setAttribute("ConcurrentError", 1);//请求参数中添加并发错误参数
            }else{
                SystemInfo.setManageAvailable(false);
            }
        }
        if (SystemInfo.getState() != SystemInfo.PREPARED) {//系统未启动
            if (request.getServletPath().equals("/manage.ym")
                    || (request.getHeader("Referer") != null
                    && request.getHeader("Referer").indexOf("/manage.ym?show=1") != -1)) {//管理员操作
                chain.doFilter(req, resp);
                if(!SystemInfo.isManageAvailable())//管理员操作完成
                    SystemInfo.setManageAvailable(true);
                return;
            }
            int state = SystemInfo.getState();
            if (state == SystemInfo.UNPREPARED) {//跳转到系统未启动提示页面
                response.sendRedirect(request.getContextPath() + "/pages/notice/jump.jsp?ref=3&env=" + request.getParameter("env"));
            }
        } else{//正常情况
            chain.doFilter(req, resp);
            if(!SystemInfo.isManageAvailable())//管理员操作完成
                SystemInfo.setManageAvailable(true);
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
