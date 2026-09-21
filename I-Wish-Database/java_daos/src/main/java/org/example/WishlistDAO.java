package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WishlistDAO {

    // 1. فانكشن لإضافة أمنية جديدة لمستخدم معين
    public boolean addWishItem(int userId, String itemName, double price, String status) {
        boolean isSuccess = false;

        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "INSERT INTO wishlist_items (user_id, item_name, price, status) VALUES (?, ?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setString(2, itemName);
            pstmt.setDouble(3, price);
            pstmt.setString(4, status);

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

    // 2. فانكشن لحذف أمنية باستخدام الـ ID بتاعها
    public boolean deleteWishItem(int itemId) {
        boolean isSuccess = false;

        try {
            // 1. فتح الاتصال بقاعدة البيانات
            Connection conn = DatabaseConnection.getConnection();

            // 2. كتابة أمر الحذف (DELETE)
            String query = "DELETE FROM wishlist_items WHERE item_id = ?";

            // 3. تجهيز الموصلاتي للأمر
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, itemId); // بنحط رقم الأمنية اللي عايزين نحذفها مكان علامة الاستفهام

            // 4. تنفيذ الأمر
            int result = pstmt.executeUpdate();

            if (result > 0) {
                isSuccess = true; // لو اتحذف صف واحد على الأقل يبقى العملية نجحت
            }

            // قفل الاتصال
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isSuccess;
    }

    // 3. عرض جميع الأماني الخاصة بمستخدم معين (View Wishlist)
    public void getWishlistByUserId(int userId) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            // أمر الاستعلام لجلب كل الأماني الخاصة بهذا اليوزر
            String query = "SELECT * FROM wishlist_items WHERE user_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();

            System.out.println("--- قائمة أماني المستخدم رقم " + userId + " ---");
            boolean hasWishes = false;

            while (rs.next()) {
                hasWishes = true;
                int itemId = rs.getInt("item_id");
                String itemName = rs.getString("item_name");
                double price = rs.getDouble("price");
                String status = rs.getString("status");

                System.out.println("🎁 [ID: " + itemId + "] المنتج: " + itemName + " | السعر: " + price + " | الحالة: " + status);
            }

            if (!hasWishes) {
                System.out.println("📭 لا توجد أماني مسجلة لهذا المستخدم حالياً.");
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}