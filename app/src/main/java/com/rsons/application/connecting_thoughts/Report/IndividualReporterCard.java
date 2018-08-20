package com.rsons.application.connecting_thoughts.Report;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.UsersActivity.AuthorProfileActivity;

/**
 * Created by ankit on 3/20/2018.
 */

public class IndividualReporterCard extends RecyclerView.ViewHolder{

    public TextView ReporterName,ReporterProblem,ReporterEmail,ReporterPhoneNo,ReportType;


    public IndividualReporterCard(final View view) {
        super(view);
        this.ReporterEmail= (TextView) view.findViewById(R.id.reporter_emailid);
        this.ReporterName= (TextView) view.findViewById(R.id.reporter_name);
        this.ReporterPhoneNo= (TextView) view.findViewById(R.id.reporter_phone_no);
        this.ReportType= (TextView) view.findViewById(R.id.report_type);
        this.ReporterProblem= (TextView) view.findViewById(R.id.reporter_problem);

    }

    public static void setupIndividualReporterCard(final IndividualReporterCard holder, final Activity activity,
                                                final SingleReporterModel singleItem) {



        holder.ReportType.setText(singleItem.getReportType());
        holder.ReporterPhoneNo.setText(singleItem.getReporterPhoneNumber());
        holder.ReporterName.setText(singleItem.getReporterName());
        holder.ReporterProblem.setText(singleItem.getReportProblem());
        holder.ReporterEmail.setText(singleItem.getReporterMailId());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent LaunchReporterProfile=new Intent(activity, AuthorProfileActivity.class);
                    LaunchReporterProfile.putExtra("user_key",singleItem.getReporterId());
                    LaunchReporterProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    LaunchReporterProfile.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    activity.startActivity(LaunchReporterProfile);

            }
        });

    }
}
