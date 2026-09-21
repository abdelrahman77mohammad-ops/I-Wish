package com.iwish.client.ui;

import com.iwish.client.ServerConnection;
import com.iwish.common.model.FriendRequest;
import com.iwish.common.model.User;
import com.iwish.common.protocol.Request;
import com.iwish.common.protocol.RequestType;
import com.iwish.common.protocol.Response;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/** "Friends" tab: search users & send requests · incoming requests · my friends. */
public class FriendsPanel extends JPanel {

    private final ServerConnection conn;

    private final JTextField searchField = new JTextField(14);
    private final DefaultListModel<User> searchModel = new DefaultListModel<>();
    private final JList<User> searchList = new JList<>(searchModel);

    private final DefaultListModel<FriendRequest> reqModel = new DefaultListModel<>();
    private final JList<FriendRequest> reqList = new JList<>(reqModel);

    private final DefaultListModel<User> friendsModel = new DefaultListModel<>();
    private final JList<User> friendsList = new JList<>(friendsModel);

    public FriendsPanel(ServerConnection conn, User me) {
        this.conn = conn;
        setLayout(new GridLayout(1, 3, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildSearchColumn());
        add(buildRequestsColumn());
        add(buildFriendsColumn());

        refreshRequests();
        refreshFriends();
    }

    private JPanel buildSearchColumn() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(new TitledBorder("Find people"));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        JButton search = new JButton("Search");
        top.add(searchField);
        top.add(search);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(searchList), BorderLayout.CENTER);
        JButton send = new JButton("Send friend request");
        p.add(send, BorderLayout.SOUTH);
        search.addActionListener(e -> doSearch());
        searchField.addActionListener(e -> doSearch());
        send.addActionListener(e -> sendRequest());
        return p;
    }

    private JPanel buildRequestsColumn() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(new TitledBorder("Incoming requests"));
        p.add(new JScrollPane(reqList), BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        JButton accept = new JButton("Accept");
        JButton decline = new JButton("Decline");
        JButton refresh = new JButton("Refresh");
        buttons.add(accept);
        buttons.add(decline);
        buttons.add(refresh);
        p.add(buttons, BorderLayout.SOUTH);
        accept.addActionListener(e -> respond(true));
        decline.addActionListener(e -> respond(false));
        refresh.addActionListener(e -> refreshRequests());
        return p;
    }

    private JPanel buildFriendsColumn() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(new TitledBorder("My friends"));
        p.add(new JScrollPane(friendsList), BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        JButton remove = new JButton("Remove");
        JButton refresh = new JButton("Refresh");
        buttons.add(remove);
        buttons.add(refresh);
        p.add(buttons, BorderLayout.SOUTH);
        remove.addActionListener(e -> removeFriend());
        refresh.addActionListener(e -> refreshFriends());
        return p;
    }

    private void doSearch() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) return;
        Response res = conn.send(new Request(RequestType.SEARCH_USERS).set("query", q));
        searchModel.clear();
        if (res.isSuccess() && res.getData() instanceof List) {
            for (Object o : (List<?>) res.getData()) searchModel.addElement((User) o);
            if (searchModel.isEmpty()) JOptionPane.showMessageDialog(this, "No users found.");
        }
    }

    private void sendRequest() {
        User sel = searchList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select a user first.");
            return;
        }
        Response res = conn.send(new Request(RequestType.SEND_FRIEND_REQUEST).set("addresseeId", sel.getId()));
        JOptionPane.showMessageDialog(this, res.getMessage());
    }

    private void respond(boolean accept) {
        FriendRequest sel = reqList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select a request first.");
            return;
        }
        conn.send(new Request(RequestType.RESPOND_FRIEND_REQUEST)
                .set("friendshipId", sel.getFriendshipId())
                .set("accept", accept));
        refreshRequests();
        refreshFriends();
    }

    private void removeFriend() {
        User sel = friendsList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select a friend first.");
            return;
        }
        conn.send(new Request(RequestType.REMOVE_FRIEND).set("friendId", sel.getId()));
        refreshFriends();
    }

    public final void refreshRequests() {
        Response res = conn.send(new Request(RequestType.LIST_FRIEND_REQUESTS));
        reqModel.clear();
        if (res.isSuccess() && res.getData() instanceof List) {
            for (Object o : (List<?>) res.getData()) reqModel.addElement((FriendRequest) o);
        }
    }

    public final void refreshFriends() {
        Response res = conn.send(new Request(RequestType.LIST_FRIENDS));
        friendsModel.clear();
        if (res.isSuccess() && res.getData() instanceof List) {
            for (Object o : (List<?>) res.getData()) friendsModel.addElement((User) o);
        }
    }
}
