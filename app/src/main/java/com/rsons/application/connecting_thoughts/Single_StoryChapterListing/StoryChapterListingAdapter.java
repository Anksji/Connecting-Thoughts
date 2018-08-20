package com.rsons.application.connecting_thoughts.Single_StoryChapterListing;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rsons.application.connecting_thoughts.R;

import java.util.ArrayList;

/**
 * Created by ankit on 3/3/2018.
 */

public class StoryChapterListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Story_Chapter_model> itemsList;
    private Activity mContext;

    public StoryChapterListingAdapter(Activity context, ArrayList<Story_Chapter_model> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v ;
        v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_chapter_listing_layout, viewGroup, false);


        return new Individual_Story_chapter_list(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int i) {

        final Individual_Story_chapter_list holder;
        holder=(Individual_Story_chapter_list) rholder;
        Individual_Story_chapter_list.setupIndividualChatFriendCard(holder, mContext, itemsList.get(i),i);


    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }


}