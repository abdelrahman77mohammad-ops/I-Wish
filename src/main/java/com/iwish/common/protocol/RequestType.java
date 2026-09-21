package com.iwish.common.protocol;

/** Every action a client can ask the server to perform. */
public enum RequestType {
    // auth
    REGISTER,
    LOGIN,
    LOGOUT,

    // friends / social
    SEARCH_USERS,
    SEND_FRIEND_REQUEST,
    LIST_FRIEND_REQUESTS,
    RESPOND_FRIEND_REQUEST,   // accept / decline
    LIST_FRIENDS,
    REMOVE_FRIEND,

    // catalog + wish list
    GET_CATALOG,
    GET_MY_WISHLIST,
    ADD_WISH_ITEM,
    UPDATE_WISH_ITEM,
    DELETE_WISH_ITEM,
    GET_FRIEND_WISHLIST,

    // contribution
    CONTRIBUTE,

    // notifications
    GET_NOTIFICATIONS,
    MARK_NOTIFICATIONS_READ
}
