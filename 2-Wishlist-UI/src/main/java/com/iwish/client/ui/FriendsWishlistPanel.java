package com.iwish.client.ui;

import com.iwish.client.ServerConnection;
import com.iwish.common.model.User;
import com.iwish.common.model.WishItem;
import com.iwish.common.protocol.Request;
import com.iwish.common.protocol.RequestType;
import com.iwish.common.protocol.Response;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** "Friends' Wish Lists" tab: pick a friend, view their list, and contribute money. */
public class FriendsWishlistPanel extends JPanel {

    private final ServerConnection conn;
    private final DefaultComboBoxModel<User> friendsModel = new DefaultComboBoxModel<>();
    private final JComboBox<User> friendsCombo = new JComboBox<>(friendsModel);
    private final DefaultListModel<WishItem> itemsModel = new DefaultListModel<>();
    private final JList<WishItem> itemsList = new JList<>(itemsModel);

    public FriendsWishlistPanel(ServerConnection conn, User me) {
        this.conn = conn;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        JButton load = new JButton("View wish list");
        JButton reloadFriends = new JButton("Reload friends");
        top.add(new JLabel("Friend:"));
        top.add(friendsCombo);
        top.add(load);
        top.add(reloadFriends);
        add(top, BorderLayout.NORTH);

        add(new JScrollPane(itemsList), BorderLayout.CENTER);

        JButton contribute = new JButton("Contribute to selected item");
        add(contribute, BorderLayout.SOUTH);

        load.addActionListener(e -> loadWishlist());
        reloadFriends.addActionListener(e -> loadFriends());
        contribute.addActionListener(e -> contribute());
        loadFriends();
    }

    public final void loadFriends() {
        Response res = conn.send(new Request(RequestType.LIST_FRIENDS));
        friendsModel.removeAllElements();
        if (res.isSuccess() && res.getData() instanceof List) {
            for (Object o : (List<?>) res.getData()) friendsModel.addElement((User) o);
        }
    }

    private void loadWishlist() {
        User friend = (User) friendsCombo.getSelectedItem();
        if (friend == null) {
            JOptionPane.showMessageDialog(this, "Pick a friend first.");
            return;
        }
        Response res = conn.send(new Request(RequestType.GET_FRIEND_WISHLIST).set("friendId", friend.getId()));
        itemsModel.clear();
        if (res.isSuccess() && res.getData() instanceof List) {
            for (Object o : (List<?>) res.getData()) itemsModel.addElement((WishItem) o);
            if (itemsModel.isEmpty()) {
                JOptionPane.showMessageDialog(this, friend.getDisplayName() + " has no wish items yet.");
            }
        } else {
            JOptionPane.showMessageDialog(this, res.getMessage());
        }
    }

    private void contribute() {
        WishItem sel = itemsList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select an item first.");
            return;
        }
        if (sel.isPurchased()) {
            JOptionPane.showMessageDialog(this, "This gift is already fully funded.");
            return;
        }
        String input = JOptionPane.showInputDialog(this,
                "Item: " + sel.getItemName()
                        + "\nRemaining: " + sel.remaining() + " EGP"
                        + "\nEnter amount to contribute:");
        if (input == null) return;
        double amount;
        try {
            amount = Double.parseDouble(input.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            return;
        }
        Response res = conn.send(new Request(RequestType.CONTRIBUTE)
                .set("wishItemId", sel.getId())
                .set("amount", amount));
        JOptionPane.showMessageDialog(this, res.getMessage());
        loadWishlist();
    }
}
