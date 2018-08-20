package com.rsons.application.connecting_thoughts.ChatFragFriends;

/**
 * Created by ankit on 2/28/2018.
 */

public class ChatFriendsModel {

    String FriendId;
    String FriendPic;
    String FriendName;
    String FriendLastMessage;
    String UnreadMessages;

    public ChatFriendsModel(String friendId, String friendPic, String friendName, String friendLastMessage, String unreadMessages) {
        FriendId = friendId;
        FriendPic = friendPic;
        FriendName = friendName;
        FriendLastMessage = friendLastMessage;
        UnreadMessages = unreadMessages;
    }

    public String getFriendId() {
        return FriendId;
    }

    public String getFriendPic() {
        return FriendPic;
    }

    public String getFriendName() {
        return FriendName;
    }

    public String getFriendLastMessage() {
        return FriendLastMessage;
    }

    public String getUnreadMessages() {
        return UnreadMessages;
    }
}
