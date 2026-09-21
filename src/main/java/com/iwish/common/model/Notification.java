package com.iwish.common.model;

import java.io.Serializable;

/** A message for a user (fired live over the socket, and stored in the DB). */
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private String message;
    private NotificationType type;
    private boolean read;
    private String createdAt;

    public Notification() { }

    public Notification(int id, int userId, String message,
                        NotificationType type, boolean read, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.read = read;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return (read ? "   " : "• ") + message;
    }
}
