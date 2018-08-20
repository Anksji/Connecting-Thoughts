package com.rsons.application.connecting_thoughts.FriendOr_Follower;

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

/**
 * Created by ankit on 2/23/2018.
 */

public class IndividualFriendOrFollower extends RecyclerView.ViewHolder  {

    protected TextView UserName;
    protected ImageView UserImage;
    TextView UserStatus;

    public IndividualFriendOrFollower(final View view) {
        super(view);
        this.UserName = (TextView) view.findViewById(R.id.user_name);
        this.UserImage = (ImageView) view.findViewById(R.id.current_user_image);
        this.UserStatus= (TextView) view.findViewById(R.id.user_status);


    }

    public static void setupIndividualFriendOrFollower_Card(final IndividualFriendOrFollower holder, final Activity activity,
                                                   final FriendOrFollowerModel singleItem) {


        holder.UserName.setText(singleItem.getFriendName());
        holder.UserStatus.setText(singleItem.getFriendStatus());


        Picasso.with(activity).load(singleItem.getFriendPic()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.card_defalt_back_cover).into(holder.UserImage,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("onerrorSection", "this is success");
                    }

                    @Override
                    public void onError() {
                        Log.e("onerrorSection", "this is error");
                        Picasso.with(activity).load(singleItem.getFriendPic()).placeholder(R.drawable.card_defalt_back_cover).into(holder.UserImage);
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent launchchat=new Intent(activity, ChatActivity.class);
                launchchat.putExtra("user_key",singleItem.getFriendId());
                launchchat.putExtra("user_name",singleItem.FriendName);
                launchchat.putExtra("user_img",singleItem.FriendPic);
                activity.startActivity(launchchat);*/
                Intent profilePage=new Intent(activity, AuthorProfileActivity.class);
                profilePage.putExtra("user_key",singleItem.getFriendId());
                activity.startActivity(profilePage);
            }
        });


    }

}
