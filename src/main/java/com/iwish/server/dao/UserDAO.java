package com.iwish.server.dao;

import com.iwish.common.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Data-access for the users table (register / login / search). */
public class UserDAO {

    public User register(String username, String passwordHash, String displayName) throws SQLException {
        String sql = "INSERT INTO users(username, password_hash, display_name) VALUES (?,?,?)";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, displayName);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new User(keys.getInt(1), username, displayName);
                }
            }
        }
        return null;
    }

    public boolean usernameExists(String username) throws SQLException {
        return findByUsername(username) != null;
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, display_name FROM users WHERE username = ?";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("display_name"));
                }
            }
        }
        return null;
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT id, username, display_name FROM users WHERE id = ?";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("display_name"));
                }
            }
        }
        return null;
    }

    /** Returns the user if username + hashed password match, otherwise null. */
    public User validateLogin(String username, String passwordHash) throws SQLException {
        String sql = "SELECT id, username, display_name FROM users WHERE username = ? AND password_hash = ?";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("display_name"));
                }
            }
        }
        return null;
    }

    /** Search users by username or display name, excluding myself. */
    public List<User> search(String query, int excludeId) throws SQLException {
        String sql = "SELECT id, username, display_name FROM users "
                   + "WHERE (username LIKE ? OR display_name LIKE ?) AND id <> ? LIMIT 50";
        List<User> out = new ArrayList<>();
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String like = "%" + query + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setInt(3, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("display_name")));
                }
            }
        }
        return out;
    }
}
