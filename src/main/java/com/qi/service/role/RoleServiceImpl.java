package com.qi.service.role;

import com.qi.dao.Basedao;
import com.qi.dao.role.RoleDao;
import com.qi.dao.role.RoleDaoImpl;
import com.qi.pojo.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RoleServiceImpl implements RoleService{

    //业务层都会调用dao层，引入dao层；无参构造直接调用
    private RoleDao roleDao;
    public RoleServiceImpl() {
        roleDao = new RoleDaoImpl();
    }

    //获得角色列表
    @Override
    public List<Role> getRoleList() {
        Connection connection = null;
        List<Role> roleList = null;

        try {
            connection = Basedao.getConnection();
            roleList = roleDao.getRoleList(connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            Basedao.closeResource(null, connection, null);
        }
        return roleList;
    }
}
