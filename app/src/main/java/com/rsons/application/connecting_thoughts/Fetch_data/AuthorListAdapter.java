package com.rsons.application.connecting_thoughts.Fetch_data;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.StoryAdapter.DraftStoryAdapter;

import java.util.ArrayList;

/**
 * Created by ankit on 3/7/2018.
 */

public class AuthorListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ITEM_VIEW_TYPE_BASIC = 0;
    private final int ITEM_VIEW_TYPE_FOOTER = 1;

    private ArrayList<AuthorModel> itemsList;
    private Activity mContext;
    private boolean value;

    public AuthorListAdapter(Activity context, ArrayList<AuthorModel> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        RecyclerView.ViewHolder vh;
        if (i == ITEM_VIEW_TYPE_BASIC) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.individual_author_card_layout, viewGroup, false);

            vh = new Individual_Author_card(v);
        } else{
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.progress_bar_for_authors, viewGroup, false);

            vh = new DraftStoryAdapter.ProgressViewHolder(v);
        }

        return vh;
        /*View v ;
        v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.individual_author_card_layout, viewGroup, false);

        return new Individual_Author_card(v);*/
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int i) {



        if (rholder instanceof Individual_Author_card) {
            final Individual_Author_card holder;
            holder=(Individual_Author_card) rholder;
            Individual_Author_card.setupIndividualAuthorCard(holder, mContext, itemsList.get(i));

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