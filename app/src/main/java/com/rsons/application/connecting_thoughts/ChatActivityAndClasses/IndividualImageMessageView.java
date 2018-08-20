package com.rsons.application.connecting_thoughts.ChatActivityAndClasses;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rsons.application.connecting_thoughts.Notification.IndividualRequestNotificationCardView;
import com.rsons.application.connecting_thoughts.Notification.NotificationModel_class;
import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.UsersActivity.AuthorProfileActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ankit on 4/26/2018.
 */

public class IndividualImageMessageView extends RecyclerView.ViewHolder{

    public TextView Friend_Pic_messageTime,My_Pic_message_time;
    public ImageView FirendImageMessage,MyImageMessage;

    public IndividualImageMessageView(final View view) {
        super(view);
        this.FirendImageMessage= (ImageView) itemView.findViewById(R.id.friend_picture_message);
        this.MyImageMessage= (ImageView) itemView.findViewById(R.id.my_picture_message);
        this.Friend_Pic_messageTime= (TextView) itemView.findViewById(R.id.friend_pic_message_time);
        this.My_Pic_message_time = (TextView) itemView.findViewById(R.id.my__pic_message_time);



    }

    public static void setupIndividualMessageView(final IndividualImageMessageView holder, final Activity context,
                                                  final Messages messages, final int i,String CurrentUserId) {


        String DateString;
        try{

            Date date = (Date) messages.getTime();
            Log.e("dkfddd","inside message adapter first try block  "+messages.getTime());
            DateString = android.text.format.DateFormat.format("dd MMM  hh:mm a",new Date(date.getTime())).toString();


        }catch (ClassCastException e){
            try {
                long timeINlong=Long.parseLong(messages.getTime()+"");
                DateString = android.text.format.DateFormat.format("dd MMM  hh:mm a",new Date(timeINlong)).toString();

            }catch (NumberFormatException exp){
                DateString=""+messages.getTime();
            }

        }

        if(CurrentUserId.equals(messages.getFrom())){
            holder.FirendImageMessage.setVisibility(View.GONE);
            holder.MyImageMessage.setVisibility(View.VISIBLE);

            final Picasso picasso = Picasso.with(context);
            picasso.setLoggingEnabled(false);
            picasso.setDebugging(false);

            picasso.load(messages.getSourceLocation()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.card_defalt_back_cover).into(holder.MyImageMessage,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.e("onerrorSection", "this is success");
                        }

                        @Override
                        public void onError() {
                            Log.e("onerrorSection", "this is error");
                            picasso.load(messages.getSourceLocation()).placeholder(R.drawable.card_defalt_back_cover).into(holder.MyImageMessage);
                        }
                    });

            holder.My_Pic_message_time.setVisibility(View.VISIBLE);
            holder.My_Pic_message_time.setText(DateString);
            holder.Friend_Pic_messageTime.setVisibility(View.GONE);
        }else {

            holder.FirendImageMessage.setVisibility(View.VISIBLE);
            holder.MyImageMessage.setVisibility(View.GONE);
            holder.My_Pic_message_time.setVisibility(View.GONE);
            holder.Friend_Pic_messageTime.setVisibility(View.VISIBLE);

            final Picasso picasso = Picasso.with(context);
            picasso.setLoggingEnabled(false);
            picasso.setDebugging(false);

            picasso.load(messages.getSourceLocation()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.card_defalt_back_cover).into(holder.FirendImageMessage,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.e("onerrorSection", "this is success");
                        }

                        @Override
                        public void onError() {
                            Log.e("onerrorSection", "this is error");
                            picasso.load(messages.getSourceLocation()).placeholder(R.drawable.card_defalt_back_cover).into(holder.FirendImageMessage);
                        }
                    });

            holder.My_Pic_message_time.setVisibility(View.GONE);
            holder.Friend_Pic_messageTime.setVisibility(View.VISIBLE);
            holder.Friend_Pic_messageTime.setText(DateString);
        }


    }


    }

