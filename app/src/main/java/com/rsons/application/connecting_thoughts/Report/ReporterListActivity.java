package com.rsons.application.connecting_thoughts.Report;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.rsons.application.connecting_thoughts.EndlessRecyclerViewScrollListener;
import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.ReadStoryActivity.ViewFirstPage_of_Story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ankit on 3/20/2018.
 */

public class ReporterListActivity extends AppCompatActivity {

    private String StoryId;
    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;
    private Toolbar mReportListToolbar;
    private String Author_id=null;
    ArrayList<SingleReporterModel> SingleSection_allStoryList=new ArrayList<>();
    ArrayList<String> StoryIdList=new ArrayList<>();


    int GetDataAt_a_time=1;
    private ProgressBar LoadingProgress;
    ReporterListAdapter SectionAdapter;
    RecyclerView section_Recycler_View;
    LinearLayoutManager section_linearLayout_manager;
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporter_list_activity);

        StoryId=getIntent().getStringExtra("story_id");

        mReportListToolbar = (Toolbar) findViewById(R.id.reporter_list);
        setSupportActionBar(mReportListToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.REPORTER_LIST));

        mFireStoreDatabase= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);
        mProgressDialog=new ProgressDialog(ReporterListActivity.this);

        LoadingProgress= (ProgressBar) findViewById(R.id.loading_progress);
        section_Recycler_View = (RecyclerView) findViewById(R.id.story_listing);
        section_Recycler_View.setHasFixedSize(true);
        SectionAdapter= new ReporterListAdapter(this, SingleSection_allStoryList);
        section_linearLayout_manager=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        section_Recycler_View.setLayoutManager(section_linearLayout_manager);
        section_Recycler_View.setAdapter(SectionAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getReporterData();
            }
        }, 50);

        mFireStoreDatabase.collection("Story").document(StoryId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (documentSnapshot.exists()){
                        if (documentSnapshot.contains("story_author_id")){
                            Author_id=documentSnapshot.getString("story_author_id");
                        }else {
                            Author_id=null;
                        }
                    }
                }
            }
        });
    }

    private void setupEndlessListener(){
        section_Recycler_View.addOnScrollListener(new EndlessRecyclerViewScrollListener(section_linearLayout_manager, 2) {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetDataAt_a_time++;
                        getReporterData();
                    }
                }, 150);
            }
        });
    }


    public void getReporterData() {

        mFireStoreDatabase.collection("Report").document(StoryId).collection("Issue").orderBy("reporter_name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()) {
                        if (!StoryIdList.contains(document.getId())) {
                            StoryIdList.add(document.getId());
                            SingleSection_allStoryList.add(new SingleReporterModel(document.getId(),document.getString("reporter_name"), document.getString("reporter_mail_id"),
                                    document.getString("reporter_phone_number"),
                                    document.getString("report_type"),document.getString("report_problem")));

                        }
                        LoadingProgress.setVisibility(View.GONE);
                        //Report_allStoryList.addAll(singleItem);
                        SectionAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        setupEndlessListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.more_report_options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if( android.R.id.home==item.getItemId()){
            onBackPressed();
        }else if (item.getItemId()==R.id.take_action){

            ShowActionDialog();

        }else if (item.getItemId()==R.id.open_story){

            Intent LaunchViewStoryFirstPage=new Intent(ReporterListActivity.this, ViewFirstPage_of_Story.class);
            LaunchViewStoryFirstPage.putExtra("story_Id",StoryId);
            LaunchViewStoryFirstPage.putExtra("from_story_listing",true);
            LaunchViewStoryFirstPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(LaunchViewStoryFirstPage);

        }else if (item.getItemId()==R.id.author_detail){
            Intent LaunchViewStoryAuthorDetail=new Intent(ReporterListActivity.this, ReportStoryAuthorDetail.class);
            LaunchViewStoryAuthorDetail.putExtra("author_id",Author_id);
            startActivity(LaunchViewStoryAuthorDetail);
        }

        return true;
    }

    private void ShowActionDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ReporterListActivity.this);
        builderSingle.setTitle(getString(R.string.TAKE_ACTION_AGAINST_REPORT));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ReporterListActivity.this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add(getString(R.string.SUSPEND_STORY));
        arrayAdapter.add(getString(R.string.IGRORE_REPORT));

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(ReporterListActivity.this);
                //builderInner.setMessage(strName);


                if (strName.equals(getString(R.string.SUSPEND_STORY))){

                    mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT_WHILE_WE_UNPUBLISH_STORY));
                    builderInner.setMessage(getString(R.string.DO_YOU_REALLY_WANT_TO_SUSPAND_STORY));
                }else {
                    mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT_WHILE_WE_REJECTING_REPORT));
                    builderInner.setMessage(getString(R.string.DO_YOU_REALLY_WANT_TO_REJCT_REPORT));
                }

                builderInner.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        mProgressDialog.show();
                        TakeAction(strName);
                        dialog.dismiss();
                    }
                });
                builderInner.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }

    private void TakeAction(String actionName) {
        if (actionName.equals(getString(R.string.SUSPEND_STORY))){
            mFireStoreDatabase.collection("Story").document(StoryId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot document=task.getResult();
                        final String authorId=document.getString("story_author_id");
                        final String StoryParentId=document.getString("parent_story_id");
                        Map unpublishStory=new HashMap();
                        unpublishStory.put("story_status","suspended");
                        mFireStoreDatabase.collection("Story").document(StoryId).set(unpublishStory,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                final Map unpublishStory=new HashMap();
                                unpublishStory.put("story_status","suspended");
                                mFireStoreDatabase.collection("Users").document(authorId).collection("Story").document(StoryId)
                                        .set(unpublishStory,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){

                                            mFireStoreDatabase.collection("Story").document(StoryParentId).collection("All_Chapter_list").document(StoryId)
                                                    .set(unpublishStory,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()){
                                                        Map reportcounter=new HashMap();
                                                        reportcounter.put("report_status","Story_unpublished");
                                                        mFireStoreDatabase.collection("Report").document(StoryId).set(reportcounter, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                mProgressDialog.dismiss();
                                                                Toast.makeText(ReporterListActivity.this,getString(R.string.STORY_UNPUBLISHED_SUCCESSFULLY),Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            });

                                        }

                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
        else {
            Map reportcounter=new HashMap();
            reportcounter.put("report_status","Fake_Report");
            mFireStoreDatabase.collection("Report").document(StoryId).set(reportcounter, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    mProgressDialog.dismiss();
                    Toast.makeText(ReporterListActivity.this,getString(R.string.REPORT_DECLARED_AS_FAKE),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
