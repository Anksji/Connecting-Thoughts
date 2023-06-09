package com.rsons.application.connecting_thoughts.NativeAds;

/**
 * Created by ankit on 3/9/2018.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.rsons.application.connecting_thoughts.R;

public class NativeAppInstallAdViewHolder extends RecyclerView.ViewHolder {
    NativeAppInstallAdViewHolder(View view) {
        super(view);
        NativeAppInstallAdView adView = (NativeAppInstallAdView) view;

        // Register the view used for each individual asset.
        // The MediaView will display a video asset if one is present in the ad, and the
        // first image asset otherwise.
        MediaView mediaView = (MediaView) adView.findViewById(R.id.appinstall_media);
        adView.setMediaView(mediaView);
        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
        adView.setPriceView(adView.findViewById(R.id.appinstall_price));
        adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));
        adView.setStoreView(adView.findViewById(R.id.appinstall_store));
    }
}