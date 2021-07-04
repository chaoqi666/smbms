package com.qi.servlet.user;

import com.qi.pojo.User;
import com.qi.service.user.UserServiceImpl;
import com.qi.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {

    //Servlet:控制层,调用业务层
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("LoginServlet--start-----------");

        //System.out.println(Charset.defaultCharset());  GBK
        //获取用户名和密码，页面中获取req，去login.jsp中找
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");

        //和数据库中密码对比，调用业务层
        UserServiceImpl userService = new UserServiceImpl();
        User user = userService.login(userCode, userPassword);

        //密码匹配，登录成功，将用户信息放入Session
        if (user != null && user.getUserPassword().equals(userPassword)) {
            req.getSession().setAttribute(Constants.USER_SESSION,user);
            //跳转到主页
            resp.sendRedirect("jsp/frame.jsp");
        }
        else {
            //转发登录页面，顺带提示密码错误
            req.setAttribute("error", "用户名或者密码错误");
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
