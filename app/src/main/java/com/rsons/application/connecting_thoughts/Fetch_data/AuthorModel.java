package com.rsons.application.connecting_thoughts.Fetch_data;

/**
 * Created by ankit on 3/7/2018.
 */

public class AuthorModel {

    String AuthorId,AuthorName,authorImageUrl,AuthorWriting,AuthorDescription;

    public String getAuthorId() {
        return AuthorId;
    }

    public String getAuthorName() {
        return AuthorName;
    }
    public String getAuthorDescription(){
        return AuthorDescription;
    }

    public String getAuthorImageUrl() {
        return authorImageUrl;
    }

    public String getAuthorWriting() {
        return AuthorWriting;
    }

    public AuthorModel(String authorId, String authorName, String authorImageUrl, String authorWriting,String mAuthorDescription) {

        AuthorId = authorId;
        AuthorName = authorName;
        this.authorImageUrl = authorImageUrl;
        AuthorWriting = authorWriting;
        this.AuthorDescription=mAuthorDescription;
    }
}
