package com.iwish.server;

import com.iwish.common.protocol.Request;
import com.iwish.common.protocol.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles one connected client on its own thread: reads {@link Request}s,
 * asks the {@link ServerController} to process them, and writes back the
 * {@link Response}. The same socket is also used to push notifications, so
 * {@link #send(Object)} is synchronized.
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final IWishServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private int userId = 0;          // 0 = not logged in yet
    private String displayName;

    public ClientHandler(Socket socket, IWishServer server) {
        this.socket = socket;
        this.server = server;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    /** Thread-safe write — used both for replies and for pushed notifications. */
    public synchronized void send(Object obj) {
        try {
            out.writeObject(obj);
            out.flush();
            out.reset();   // avoid the stream caching stale copies of mutable objects
        } catch (IOException e) {
            server.log("Send failed to user #" + userId + ": " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // NOTE: create the output stream first (and flush) before the input
            // stream, otherwise both ends can block waiting for a stream header.
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            Object obj;
            while ((obj = in.readObject()) != null) {
                if (obj instanceof Request) {
                    Response response = server.getController().handle((Request) obj, this);
                    send(response);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // client disconnected or stream ended — fall through to cleanup
        } finally {
            server.removeOnline(userId);
            try { socket.close(); } catch (IOException ignored) { }
        }
    }
}
