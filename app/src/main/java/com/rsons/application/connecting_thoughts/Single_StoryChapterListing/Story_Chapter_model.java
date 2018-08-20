package com.rsons.application.connecting_thoughts.Single_StoryChapterListing;

/**
 * Created by ankit on 3/3/2018.
 */

public class Story_Chapter_model {

    String StoryId,StoryName;
    private static String Current_story_id;

    public Story_Chapter_model(String storyId, String storyName) {
        StoryId = storyId;
        StoryName = storyName;

    }

    public static void setCurrent_story_id(String current_story_id) {
        Current_story_id = current_story_id;
    }

    public String getCurrent_story_id() {
        return Current_story_id;
    }

    public String getStoryId() {
        return StoryId;
    }

    public String getStoryName() {
        return StoryName;
    }
}
