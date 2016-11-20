package com.kimi.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class EncodingFilter implements Filter {

    Map<String, String> params = new HashMap<String, String>();

    @Override
    public void destroy() {
//        System.out.println("EncodeFilter destroy");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig cfg) throws ServletException {
        Enumeration<String> names = cfg.getInitParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            params.put(name, cfg.getInitParameter(name));
            System.out.println(name);
        }
    }

}