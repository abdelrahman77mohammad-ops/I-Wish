package com.iwish.client.ui;

import com.iwish.client.ServerConnection;
import com.iwish.common.model.User;
import com.iwish.common.protocol.Request;
import com.iwish.common.protocol.RequestType;

import javax.swing.*;

/** The main window after sign-in: four tabs + live notifications. */
public class MainFrame extends JFrame {

    private final ServerConnection conn;
    private final NotificationsPanel notificationsPanel;

    public MainFrame(ServerConnection conn, User me) {
        super("I-Wish — " + me.getDisplayName());
        this.conn = conn;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(780, 560);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("My Wish List", new WishlistPanel(conn, me));
        tabs.addTab("Friends", new FriendsPanel(conn, me));
        tabs.addTab("Friends' Wish Lists", new FriendsWishlistPanel(conn, me));
        notificationsPanel = new NotificationsPanel(conn, me);
        tabs.addTab("Notifications", notificationsPanel);
        add(tabs);

        setJMenuBar(buildMenu());

        // live push: show a popup and add it to the Notifications tab (on the EDT)
        conn.setNotificationListener(n -> SwingUtilities.invokeLater(() -> {
            notificationsPanel.addNotification(n);
            JOptionPane.showMessageDialog(this, n.getMessage(),
                    "Notification", JOptionPane.INFORMATION_MESSAGE);
        }));
    }

    private JMenuBar buildMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Account");
        JMenuItem logout = new JMenuItem("Logout");
        logout.addActionListener(e -> {
            conn.send(new Request(RequestType.LOGOUT));
            conn.close();
            dispose();
            new LoginFrame().setVisible(true);
        });
        menu.add(logout);
        bar.add(menu);
        return bar;
    }
}
