package com.rsons.application.connecting_thoughts.StoryClasses;

/**
 * Created by ankit on 2/11/2018.
 */

public class Simple_Story {

    public String story_id_server_name;
    public String story_title;
    public String story_cover_img;
    //public String LastUpDatedOn;
    private Object LastUpDatedOn;

    public Simple_Story(String story_server_name, String story_title, String story_cover_img,Object lastUpDatedOn) {
        this.story_id_server_name = story_server_name;
        this.story_title = story_title;
        this.story_cover_img = story_cover_img;
        this.LastUpDatedOn=lastUpDatedOn;
    }

    public Object getLastUpDatedOn(){
        return LastUpDatedOn;
    }

    public String getStory_id_server_name() {
        return story_id_server_name;
    }

    public String getStory_title() {
        return story_title;
    }

    public String getStory_cover_img() {
        return story_cover_img;
    }

    public Simple_Story() {
    }
}
