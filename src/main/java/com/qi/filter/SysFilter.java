package com.qi.filter;

import com.qi.pojo.User;
import com.qi.util.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SysFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        //过滤器，从session中获取用户
        User user = (User) request.getSession().getAttribute(Constants.USER_SESSION);

        if (user==null) {
            response.sendRedirect("/smbms/error.jsp");
        } else {
            filterChain.doFilter(req,resp);
        }
    }
}
