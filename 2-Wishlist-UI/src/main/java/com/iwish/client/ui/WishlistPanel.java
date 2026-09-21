package com.iwish.client.ui;

import com.iwish.client.ServerConnection;
import com.iwish.common.model.CatalogItem;
import com.iwish.common.model.User;
import com.iwish.common.model.WishItem;
import com.iwish.common.protocol.Request;
import com.iwish.common.protocol.RequestType;
import com.iwish.common.protocol.Response;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** "My Wish List" tab: add items from the catalog, remove items, see funding progress. */
public class WishlistPanel extends JPanel {

    private final ServerConnection conn;
    private final DefaultListModel<WishItem> model = new DefaultListModel<>();
    private final JList<WishItem> list = new JList<>(model);

    public WishlistPanel(ServerConnection conn, User me) {
        this.conn = conn;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Your wish list — friends can contribute toward these:"), BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add item");
        JButton remove = new JButton("Remove selected");
        JButton refresh = new JButton("Refresh");
        buttons.add(add);
        buttons.add(remove);
        buttons.add(refresh);
        add(buttons, BorderLayout.SOUTH);

        add.addActionListener(e -> addItem());
        remove.addActionListener(e -> removeItem());
        refresh.addActionListener(e -> refresh());
        refresh();
    }

    public final void refresh() {
        Response res = conn.send(new Request(RequestType.GET_MY_WISHLIST));
        model.clear();
        if (res.isSuccess() && res.getData() instanceof List) {
            for (Object o : (List<?>) res.getData()) {
                model.addElement((WishItem) o);
            }
        }
    }

    private void addItem() {
        Response cat = conn.send(new Request(RequestType.GET_CATALOG));
        if (!cat.isSuccess() || !(cat.getData() instanceof List)) {
            JOptionPane.showMessageDialog(this, "Could not load the catalog.");
            return;
        }
        @SuppressWarnings("unchecked")
        List<CatalogItem> items = (List<CatalogItem>) cat.getData();
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The catalog is empty. Ask the admin (server) to add items.");
            return;
        }
        CatalogItem choice = (CatalogItem) JOptionPane.showInputDialog(
                this, "Pick an item to add:", "Add to wish list",
                JOptionPane.PLAIN_MESSAGE, null, items.toArray(), items.get(0));
        if (choice != null) {
            Response res = conn.send(new Request(RequestType.ADD_WISH_ITEM).set("catalogItemId", choice.getId()));
            if (!res.isSuccess()) JOptionPane.showMessageDialog(this, res.getMessage());
            refresh();
        }
    }

    private void removeItem() {
        WishItem sel = list.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select an item first.");
            return;
        }
        Response res = conn.send(new Request(RequestType.DELETE_WISH_ITEM).set("wishItemId", sel.getId()));
        if (!res.isSuccess()) JOptionPane.showMessageDialog(this, res.getMessage());
        refresh();
    }
}
