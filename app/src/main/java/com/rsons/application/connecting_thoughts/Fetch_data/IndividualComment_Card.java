package com.rsons.application.connecting_thoughts.Fetch_data;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.UsersActivity.AuthorProfileActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;

/**
 * Created by ankit on 2/20/2018.
 */

public class IndividualComment_Card extends RecyclerView.ViewHolder {

    protected TextView UserName;
    protected ImageView UserImage;
    protected TextView CommentDate;
    ImageView firstStart,secondStar,thirdStar,fourthStar,fifthStar;
    private TextView UserComment;


    public IndividualComment_Card(final View view) {
        super(view);
        this.UserName = (TextView) view.findViewById(R.id.user_name);
        this.UserImage = (ImageView) view.findViewById(R.id.current_user_image);
        this.firstStart= (ImageView) view.findViewById(R.id.first_star);
        this.secondStar= (ImageView) view.findViewById(R.id.second_star);
        this.thirdStar= (ImageView) view.findViewById(R.id.third_star);
        this.fourthStar= (ImageView) view.findViewById(R.id.fourth_star);
        this.fifthStar= (ImageView) view.findViewById(R.id.fifth_star);

        this.CommentDate = (TextView) view.findViewById(R.id.comment_date);
        this.UserComment = (TextView) view.findViewById(R.id.user_comment);


    }

    public static void setupIndividualComment_Card(final IndividualComment_Card holder, final Activity activity,
                                                   final CommentModel singleItem) {


        holder.UserName.setText(singleItem.getUserName());

        long timeINlong;

        try{
            Log.e("jkhdksjfh","try part of timestamp published");
            Date date = (Date) singleItem.getCommentDate();
            timeINlong=date.getTime();
        }catch (Exception e){
            Log.e("jkhdksjfh","catch part of timestamp published");
            timeINlong=Long.parseLong(singleItem.getCommentDate()+"");
        }


        String DateString= android.text.format.DateFormat.format("MM/dd/yyyy",new Date(timeINlong)).toString();


        holder.CommentDate.setText(DateString);

        Float tempStar=Float.parseFloat(""+singleItem.getUserRatingGiven());
        SetStarVisibility(holder, tempStar);

        holder.UserComment.setText(singleItem.getUserComment());

        Picasso.with(activity).load(singleItem.getUserPicUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.card_defalt_back_cover).into(holder.UserImage,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("onerrorSection", "this is success");
                    }

                    @Override
                    public void onError() {
                        Log.e("onerrorSection", "this is error");
                        Picasso.with(activity).load(singleItem.getUserPicUrl()).placeholder(R.drawable.card_defalt_back_cover).into(holder.UserImage);
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LaunchReporterProfile=new Intent(activity, AuthorProfileActivity.class);
                LaunchReporterProfile.putExtra("user_key",singleItem.getUserId());
                LaunchReporterProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                LaunchReporterProfile.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                activity.startActivity(LaunchReporterProfile);
            }
        });


    }

    private static void SetStarVisibility(IndividualComment_Card holder, float alreadyRatedStar) {
        if (alreadyRatedStar==1){
            holder.secondStar.setVisibility(View.GONE);
            holder.thirdStar.setVisibility(View.GONE);
            holder.fifthStar.setVisibility(View.GONE);
            holder.fourthStar.setVisibility(View.GONE);
        }
        else if (alreadyRatedStar==2){
            holder.firstStart.setVisibility(View.VISIBLE);
            holder.secondStar.setVisibility(View.VISIBLE);
            holder.thirdStar.setVisibility(View.GONE);
            holder.fifthStar.setVisibility(View.GONE);
            holder.fourthStar.setVisibility(View.GONE);
        }
        else if (alreadyRatedStar==3){
            holder.firstStart.setVisibility(View.VISIBLE);
            holder.secondStar.setVisibility(View.VISIBLE);
            holder.thirdStar.setVisibility(View.VISIBLE);
            holder.fifthStar.setVisibility(View.GONE);
            holder.fourthStar.setVisibility(View.GONE);
        }
        else if (alreadyRatedStar==4){
            holder.firstStart.setVisibility(View.VISIBLE);
            holder.secondStar.setVisibility(View.VISIBLE);
            holder.thirdStar.setVisibility(View.VISIBLE);
            holder.fourthStar.setVisibility(View.VISIBLE);
            holder.fifthStar.setVisibility(View.GONE);
        }
        else if (alreadyRatedStar==5){
            holder.firstStart.setVisibility(View.VISIBLE);
            holder.secondStar.setVisibility(View.VISIBLE);
            holder.thirdStar.setVisibility(View.VISIBLE);
            holder.fourthStar.setVisibility(View.VISIBLE);
            holder.fifthStar.setVisibility(View.VISIBLE);
        }
    }
}