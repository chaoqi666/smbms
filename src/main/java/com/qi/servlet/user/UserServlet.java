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

    //??????Servlet??????,????????????
    public void updatePwd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //???session???id
        User user = (User) req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");
        //System.out.println("UserServlet: "+newpassword);
        boolean flag = false;

        if (user != null && !StringUtils.isNullOrEmpty(newpassword)) {
            UserServiceImpl userService = new UserServiceImpl();
            flag = userService.updatePwd(user.getId(),newpassword);
            if (flag) {
                System.out.println("??????????????????");
                req.setAttribute("message","??????????????????????????????????????????????????????");
                //???????????????????????????session
                req.getSession().removeAttribute(Constants.USER_SESSION);
            } else {
                System.out.println("??????????????????");
                req.setAttribute("message","?????????????????????");
            }
        } else {
            System.out.println("??????????????????");
            req.setAttribute("message","?????????????????????");
        }
        resp.sendRedirect("pwdmodify.jsp");
    }

    //??????????????????session?????????????????????
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp){
        //???Session?????????ID???
        User user = (User) req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");

        //?????????Map????????????
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (user == null){  //Session????????????session??????
            resultMap.put("result", "sessionerror");
        }else if (StringUtils.isNullOrEmpty(oldpassword)){  //?????????????????????
            resultMap.put("result", "error");
        }else {
            String userPassword = user.getUserPassword();   //session??????????????????
            if (oldpassword.equals(userPassword)){
                resultMap.put("result", "true");
            }else {
                System.out.println("?????????????????????");
                resultMap.put("result", "false");
            }
        }

        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            //JsonArray ???????????????JSON????????????????????????
        /*
            resultMap = ["result", "sessionerror", "result", "error"]
            Json?????? = {key: value}
        * */
            writer.write(JSONArray.toJSONString(resultMap));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //??????????????????-userList(??????+??????)
    public void query(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //??????????????????

        //?????????????????????
        String queryUserName = req.getParameter("queryname");//????????????
        String temp = req.getParameter("queryUserRole");     //??????role
        String pageIndex = req.getParameter("pageIndex");    //??????
        int queryUserRole = 0; //????????????role???0

        //??????????????????
        UserServiceImpl userService = new UserServiceImpl();
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<User> userList = null;
        List<Role> roleList = null;

        //??????????????????????????????????????????????????????????????????
        int pageSize = Constants.PAGE_SIZE;   //??????????????????????????????????????????
        int currentPageNo = 1;

        //??????if??????????????????????????????
        if (queryUserName == null){
            queryUserName = "";
        }
        if (temp != null && !temp.equals("")) {
            queryUserRole = Integer.parseInt(temp);//??????????????? 0,1,2
        }
        if (pageIndex != null) {
            try {
                currentPageNo = Integer.parseInt(pageIndex);
            } catch (NumberFormatException e) {
                resp.sendRedirect("error.jsp");
            }
        }

        //??????????????????
        int totalCount = userService.getUserCount(queryUserName, queryUserRole);

        //???????????????,????????????
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);

        //??????????????????
        int totalPageCount = pageSupport.getTotalPageCount();
        if (currentPageNo < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {
            currentPageNo = totalPageCount;
        }

        //??????????????????
        userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        req.setAttribute("userList",userList);

        //????????????
        roleList = roleService.getRoleList();
        req.setAttribute("roleList", roleList); //????????????
        req.setAttribute("totalCount", totalCount); //??????????????????
        req.setAttribute("currentPageNo", currentPageNo);   //?????????
        req.setAttribute("totalPageCount",totalPageCount);   //????????????
        req.setAttribute("queryUserName",queryUserName);
        req.setAttribute("queryUserRole",queryUserRole);

        //????????????
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //??????????????????
    public void addUser(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        UserService userService = null;
        User user = null;

        //??????????????????
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        //String ruserPassword = req.getParameter("ruserPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        //???????????????
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
        if (userService.addUser(user)) { //??????????????????
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        } else {
            req.getRequestDispatcher("useradd.jsp").forward(req, resp);
        }
    }

    //????????????
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

    //??????????????????
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
        //???roleList?????????json????????????
        response.setContentType("application/json");
        PrintWriter outPrintWriter = response.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(roleList));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    //userCodeExist
    private void userCodeExist(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //??????????????????????????????
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

        //???resultMap??????json????????????json???????????????
        //??????????????????????????????
        response.setContentType("application/json");
        //???response??????????????????????????????writer??????
        PrintWriter outPrintWriter = response.getWriter();
        //???resultMap??????json????????? ??????
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();//??????
        outPrintWriter.close();//?????????
    }

    //getUserById
    private void getUserById(HttpServletRequest request, HttpServletResponse response, String url)
            throws ServletException, IOException {
        String id = request.getParameter("uid");
        if (!StringUtils.isNullOrEmpty(id)) {
            //????????????????????????user??????
            UserService userService = new UserServiceImpl();
            User user = userService.getUserById(id);
            request.setAttribute("user", user);
            request.getRequestDispatcher(url).forward(request, response);
        }

    }


}
