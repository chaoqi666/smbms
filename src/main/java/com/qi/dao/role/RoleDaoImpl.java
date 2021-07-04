package com.qi.dao.role;

import com.qi.dao.Basedao;
import com.qi.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao{

    //获取角色列表
    @Override
    public List<Role> getRoleList(Connection connection) throws SQLException {
        ResultSet rs = null;
        PreparedStatement pstm = null;
        List<Role> list = new ArrayList<Role>();

        if (connection != null) {
            String sql = "select * from smbms_role";
            pstm = connection.prepareStatement(sql);
            Object[] params = {};
            rs = Basedao.execute(pstm, connection, sql, params, rs);

            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setRoleCode(rs.getString("roleCode"));
                role.setRoleName(rs.getString("roleName"));
                list.add(role);
            }
            Basedao.closeResource(pstm, null, rs);
        }

        return list;
    }
}
