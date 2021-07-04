package com.qi.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

//操作数据库公共类
public class Basedao {

    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    //静态代码块，类加载的时候初始化,读取文件db.properties
    static {
        Properties properties = new Properties();
        InputStream is = Basedao.class.getClassLoader().getResourceAsStream("db.properties");

        try {
            properties.load(is);//加载
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
    }

    //数据库连接
    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url,username,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    //编写查询公共类
    public static ResultSet execute(PreparedStatement preparedStatement,Connection connection,String sql,Object[] params,ResultSet resultSet) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            //setObject占位符从1开始，数组0开始
            preparedStatement.setObject(i+1,params[i]);
        }
        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    //编写增删改公共类
    public static int update(PreparedStatement preparedStatement,Connection connection,String sql,Object[] params) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            //setObject占位符从1开始，数组0开始
            preparedStatement.setObject(i+1,params[i]);
        }
        int updateRows = preparedStatement.executeUpdate();
        return updateRows;
    }

    //关闭链接
    public static boolean closeResource(PreparedStatement preparedStatement,Connection connection,ResultSet resultSet) {
        boolean flag = true;

        if (resultSet!=null){
            try {
                resultSet.close();
                resultSet = null;      //GC回收
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }
        if (connection!=null){
            try {
                connection.close();
                connection = null;      //GC回收
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }
        if (preparedStatement!=null){
            try {
                preparedStatement.close();
                preparedStatement = null;      //GC回收
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }

        return flag;
    }

}
