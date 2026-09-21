package com.iwish.server.dao;

import com.iwish.common.model.WishItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Data-access for a user's wish list. */
public class WishlistDAO {

    // one row -> a WishItem, including the money contributed so far
    private static final String SELECT_BASE =
            "SELECT w.id, w.user_id, w.catalog_item_id, ci.name, ci.price, w.purchased, "
          + "COALESCE((SELECT SUM(amount) FROM contributions WHERE wish_item_id = w.id), 0) AS contributed "
          + "FROM wish_items w JOIN catalog_items ci ON ci.id = w.catalog_item_id ";

    private WishItem map(ResultSet rs) throws SQLException {
        return new WishItem(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("catalog_item_id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getDouble("contributed"),
                rs.getBoolean("purchased"));
    }

    public List<WishItem> getWishlist(int userId) throws SQLException {
        String sql = SELECT_BASE + "WHERE w.user_id = ? ORDER BY w.created_at DESC";
        List<WishItem> out = new ArrayList<>();
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    public WishItem getWishItem(int wishItemId) throws SQLException {
        String sql = SELECT_BASE + "WHERE w.id = ?";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, wishItemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public void addWishItem(int userId, int catalogItemId) throws SQLException {
        String sql = "INSERT INTO wish_items(user_id, catalog_item_id) VALUES (?,?)";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, catalogItemId);
            ps.executeUpdate();
        }
    }

    /** Change which catalog item a wish entry points to (only the owner, not yet purchased). */
    public void updateWishItem(int wishItemId, int ownerId, int newCatalogItemId) throws SQLException {
        String sql = "UPDATE wish_items SET catalog_item_id = ? WHERE id = ? AND user_id = ? AND purchased = FALSE";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, newCatalogItemId);
            ps.setInt(2, wishItemId);
            ps.setInt(3, ownerId);
            ps.executeUpdate();
        }
    }

    /** Delete a wish entry the user owns (removes its contributions first for FK safety). */
    public void deleteWishItem(int wishItemId, int ownerId) throws SQLException {
        try (Connection c = ConnectionManager.getConnection()) {
            try (PreparedStatement del = c.prepareStatement(
                    "DELETE FROM contributions WHERE wish_item_id = ?")) {
                del.setInt(1, wishItemId);
                del.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "DELETE FROM wish_items WHERE id = ? AND user_id = ?")) {
                ps.setInt(1, wishItemId);
                ps.setInt(2, ownerId);
                ps.executeUpdate();
            }
        }
    }
}
