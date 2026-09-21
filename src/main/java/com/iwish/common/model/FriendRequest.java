package com.iwish.common.model;

import java.io.Serializable;

/** A pending, incoming friend request (shown so the user can Accept/Decline). */
public class FriendRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int friendshipId;
    private int requesterId;
    private String requesterName;

    public FriendRequest() { }

    public FriendRequest(int friendshipId, int requesterId, String requesterName) {
        this.friendshipId = friendshipId;
        this.requesterId = requesterId;
        this.requesterName = requesterName;
    }

    public int getFriendshipId() { return friendshipId; }
    public void setFriendshipId(int friendshipId) { this.friendshipId = friendshipId; }

    public int getRequesterId() { return requesterId; }
    public void setRequesterId(int requesterId) { this.requesterId = requesterId; }

    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }

    @Override
    public String toString() {
        return requesterName + " wants to be your friend";
    }
}
