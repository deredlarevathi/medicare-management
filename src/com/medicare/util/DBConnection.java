package com.medicare.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Single place that knows how to open a JDBC connection (encapsulation). */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/medicare_db";
    private static final String USER = "root";
    private static String password = "Mysql@123"; // asked at start-up, no need to edit this file

    private DBConnection() { }

    public static void setPassword(String newPassword) {
        password = newPassword;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, password);
    }

    /** Used once at start-up to make sure MySQL is reachable. */
    public static void testConnection() throws SQLException {
        try (Connection con = getConnection()) {
            // opening and closing is enough
        }
    }
}
