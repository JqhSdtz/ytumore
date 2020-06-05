package servlet;

import info.SystemInfo;
import listener.YmAsyncListener;
import util.ThreadUtil;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author JQH
 * @since 下午 5:04 19/11/12
 */
public class AsyncDispatchServlet extends HttpServlet {

    protected void service(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("fromAsyncDispatcher", true);
        dispatch(request, response);
    }

    class AsyncTask implements Runnable {
        HttpServletRequest request;
        HttpServletResponse response;
        YmAsyncListener listener;
        AsyncContext asyncContext;

        AsyncTask(HttpServletRequest request, HttpServletResponse response, YmAsyncListener listener, AsyncContext asyncContext) {
            this.request = request;
            this.response = response;
            this.listener = listener;
            this.asyncContext = asyncContext;
        }

        public void run() {
            String realServletPath = "/_async_" + request.getServletPath().substring(1);
            try {
                asyncContext.dispatch(realServletPath);
                //request.getRequestDispatcher(realServletPath).forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatch(HttpServletRequest request, HttpServletResponse response) {
        if (SystemInfo.IS_ASYNC == 1) {//异步
            AsyncContext asyncContext;
            if (request.isAsyncStarted())
                asyncContext = request.getAsyncContext();
            else
                asyncContext = request.startAsync();
            YmAsyncListener listener = new YmAsyncListener();
            asyncContext.addListener(listener);
            asyncContext.setTimeout(SystemInfo.ASYNC_SERVLET_TIMEOUT);
            ThreadUtil.getAsyncExecutor().execute(new AsyncTask(request, response, listener, asyncContext));
        } else {//同步
            String realServletPath = "/_async_" + request.getServletPath().substring(1);
            try {
                request.getRequestDispatcher(realServletPath).forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
