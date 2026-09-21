package com.iwish.common.protocol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A message the client sends to the server: a {@link RequestType} plus a
 * small bag of named parameters. Using a map keeps the protocol simple and
 * lets us add a parameter without creating a new class each time.
 * (All values put here must be Serializable — String / Integer / Double, etc.)
 */
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private RequestType type;
    private final Map<String, Object> params = new HashMap<>();

    public Request() { }

    public Request(RequestType type) {
        this.type = type;
    }

    public RequestType getType() { return type; }
    public void setType(RequestType type) { this.type = type; }

    /** Add a parameter; returns this so calls can be chained. */
    public Request set(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public Object get(String key) { return params.get(key); }

    public int getInt(String key) {
        Object o = params.get(key);
        return (o == null) ? 0 : ((Number) o).intValue();
    }

    public double getDouble(String key) {
        Object o = params.get(key);
        return (o == null) ? 0 : ((Number) o).doubleValue();
    }

    public String getString(String key) {
        Object o = params.get(key);
        return (o == null) ? null : o.toString();
    }

    public Map<String, Object> getParams() { return params; }
}
