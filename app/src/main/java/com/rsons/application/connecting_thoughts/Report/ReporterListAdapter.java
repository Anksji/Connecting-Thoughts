package com.rsons.application.connecting_thoughts.Report;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rsons.application.connecting_thoughts.R;

import java.util.ArrayList;

/**
 * Created by ankit on 3/20/2018.
 */

public class ReporterListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<SingleReporterModel> itemsList;
    private Activity mContext;


    public ReporterListAdapter(Activity context, ArrayList<SingleReporterModel> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v ;
        v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.individual_reporter_card_view, viewGroup, false);

        return new IndividualReporterCard(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int i) {

        final IndividualReporterCard holder;
        holder=(IndividualReporterCard) rholder;
        IndividualReporterCard.setupIndividualReporterCard(holder, mContext, itemsList.get(i));


    }




    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }


}