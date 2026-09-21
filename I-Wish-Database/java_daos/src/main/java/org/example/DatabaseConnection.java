package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // بيانات الاتصال بقاعدة البيانات اللي عملناها
    private static final String URL = "jdbc:mysql://localhost:3306/i_wish_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Omar#2005"; // اكتب الباسورد اللي بتدخل بيه على الميسكل

    // فانكشن بترجع لنا كائن الاتصال (Connection)
    public static Connection getConnection() {
        try {
            // فتح الاتصال باستخدام البيانات فوق
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return null;
        }
    }
}