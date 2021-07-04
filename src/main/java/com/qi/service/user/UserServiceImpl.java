package com.qi.service.user;

import com.qi.dao.Basedao;
import com.qi.dao.user.UserDao;
import com.qi.dao.user.UserDaoImpl;
import com.qi.pojo.User;

import java.util.Iterator;
import java.util.List;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class UserServiceImpl implements UserService{

    //业务层都会调用dao层，引入dao层；无参构造直接调用
    private UserDao userDao;
    public UserServiceImpl(){
        userDao = new UserDaoImpl();
    }

    //用户登录
    @Override
    public User login(String userCode, String password) {
        Connection connection = null;
        User user = null;

        try {
            connection = Basedao.getConnection();
            //业务层调用dao层，具体数据库操作
            user = userDao.getLoginUser(connection,userCode);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            Basedao.closeResource(null, connection,null);
        }
        return user;
    }

    //修改密码
    @Override
    public boolean updatePwd(int id, String pwd) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = Basedao.getConnection();
            if(userDao.updatePwd(connection, id, pwd) > 0) {
                flag =true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            Basedao.closeResource(null,connection,null);
        }
        return flag;
    }

    //查询用户总数
    @Override
    public int getUserCount(String userNmae, int userRole) {
        Connection connection = null;
        int count = 0;

        try {
            connection = Basedao.getConnection();
            count = userDao.getUserCount(connection, userNmae, userRole);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            Basedao.closeResource(null, connection, null);
        }
        return count;
    }

    //根据条件查询用户列表
    @Override
    public List<User> getUserList(String userName, int userRole, int currentPageNo, int pageSize) {
        Connection connection = null;
        List<User> userList = null;
        System.out.println("queryUserName ---- > " + userName);
        System.out.println("queryUserRole ---- > " + userRole);
        System.out.println("currentPageNo ---- > " + currentPageNo);
        System.out.println("pageSize ---- > " + pageSize);
        try {
            connection = Basedao.getConnection();
            userList = userDao.getUserList(connection, userName,userRole,currentPageNo,pageSize);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            Basedao.closeResource(null, connection, null);
        }
        return userList;
    }

    //添加用户信息
    @Override
    public boolean addUser(User user) {
        Connection connection = null;
        boolean flag = false;
        int updateRows = 0;

        try {
            connection = Basedao.getConnection();
            connection.setAutoCommit(false);   //开启事务
            updateRows = userDao.addUser(connection, user);
            connection.commit();
            if (updateRows > 0) {
                flag = true;
                System.out.println("添加成功");
            } else {
                System.out.println("添加失败");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();//添加失败，数据回滚
            } catch (SQLException e) {
                e.printStackTrace();
            } finally{
                Basedao.closeResource(null, connection, null);
            }
        }
        return flag;
    }

    //通过id删除用户
    public boolean deleteUserById(Integer delId) throws Exception {
        Connection connection = null;
        boolean flag = false;
        int updateRows = 0;
        try {
            connection = Basedao.getConnection();
            connection.setAutoCommit(false);   //开启事务
            updateRows = userDao.deleteUserById(connection, delId);
            connection.commit();
            if (updateRows > 0) {
                flag = true;
            } else {
                System.out.println("删除失败");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();//添加失败，数据回滚
            } catch (SQLException e) {
                e.printStackTrace();
            } finally{
                Basedao.closeResource(null, connection, null);
            }
        }
        return flag;
    }

      //修改用户
      @Override
      public boolean modify(User user) {
          // TODO Auto-generated method stub
          Connection connection = null;
          boolean flag = false;
          try {
              connection = Basedao.getConnection();
              if (userDao.modify(connection, user) > 0)
                  flag = true;
          } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          } finally {
              Basedao.closeResource(null, connection, null);
          }
          return flag;
      }


    @Override
    public User getUserById(String id) {
        // TODO Auto-generated method stub
        User user = null;
        Connection connection = null;
        try {
            connection = Basedao.getConnection();
            user = userDao.getUserById(connection, id);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            user = null;
        } finally {
            Basedao.closeResource(null, connection, null);
        }
        return user;
    }

    @Override
    public User selectUserCodeExist(String userCode) {
        // TODO Auto-generated method stub
        Connection connection = null;
        User user = null;
        try {
            connection = Basedao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            Basedao.closeResource(null, connection, null);
        }
        return user;
    }


    @Test
    public void test() {
        UserServiceImpl userService = new UserServiceImpl();
        User admin = userService.login("admin","1234567");
        System.out.println("Username:" + admin.getUserName());
        System.out.println("RoleID:" + admin.getUserRole());
        System.out.println("Age:" + admin.getAge());
        System.out.println("Password:" + admin.getUserPassword());
        int count = userService.getUserCount(null, 0);
        System.out.println(count);
        List<User> userList = userService.getUserList("", 3, 1, 5);
        Iterator<User> iter = userList.iterator();
        while (iter.hasNext()){
            User s = (User) iter.next();
            System.out.println("UserName:" +s.getUserName());
            System.out.println("UserCode:" +s.getUserCode());
            System.out.println("UserRoleName:" +s.getUserRoleName());
            System.out.println("---------------------------------------");
        }
    }
}
