package com.qi.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import com.qi.pojo.Role;
import com.qi.pojo.User;
import com.qi.service.role.RoleService;
import com.qi.service.role.RoleServiceImpl;
import com.qi.service.user.UserService;
import com.qi.service.user.UserServiceImpl;
import com.qi.util.Constants;
import com.qi.util.PageSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("UserServlet--start--->");
        String method = req.getParameter("method");
        if (method.equals("savepwd") && method != null) {
            System.out.println("--->savepwd");
            this.updatePwd(req,resp);
        } else if (method.equals("pwdmodify") && method != null) {
            System.out.println("--->pwdmodify");
            this.pwdModify(req,resp);
        } else if (method != null && method.equals("getrolelist")) {
            System.out.println("--->getrolelist");
            this.getRoleList(req, resp);
        } else if (method != null && method.equals("ucexist")) {
            System.out.println("--->ucexist");
            this.userCodeExist(req, resp);
        } else if (method != null && method.equals("view")) {
            System.out.println("--->view");
            this.getUserById(req, resp, "userview.jsp");
        } else if (method.equals("query") && method != null) {
            System.out.println("--->query");
            this.query(req,resp);
        } else if (method.equals("add") && method != null) {
            System.out.println("--->addUser");
            this.addUser(req,resp);
        } else if (method.equals("deluser") && method != null) {
            try {
                System.out.println("--->delUser");
                this.delUser(req,resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (method.equals("modify") && method != null) {
            try {
                System.out.println("--->modifyUse");
                this.modify(req,resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet (req, resp);
    }

    //实现Servlet复用,修改密码
    public void updatePwd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //从session拿id
        User user = (User) req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");
        //System.out.println("UserServlet: "+newpassword);
        boolean flag = false;

        if (user != null && !StringUtils.isNullOrEmpty(newpassword)) {
            UserServiceImpl userService = new UserServiceImpl();
            flag = userService.updatePwd(user.getId(),newpassword);
            if (flag) {
                System.out.println("修改密码成功");
                req.setAttribute("message","修改密码成功，请退出，使用新密码登录");
                //密码修改成功，移除session
                req.getSession().removeAttribute(Constants.USER_SESSION);
            } else {
                System.out.println("密码修改失败");
                req.setAttribute("message","修改密码失败！");
            }
        } else {
            System.out.println("新密码有问题");
            req.setAttribute("message","新密码有问题！");
        }
        resp.sendRedirect("pwdmodify.jsp");
    }

    //验证旧密码，session中有用户的密码
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp){
        //从Session里面拿ID；
        User user = (User) req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");

        //万能的Map：结果集
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (user == null){  //Session失效了，session过期
            resultMap.put("result", "sessionerror");
        }else if (StringUtils.isNullOrEmpty(oldpassword)){  //输入的密码为空
            resultMap.put("result", "error");
        }else {
            String userPassword = user.getUserPassword();   //session中用户的密码
            if (oldpassword.equals(userPassword)){
                resultMap.put("result", "true");
            }else {
                System.out.println("旧密码输入错误");
                resultMap.put("result", "false");
            }
        }

        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            //JsonArray 阿里巴巴的JSON工具类，转换格式
        /*
            resultMap = ["result", "sessionerror", "result", "error"]
            Json格式 = {key: value}
        * */
            writer.write(JSONArray.toJSONString(resultMap));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //通过条件查询-userList(重点+难点)
    public void query(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //查询用户列表

        //从前端获取数据
        String queryUserName = req.getParameter("queryname");//用户姓名
        String temp = req.getParameter("queryUserRole");     //用户role
        String pageIndex = req.getParameter("pageIndex");    //页标
        int queryUserRole = 0; //默认用户role为0

        //获取用户列表
        UserServiceImpl userService = new UserServiceImpl();
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<User> userList = null;
        List<Role> roleList = null;

        //第一次走这个请求，一定是第一页，页面大小固定
        int pageSize = Constants.PAGE_SIZE;   //可以写到配置文件中，方便修改
        int currentPageNo = 1;

        //三个if，判断前段请求的参数
        if (queryUserName == null){
            queryUserName = "";
        }
        if (temp != null && !temp.equals("")) {
            queryUserRole = Integer.parseInt(temp);//给查询赋值 0,1,2
        }
        if (pageIndex != null) {
            try {
                currentPageNo = Integer.parseInt(pageIndex);
            } catch (NumberFormatException e) {
                resp.sendRedirect("error.jsp");
            }
        }

        //获取用户总数
        int totalCount = userService.getUserCount(queryUserName, queryUserRole);

        //总页数支持,设置参数
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);

        //控制首页尾页
        int totalPageCount = pageSupport.getTotalPageCount();
        if (currentPageNo < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {
            currentPageNo = totalPageCount;
        }

        //用户列表展示
        userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        req.setAttribute("userList",userList);

        //角色列表
        roleList = roleService.getRoleList();
        req.setAttribute("roleList", roleList); //用户列表
        req.setAttribute("totalCount", totalCount); //用户总数总数
        req.setAttribute("currentPageNo", currentPageNo);   //当前页
        req.setAttribute("totalPageCount",totalPageCount);   //页面总数
        req.setAttribute("queryUserName",queryUserName);
        req.setAttribute("queryUserRole",queryUserRole);

        //返回前端
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //添加用户信息
    public void addUser(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        UserService userService = null;
        User user = null;

        //获取前端信息
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        //String ruserPassword = req.getParameter("ruserPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        //给用户赋值
        user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setAddress(address);
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        user.setCreatedBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());

        userService = new UserServiceImpl();
        if (userService.addUser(user)) { //添加用户成功
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        } else {
            req.getRequestDispatcher("useradd.jsp").forward(req, resp);
        }
    }

    //删除用户
    private void delUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("uid");
        Integer delId = 0;
        try {
            delId = Integer.parseInt(id);
        } catch (Exception e) {
            delId = 0;
        }
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (delId <= 0) {
            resultMap.put("delResult", "notexist");
        } else {
            UserService userService = new UserServiceImpl();
            if (userService.deleteUserById(delId)) {
                resultMap.put("delResult", "true");
            } else {
                resultMap.put("delResult", "false");
            }
        }
    }

    //修改用户数据
    private void modify(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String id = request.getParameter("uid");
        String userName = request.getParameter("userName");
        String gender = request.getParameter("gender");
        String birthday = request.getParameter("birthday");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String userRole = request.getParameter("userRole");

        User user = new User();
        user.setId(Integer.valueOf(id));
        user.setUserName(userName);
        user.setGender(Integer.valueOf(gender));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        user.setModifyBy(((User) request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());

        UserService userService = new UserServiceImpl();
        if (userService.modify(user)) {
            response.sendRedirect(request.getContextPath() + "/jsp/user.do?method=query");
        } else {
            request.getRequestDispatcher("usermodify.jsp").forward(request, response);
        }

    }

    //getRoleList
    private void getRoleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Role> roleList = null;
        RoleService roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();
        //把roleList转换成json对象输出
        response.setContentType("application/json");
        PrintWriter outPrintWriter = response.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(roleList));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    //userCodeExist
    private void userCodeExist(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //判断用户账号是否可用
        String userCode = request.getParameter("userCode");

        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (StringUtils.isNullOrEmpty(userCode)) {
            //userCode == null || userCode.equals("")
            resultMap.put("userCode", "exist");
        } else {
            UserService userService = new UserServiceImpl();
            User user = userService.selectUserCodeExist(userCode);
            if (null != user) {
                resultMap.put("userCode", "exist");
            } else {
                resultMap.put("userCode", "notexist");
            }
        }

        //把resultMap转为json字符串以json的形式输出
        //配置上下文的输出类型
        response.setContentType("application/json");
        //从response对象中获取往外输出的writer对象
        PrintWriter outPrintWriter = response.getWriter();
        //把resultMap转为json字符串 输出
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();//刷新
        outPrintWriter.close();//关闭流
    }

    //getUserById
    private void getUserById(HttpServletRequest request, HttpServletResponse response, String url)
            throws ServletException, IOException {
        String id = request.getParameter("uid");
        if (!StringUtils.isNullOrEmpty(id)) {
            //调用后台方法得到user对象
            UserService userService = new UserServiceImpl();
            User user = userService.getUserById(id);
            request.setAttribute("user", user);
            request.getRequestDispatcher(url).forward(request, response);
        }

    }


}
