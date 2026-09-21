package com.iwish.server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central place that opens a JDBC connection to the MySQL "iwish" database.
 * Each DAO method opens a short-lived connection and closes it with
 * try-with-resources — simple and safe for a course-sized project.
 *
 *  >>> EDIT the USER / PASSWORD below to match your local MySQL. <<<
 */
public final class ConnectionManager {

    private static final String URL =
            "jdbc:mysql://localhost:3306/iwish"
          + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";   // <-- change to your MySQL password

    private ConnectionManager() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
