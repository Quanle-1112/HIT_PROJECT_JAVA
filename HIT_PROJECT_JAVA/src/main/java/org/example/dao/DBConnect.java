package org.example.dao;

import org.example.constant.Common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/WOWTruyen";
    private static final String USER = "root";
    private static final String PASS = "123456";

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");

                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println(Common.DB_CONNECT_SUCCESS);
            }
        } catch (ClassNotFoundException e) {
            System.err.println(Common.DB_DRIVER_NOT_FOUND + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println(Common.DB_CONNECT_ERROR + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println(Common.DB_CLOSE_SUCCESS);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        getConnection();
    }
}