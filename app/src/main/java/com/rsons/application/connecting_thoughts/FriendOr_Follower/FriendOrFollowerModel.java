package com.rsons.application.connecting_thoughts.FriendOr_Follower;

/**
 * Created by ankit on 2/23/2018.
 */

public class FriendOrFollowerModel {

    String FriendId,FriendPic,FriendName,FriendStatus;

    public String getFriendId() {
        return FriendId;
    }

    public String getFriendPic() {
        return FriendPic;
    }

    public String getFriendName() {
        return FriendName;
    }

    public String getFriendStatus() {
        return FriendStatus;
    }

    public FriendOrFollowerModel(String friendId, String friendPic, String friendName, String friendStatus) {

        FriendId = friendId;
        FriendPic = friendPic;
        FriendName = friendName;
        FriendStatus = friendStatus;
    }
}
