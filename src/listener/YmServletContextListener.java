package listener;

import info.SystemInfo;
import util.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener()
public class YmServletContextListener implements ServletContextListener,
        HttpSessionListener, HttpSessionAttributeListener {

    public YmServletContextListener() {
    }

    public void contextInitialized(ServletContextEvent sce) {
        ThreadUtil.createAsyncExecutor();// 创建异步Servlet线程池
        C3P0Util.init();//先定义数据库
        SystemInfo.init();
        LogSessionUtil.init();
        CalendarUtil.init();
        LogUtil.readAdminListFromFile();
        LogUtil.addAdmin(SystemInfo.INIT_ADMIN);
        RequestUtil.readIpBlackListFromFile();
        SystemInfo.setState(SystemInfo.PREPARED);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        C3P0Util.closeDataSource();
        SystemInfo.writeInfoToFile();
        RequestUtil.writeIpBlackListToFile();
        LogUtil.writeAdminListFromFile();
        ThreadUtil.closeAsyncExecutor();//关闭异步Servlet线程池
    }

    public void sessionCreated(HttpSessionEvent se) {
        SystemInfo.CUR_SESSION_NUM.incrementAndGet();
        if (se.getSession().getAttribute("clientUid") != null) {
                SystemInfo.CUR_ONLINE_USER_NUM.incrementAndGet();
            if (se.getSession().getAttribute("connected") != null) {
                SystemInfo.CUR_CONNECTED_USER_NUM.incrementAndGet();
            }
        }
    }

    public void sessionDestroyed(HttpSessionEvent se) {
        if (se.getSession().getAttribute("clientUid") != null) {
            if (SystemInfo.CUR_ONLINE_USER_NUM.get() > 0)
                SystemInfo.CUR_ONLINE_USER_NUM.decrementAndGet();
            if (se.getSession().getAttribute("connected") != null) {
                //有clientUid才可能有connected属性
                if (SystemInfo.CUR_CONNECTED_USER_NUM.get() > 0)
                    SystemInfo.CUR_CONNECTED_USER_NUM.decrementAndGet();
            }
        }
        if (SystemInfo.CUR_SESSION_NUM.get() > 0)
            SystemInfo.CUR_SESSION_NUM.decrementAndGet();
    }


    public void attributeAdded(HttpSessionBindingEvent sbe) {
        if ("clientUid".equals(sbe.getName()))
            SystemInfo.CUR_ONLINE_USER_NUM.incrementAndGet();
        else if ("connected".equals(sbe.getName()))
            SystemInfo.CUR_CONNECTED_USER_NUM.incrementAndGet();
    }

    public void attributeRemoved(HttpSessionBindingEvent sbe) {
        if ("clientUid".equals(sbe.getName())) {
            if (SystemInfo.CUR_ONLINE_USER_NUM.get() > 0)
                SystemInfo.CUR_ONLINE_USER_NUM.decrementAndGet();
        } else if ("connected".equals(sbe.getName())) {
            if (SystemInfo.CUR_CONNECTED_USER_NUM.get() > 0)
                SystemInfo.CUR_CONNECTED_USER_NUM.decrementAndGet();
        }
    }

    public void attributeReplaced(HttpSessionBindingEvent sbe) {
      /* This method is invoked when an attibute
         is replaced in a session.
      */
    }
}
