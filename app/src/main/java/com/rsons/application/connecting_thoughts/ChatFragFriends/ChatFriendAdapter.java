package com.rsons.application.connecting_thoughts.ChatFragFriends;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.rsons.application.connecting_thoughts.ChatActivityAndClasses.ChatActivity;
import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.StoryAdapter.DraftStoryAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ankit on 2/28/2018.
 */

public class ChatFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ITEM_VIEW_TYPE_BASIC = 0;
    private final int ITEM_VIEW_TYPE_FOOTER = 1;

    private ArrayList<ChatFriendsModel> itemsList;
    private Activity activity;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStoreDatabase;
    private boolean isShareMessage;
    private String shareType,Message;
    private boolean value;

    public ChatFriendAdapter(Activity context, ArrayList<ChatFriendsModel> itemsList,boolean is_share_message,
                             String share_type,String message) {
        this.itemsList = itemsList;
        this.activity = context;
        mAuth = FirebaseAuth.getInstance();
        mFireStoreDatabase=FirebaseFirestore.getInstance();
        isShareMessage=is_share_message;
        shareType=share_type;
        Message=message;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        RecyclerView.ViewHolder vh;
        if (i == ITEM_VIEW_TYPE_BASIC) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.single_chat_friend, viewGroup, false);

            vh = new IndividualChat_Friend(v);
        } else{
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.progress_item, viewGroup, false);

            vh = new DraftStoryAdapter.ProgressViewHolder(v);
        }


        return vh;

        /*View v ;
        v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_chat_friend, viewGroup, false);


        return new IndividualChat_Friend(v);*/
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int i) {


        if (rholder instanceof IndividualChat_Friend) {


            final IndividualChat_Friend holder;
            holder=(IndividualChat_Friend) rholder;
            //IndividualChat_Friend.setupIndividualChatFriendCard(holder, mContext, itemsList.get(i));
            final ChatFriendsModel singleItem=itemsList.get(i);
            mFireStoreDatabase.collection("FriendsData").document(mAuth.getUid()).collection("FriendList").document(singleItem.FriendId).
                    addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                            if (documentSnapshot!=null&&documentSnapshot.exists()){
                                if (documentSnapshot.contains("unread_message")){
                                    Log.e("dsfjkfsdf","callling inside data " +documentSnapshot.get("unread_message").toString());
                                    String Unread_messages=documentSnapshot.get("unread_message").toString();
                                    Log.e("fjksjfksjf","this is inside adapter unread message "+documentSnapshot.get("unread_message").toString());
                                    Log.e("fjksjfksjf","this is inside adapter friend name "+documentSnapshot.get("friend_name").toString());
                                    int unreadMessages=Integer.parseInt(Unread_messages);
                                    if (holder.UserName.getText().toString().equals(documentSnapshot.get("friend_name").toString())){
                                        holder.Unread_messages_number.setText(Unread_messages);
                                        if (unreadMessages>0){
                                            holder.Unread_messages_layout.setVisibility(View.VISIBLE);
                                            // holder.Unread_messages_number.setVisibility(View.VISIBLE);
                                        }else {
                                            holder.Unread_messages_layout.setVisibility(View.GONE);
                                            // holder.Unread_messages_number.setVisibility(View.GONE);
                                        }
                                    }

                                }
                                Log.e("fjksjfdfsksjf","my friend name is "+documentSnapshot.get("friend_name").toString());
                                if (holder.UserName.getText().toString().equals(documentSnapshot.get("friend_name").toString())){
                                    if (documentSnapshot.contains("last_message")){
                                        holder.FriendLastMessage.setText(documentSnapshot.getString("last_message"));
                                    }
                                }

                            }

                        }
                    });


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
                    if (isShareMessage){
                        launchchat.putExtra("calling_from","ALL_FRIEND_LIST");
                        launchchat.putExtra("type",shareType);
                        launchchat.putExtra("message",Message);
                        activity.finish();
                    }else {
                        launchchat.putExtra("calling_from","simple");
                    }
                    launchchat.putExtra("user_key",singleItem.getFriendId());
                    launchchat.putExtra("user_name",singleItem.FriendName);
                    launchchat.putExtra("user_img",singleItem.FriendPic);

                    activity.startActivity(launchchat);
                }
            });
        /*if (singleItem.getUnreadMessages()!=null){
            int unreadMessages=Integer.parseInt(singleItem.getUnreadMessages());
            if (unreadMessages>0){
                holder.Unread_messages_layout.setVisibility(View.VISIBLE);
                holder.Unread_messages_number.setText(singleItem.getUnreadMessages());
            }
        }*/




        } else {
            if (!value) {
                ((DraftStoryAdapter.ProgressViewHolder) rholder).progressBar.setVisibility(View.VISIBLE);
                ((DraftStoryAdapter.ProgressViewHolder) rholder).progressBar.setIndeterminate(true);
            } else ((DraftStoryAdapter.ProgressViewHolder) rholder).progressBar.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemViewType(int position) {
        return itemsList.get(position) != null ? ITEM_VIEW_TYPE_BASIC : ITEM_VIEW_TYPE_FOOTER;
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }


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

        public  void setupIndividualChatFriendCard(final com.rsons.application.connecting_thoughts.ChatFragFriends.IndividualChat_Friend holder, final Activity activity,
                                                         final ChatFriendsModel singleItem) {

       /*
*/


        }

    }


}