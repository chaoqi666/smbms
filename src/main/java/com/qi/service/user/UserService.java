package com.qi.service.user;

import com.qi.pojo.User;
import java.util.List;
import java.awt.*;
import java.sql.Connection;

public interface UserService {
    //用户登录(账户，密码)
    public User login(String userCode, String password);

    //根据用户ID修改密码
    public boolean updatePwd(int id, String pwd);

    //查询记录数
    public int getUserCount(String userNmae, int userRole);

    //根据条件查询用户列表
    public List<User> getUserList(String userName, int userRole, int currentPageNo, int pageSize);

    //添加用户信息
    public boolean addUser(User user);

    //删除用户
    public boolean deleteUserById(Integer delId) throws Exception;

    //修改用户
    public boolean modify(User user);

    //根据ID查找user
    public User getUserById(String id);

    //根据userCode查询出User
    public User selectUserCodeExist(String userCode);
}
