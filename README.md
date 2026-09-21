# I-Wish

A Java desktop **client–server** application. A user adds friends, builds a wish
list from a shop catalog, views friends' wish lists, and contributes money toward
buying their gifts. When a gift is fully funded, the **buyers** and the **receiver**
get notifications.

- **Client** and **Server** are separate Swing apps that talk over **TCP sockets**
  (Java object streams).
- The server stores everything in **MySQL** through a **DAO** layer (JDBC).
- The only 3rd-party library is the **MySQL JDBC driver** (added automatically by Maven).

```
 Client (Swing)  ──socket──►  Server (Swing GUI + a thread per client)  ──JDBC──►  MySQL
    sends Request               ServerController routes the request               DAO layer
    gets  Response              sends back a Response
    gets  Notification  ◄────push──── when a gift is fully funded
```

---

## Team roles & contributions

| # | Role | Main files |
|---|------|-----------|
| 1 | **Auth & social UI** | `client/ui/LoginFrame.java`, `client/ui/FriendsPanel.java` |
| 2 | **Wishlist & contribution UI** | `client/ui/WishlistPanel.java`, `client/ui/FriendsWishlistPanel.java`, `client/ui/NotificationsPanel.java`, `client/ui/MainFrame.java` |
| 3 | **Client network & notifications** | `client/ServerConnection.java`, `client/ClientApp.java` |
| 4 | **DAO layer (Database)** | `server/dao/*` (Connection + 6 DAOs), `database/schema.sql` |
| 5 | **Network & controller** | `server/IWishServer.java`, `server/ClientHandler.java`, `server/ServerController.java`, `common/protocol/*` |
| 6 | **Server GUI & Notification controller** | `server/ui/ServerFrame.java`, `server/ServerApp.java`, `server/NotificationController.java` |

`common/model/*` (User, WishItem, …) is shared by everyone.

---

## Requirements
- **JDK 17+**
- **MySQL 8** running locally
- An IDE that opens Maven projects (IntelliJ IDEA or NetBeans)

## Setup (once)
1. **Create the database**: open `database/schema.sql` in MySQL Workbench and run it
   (or `mysql -u root -p < database/schema.sql`). It creates the `iwish` database,
   all tables, and a few sample catalog items.
2. **Set your DB password**: open
   `src/main/java/com/iwish/server/dao/ConnectionManager.java`
   and change `USER` / `PASSWORD` to your local MySQL credentials.
3. Open the project folder in IntelliJ (**File → Open**, pick the folder with
   `pom.xml`). IntelliJ downloads the MySQL driver automatically.

## Run
1. Run **`server/ServerApp.java`** → the Server window opens → click **Start**.
2. Run **`client/ClientApp.java`** → **Register** two users, then **Sign in**.
   Run `ClientApp` again (a second instance) to sign in as the second user, so you
   can test friends and contributions between them.

> Tip: to run two clients in IntelliJ, enable *Run → Edit Configurations → Modify
> options → Allow multiple instances* for `ClientApp`.

---

## Demo walkthrough (for the discussion)
1. Start the server (**Start**).
2. Client A: register `sara`, Client B: register `omar`; sign both in.
3. B → **Friends** → search `sara` → *Send friend request*.
4. A → **Friends** → *Accept* Omar's request (A also gets a live notification).
5. A → **My Wish List** → *Add item* (e.g. Smart Watch, 2200).
6. B → **Friends' Wish Lists** → pick Sara → *View* → *Contribute* 2200.
7. The item is now fully funded → **both** A (receiver) and B (buyer) get a live
   notification popup, and it appears in the **Notifications** tab.
8. Server window shows the whole log (connections, online users, notifications).

---

## Suggested commit plan (build it in order, commit each step as you finish it)

| Day | Commit |
|-----|--------|
| Sat | `chore: init Maven project + MySQL schema` |
| Sat | `feat(common): shared model + client-server protocol` |
| Sun | `feat(dao): JDBC data-access layer` |
| Sun | `feat(server): socket server + client handler + controller` |
| Mon | `feat(server): server GUI + notification controller` |
| Mon | `feat(client): server connection (networking + live notifications)` |
| Tue | `feat(client): login + friends UI` |
| Tue | `feat(client): wishlist, friends' wishlist & notifications UI` |
| Tue | `docs: README (setup, roles, demo)` |

---

## Notes
- Passwords are stored as **SHA-256** hashes, never plain text (`server/util/PasswordUtil`).
- The server is **multi-threaded**: one `ClientHandler` thread per connected client;
  online users are tracked in a `ConcurrentHashMap` so notifications can be pushed.
- The client has **one reader thread**: a `Response` is a reply to a request, a
  `Notification` is a live push — they are told apart by their type.
