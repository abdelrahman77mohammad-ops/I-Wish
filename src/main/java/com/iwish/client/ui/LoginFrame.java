package com.iwish.client.ui;

import com.iwish.client.ServerConnection;
import com.iwish.common.model.User;
import com.iwish.common.protocol.Request;
import com.iwish.common.protocol.RequestType;
import com.iwish.common.protocol.Response;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/** Sign-in / Register window. On success it opens the {@link MainFrame}. */
public class LoginFrame extends JFrame {

    private final JTextField host = new JTextField("localhost", 10);
    private final JTextField port = new JTextField("5000", 5);
    private final JTextField username = new JTextField(14);
    private final JPasswordField password = new JPasswordField(14);
    private final JTextField displayName = new JTextField(14);

    public LoginFrame() {
        super("I-Wish — Sign in");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 320);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        addRow(g, y++, "Server:", hostPortPanel());
        addRow(g, y++, "Username:", username);
        addRow(g, y++, "Password:", password);
        addRow(g, y++, "Name (sign up):", displayName);

        JButton loginBtn = new JButton("Sign in");
        JButton registerBtn = new JButton("Register");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.add(loginBtn);
        buttons.add(registerBtn);
        g.gridx = 0; g.gridy = y; g.gridwidth = 2;
        add(buttons, g);

        loginBtn.addActionListener(e -> submit(false));
        registerBtn.addActionListener(e -> submit(true));
    }

    private JPanel hostPortPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.add(host);
        p.add(new JLabel(":"));
        p.add(port);
        return p;
    }

    private void addRow(GridBagConstraints g, int y, String label, Component field) {
        g.gridx = 0; g.gridy = y; g.gridwidth = 1;
        add(new JLabel(label), g);
        g.gridx = 1; g.gridy = y;
        add(field, g);
    }

    private void submit(boolean register) {
        String user = username.getText().trim();
        String pass = new String(password.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username and password.");
            return;
        }

        ServerConnection conn = new ServerConnection();
        try {
            conn.connect(host.getText().trim(), Integer.parseInt(port.getText().trim()));
        } catch (IOException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Cannot reach the server: " + ex.getMessage() + "\nMake sure the server is started.");
            return;
        }

        Request req = new Request(register ? RequestType.REGISTER : RequestType.LOGIN)
                .set("username", user)
                .set("password", pass);
        if (register) {
            req.set("displayName", displayName.getText().trim());
        }

        Response res = conn.send(req);

        if (register) {
            JOptionPane.showMessageDialog(this,
                    res.isSuccess() ? "Registered! You can sign in now." : res.getMessage());
            conn.close();
            return;
        }

        if (!res.isSuccess()) {
            JOptionPane.showMessageDialog(this, res.getMessage());
            conn.close();
            return;
        }

        User me = (User) res.getData();
        new MainFrame(conn, me).setVisible(true);
        dispose();
    }
}
