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
import com.rsons.application.connecting_thoughts.Report.ReporterListActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by ankit on 2/16/2018.
 */

public class Individual_story_card extends RecyclerView.ViewHolder{

    protected TextView story_title;
    protected ImageView coverImage;
    protected TextView StoryInfo;
    protected TextView ViewCount;
    protected TextView StarCount;
    public ImageView MoreCardOptions;
    public LinearLayout NoOfReportsLayout;
    public TextView NoOfReports;


    public Individual_story_card(final View view) {
        super(view);
        this.story_title = (TextView) view.findViewById(R.id.story_title);
        this.coverImage = (ImageView) view.findViewById(R.id.cover_image);
        this.StoryInfo= (TextView) view.findViewById(R.id.story_info);
        this.ViewCount= (TextView) view.findViewById(R.id.view_count);
        this.StarCount= (TextView) view.findViewById(R.id.star_count);
        this.MoreCardOptions= (ImageView) view.findViewById(R.id.card_more_option);
        this.NoOfReportsLayout= (LinearLayout) view.findViewById(R.id.no_of_reports_layout);
        this.NoOfReports= (TextView) view.findViewById(R.id.no_of_reports);

    }

    public static void setupIndividualStoryCard(final Individual_story_card holder, final Activity activity,
                                                final SingleStoryModel singleItem, final boolean IsReport) {

        if (IsReport){
            holder.NoOfReportsLayout.setVisibility(View.VISIBLE);
            holder.NoOfReports.setText(""+singleItem.getNumberOfReports());
        }


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
                    if (IsReport){
                        Intent LaunchReporterList=new Intent(activity, ReporterListActivity.class);
                        LaunchReporterList.putExtra("story_id",singleItem.getStoryId());
                        activity.startActivity(LaunchReporterList);
                    }else {
                        Log.e("fjskjsslfkslf","is sory is hide or not "+singleItem.getIsAuthorHide());
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
                    }

                    //mContext.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                }
            });

    }
}
