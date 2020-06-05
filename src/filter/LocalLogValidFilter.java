package filter;

import util.LogUtil;
import util.RequestUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author JQH
 * @since 下午 8:50 19/09/15
 */
public class LocalLogValidFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
       //System.out.println("LocalLogValidFilter:" + ((HttpServletRequest)req).getRequestURL());
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("clientUid");
        if (uid == null) {
            String str[] = RequestUtil.getUidAndPwdFromCookie(request);
            String _uid = str[0];
            String pwd = str[1];
            if (_uid != null && !"".equals(_uid) && pwd != null && !"".equals(pwd)) {
                int state = LogUtil.locallyLogin(_uid, pwd, request, response);
                if (state >= 0) {
                    chain.doFilter(request, response);
                } else
                    LogUtil.dispatchToLogin(request, response, state);
            } else {
                LogUtil.dispatchToLogin(request, response, 0);
            }
            return;
        }
        chain.doFilter(request, response);
    }

}
