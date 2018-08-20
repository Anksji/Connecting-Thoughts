package com.rsons.application.connecting_thoughts.Notification;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.ReadStoryActivity.ViewFirstPage_of_Story;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;

/**
 * Created by ankit on 3/26/2018.
 */

public class IndividualNotificationCardView extends RecyclerView.ViewHolder{

    protected TextView story_title;
    protected ImageView coverImage;
    protected TextView StoryInfo;
    public TextView StoryPublishedDate;


    public IndividualNotificationCardView(final View view) {
        super(view);
        this.story_title = (TextView) view.findViewById(R.id.story_title);
        this.coverImage = (ImageView) view.findViewById(R.id.cover_image);
        this.StoryInfo= (TextView) view.findViewById(R.id.story_info);
        this.StoryPublishedDate= (TextView) view.findViewById(R.id.published_date);


    }

    public static void setupIndividualNotificationCard(final IndividualNotificationCardView holder, final Activity activity,
                                                final NotificationModel_class singleItem, int i) {


        holder.story_title.setText(singleItem.getStoryPublishedTitle());

        final Picasso picasso = Picasso.with(activity);
        picasso.setLoggingEnabled(false);
        picasso.setDebugging(false);

        picasso.load(singleItem.getStoryPublishedImage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.card_defalt_back_cover).into(holder.coverImage,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("onerrorSection", "this is success");
                    }

                    @Override
                    public void onError() {
                        Log.e("onerrorSection", "this is error");
                        picasso.load(singleItem.getStoryPublishedImage()).placeholder(R.drawable.card_defalt_back_cover).into(holder.coverImage);
                    }
                });
        holder.StoryInfo.setText(singleItem.getStoryPublishedIntro());

        String DateString;
        try{
            Log.e("dkfddd","inside first try block  "+singleItem.getStoryPublishedPublishedDate());
            Date date = (Date) singleItem.getStoryPublishedPublishedDate();
            Log.e("dkfddd","inside first try block "+date);
            DateString = android.text.format.DateFormat.format("dd MMM yyyy",new Date(date.getTime())).toString();

            Log.e("dkfddd","inside try block "+DateString);

        }catch (ClassCastException e){
            try {
                long timeINlong=Long.parseLong(singleItem.getStoryPublishedPublishedDate()+"");
                DateString = android.text.format.DateFormat.format("dd MMM yyyy",new Date(timeINlong)).toString();
                Log.e("dkfddd","inside second try block "+DateString);
            }catch (NumberFormatException exp){
                DateString=""+singleItem.getStoryPublishedPublishedDate();
                Log.e("dkfddd","inside second last catch block "+DateString);
            }

        }

        //long timeINlong=Long.parseLong(singleItem.getStoryPublishedPublishedDate()+"");
        //String DateString= android.text.format.DateFormat.format("dd MMM yyyy",new Date(timeINlong)).toString();

        holder.StoryPublishedDate.setText(" By "+singleItem.getStoryPublishedAuthor()+" on "+DateString);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LaunchViewStoryFirstPage=new Intent(activity, ViewFirstPage_of_Story.class);
                LaunchViewStoryFirstPage.putExtra("story_Id",singleItem.getStoryPublishedStoryId());
                LaunchViewStoryFirstPage.putExtra("from_story_listing",true);

                activity.startActivity(LaunchViewStoryFirstPage);
                //mContext.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
        //  }


    }
}
