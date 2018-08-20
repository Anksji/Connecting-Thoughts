package com.rsons.application.connecting_thoughts.ChatFragFriends;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rsons.application.connecting_thoughts.ChatActivityAndClasses.ChatActivity;
import com.rsons.application.connecting_thoughts.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by ankit on 2/28/2018.
 */

public class IndividualChat_Friend extends RecyclerView.ViewHolder  {

    protected TextView UserName;
    protected ImageView UserImage;
    TextView FriendLastMessage;
    LinearLayout Unread_messages_layout;
    TextView Unread_messages_number;

    public IndividualChat_Friend(final View view) {
        super(view);
        this.UserName = (TextView) view.findViewById(R.id.user_name);
        this.UserImage = (ImageView) view.findViewById(R.id.current_user_image);
        this.FriendLastMessage = (TextView) view.findViewById(R.id.friend_last_message);
        this.Unread_messages_layout= (LinearLayout) view.findViewById(R.id.unread_messages_layout);
        this.Unread_messages_number= (TextView) view.findViewById(R.id.unread_messages);

    }

    public static void setupIndividualChatFriendCard(final IndividualChat_Friend holder, final Activity activity,
                                                            final ChatFriendsModel singleItem) {

       /* if (singleItem.getUnreadMessages()!=null){
            int unreadMessages=Integer.parseInt(singleItem.getUnreadMessages());
            if (unreadMessages>0){
                holder.Unread_messages_layout.setVisibility(View.VISIBLE);
                holder.Unread_messages_number.setText(singleItem.getUnreadMessages());
            }
        }
*/

        holder.UserName.setText(singleItem.getFriendName());
        holder.FriendLastMessage.setText(singleItem.getFriendLastMessage());


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
                Intent launchchat=new Intent(activity, ChatActivity.class);
                launchchat.putExtra("user_key",singleItem.getFriendId());
                launchchat.putExtra("user_name",singleItem.FriendName);
                launchchat.putExtra("user_img",singleItem.FriendPic);
                activity.startActivity(launchchat);
            }
        });


    }

}
