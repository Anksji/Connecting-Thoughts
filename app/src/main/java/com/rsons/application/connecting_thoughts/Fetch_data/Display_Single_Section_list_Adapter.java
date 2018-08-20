package com.rsons.application.connecting_thoughts.Fetch_data;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.StoryAdapter.DraftStoryAdapter;
import com.rsons.application.connecting_thoughts.StoryAdapter.DraftStoryHolderClass;

import java.util.ArrayList;

/**
 * Created by ankit on 2/26/2018.
 */

public class Display_Single_Section_list_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ITEM_VIEW_TYPE_BASIC = 0;
    private final int ITEM_VIEW_TYPE_FOOTER = 1;

    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;
    private ArrayList<SingleStoryModel> itemsList;
    private Activity mContext;
    private boolean IsLibrary;
    private boolean IsReport;
    private boolean value;

    public Display_Single_Section_list_Adapter(Activity context, ArrayList<SingleStoryModel> itemsList,boolean isLibrary,boolean isReport) {
        this.itemsList = itemsList;
        this.mContext = context;
        mFireStoreDatabase= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        IsLibrary=isLibrary;
        IsReport=isReport;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        RecyclerView.ViewHolder vh;
        if (i == ITEM_VIEW_TYPE_BASIC) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.individual_sectionlist_single_story_card, viewGroup, false);

            vh = new Individual_story_card(v);
        } else{
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.progress_item, viewGroup, false);

            vh = new DraftStoryAdapter.ProgressViewHolder(v);
        }

        return vh;
/*
        View v ;
        v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.individual_sectionlist_single_story_card, viewGroup, false);

        return new Individual_story_card(v);*/
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int i) {

        if (rholder instanceof Individual_story_card) {
            final Individual_story_card holder;
            holder=(Individual_story_card) rholder;
            Individual_story_card.setupIndividualStoryCard(holder, mContext, itemsList.get(i),IsReport);

            if (IsLibrary){
                holder.MoreCardOptions.setVisibility(View.VISIBLE);
                holder.MoreCardOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopupMenu(holder.MoreCardOptions,i);

                    }
                });
            }

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

    private void showPopupMenu(View view,int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_view_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;
        public MyMenuItemClickListener(int positon) {
            this.position=positon;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.delete_stry:
                    mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Library").document(itemsList.get(position).getStoryId())
                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                itemsList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, getItemCount());
                            }
                        }
                    });
                    return true;

                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }


}