package filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//如果使用nginx动静分离，则该过滤器实际不会拦截任何请求
public class CacheFilter implements Filter {

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        //System.out.println("CacheFilter:" + ((HttpServletRequest)req).getRequestURL());
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Cache-Control", "public");
        response.addHeader("Cache-Control", "max-age=2592000");
        chain.doFilter(req, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

}