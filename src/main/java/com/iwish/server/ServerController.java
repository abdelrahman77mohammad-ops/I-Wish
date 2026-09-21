package com.iwish.server;

import com.iwish.common.model.NotificationType;
import com.iwish.common.model.User;
import com.iwish.common.model.WishItem;
import com.iwish.common.protocol.Request;
import com.iwish.common.protocol.Response;
import com.iwish.server.dao.CatalogDAO;
import com.iwish.server.dao.ContributionDAO;
import com.iwish.server.dao.FriendDAO;
import com.iwish.server.dao.NotificationDAO;
import com.iwish.server.dao.UserDAO;
import com.iwish.server.dao.WishlistDAO;
import com.iwish.server.util.PasswordUtil;

/**
 * The "brain" of the server: takes a {@link Request}, performs it through the
 * DAOs, fires notifications when needed, and returns a {@link Response}.
 */
public class ServerController {

    private final IWishServer server;
    private final NotificationController notifications;

    private final UserDAO userDAO = new UserDAO();
    private final FriendDAO friendDAO = new FriendDAO();
    private final CatalogDAO catalogDAO = new CatalogDAO();
    private final WishlistDAO wishlistDAO = new WishlistDAO();
    private final ContributionDAO contributionDAO = new ContributionDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    public ServerController(IWishServer server) {
        this.server = server;
        this.notifications = new NotificationController(server);
    }

    public Response handle(Request req, ClientHandler session) {
        try {
            switch (req.getType()) {
                case REGISTER: return register(req);
                case LOGIN:    return login(req, session);
                case LOGOUT:   return logout(session);
                default:       return handleAuthed(req, session);
            }
        } catch (Exception e) {
            server.log("Error handling " + req.getType() + ": " + e.getMessage());
            return Response.fail("Server error: " + e.getMessage());
        }
    }

    // ---- actions that require a logged-in user ----
    private Response handleAuthed(Request req, ClientHandler session) throws Exception {
        int me = session.getUserId();
        if (me == 0) return Response.fail("You must be logged in.");

        switch (req.getType()) {
            case SEARCH_USERS:
                return Response.ok(userDAO.search(req.getString("query"), me));

            case SEND_FRIEND_REQUEST: {
                int to = req.getInt("addresseeId");
                if (!friendDAO.sendRequest(me, to)) {
                    return Response.fail("A request or friendship already exists.");
                }
                notifications.notifyUser(to,
                        session.getDisplayName() + " sent you a friend request.",
                        NotificationType.FRIEND_REQUEST);
                return Response.ok("Friend request sent", null);
            }

            case LIST_FRIEND_REQUESTS:
                return Response.ok(friendDAO.listPendingRequests(me));

            case RESPOND_FRIEND_REQUEST:
                friendDAO.respondToRequest(req.getInt("friendshipId"), (Boolean) req.get("accept"));
                return Response.ok("Done", null);

            case LIST_FRIENDS:
                return Response.ok(friendDAO.listFriends(me));

            case REMOVE_FRIEND:
                friendDAO.removeFriend(me, req.getInt("friendId"));
                return Response.ok("Friend removed", null);

            case GET_CATALOG:
                return Response.ok(catalogDAO.listAll());

            case GET_MY_WISHLIST:
                return Response.ok(wishlistDAO.getWishlist(me));

            case ADD_WISH_ITEM:
                wishlistDAO.addWishItem(me, req.getInt("catalogItemId"));
                return Response.ok("Item added", null);

            case UPDATE_WISH_ITEM:
                wishlistDAO.updateWishItem(req.getInt("wishItemId"), me, req.getInt("catalogItemId"));
                return Response.ok("Item updated", null);

            case DELETE_WISH_ITEM:
                wishlistDAO.deleteWishItem(req.getInt("wishItemId"), me);
                return Response.ok("Item removed", null);

            case GET_FRIEND_WISHLIST: {
                int friendId = req.getInt("friendId");
                if (!friendDAO.areFriends(me, friendId)) return Response.fail("You are not friends.");
                return Response.ok(wishlistDAO.getWishlist(friendId));
            }

            case CONTRIBUTE:
                return contribute(req, me);

            case GET_NOTIFICATIONS:
                return Response.ok(notificationDAO.list(me));

            case MARK_NOTIFICATIONS_READ:
                notificationDAO.markAllRead(me);
                return Response.ok("Marked read", null);

            default:
                return Response.fail("Unknown request: " + req.getType());
        }
    }

    // ---- auth ----
    private Response register(Request req) throws Exception {
        String username = req.getString("username");
        String password = req.getString("password");
        String displayName = req.getString("displayName");
        if (isBlank(username) || isBlank(password)) {
            return Response.fail("Username and password are required.");
        }
        if (userDAO.usernameExists(username)) {
            return Response.fail("Username already taken.");
        }
        User u = userDAO.register(username, PasswordUtil.sha256(password),
                isBlank(displayName) ? username : displayName);
        return Response.ok("Registered", u);
    }

    private Response login(Request req, ClientHandler session) throws Exception {
        User u = userDAO.validateLogin(req.getString("username"),
                PasswordUtil.sha256(req.getString("password")));
        if (u == null) return Response.fail("Wrong username or password.");
        session.setUserId(u.getId());
        session.setDisplayName(u.getDisplayName());
        server.registerOnline(u.getId(), session);
        return Response.ok("Welcome " + u.getDisplayName(), u);
    }

    private Response logout(ClientHandler session) {
        server.removeOnline(session.getUserId());
        session.setUserId(0);
        return Response.ok("Logged out", null);
    }

    // ---- the contribution + notification flow (spec items 7, 8, 9) ----
    private Response contribute(Request req, int me) throws Exception {
        int wishItemId = req.getInt("wishItemId");
        double amount = req.getDouble("amount");
        if (amount <= 0) return Response.fail("Amount must be greater than zero.");

        WishItem item = wishlistDAO.getWishItem(wishItemId);
        if (item == null) return Response.fail("Wish item not found.");
        if (item.isPurchased()) return Response.fail("This gift is already fully funded.");
        if (item.getOwnerId() == me) return Response.fail("You cannot contribute to your own wish list.");
        if (!friendDAO.areFriends(me, item.getOwnerId())) {
            return Response.fail("You can only contribute to friends' wish lists.");
        }

        double remaining = item.getPrice() - item.getContributed();
        if (amount > remaining) amount = remaining;    // never over-pay
        contributionDAO.add(wishItemId, me, amount);

        double total = contributionDAO.totalContributed(wishItemId);
        if (total >= item.getPrice()) {
            contributionDAO.markPurchased(wishItemId);
            String itemName = item.getItemName();
            User owner = userDAO.findById(item.getOwnerId());
            String ownerName = (owner == null) ? "your friend" : owner.getDisplayName();

            // (8) tell every buyer the gift is complete
            for (int buyerId : contributionDAO.distinctContributorIds(wishItemId)) {
                notifications.notifyUser(buyerId,
                        "The gift '" + itemName + "' for " + ownerName + " is now fully funded!",
                        NotificationType.ITEM_FUNDED);
            }
            // (9) tell the receiver an item on their list was bought
            notifications.notifyUser(item.getOwnerId(),
                    "'" + itemName + "' from your wish list has been bought by your friends!",
                    NotificationType.ITEM_BOUGHT);

            return Response.ok("Contribution added — gift fully funded!", null);
        }
        return Response.ok("Contribution added. Remaining: " + (item.getPrice() - total) + " EGP", null);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
