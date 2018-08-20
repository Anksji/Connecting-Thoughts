package com.rsons.application.connecting_thoughts.Fetch_data;

/**
 * Created by ankit on 2/20/2018.
 */

public class CommentModel {

    String UserName,UserPicUrl,UserComment,UserRatingGiven,UserId;
    Object CommentDate;

    public CommentModel(String userId, String userPicUrl, Object commentDate,String userRatingGiven,String userComment,String userName) {
        UserName = userName;
        UserPicUrl = userPicUrl;
        UserComment = userComment;
        CommentDate = commentDate;
        UserRatingGiven = userRatingGiven;
        UserId=userId;
    }

    public String getUserName() {
        return UserName;
    }

    public String getUserId() {
        return UserId;
    }

    public String getUserPicUrl() {
        return UserPicUrl;
    }

    public String getUserComment() {
        return UserComment;
    }

    public Object getCommentDate() {
        return CommentDate;
    }

    public String getUserRatingGiven() {
        return UserRatingGiven;
    }
}
