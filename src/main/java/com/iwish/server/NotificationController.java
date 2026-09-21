package com.iwish.server;

import com.iwish.common.model.Notification;
import com.iwish.common.model.NotificationType;
import com.iwish.server.dao.NotificationDAO;

/**
 * Creates notifications: always stores them in the DB (so an offline user
 * still sees them at next login), and pushes them live over the socket if
 * the target user is currently online.
 */
public class NotificationController {

    private final IWishServer server;
    private final NotificationDAO notificationDAO = new NotificationDAO();

    public NotificationController(IWishServer server) {
        this.server = server;
    }

    public void notifyUser(int userId, String message, NotificationType type) {
        try {
            Notification n = notificationDAO.add(userId, message, type);
            ClientHandler handler = server.getOnline(userId);
            if (handler != null) {
                handler.send(n);   // live push to the connected client
            }
            server.log("Notify user #" + userId + ": " + message);
        } catch (Exception e) {
            server.log("Notify failed for user #" + userId + ": " + e.getMessage());
        }
    }
}
