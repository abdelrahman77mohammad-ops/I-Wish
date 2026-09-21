package com.iwish.server.ui;

import com.iwish.server.IWishServer;
import com.iwish.server.dao.CatalogDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/** The Server control window: Start/Stop, live log, and an admin panel to add catalog items. */
public class ServerFrame extends JFrame {

    private final JTextField portField = new JTextField(String.valueOf(IWishServer.DEFAULT_PORT), 6);
    private final JButton startBtn = new JButton("Start");
    private final JButton stopBtn  = new JButton("Stop");
    private final JLabel statusLbl = new JLabel("Stopped");
    private final JTextArea logArea = new JTextArea();

    // admin add-item fields
    private final JTextField itemName  = new JTextField(14);
    private final JTextField itemDesc  = new JTextField(14);
    private final JTextField itemPrice = new JTextField(6);

    private final CatalogDAO catalogDAO = new CatalogDAO();
    private IWishServer server;
    private Timer statusTimer;

    public ServerFrame() {
        super("I-Wish Server");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 520);
        setLocationRelativeTo(null);

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildLog(), BorderLayout.CENTER);
        add(buildAdminPanel(), BorderLayout.SOUTH);

        stopBtn.setEnabled(false);
        startBtn.addActionListener(e -> startServer());
        stopBtn.addActionListener(e -> stopServer());
    }

    private JPanel buildTopBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        p.add(new JLabel("Port:"));
        p.add(portField);
        p.add(startBtn);
        p.add(stopBtn);
        p.add(new JLabel("   Status:"));
        statusLbl.setFont(statusLbl.getFont().deriveFont(Font.BOLD));
        p.add(statusLbl);
        return p;
    }

    private JScrollPane buildLog() {
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(logArea);
        sp.setBorder(new TitledBorder("Server log"));
        return sp;
    }

    private JPanel buildAdminPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        p.setBorder(new TitledBorder("Admin — add a catalog item"));
        p.add(new JLabel("Name:"));   p.add(itemName);
        p.add(new JLabel("Desc:"));   p.add(itemDesc);
        p.add(new JLabel("Price:"));  p.add(itemPrice);
        JButton addBtn = new JButton("Add item");
        addBtn.addActionListener(e -> addCatalogItem());
        p.add(addBtn);
        return p;
    }

    private void startServer() {
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Port must be a number.");
            return;
        }
        server = new IWishServer(port);
        server.setLogger(this::log);   // log() marshals to the EDT
        try {
            server.start();
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            portField.setEnabled(false);
            statusLbl.setText("Running (0 online)");
            statusTimer = new Timer(1000, e ->
                    statusLbl.setText("Running (" + server.getOnlineCount() + " online)"));
            statusTimer.start();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not start server: " + ex.getMessage());
        }
    }

    private void stopServer() {
        if (server != null) server.stop();
        if (statusTimer != null) statusTimer.stop();
        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        portField.setEnabled(true);
        statusLbl.setText("Stopped");
    }

    private void addCatalogItem() {
        String name = itemName.getText().trim();
        String desc = itemDesc.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Item name is required.");
            return;
        }
        double price;
        try {
            price = Double.parseDouble(itemPrice.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a number.");
            return;
        }
        try {
            catalogDAO.add(name, desc, price);
            log("Catalog item added: " + name + " (" + price + " EGP)");
            itemName.setText(""); itemDesc.setText(""); itemPrice.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not add item: " + ex.getMessage()
                    + "\n(Is the database running and schema.sql imported?)");
        }
    }

    /** Append a timestamped line to the log (safe to call from any thread). */
    private void log(String msg) {
        String line = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + msg + "\n";
        SwingUtilities.invokeLater(() -> {
            logArea.append(line);
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}
