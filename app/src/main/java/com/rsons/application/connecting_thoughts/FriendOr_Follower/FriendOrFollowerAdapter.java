package com.rsons.application.connecting_thoughts.FriendOr_Follower;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.StoryAdapter.DraftStoryAdapter;

import java.util.ArrayList;

/**
 * Created by ankit on 2/23/2018.
 */

public class FriendOrFollowerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ITEM_VIEW_TYPE_BASIC = 0;
    private final int ITEM_VIEW_TYPE_FOOTER = 1;

    private ArrayList<FriendOrFollowerModel> itemsList;
    private Activity mContext;
    private boolean value;

    public FriendOrFollowerAdapter(Activity context, ArrayList<FriendOrFollowerModel> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        RecyclerView.ViewHolder vh;
        if (i == ITEM_VIEW_TYPE_BASIC) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.single_friend_or_follower, viewGroup, false);

            vh = new IndividualFriendOrFollower(v);
        } else{
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.progress_item, viewGroup, false);

            vh = new DraftStoryAdapter.ProgressViewHolder(v);
        }


        return vh;
        /*View v ;
        v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_friend_or_follower, viewGroup, false);


        return new IndividualFriendOrFollower(v);*/
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int i) {

        if (rholder instanceof IndividualFriendOrFollower) {

            final IndividualFriendOrFollower holder;
            holder=(IndividualFriendOrFollower) rholder;
            IndividualFriendOrFollower.setupIndividualFriendOrFollower_Card(holder, mContext, itemsList.get(i));


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


}