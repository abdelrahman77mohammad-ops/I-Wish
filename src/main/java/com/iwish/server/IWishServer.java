package com.iwish.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * The network core of the server: listens for client connections, starts a
 * {@link ClientHandler} thread for each one, and keeps a registry of which
 * users are currently online (so notifications can be pushed to them).
 */
public class IWishServer {

    public static final int DEFAULT_PORT = 5000;

    private final int port;
    private ServerSocket serverSocket;
    private Thread acceptThread;
    private volatile boolean running = false;

    private final Map<Integer, ClientHandler> online = new ConcurrentHashMap<>();
    private final ServerController controller;
    private Consumer<String> logger = msg -> { };   // the GUI plugs its log here

    public IWishServer(int port) {
        this.port = port;
        this.controller = new ServerController(this);
    }

    public void setLogger(Consumer<String> logger) { this.logger = logger; }
    public void log(String msg) { logger.accept(msg); }

    public ServerController getController() { return controller; }
    public boolean isRunning() { return running; }
    public int getPort() { return port; }
    public int getOnlineCount() { return online.size(); }

    public void start() throws IOException {
        if (running) return;
        serverSocket = new ServerSocket(port);
        running = true;
        log("Server started on port " + port);
        acceptThread = new Thread(this::acceptLoop, "accept-loop");
        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                Thread t = new Thread(handler, "client-" + socket.getPort());
                t.setDaemon(true);
                t.start();
                log("New connection from " + socket.getInetAddress().getHostAddress());
            } catch (IOException e) {
                if (running) log("Accept error: " + e.getMessage());
            }
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) { }
        online.clear();
        log("Server stopped");
    }

    // ---- online registry (used to push notifications) ----
    public void registerOnline(int userId, ClientHandler handler) {
        online.put(userId, handler);
        log("User #" + userId + " is online (" + online.size() + " online)");
    }

    public void removeOnline(int userId) {
        if (userId > 0 && online.remove(userId) != null) {
            log("User #" + userId + " went offline (" + online.size() + " online)");
        }
    }

    public ClientHandler getOnline(int userId) {
        return online.get(userId);
    }
}
