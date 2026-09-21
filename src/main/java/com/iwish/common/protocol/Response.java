package com.iwish.common.protocol;

import java.io.Serializable;

/**
 * The server's reply to a {@link Request}.
 * {@code data} carries the payload (a model object or a List of them).
 */
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private Object data;

    public Response() { }

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Response(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // convenience factory methods
    public static Response ok(Object data) { return new Response(true, "OK", data); }
    public static Response ok(String message, Object data) { return new Response(true, message, data); }
    public static Response fail(String message) { return new Response(false, message); }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
