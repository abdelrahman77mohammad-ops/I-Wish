package com.iwish.common.model;

/** Kind of notification (used for icons / filtering). */
public enum NotificationType {
    FRIEND_REQUEST,   // someone sent you a friend request
    ITEM_FUNDED,      // [buyer] a gift you contributed to is now fully funded
    ITEM_BOUGHT,      // [receiver] an item on your wish list was bought
    GENERAL
}
