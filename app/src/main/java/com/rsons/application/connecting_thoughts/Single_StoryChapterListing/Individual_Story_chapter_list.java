package com.rsons.application.connecting_thoughts.Single_StoryChapterListing;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.ReadStoryActivity.ViewFirstPage_of_Story;

/**
 * Created by ankit on 3/3/2018.
 */

public class Individual_Story_chapter_list extends RecyclerView.ViewHolder  {

    protected TextView StoryName;
    private TextView ItemcountNumber;
    LinearLayout SingleItemView;


    public Individual_Story_chapter_list(final View view) {
        super(view);
        this.StoryName = (TextView) view.findViewById(R.id.story_name);
        this.ItemcountNumber= (TextView) view.findViewById(R.id.story_counter);
        this.SingleItemView= (LinearLayout) view.findViewById(R.id.single_item_view);

    }

    public static void setupIndividualChatFriendCard(final Individual_Story_chapter_list holder, final Activity activity,
                                                     final Story_Chapter_model singleItem,int position) {


        holder.StoryName.setText(singleItem.getStoryName());
        holder.ItemcountNumber.setText(""+(position+1));

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        holder.SingleItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (singleItem.getCurrent_story_id().equals(singleItem.getStoryId())){
                    activity.onBackPressed();
                }else {
                    Intent LaunchViewStoryFirstPage=new Intent(activity, ViewFirstPage_of_Story.class);
                    LaunchViewStoryFirstPage.putExtra("story_Id",singleItem.getStoryId());
                    LaunchViewStoryFirstPage.putExtra("from_story_listing",true);
                    LaunchViewStoryFirstPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(LaunchViewStoryFirstPage);
                   // Toast.makeText(activity,"not implemented",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}
