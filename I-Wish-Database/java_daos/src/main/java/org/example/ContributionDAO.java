package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContributionDAO {

    // 1. تسجيل مساهمة جديدة في أمنية صديق
    public boolean addContribution(int itemId, int userId, double amount) {
        boolean isSuccess = false;
        try {
            Connection conn = DatabaseConnection.getConnection();

            // أمر إدخال المساهمة في الجدول
            String query = "INSERT INTO contributions (item_id, user_id, amount) VALUES (?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, itemId);   // رقم الأمنية أو الهدية
            pstmt.setInt(2, userId);   // رقم الشخص المساهم
            pstmt.setDouble(3, amount); // المبلغ اللي ساهم بيه

            int result = pstmt.executeUpdate();
            if (result > 0) {
                isSuccess = true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    // 2. معرفة إجمالي المبلغ اللي اتجمع لحد دلوقتي للأمنية دي (اختياري بس مفيد جداً للمشروع)
    public double getTotalContributionsForItem(int itemId) {
        double totalAmount = 0;
        try {
            Connection conn = DatabaseConnection.getConnection();

            // بنجمع كل المبالغ اللي اتدفعت لنفس الأمنية
            String query = "SELECT SUM(amount) AS total FROM contributions WHERE item_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, itemId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                totalAmount = rs.getDouble("total");
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalAmount;
    }
}