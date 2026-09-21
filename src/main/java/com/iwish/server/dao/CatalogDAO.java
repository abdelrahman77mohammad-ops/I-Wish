package com.iwish.server.dao;

import com.iwish.common.model.CatalogItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Data-access for the shop catalog (items users can add to a wish list). */
public class CatalogDAO {

    public List<CatalogItem> listAll() throws SQLException {
        String sql = "SELECT id, name, description, price FROM catalog_items ORDER BY name";
        List<CatalogItem> out = new ArrayList<>();
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new CatalogItem(rs.getInt("id"), rs.getString("name"),
                        rs.getString("description"), rs.getDouble("price")));
            }
        }
        return out;
    }

    /** Admin adds a new catalog item (from the Server GUI). Returns its id. */
    public int add(String name, String description, double price) throws SQLException {
        String sql = "INSERT INTO catalog_items(name, description, price) VALUES (?,?,?)";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setDouble(3, price);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }
}
