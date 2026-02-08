package org.example.dao;

import org.example.constant.MessageConstant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/WOWTruyen";
    private static final String USER = "root";
    private static final String PASS = "123456";


    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);


        } catch (ClassNotFoundException e) {
            System.err.println(MessageConstant.ERR_DB_CONNECT + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println(MessageConstant.ERR_DB_CONNECT + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}