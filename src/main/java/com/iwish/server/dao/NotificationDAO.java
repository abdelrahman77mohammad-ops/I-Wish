package com.iwish.server.dao;

import com.iwish.common.model.Notification;
import com.iwish.common.model.NotificationType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Data-access for stored notifications. */
public class NotificationDAO {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Insert a notification and return it (with generated id + timestamp). */
    public Notification add(int userId, String message, NotificationType type) throws SQLException {
        String sql = "INSERT INTO notifications(user_id, message, type) VALUES (?,?,?)";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(2, message);
            ps.setInt(1, userId);
            ps.setString(3, type.name());
            ps.executeUpdate();
            int id = 0;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) id = keys.getInt(1);
            }
            String now = java.time.LocalDateTime.now().format(FMT);
            return new Notification(id, userId, message, type, false, now);
        }
    }

    public List<Notification> list(int userId) throws SQLException {
        String sql = "SELECT id, user_id, message, type, is_read, created_at "
                   + "FROM notifications WHERE user_id = ? ORDER BY created_at DESC, id DESC";
        List<Notification> out = new ArrayList<>();
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("created_at");
                    String created = (ts == null) ? "" : ts.toLocalDateTime().format(FMT);
                    out.add(new Notification(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("message"),
                            NotificationType.valueOf(rs.getString("type")),
                            rs.getBoolean("is_read"),
                            created));
                }
            }
        }
        return out;
    }

    public void markAllRead(int userId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ?";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
}
