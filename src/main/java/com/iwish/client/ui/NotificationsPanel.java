package com.iwish.client.ui;

import com.iwish.client.ServerConnection;
import com.iwish.common.model.Notification;
import com.iwish.common.model.User;
import com.iwish.common.protocol.Request;
import com.iwish.common.protocol.RequestType;
import com.iwish.common.protocol.Response;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** "Notifications" tab: list stored notifications + receive live pushes. */
public class NotificationsPanel extends JPanel {

    private final ServerConnection conn;
    private final DefaultListModel<Notification> model = new DefaultListModel<>();
    private final JList<Notification> list = new JList<>(model);

    public NotificationsPanel(ServerConnection conn, User me) {
        this.conn = conn;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Your notifications ( • = unread ):"), BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton markRead = new JButton("Mark all read");
        JButton refresh = new JButton("Refresh");
        buttons.add(markRead);
        buttons.add(refresh);
        add(buttons, BorderLayout.SOUTH);

        markRead.addActionListener(e -> {
            conn.send(new Request(RequestType.MARK_NOTIFICATIONS_READ));
            refresh();
        });
        refresh.addActionListener(e -> refresh());
        refresh();
    }

    public final void refresh() {
        Response res = conn.send(new Request(RequestType.GET_NOTIFICATIONS));
        model.clear();
        if (res.isSuccess() && res.getData() instanceof List) {
            for (Object o : (List<?>) res.getData()) {
                model.addElement((Notification) o);
            }
        }
    }

    /** Called from the live push listener when a new notification arrives. */
    public void addNotification(Notification n) {
        model.add(0, n);
    }
}
