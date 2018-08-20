package com.rsons.application.connecting_thoughts.Notification;

/**
 * Created by ankit on 3/26/2018.
 */

public class NotificationModel_class {

    String RequestUserName,RequestUserImage,RequestUserId,NotificationType;
    String StoryPublishedAuthor,StoryPublishedStoryId,StoryPublishedTitle,StoryPublishedIntro,StoryPublishedImage;
    Object StoryPublishedPublishedDate;
    Object RequestNotificationDate;
    public NotificationModel_class(String requestUserName, Object requestNotificationDate, String requestUserImage,
                                   String requestUserId, String notificationType) {
        RequestUserName = requestUserName;
        RequestNotificationDate = requestNotificationDate;
        RequestUserImage = requestUserImage;
        RequestUserId = requestUserId;
        NotificationType = notificationType;
    }

    public NotificationModel_class( String storyPublishedAuthor, String storyPublishedStoryId, String storyPublishedTitle,
                                    String storyPublishedIntro, Object storyPublishedPublishedDate,String storypublished_image,
                                    String notificationType) {
        this.StoryPublishedImage=storypublished_image;
        NotificationType = notificationType;
        StoryPublishedAuthor = storyPublishedAuthor;
        StoryPublishedStoryId = storyPublishedStoryId;
        StoryPublishedTitle = storyPublishedTitle;
        StoryPublishedIntro = storyPublishedIntro;
        StoryPublishedPublishedDate = storyPublishedPublishedDate;
    }

    public String getStoryPublishedImage() {
        return StoryPublishedImage;
    }

    public String getRequestUserName() {
        return RequestUserName;
    }

    public Object getRequestNotificationDate() {
        return RequestNotificationDate;
    }

    public String getRequestUserImage() {
        return RequestUserImage;
    }

    public String getRequestUserId() {
        return RequestUserId;
    }

    public String getNotificationType() {
        return NotificationType;
    }

    public String getStoryPublishedAuthor() {
        return StoryPublishedAuthor;
    }

    public String getStoryPublishedStoryId() {
        return StoryPublishedStoryId;
    }

    public String getStoryPublishedTitle() {
        return StoryPublishedTitle;
    }

    public String getStoryPublishedIntro() {
        return StoryPublishedIntro;
    }

    public Object getStoryPublishedPublishedDate() {
        return StoryPublishedPublishedDate;
    }
}
