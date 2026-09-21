package com.iwish.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Data-access for money contributed toward wish-list items. */
public class ContributionDAO {

    public void add(int wishItemId, int buyerId, double amount) throws SQLException {
        String sql = "INSERT INTO contributions(wish_item_id, buyer_id, amount) VALUES (?,?,?)";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, wishItemId);
            ps.setInt(2, buyerId);
            ps.setDouble(3, amount);
            ps.executeUpdate();
        }
    }

    public double totalContributed(int wishItemId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount),0) AS total FROM contributions WHERE wish_item_id = ?";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, wishItemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
            }
        }
        return 0;
    }

    public void markPurchased(int wishItemId) throws SQLException {
        String sql = "UPDATE wish_items SET purchased = TRUE WHERE id = ?";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, wishItemId);
            ps.executeUpdate();
        }
    }

    /** Distinct buyers who contributed to an item — used to notify them when it is fully funded. */
    public List<Integer> distinctContributorIds(int wishItemId) throws SQLException {
        String sql = "SELECT DISTINCT buyer_id FROM contributions WHERE wish_item_id = ?";
        List<Integer> out = new ArrayList<>();
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, wishItemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(rs.getInt("buyer_id"));
            }
        }
        return out;
    }
}
