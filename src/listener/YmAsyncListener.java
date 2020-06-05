package listener;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

/**
 * @author JQH
 * @since 下午 5:49 19/11/10
 */
@WebListener
public class YmAsyncListener implements AsyncListener{
    public boolean hasCompleted = false;

    public YmAsyncListener() {};

    @Override
    public void onComplete(AsyncEvent asyncEvent) {
        hasCompleted = true;
        // we can do resource cleanup activity here
    }

    @Override
    public void onError(AsyncEvent asyncEvent) throws IOException{
        hasCompleted = true;
        System.out.println("YmAsyncListener onError " + new Date());
        asyncEvent.getThrowable().printStackTrace();
        sendToBusyPage(asyncEvent);
        //we can return error response to client
    }

    @Override
    public void onStartAsync(AsyncEvent asyncEvent) {
        //System.out.println("YmAsyncListener onStartAsync " + new Date());
        //we can log the event here
    }

    @Override
    public void onTimeout(AsyncEvent asyncEvent) throws IOException {
        hasCompleted = true;
        System.out.println("YmAsyncListener timeout! " + new Date());
        sendToBusyPage(asyncEvent);
    }

    private void sendToBusyPage(AsyncEvent asyncEvent) throws IOException {
        HttpServletRequest request = (HttpServletRequest)asyncEvent.getSuppliedRequest();
        request.setAttribute("asyncTimeout", true);
        String url = request.getContextPath() + "/pages/notice/jump.jsp?ref=0&env=" + request.getParameter("env");
        String str = "<script>window.location.href='" + url + "'</script>";
        asyncEvent.getSuppliedResponse().getWriter().write(str);
    }
}
