package com.iwish.client;

import com.iwish.client.ui.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Entry point for the CLIENT application. Start the server first, then run this. */
public class ClientApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
