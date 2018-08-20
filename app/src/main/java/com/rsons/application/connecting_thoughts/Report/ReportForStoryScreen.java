package com.rsons.application.connecting_thoughts.Report;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.rsons.application.connecting_thoughts.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ankit on 3/18/2018.
 */

public class ReportForStoryScreen extends AppCompatActivity {


    TextView CopyrightIssue,ImproperContent,GarbageContent;
    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;
    private Toolbar mReportToolbar;
    private String ReporterName="",ReporterEmailId="",ReporterNumber="",StoryId,ReportType;
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_for_story);

        mReportToolbar = (Toolbar) findViewById(R.id.report_toolbar);
        setSupportActionBar(mReportToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.REPORT_TO_STORY));

        CopyrightIssue= (TextView) findViewById(R.id.copyright_issue);
        ImproperContent= (TextView) findViewById(R.id.inappropriate_content);
        GarbageContent= (TextView) findViewById(R.id.garbage_content);

        mFireStoreDatabase=FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mProgressDialog=new ProgressDialog(ReportForStoryScreen.this);

        mProgressDialog.setCanceledOnTouchOutside(false);
        StoryId=getIntent().getStringExtra("story_id");


        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (documentSnapshot.contains("email")){
                        ReporterEmailId=documentSnapshot.getString("email");
                    }
                    if (documentSnapshot.contains("name")){
                        ReporterName=documentSnapshot.getString("name");
                    }
                    if (documentSnapshot.contains("phone_no")){
                        ReporterNumber=documentSnapshot.getString("phone_no");
                    }
                }
            }
        });


        ImproperContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StoryId!=null||StoryId.length()>5){
                    AlertPopUpDialog(ReportForStoryScreen.this);
                    ReportType="improper_content";
                }

            }
        });

        CopyrightIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StoryId!=null||StoryId.length()>5){
                    AlertPopUpDialog(ReportForStoryScreen.this);
                    ReportType="copyright_issue";
                }

            }
        });

        GarbageContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StoryId!=null||StoryId.length()>5){
                    AlertPopUpDialog(ReportForStoryScreen.this);
                    ReportType="garbage_content";
                }

            }
        });
    }

    public  void AlertPopUpDialog(final Activity activity){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View mView = activity.getLayoutInflater().inflate(R.layout.report_form_dialog, null);


        ImageView report_Info= (ImageView) mView.findViewById(R.id.report_info);
        RelativeLayout submit_story= (RelativeLayout) mView.findViewById(R.id.submit_report);
        RelativeLayout CancelBtn= (RelativeLayout) mView.findViewById(R.id.cancel_btn);
        TextView DialogTitle= (TextView) mView.findViewById(R.id.dialog_title);
        final EditText Reporter_name= (EditText) mView.findViewById(R.id.reporter_name);
        final EditText Reporter_mobile_number= (EditText) mView.findViewById(R.id.reporter_number);
        final EditText ReporterMailId= (EditText) mView.findViewById(R.id.email_input_text);
        final EditText ReporterProblem= (EditText) mView.findViewById(R.id.your_problem_field);

        //DialogTitle.setText(dialogTitle);
        if (ReporterNumber.length()>0){
            Reporter_mobile_number.setText(ReporterNumber);
        }
        if (ReporterEmailId.length()>0){
            ReporterMailId.setText(ReporterEmailId);
        }
        if (ReporterName.length()>0){
            Reporter_name.setText(ReporterName);
        }

        builder.setView(mView);
        final AlertDialog Warning_dialog = builder.create();

        report_Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnotherInfoDialog(activity);
            }
        });

        submit_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Reporter_name.getText().toString().length()>0&&Reporter_mobile_number.getText().toString().length()>0&&
                        ReporterMailId.getText().toString().length()>0&&ReporterProblem.getText().toString().length()>0){
                    mProgressDialog.setTitle(getString(R.string.SUBMITING_REPORT));
                    mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT_WHILE_WE_SUBMIT_REPORT));
                    mProgressDialog.show();
                    Warning_dialog.dismiss();
                    SubmitReport(Reporter_name.getText().toString(),Reporter_mobile_number.getText().toString(),
                            ReporterMailId.getText().toString(),ReporterProblem.getText().toString());

                }else {
                    if (Reporter_name.getText().toString().length()==0){
                        Reporter_name.setError(getString(R.string.PLEASE_PROVIDE_YOUR_NAME));

                    }else if (Reporter_mobile_number.getText().toString().length()==0){
                        Reporter_mobile_number.setError(getString(R.string.PROVIDE_YOUR_NUMBER));
                    }else  if (ReporterMailId.getText().toString().length()==0){
                        ReporterMailId.setError(getString(R.string.PLEASE_PROVIDE_EMAIL));
                    }else if (ReporterProblem.getText().toString().length()==0){
                        ReporterProblem.setError(getString(R.string.PLEASE_WRITE_ABOUT_YOUR_PROBLEM));
                    }
                }


            }
        });

        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Warning_dialog.dismiss();
            }
        });

        Warning_dialog.show();
    }

    private void SubmitReport(final String name, final String mobileNo, final String mailId, final String ReportProblem) {


        mFireStoreDatabase.collection("Report").document(StoryId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (documentSnapshot.exists()){
                        if (documentSnapshot.contains("total_number_of_reports")){
                            long report_count=documentSnapshot.getLong("total_number_of_reports").longValue();
                            FinalReportSubmit(name,mobileNo,mailId,ReportProblem,++report_count);
                        }else {
                            FinalReportSubmit(name,mobileNo,mailId,ReportProblem,1);
                        }
                    }else {
                        FinalReportSubmit(name,mobileNo,mailId,ReportProblem,1);
                    }
                }else {
                    FinalReportSubmit(name,mobileNo,mailId,ReportProblem,1);
                }
            }
        });

    }

    private void FinalReportSubmit(final String name, final String mobileNo, final String mailId, final String reportProblem, long reportCounter) {
        Map reportcounter=new HashMap();
        reportcounter.put("total_number_of_reports",reportCounter);
        reportcounter.put("report_status","new_report");

        mFireStoreDatabase.collection("Report").document(StoryId).set(reportcounter,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Map loadData=new HashMap();
                    loadData.put("reporter_name",name);
                    loadData.put("reporter_phone_number",mobileNo);
                    loadData.put("reporter_mail_id",mailId);
                    loadData.put("report_problem",reportProblem);
                    loadData.put("report_type",ReportType);
                    mFireStoreDatabase.collection("Report").document(StoryId).collection("Issue")
                            .document(mAuth.getUid()).set(loadData, SetOptions.merge())
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(ReportForStoryScreen.this,getString(R.string.WE_WILL_TAKE_ACTION),Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });
    }

    public  void AnotherInfoDialog(final Activity activity){
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.YOU_CAN_REPORT_FOR))
                .setMessage(getString(R.string.INAPPROPRIATE_CONTENT_HINT))

                .setNegativeButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if( android.R.id.home==item.getItemId()){
            onBackPressed();
        }
        return true;
    }

}
