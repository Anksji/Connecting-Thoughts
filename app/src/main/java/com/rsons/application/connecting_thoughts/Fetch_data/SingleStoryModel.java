package com.rsons.application.connecting_thoughts.Fetch_data;

/**
 * Created by ankit on 2/13/2018.
 */

public class SingleStoryModel {

    String StoryName,StoryImage,StoryIntro,Storyviewcount,StoryRating,StoryId,AuthorId;
    long numberOfReports;
    boolean IsAuthorHide;

    public SingleStoryModel(long NumberOfReport,String storyName, String storyImage, String storyIntro,
                            String storyviewcount, String storyRating, String storyId, String authorId,boolean is_author_hide) {
        StoryName = storyName;
        StoryImage = storyImage;
        StoryIntro = storyIntro;
        Storyviewcount = storyviewcount;
        StoryRating = storyRating;
        StoryId = storyId;
        AuthorId = authorId;
        numberOfReports=NumberOfReport;
        IsAuthorHide=is_author_hide;
    }

    public SingleStoryModel() {

    }
    public boolean getIsAuthorHide(){
        return IsAuthorHide;
    }

    public long getNumberOfReports() {
        return numberOfReports;
    }

    public String getStoryName() {
        return StoryName;
    }

    public String getStoryImage() {
        return StoryImage;
    }

    public String getStoryIntro() {
        return StoryIntro;
    }

    public String getStoryviewcount() {
        return Storyviewcount;
    }

    public String getStoryRating() {
        return StoryRating;
    }


    public String getStoryId() {
        return StoryId;
    }

    public String getAuthorId() {
        return AuthorId;
    }
}
