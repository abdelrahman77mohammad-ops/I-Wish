package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // 1. فانكشن لتسجيل مستخدم جديد في جدول users
    public boolean registerUser(String username, String email, String password) {
        String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";

        // بنستخدم كلاس الاتصال اللي عملناه عشان نفتح قناة مع الداتا بيز
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // بنحط القيم مكان علامات الاستفهام (?)
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password);

            // بنفذ الأمر
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0; // لو العملية تمت بنجاح هيرجع true

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. فانكشن لتسجيل الدخول (التاكد من الإيميل والباسورد)
    public boolean loginUser(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            // بننفذ الاستعلام ونشوف هل فيه مستخدم بالبيانات دي ولا لا
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // لو لقينا نتيجة، يبقى الدخول صحيح (true)

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}