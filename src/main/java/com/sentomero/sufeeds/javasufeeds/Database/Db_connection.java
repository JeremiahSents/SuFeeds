package com.sentomero.sufeeds.javasufeeds.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db_connection {
    private static final String URL = "jdbc:mysql://localhost:3306/db_jeremiah_sentomero_191337";
    private static final String USER = "sufeeds_admin";
    private static final String PASSWORD = "@remsAdrian123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}