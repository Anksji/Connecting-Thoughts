package com.rsons.application.connecting_thoughts.Fetch_data;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.SingleSectionListingActivity;

import java.util.ArrayList;

/**
 * Created by ankit on 2/13/2018.
 */

public class RecyclerViewDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SectionDataModel> dataList;
    private Activity mContext;

    public RecyclerViewDataAdapter(Activity context, ArrayList<SectionDataModel> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v;


            v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_story_rwo_recycler, null);
           return new ItemRowHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rholder, int i) {

        final ItemRowHolder  itemRowHolder;
        itemRowHolder=(ItemRowHolder) rholder;


        if (dataList.get(i).getHeaderTitle().equals("Sample")){
            Log.e("dfjksfs","inside sample current row iteam "+i);
            itemRowHolder.RecyclerView.setVisibility(View.GONE);
            itemRowHolder.AdViewCard.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            itemRowHolder.FeedAdView.loadAd(adRequest);
        }else {

            final String sectionName = dataList.get(i).getHeaderTitle();
            ArrayList singleSectionItems = dataList.get(i).getAllItemsInSection();
            Log.e("sdjfksfs","list size "+singleSectionItems);

                itemRowHolder.itemTitle.setText(sectionName);

                SectionListDataAdapter itemListDataAdapter = new SectionListDataAdapter(mContext, singleSectionItems);

                itemRowHolder.recycler_view_list.setHasFixedSize(true);
                itemRowHolder.recycler_view_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                itemRowHolder.recycler_view_list.setAdapter(itemListDataAdapter);


                itemRowHolder.btnMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent LaunchSectionListing=new Intent(mContext, SingleSectionListingActivity.class);
                        LaunchSectionListing.putExtra("section_name",sectionName);
                        mContext.startActivity(LaunchSectionListing);


                    }
                });


        }


       /* Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView itemTitle;
        protected RecyclerView recycler_view_list;
        protected CardView RecyclerView,AdViewCard;

        protected AdView FeedAdView;
        protected ImageView btnMore;



        public ItemRowHolder(View view) {
            super(view);
            this.RecyclerView= (CardView) view.findViewById(R.id.recycler_card);
            this.AdViewCard= (CardView) view.findViewById(R.id.adView_in_card);
            this.itemTitle = (TextView) view.findViewById(R.id.itemTitle);
            this.FeedAdView= (AdView) view.findViewById(R.id.feed_ads);
            this.recycler_view_list = (RecyclerView) view.findViewById(R.id.recycler_view_list);
            this.btnMore= (ImageView) view.findViewById(R.id.btnMore);
        }

    }



}