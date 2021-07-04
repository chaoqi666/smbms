package com.qi.servlet.user;

import com.qi.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //移除用户session
        System.out.println("LogoutServlet--start-----------");
        req.getSession().removeAttribute(Constants.USER_SESSION);
        resp.sendRedirect("/smbms/login.jsp");   //返回登录页面
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
