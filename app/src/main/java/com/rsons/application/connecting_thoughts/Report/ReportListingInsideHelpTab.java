package com.rsons.application.connecting_thoughts.Report;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rsons.application.connecting_thoughts.Const;
import com.rsons.application.connecting_thoughts.EndlessRecyclerViewScrollListener;
import com.rsons.application.connecting_thoughts.Fetch_data.Display_Single_Section_list_Adapter;
import com.rsons.application.connecting_thoughts.Fetch_data.SingleStoryModel;
import com.rsons.application.connecting_thoughts.R;

import java.util.ArrayList;

/**
 * Created by ankit on 3/20/2018.
 */

public class ReportListingInsideHelpTab extends AppCompatActivity {

    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;
    private Toolbar mReportListToolbar;

    ArrayList<SingleStoryModel> Report_allStoryList =new ArrayList<>();
    ArrayList<String> StoryIdList=new ArrayList<>();


    int GetDataAt_a_time=1;
    //private ProgressBar LoadingProgress;
    Display_Single_Section_list_Adapter ReportListingAdapter;
    RecyclerView Report_Recycler_View;
    LinearLayoutManager ReportList_linearLayout_manager;

    private ImageView MovingAlone;
    private TextView MovingAloneMessage;
    private ProgressBar LoadingCircle;
    private ImageView OfflineCloud;
    RelativeLayout LoadingScreen;

    boolean isLoading,IsDataPresent=true;
    private int visibleThreshold = 10;
    int totalItemCount,lastVisibleItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_listing);

        mReportListToolbar = (Toolbar) findViewById(R.id.report_listing_toolbar);
        setSupportActionBar(mReportListToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.Incomming_report));

        mFireStoreDatabase=FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);

        LoadingCircle= (ProgressBar) findViewById(R.id.loading_circle);
        MovingAlone= (ImageView) findViewById(R.id.empty_folder);
        LoadingScreen= (RelativeLayout) findViewById(R.id.loading);
        MovingAloneMessage= (TextView) findViewById(R.id.message_text);
        OfflineCloud= (ImageView) findViewById(R.id.offline_cloud);

        //LoadingProgress= (ProgressBar) findViewById(R.id.loading_progress);
        Report_Recycler_View = (RecyclerView) findViewById(R.id.story_listing);
        Report_Recycler_View.setHasFixedSize(true);
        ReportListingAdapter = new Display_Single_Section_list_Adapter(this, Report_allStoryList,false,true);
        ReportList_linearLayout_manager =new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        Report_Recycler_View.setLayoutManager(ReportList_linearLayout_manager);
        Report_Recycler_View.setAdapter(ReportListingAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getStoryData();
            }
        }, 150);


        Report_Recycler_View.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // total number of items in the data set held by the adapter
                totalItemCount =ReportList_linearLayout_manager.getItemCount();
                //adapter position of the first visible view.
                lastVisibleItem =ReportList_linearLayout_manager.findLastVisibleItemPosition();

                //if it isn't currently loading, we check to see if it reached
                //the visibleThreshold(ex,5) and need to reload more data,
                //if it need to reload some more data, execute loadData() to
                //fetch the data (showed next)
                if (!isLoading && totalItemCount <=
                        (lastVisibleItem+visibleThreshold)) {
                    if (IsDataPresent){
                        GetDataAt_a_time++;
                        getStoryData();
                        isLoading = true;
                    }

                }
            }
        });

    }


  /*  private void setupEndlessListener(){
        Report_Recycler_View.addOnScrollListener(new EndlessRecyclerViewScrollListener(ReportList_linearLayout_manager, 2) {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetDataAt_a_time++;
                        getStoryData();
                    }
                }, 150);
            }
        });
    }*/

    public void getStoryData() {

        if (Report_allStoryList.size()>0&&IsDataPresent){
            Report_allStoryList.add(null);
            ReportListingAdapter.notifyItemInserted(Report_allStoryList.size() - 1);
        }

        Log.e("sdkjskfsjks","CALL  getStoryData for loop "+Const.CURRENT_LANGUAGE);


        Query getData=mFireStoreDatabase.collection("Report").whereEqualTo("report_status","new_report")
                .orderBy("total_number_of_reports", Query.Direction.DESCENDING).limit(GetDataAt_a_time*10);

        getData.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fskdjlfs","this is exception "+e.getMessage());
            }
        });
        getData.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    final ArrayList<SingleStoryModel> Temp_Report_allStoryList =new ArrayList<>();

                    Log.e("sdkjskfsjks","OUTSIDE for loop "+task.getResult());
                    for (DocumentSnapshot docs : task.getResult()) {
                        Log.e("sdkjskfsjks","inside for loop "+Const.CURRENT_LANGUAGE);
                        final long NoOfReports=docs.getLong("total_number_of_reports").longValue();
                        mFireStoreDatabase.collection("Story").document(docs.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if (document.getString("story_language").equals(Const.CURRENT_LANGUAGE)){
                                        if (!StoryIdList.contains(document.getId())){
                                            StoryIdList.add(document.getId());
                                            Temp_Report_allStoryList.add(new SingleStoryModel(NoOfReports,document.getString("story_title"), document.getString("cover_image"), document.getString("story_intro"),
                                                    document.get("view_count")+"",""+document.get("AvgRating"),document.getId(),document.getString("story_author_id"),false));
                                            Log.e("sdkjskfsjks","inside for loop "+Const.CURRENT_LANGUAGE);

                                            //LoadingProgress.setVisibility(View.GONE);
                                            /*ReportListingAdapter.notifyDataSetChanged();
                                            LoadingScreen.setVisibility(View.GONE);*/
                                        }

                                    }

                                }
                            }
                        });

                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Report_allStoryList.size()>0&&IsDataPresent){
                                Report_allStoryList.remove(Report_allStoryList.size() - 1);
                                ReportListingAdapter.notifyItemRemoved(Report_allStoryList.size());

                            }
                            if (Temp_Report_allStoryList.size()<10){
                                IsDataPresent=false;

                            }

                            Report_allStoryList.addAll(Temp_Report_allStoryList);
                            if (Report_allStoryList.size()>0){
                                LoadingScreen.setVisibility(View.GONE);
                            }
                            isLoading=false;
                            ReportListingAdapter.notifyDataSetChanged();

                        }
                    },2000);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Report_allStoryList.size()==0){
                                LoadingCircle.setVisibility(View.GONE);
                                MovingAlone.setVisibility(View.VISIBLE);
                                MovingAloneMessage.setVisibility(View.VISIBLE);
                                MovingAloneMessage.setText(getString(R.string.NO_REPORT_YET));
                            }
                        }
                    }, 5000);

                }
            }
        });

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
