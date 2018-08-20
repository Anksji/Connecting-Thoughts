package com.rsons.application.connecting_thoughts.Notification;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.UsersActivity.AuthorProfileActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ankit on 3/26/2018.
 */

public class IndividualRequestNotificationCardView extends RecyclerView.ViewHolder{

    public CircleImageView UserImage;
    public TextView UserName;
    public TextView NotificationDate;


    public IndividualRequestNotificationCardView(final View view) {
        super(view);
       this.UserImage= (CircleImageView) view.findViewById(R.id.user_image);
        this.UserName= (TextView) view.findViewById(R.id.user_name);
        this.NotificationDate= (TextView) view.findViewById(R.id.notification_time);


    }

    public static void setupIndividualRequestNotificationCard(final IndividualRequestNotificationCardView holder, final Activity activity,
                                                       final NotificationModel_class singleItem, int i) {


        holder.UserName.setText(singleItem.getRequestUserName());
        String DateString;
        try{

            Date date = (Date) singleItem.getRequestNotificationDate();

            DateString = android.text.format.DateFormat.format("dd MMM yyyy",new Date(date.getTime())).toString();

            Log.e("dkfddd","inside try block "+DateString);

        }catch (ClassCastException e){
            try {
                long timeINlong=Long.parseLong(singleItem.getRequestNotificationDate()+"");
                DateString = android.text.format.DateFormat.format("dd MMM yyyy",new Date(timeINlong)).toString();
                Log.e("dkfddd","inside second try block "+DateString);
            }catch (NumberFormatException exp){
                DateString=""+singleItem.getRequestNotificationDate();
                Log.e("dkfddd","inside second last catch block "+DateString);
            }

        }

        /*long timeINlong=Long.parseLong(singleItem.getRequestNotificationDate()+"");
        String DateString= android.text.format.DateFormat.format("dd MMM yyyy",new Date(timeINlong)).toString();*/

        holder.NotificationDate.setText(DateString);
        final Picasso picasso = Picasso.with(activity);
        picasso.setLoggingEnabled(false);
        picasso.setDebugging(false);

        picasso.load(singleItem.getRequestUserImage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.card_defalt_back_cover).into(holder.UserImage,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("onerrorSection", "this is success");
                    }

                    @Override
                    public void onError() {
                        Log.e("onerrorSection", "this is error");
                        picasso.load(singleItem.getRequestUserImage()).placeholder(R.drawable.card_defalt_back_cover).into(holder.UserImage);
                    }
                });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LaunchViewUserProfile=new Intent(activity, AuthorProfileActivity.class);
                LaunchViewUserProfile.putExtra("user_key",singleItem.getRequestUserId());
                activity.startActivity(LaunchViewUserProfile);
                //mContext.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
        //  }


    }
}
