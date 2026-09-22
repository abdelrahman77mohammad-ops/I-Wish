package com.iwish.client;

import com.iwish.common.model.Notification;
import com.iwish.common.protocol.Request;
import com.iwish.common.protocol.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * The client's link to the server. One background reader thread reads objects
 * from the socket: a {@link Response} is a reply to a request (handed to the
 * waiting {@link #send} call), while a {@link Notification} is a live push
 * (handed to the notification listener).
 */
public class ServerConnection {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean connected = false;

    private final BlockingQueue<Response> responses = new LinkedBlockingQueue<>();
    private Consumer<Notification> notificationListener = n -> { };

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
        connected = true;

        Thread reader = new Thread(this::readLoop, "server-reader");
        reader.setDaemon(true);
        reader.start();
    }

    public void setNotificationListener(Consumer<Notification> listener) {
        this.notificationListener = (listener == null) ? n -> { } : listener;
    }

    private void readLoop() {
        try {
            Object o;
            while (connected && (o = in.readObject()) != null) {
                if (o instanceof Notification) {
                    notificationListener.accept((Notification) o);
                } else if (o instanceof Response) {
                    responses.put((Response) o);
                }
            }
        } catch (Exception e) {
            // server closed the connection
        } finally {
            connected = false;
        }
    }

    /** Send a request and block until the matching reply arrives. */
    public synchronized Response send(Request req) {
        if (!connected) return Response.fail("Not connected to the server.");
        try {
            out.writeObject(req);
            out.flush();
            out.reset();
            return responses.take();
        } catch (Exception e) {
            return Response.fail("Connection error: " + e.getMessage());
        }
    }

    public boolean isConnected() { return connected; }

    public void close() {
        connected = false;
        try { if (socket != null) socket.close(); } catch (IOException ignored) { }
    }
}
