package org.example;

public class Main {
    public static void main(String[] args) {
        // بنعمل كائن من WishlistDAO عشان نستخدم فانكشن الإضافة
        WishlistDAO wishlistDAO = new WishlistDAO();

        // بيانات تجريبية للأمنية أو المنتج
        int userId = 1; // رقم المستخدم (تأكد إن فيه يوزر بالرقم ده في جدول users أو حط رقم يوزر موجود عندك)
        String itemName = "Laptop الجديد";
        double price = 25000.0;
        String status = "Pending";

        // تنفيذ عملية الإضافة
        boolean isAdded = wishlistDAO.deleteWishItem( 1);

        if (isAdded) {
            System.out.println("✅ مبروك! تمت إضافة الأمنية في جدول wishlist_items بنجاح.");
        } else {
            System.out.println("❌ فشلت إضافة الأمنية، راجع الكود أو الاتصال.");
        }
    }
}