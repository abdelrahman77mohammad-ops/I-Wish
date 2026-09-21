package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendDAO {

    // 1. إرسال طلب صداقة (إضافة في الجدول بحالة pending)
    public boolean sendFriendRequest(int userId, int friendId) {
        boolean isSuccess = false;
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, 'pending')";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, friendId);

            int result = pstmt.executeUpdate();
            if (result > 0) isSuccess = true;
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    // 2. قبول أو رفض طلب الصداقة (تحديث الـ status من الناحيتين لضمان المطابقة)
    public boolean updateFriendStatus(int userId, int friendId, String newStatus) {
        boolean isSuccess = false;
        try {
            Connection conn = DatabaseConnection.getConnection();

            // بنستخدم OR عشان نضمن إننا بنعدل السطر بغض النظر مين اللي بعت ومين اللي استقبل
            String query = "UPDATE friends SET status = ? WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, newStatus); // زي 'accepted' أو 'declined'
            pstmt.setInt(2, userId);
            pstmt.setInt(3, friendId);
            pstmt.setInt(4, friendId);
            pstmt.setInt(5, userId);

            int result = pstmt.executeUpdate();
            if (result > 0) isSuccess = true;
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    // 3. إزالة صديق (حذف من الجدول)
    public boolean removeFriend(int userId, int friendId) {
        boolean isSuccess = false;
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "DELETE FROM friends WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, friendId);
            pstmt.setInt(3, friendId);
            pstmt.setInt(4, userId); // عشان نمسح العلاقة من الناحيتين لو أمكن

            int result = pstmt.executeUpdate();
            if (result > 0) isSuccess = true;
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    // 4. عرض قائمة أصدقائي (View my Friends list)
    public void getFriendsList(int userId) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            // بنبحث عن السطور اللي فيها المستخدم طرف في العلاقة وحالة الصداقة 'accepted'
            String query = "SELECT * FROM friends WHERE (user_id = ? OR friend_id = ?) AND status = 'accepted'";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);

            ResultSet rs = pstmt.executeQuery();

            System.out.println("--- قائمة أصدقائك المقبولين ---");
            while (rs.next()) {
                int u1 = rs.getInt("user_id");
                int f1 = rs.getInt("friend_id");

                // لو اليوزر بتاعي هو user_id، يبقى الصديق هو friend_id، والعكس صحيح
                int friendId = (u1 == userId) ? f1 : u1;

                System.out.println("👤 صديق برقم ID: " + friendId);
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}