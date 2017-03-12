package cn.orditech.stockanalysis.filter;

import cn.orditech.stockanalysis.tool.CacheTool;
import cn.orditech.tools.RequestTool;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by kimi on 2017/3/12.
 */
public class LoginFilter implements Filter {
    @Override
    public void init (FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter (ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        CacheTool.putLoginIp (RequestTool.getIp (request));
        chain.doFilter (request, response);
    }

    @Override
    public void destroy () {

    }
}
