/*
 * Copyright (C) 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rsons.application.connecting_thoughts.NativeAds;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.rsons.application.connecting_thoughts.Fetch_data.SectionDataModel;
import com.rsons.application.connecting_thoughts.Fetch_data.SectionListDataAdapter;
import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.SingleSectionListingActivity;

import java.util.ArrayList;
import java.util.List;
/*
*/

/**
 * The {@link RecyclerViewAdapter} class.
 * <p>The adapter provides access to the items in the {@link MenuItemViewHolder}
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // A menu item view type.
    private static final int MENU_ITEM_VIEW_TYPE = 0;
    private static final int NATIVE_APP_INSTALL_AD_VIEW_TYPE = 1;

    // The native content ad view type.
    private static final int NATIVE_CONTENT_AD_VIEW_TYPE = 2;
    // An Activity's Context.
    private final Activity mContext;

    // The list of menu items.
    private final List<Object> mRecyclerViewItems;

    /**
     * For this example app, the recyclerViewItems list contains only
     * {@link// MenuItem} types.
     */
    public RecyclerViewAdapter(Activity context, List<Object> recyclerViewItems) {
        this.mContext = context;
        this.mRecyclerViewItems = recyclerViewItems;
    }

    /**
     * The {@link //MenuItemViewHolder} class.
     * Provides a reference to each view in the menu item view.
     */
    public class ItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView itemTitle;

        protected RecyclerView recycler_view_list;

        protected ImageView btnMore;



        public ItemRowHolder(View view) {
            super(view);

            this.itemTitle = (TextView) view.findViewById(R.id.itemTitle);
            this.recycler_view_list = (RecyclerView) view.findViewById(R.id.recycler_view_list);
            this.btnMore= (ImageView) view.findViewById(R.id.btnMore);


        }

    }

    @Override
    public int getItemCount() {
        return mRecyclerViewItems.size();
    }

    /**
     * Determines the view type for the given position.
     */
    @Override
    public int getItemViewType(int position) {


        Object recyclerViewItem = mRecyclerViewItems.get(position);
        if (recyclerViewItem instanceof AdView){
            return 2;
        }
        if (recyclerViewItem instanceof NativeAppInstallAd) {
            return NATIVE_APP_INSTALL_AD_VIEW_TYPE;
        } else if (recyclerViewItem instanceof NativeContentAd) {
            return NATIVE_CONTENT_AD_VIEW_TYPE;
        }

        return MENU_ITEM_VIEW_TYPE;
    }

    /**
     * Creates a new view for a menu item view. This method is invoked by the layout manager.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case 2:
                View BannerAdView1 = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.ad_view_banner_ad,
                        viewGroup, false);
                return new BannerAdViewHolder(BannerAdView1);
            case 4:
                View BannerAdView2 = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.ad_view_banner_ad,
                        viewGroup, false);
                return new BannerAdViewHolder(BannerAdView2);
            case MENU_ITEM_VIEW_TYPE:
                // Fall through.
            default:
                View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.single_story_rwo_recycler, viewGroup, false);
                return new ItemRowHolder(menuItemLayoutView);
        }
    }

    /**
     * Replaces the content in the views that make up the menu item view. This method is invoked
     * by the layout manager.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        int viewType = getItemViewType(i);
        switch (viewType) {

            case 2:
                BannerAdViewHolder.SetUpAds((BannerAdViewHolder)holder);
                //NativeAppInstallAd appInstallAd = (NativeAppInstallAd) mRecyclerViewItems.get(i);
                //populateAppInstallAdView(appInstallAd, (NativeAppInstallAdView) holder.itemView);
                break;
            case 4:
                BannerAdViewHolder.SetUpAds((BannerAdViewHolder)holder);
                break;
            case MENU_ITEM_VIEW_TYPE:
                // fall through
            default:
                SectionDataModel singlerow = (SectionDataModel) mRecyclerViewItems.get(i);
                final String sectionName = singlerow.getHeaderTitle();

                ArrayList singleSectionItems = singlerow.getAllItemsInSection();
                ItemRowHolder itemRowHolder;
                itemRowHolder=(ItemRowHolder) holder;

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
    }

    private void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd,
                                          NativeAppInstallAdView adView) {

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((ImageView) adView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon()
                .getDrawable());
        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());

        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        if (nativeAppInstallAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAppInstallAd.getPrice());
        }

        if (nativeAppInstallAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAppInstallAd.getStore());
        }

        if (nativeAppInstallAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAppInstallAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);
    }

    private void populateContentAdView(NativeContentAd nativeContentAd,
                                       NativeContentAdView adView) {
        // Some assets are guaranteed to be in every NativeContentAd.
        ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(nativeContentAd.getCallToAction());
        ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

        List<NativeAd.Image> images = nativeContentAd.getImages();

        if (images.size() > 0) {
            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }

        // Some aren't guaranteed, however, and should be checked.
        NativeAd.Image logoImage = nativeContentAd.getLogo();

        if (logoImage == null) {
            adView.getLogoView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
            adView.getLogoView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeContentAd);
    }
}
