package com.iwish.server.dao;

import com.iwish.common.model.FriendRequest;
import com.iwish.common.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Data-access for friendships (requests + accepted friends). */
public class FriendDAO {

    /** Returns false if a friendship/request already exists between the two. */
    public boolean sendRequest(int requesterId, int addresseeId) throws SQLException {
        if (relationExists(requesterId, addresseeId)) {
            return false;
        }
        String sql = "INSERT INTO friendships(requester_id, addressee_id, status) VALUES (?,?,'PENDING')";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, requesterId);
            ps.setInt(2, addresseeId);
            ps.executeUpdate();
            return true;
        }
    }

    /** Any row (pending or accepted) in either direction? */
    public boolean relationExists(int a, int b) throws SQLException {
        String sql = "SELECT 1 FROM friendships "
                   + "WHERE (requester_id=? AND addressee_id=?) OR (requester_id=? AND addressee_id=?) LIMIT 1";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, a); ps.setInt(2, b); ps.setInt(3, b); ps.setInt(4, a);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<FriendRequest> listPendingRequests(int userId) throws SQLException {
        String sql = "SELECT f.id AS fid, u.id AS uid, u.display_name AS dname "
                   + "FROM friendships f JOIN users u ON u.id = f.requester_id "
                   + "WHERE f.addressee_id = ? AND f.status = 'PENDING'";
        List<FriendRequest> out = new ArrayList<>();
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new FriendRequest(rs.getInt("fid"), rs.getInt("uid"), rs.getString("dname")));
                }
            }
        }
        return out;
    }

    /** Accept (status ACCEPTED) or decline (delete the row) a request. */
    public void respondToRequest(int friendshipId, boolean accept) throws SQLException {
        String sql = accept
                ? "UPDATE friendships SET status='ACCEPTED' WHERE id=?"
                : "DELETE FROM friendships WHERE id=?";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, friendshipId);
            ps.executeUpdate();
        }
    }

    public List<User> listFriends(int userId) throws SQLException {
        String sql = "SELECT u.id, u.username, u.display_name FROM friendships f "
                   + "JOIN users u ON u.id = CASE WHEN f.requester_id=? THEN f.addressee_id ELSE f.requester_id END "
                   + "WHERE (f.requester_id=? OR f.addressee_id=?) AND f.status='ACCEPTED'";
        List<User> out = new ArrayList<>();
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, userId); ps.setInt(3, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("display_name")));
                }
            }
        }
        return out;
    }

    public void removeFriend(int userId, int friendId) throws SQLException {
        String sql = "DELETE FROM friendships WHERE status='ACCEPTED' AND "
                   + "((requester_id=? AND addressee_id=?) OR (requester_id=? AND addressee_id=?))";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, friendId); ps.setInt(3, friendId); ps.setInt(4, userId);
            ps.executeUpdate();
        }
    }

    public boolean areFriends(int a, int b) throws SQLException {
        String sql = "SELECT 1 FROM friendships WHERE status='ACCEPTED' AND "
                   + "((requester_id=? AND addressee_id=?) OR (requester_id=? AND addressee_id=?)) LIMIT 1";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, a); ps.setInt(2, b); ps.setInt(3, b); ps.setInt(4, a);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
