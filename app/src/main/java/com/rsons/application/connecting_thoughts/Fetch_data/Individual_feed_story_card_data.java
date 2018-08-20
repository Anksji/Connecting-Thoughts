package com.rsons.application.connecting_thoughts.Fetch_data;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rsons.application.connecting_thoughts.FormatLognValue;
import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.ReadStoryActivity.ViewFirstPage_of_Story;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by ankit on 3/14/2018.
 */

public class Individual_feed_story_card_data extends RecyclerView.ViewHolder{

    protected TextView story_title;
    protected ImageView coverImage;
    protected TextView StoryInfo;
    protected TextView ViewCount;
    protected TextView StarCount;
    public ImageView MoreCardOptions;
    public LinearLayout StoryCardView;
    //public AdView BannerFeedAdView;


    public Individual_feed_story_card_data(final View view) {
        super(view);
        this.story_title = (TextView) view.findViewById(R.id.story_title);
        this.coverImage = (ImageView) view.findViewById(R.id.cover_image);
        this.StoryInfo= (TextView) view.findViewById(R.id.story_info);
        this.ViewCount= (TextView) view.findViewById(R.id.view_count);
        this.StarCount= (TextView) view.findViewById(R.id.star_count);
        this.MoreCardOptions= (ImageView) view.findViewById(R.id.card_more_option);
        this.StoryCardView= (LinearLayout) view.findViewById(R.id.story_card_view);
        //this.BannerFeedAdView= (AdView) view.findViewById(R.id.feed_story_ad);


    }

    public static void setupIndividualStoryCard(final Individual_feed_story_card_data holder, final Activity activity,
                                                final SingleStoryModel singleItem,int i) {

       /* if (i==2||i==4||i==6){
            holder.StoryCardView.setVisibility(View.GONE);
            holder.BannerFeedAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            holder.BannerFeedAdView.loadAd(adRequest);

        }else {*/
            holder.story_title.setText(singleItem.getStoryName());

            final Picasso picasso = Picasso.with(activity);
            picasso.setLoggingEnabled(false);
            picasso.setDebugging(false);

            picasso.load(singleItem.getStoryImage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.card_defalt_back_cover).into(holder.coverImage,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.e("onerrorSection", "this is success");
                        }

                        @Override
                        public void onError() {
                            Log.e("onerrorSection", "this is error");
                            picasso.load(singleItem.getStoryImage()).placeholder(R.drawable.card_defalt_back_cover).into(holder.coverImage);
                        }
                    });
            holder.StoryInfo.setText(singleItem.getStoryIntro());
            String formattedViewCount = FormatLognValue.format(Long.parseLong(singleItem.getStoryviewcount()));
            holder.ViewCount.setText(formattedViewCount);

            holder.StarCount.setText(singleItem.getStoryRating());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent LaunchViewStoryFirstPage=new Intent(activity, ViewFirstPage_of_Story.class);
                    LaunchViewStoryFirstPage.putExtra("storyTitle",singleItem.getStoryName());
                    LaunchViewStoryFirstPage.putExtra("story_cover",singleItem.getStoryImage());
                    LaunchViewStoryFirstPage.putExtra("view_count",singleItem.getStoryviewcount());
                    LaunchViewStoryFirstPage.putExtra("story_info",singleItem.getStoryIntro());
                    LaunchViewStoryFirstPage.putExtra("story_rating",singleItem.getStoryRating());
                    LaunchViewStoryFirstPage.putExtra("story_Id",singleItem.getStoryId());
                    LaunchViewStoryFirstPage.putExtra("Is_hide",singleItem.getIsAuthorHide());
                    LaunchViewStoryFirstPage.putExtra("from_story_listing",false);
                    LaunchViewStoryFirstPage.putExtra("story_AuthorId",singleItem.getAuthorId());
                    LaunchViewStoryFirstPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(LaunchViewStoryFirstPage);
                    //mContext.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                }
            });
      //  }


    }
}
