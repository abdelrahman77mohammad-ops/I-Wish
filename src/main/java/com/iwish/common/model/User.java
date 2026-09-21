package com.iwish.common.model;

import java.io.Serializable;

/** A user account (never carries the password when sent to a client). */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String displayName;

    public User() { }

    public User(int id, String username, String displayName) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    @Override
    public String toString() {           // shown directly in JList / JComboBox
        return displayName + " (@" + username + ")";
    }
}
