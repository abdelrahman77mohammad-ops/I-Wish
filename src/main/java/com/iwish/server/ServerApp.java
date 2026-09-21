package com.iwish.server;

import com.iwish.server.ui.ServerFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Entry point for the SERVER application. Run this first, then Start it. */
public class ServerApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new ServerFrame().setVisible(true));
    }
}
