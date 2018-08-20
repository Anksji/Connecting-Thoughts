package com.rsons.application.connecting_thoughts.Fetch_data;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rsons.application.connecting_thoughts.R;

import java.util.ArrayList;

/**
 * Created by ankit on 2/13/2018.
 */

public class SectionListDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SingleStoryModel> itemsList;
    private Activity mContext;

    public SectionListDataAdapter(Activity context, ArrayList<SingleStoryModel> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v ;
        v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_single_story_card, viewGroup, false);

        return new Individual_feed_story_card_data(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int i) {

        final Individual_feed_story_card_data holder;
        holder=(Individual_feed_story_card_data) rholder;
        Individual_feed_story_card_data.setupIndividualStoryCard(holder, mContext, itemsList.get(i),i);

        /*holder.story_title.setText(singleItem.getStoryName());

        Picasso.with(mContext).load(singleItem.getStoryImage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.card_defalt_back_cover).into(holder.coverImage,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("onerrorSection", "this is success");
                    }

                    @Override
                    public void onError() {
                        Log.e("onerrorSection", "this is error");
                        Picasso.with(mContext).load(singleItem.getStoryImage()).placeholder(R.drawable.card_defalt_back_cover).into(holder.coverImage);
                    }
                });
        holder.StoryInfo.setText(singleItem.getStoryIntro());
        holder.ViewCount.setText(singleItem.getStoryviewcount());
        holder.StarCount.setText(singleItem.getStoryRating());

*/

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemClickListener.OnItemClickListener(holder.getAdapterPosition(), singleItem, holder.coverImage);
                //(holder.getAdapterPosition(), singleItem, holder.story_title);
            }
        });*/


    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    /*public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView story_title;
        protected ImageView coverImage;
        protected TextView StoryInfo;
        private TextView ViewCount;
        private TextView StarCount;


        public SingleItemRowHolder(View view) {
            super(view);

            this.story_title = (TextView) view.findViewById(R.id.story_title);
            this.coverImage = (ImageView) view.findViewById(R.id.cover_image);
            this.StoryInfo= (TextView) view.findViewById(R.id.story_info);
            this.ViewCount= (TextView) view.findViewById(R.id.view_count);
            this.StarCount= (TextView) view.findViewById(R.id.star_count);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent LaunchViewStoryFirstPage=new Intent(mContext, ViewFirstPage_of_Story.class);
                    LaunchViewStoryFirstPage.putExtra("storyTitle",story_title.getText().toString());
                    LaunchViewStoryFirstPage.putExtra("story_cover","e");
                    LaunchViewStoryFirstPage.putExtra("view_count","e");
                    LaunchViewStoryFirstPage.putExtra("story_info","e");
                    LaunchViewStoryFirstPage.putExtra("story_rating","e");
                    mContext.startActivity(LaunchViewStoryFirstPage);
                    //mContext.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);


                }
            });

        }

    }*/

}