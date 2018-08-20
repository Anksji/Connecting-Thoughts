package com.rsons.application.connecting_thoughts.NativeAds;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rsons.application.connecting_thoughts.R;

/**
 * Created by ankit on 3/16/2018.
 */

public class BannerAdViewHolder extends RecyclerView.ViewHolder {

    public AdView BannerAds;

    public BannerAdViewHolder(View itemView) {
        super(itemView);
        BannerAds= (AdView) itemView.findViewById(R.id.story_view_ad);
    }
    public static void SetUpAds(final BannerAdViewHolder holder){
        //AdView adView = new AdView(context);

        AdRequest adRequest = new AdRequest.Builder().build();
        holder.BannerAds.loadAd(adRequest);
    }
}
