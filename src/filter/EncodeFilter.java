package filter;

import org.apache.catalina.connector.RequestFacade;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class EncodeFilter implements Filter {
    private static String encoding;

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        //System.out.println("EncodeFilter:" + ((HttpServletRequest)req).getRequestURL());
        req.setCharacterEncoding(encoding);
        resp.setCharacterEncoding(encoding);
        if("localhost:8080".equals(((RequestFacade) req).getHeader("host")))
            req.setAttribute("ymLocal", true);
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) {
        encoding = config.getInitParameter("encoding");
    }

}

