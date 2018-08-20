package com.rsons.application.connecting_thoughts.Notification;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.StoryAdapter.DraftStoryAdapter;

import java.util.ArrayList;

/**
 * Created by ankit on 3/26/2018.
 */

public class NotificationAdapterClass extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<NotificationModel_class> NotificationList;
    private Activity mContext;
    private boolean value;

    public NotificationAdapterClass(Activity context, ArrayList<NotificationModel_class> itemsList) {
        this.NotificationList = itemsList;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (NotificationList.get(position)==null){
            return 1;
        }else if (NotificationList.get(position).getNotificationType().equals("request")){
            return 2;
        }else {
            return 3;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case 1:
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.progress_item, viewGroup, false);

                return new DraftStoryAdapter.ProgressViewHolder(v);
            case 2:
                View RequestNotification = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.individual_request_notification_card,
                        viewGroup, false);
                return new IndividualRequestNotificationCardView(RequestNotification);

            default:
                View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.individual_story_published_notification, viewGroup, false);
                return new IndividualNotificationCardView(menuItemLayoutView);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int i) {

        int viewType = getItemViewType(i);


        switch (viewType) {

            case 1:
                if (!value) {
                    ((DraftStoryAdapter.ProgressViewHolder) rholder).progressBar.setVisibility(View.VISIBLE);
                    ((DraftStoryAdapter.ProgressViewHolder) rholder).progressBar.setIndeterminate(true);
                } else ((DraftStoryAdapter.ProgressViewHolder) rholder).progressBar.setVisibility(View.GONE);

                break;

            case 2:
                final IndividualRequestNotificationCardView holder;
                holder=(IndividualRequestNotificationCardView) rholder;
                IndividualRequestNotificationCardView.setupIndividualRequestNotificationCard(holder, mContext, NotificationList.get(i),i);

                break;

            case 3:
                // fall through
            default:
                final IndividualNotificationCardView Storyholder;
                Storyholder=(IndividualNotificationCardView) rholder;
                IndividualNotificationCardView.setupIndividualNotificationCard(Storyholder, mContext, NotificationList.get(i),i);


        }


    }

    @Override
    public int getItemCount() {
        return (null != NotificationList ? NotificationList.size() : 0);
    }


}