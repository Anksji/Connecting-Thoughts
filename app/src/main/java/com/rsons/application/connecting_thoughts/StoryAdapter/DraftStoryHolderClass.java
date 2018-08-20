package com.rsons.application.connecting_thoughts.StoryAdapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.StoryClasses.Simple_Story;
import com.rsons.application.connecting_thoughts.WritingActivity.WriteStoryActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;

/**
 * Created by ankit on 2/11/2018.
 */

public class DraftStoryHolderClass extends RecyclerView.ViewHolder{

    ImageView DeleteButton;
    TextView StoryTitle;
    ImageView StoryCover;
    boolean IsPublishedStory;
    public TextView UpdatedDate;


    public DraftStoryHolderClass(final View itemView) {
        super(itemView);
        StoryTitle= (TextView) itemView.findViewById(R.id.title_of_story);
        DeleteButton = (ImageView) itemView.findViewById(R.id.delete_story);
        StoryCover= (ImageView) itemView.findViewById(R.id.cover_image);
        UpdatedDate= (TextView) itemView.findViewById(R.id.updated_date);


    }

    public static void setupStoryCard(final DraftStoryHolderClass holder, final Activity activity,
                                      final Simple_Story data, final boolean isPublishedStory) {

        /*String Language=Locale.getDefault().getDisplayLanguage();
        Log.e("localLang","lan is "+Language);
        if (Language.equals("English")){
            Typeface storyFont = Typeface.createFromAsset(activity.getAssets(),  "fonts/Playfair_Regular.ttf");
            holder.StoryTitle.setTypeface(storyFont);
        }
        */
        holder.StoryTitle.setText(data.getStory_title());
        long timeINlong;
                //Long.parseLong(data.getLastUpDatedOn());

        try{
            Log.e("jkhdksjfh","try part of timestamp published");
            Date date = (Date) data.getLastUpDatedOn();
            timeINlong=date.getTime();
        }catch (Exception e){
            Log.e("jkhdksjfh","catch part of timestamp published");
            timeINlong=Long.parseLong(data.getLastUpDatedOn()+"");
        }


        String DateString= android.text.format.DateFormat.format("MM/dd/yyyy hh:mm a",new Date(timeINlong)).toString();
        if (isPublishedStory){
            holder.UpdatedDate.setText(activity.getString(R.string.PUBLISHED_ON)+" "+DateString);
        }else {
            holder.UpdatedDate.setText(activity.getString(R.string.UPDATED_ON)+" "+DateString);
        }


        Picasso.with(activity).load(data.getStory_cover_img()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_rectangle).into(holder.StoryCover,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("onerrorSection", "this is success");
                    }

                    @Override
                    public void onError() {
                        Log.e("onerrorSection", "this is error");
                        Picasso.with(activity).load(data.getStory_cover_img()).placeholder(R.drawable.ic_rectangle).into(holder.StoryCover);
                    }
                });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPublishedStory){
                    AlertPopUpDialog(activity,data.getStory_id_server_name());
                   // Toast.makeText(activity,activity.getString(R.string.NOW_YOUR_STORY_MOVE_TO_DRAFT_SECTION),Toast.LENGTH_LONG).show();

                }else {
                    Intent LaunchWriteActivity=new Intent(activity, WriteStoryActivity.class);
                    LaunchWriteActivity.putExtra("storyId",data.getStory_id_server_name());
                    activity.startActivity(LaunchWriteActivity);
                    //activity.overridePendingTransition(R.anim.from_right, R.anim.to_right);
                }

            }
        });

      /*  holder.author_name.setTextSize(12);
        holder.series_name.setTextSize(12);
        holder.read_time.setTextSize(12);
        holder.chapter_name.setText(data.getChapterName());

        if (data.getSeriesType() == Const.MODE_SEARCH_SERIES) {
            holder.series_name.setText(data.getSeriesName());
        } else {
            holder.series_name.setText(Const.GenresMap.get(data.getGenre()));
        }
*/
    }

    public static void AlertPopUpDialog(final Activity activity, final String StoryId){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View mView = activity.getLayoutInflater().inflate(R.layout.want_to_edit_story, null);


        RelativeLayout edit_published_story= (RelativeLayout) mView.findViewById(R.id.edit_published_story);
        RelativeLayout CancelBtn= (RelativeLayout) mView.findViewById(R.id.cancel_btn);


        builder.setView(mView);
        final AlertDialog Warning_dialog = builder.create();

        edit_published_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent LaunchWriteActivity=new Intent(activity, WriteStoryActivity.class);
                LaunchWriteActivity.putExtra("storyId",StoryId);
                activity.startActivity(LaunchWriteActivity);

                    Warning_dialog.dismiss();

            }
        });

        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Warning_dialog.dismiss();
            }
        });

        Warning_dialog.show();
    }


}

