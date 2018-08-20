package com.rsons.application.connecting_thoughts.UsersActivity;

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
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rsons.application.connecting_thoughts.Const;
import com.rsons.application.connecting_thoughts.EndlessRecyclerViewScrollListener;
import com.rsons.application.connecting_thoughts.Fetch_data.Display_Single_Section_list_Adapter;
import com.rsons.application.connecting_thoughts.Fetch_data.SingleStoryModel;
import com.rsons.application.connecting_thoughts.R;

import java.util.ArrayList;

/**
 * Created by ankit on 3/26/2018.
 */

public class LoadAuthorWritings extends AppCompatActivity {


    String UserName;
    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;
    //Query GetSection_Data_query;
    int GetDataAt_a_time=1;
    private ProgressBar LoadingProgress;
    Display_Single_Section_list_Adapter SectionAdapter;
    RecyclerView section_Recycler_View;
    LinearLayoutManager section_linearLayout_manager;


    boolean isLoading,IsDataPresent=true;
    private int visibleThreshold = 10;
    int totalItemCount,lastVisibleItem;


    ArrayList<SingleStoryModel> SingleSection_allStoryList=new ArrayList<>();
    ArrayList<String> StoryIdList=new ArrayList<>();
    private Toolbar mIndividualSectionToolbar;
    Query query;
    private String AuthorId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.section_listing_activity);

        UserName =getIntent().getStringExtra("user_name");
        AuthorId=getIntent().getStringExtra("author_id");


        mFireStoreDatabase=FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        //GetSection_Data_query= ;

        mIndividualSectionToolbar = (Toolbar) findViewById(R.id.section_toolbar);
        setSupportActionBar(mIndividualSectionToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(UserName);

        LoadingProgress= (ProgressBar) findViewById(R.id.loading_progress);
        section_Recycler_View = (RecyclerView) findViewById(R.id.story_listing);
        section_Recycler_View.setHasFixedSize(true);
        SectionAdapter= new Display_Single_Section_list_Adapter(this, SingleSection_allStoryList,false,false);
        section_linearLayout_manager=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        section_Recycler_View.setLayoutManager(section_linearLayout_manager);
        section_Recycler_View.setAdapter(SectionAdapter);


        section_Recycler_View.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // total number of items in the data set held by the adapter
                totalItemCount =section_linearLayout_manager.getItemCount();
                //adapter position of the first visible view.
                lastVisibleItem =section_linearLayout_manager.findLastVisibleItemPosition();

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
        query= mFireStoreDatabase.collection("Users").document(AuthorId).collection("Story").whereEqualTo("is_hide",false).
                orderBy("all_time_rank", Query.Direction.DESCENDING);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getStoryData();
            }
        }, 100);

    }
/*
    private void setupEndlessListener(){
        section_Recycler_View.addOnScrollListener(new EndlessRecyclerViewScrollListener(section_linearLayout_manager, 2) {
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

        if (SingleSection_allStoryList.size()>0&&IsDataPresent){
            SingleSection_allStoryList.add(null);
            SectionAdapter.notifyItemInserted(SingleSection_allStoryList.size() - 1);
        }

        final ArrayList<SingleStoryModel> Temp_SingleSection_allStoryList=new ArrayList<>();
        query.limit(GetDataAt_a_time*10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    ArrayList<SingleStoryModel> singleItem=new ArrayList<SingleStoryModel>();

                    Log.e("sdkjskfsjks","outside for loop");
                    for (DocumentSnapshot document : task.getResult()) {

                        if (!StoryIdList.contains(document.getId())){
                            Log.e("sdkjskfsjks","first if for loop language from sever "+document.getString("story_language")+" internal lang "+Const.CURRENT_LANGUAGE);
                            if (document.getString("story_status").equals("publish")){
                                StoryIdList.add(document.getId());
                                SingleSection_allStoryList.add(new SingleStoryModel(1,document.getString("story_title"), document.getString("cover_image"), document.getString("story_intro"),
                                        document.get("view_count")+"",""+document.get("AvgRating"),document.getId(),AuthorId,document.getBoolean("is_hide").booleanValue()));
                                Log.e("sdkjskfsjks","inside for loop "+Const.CURRENT_LANGUAGE);
                            }
                        }
                        /*LoadingProgress.setVisibility(View.GONE);
                        //SingleSection_allStoryList.addAll(singleItem);
                        SectionAdapter.notifyDataSetChanged();*/
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (SingleSection_allStoryList.size()>0&&IsDataPresent){
                                SingleSection_allStoryList.remove(SingleSection_allStoryList.size() - 1);
                                SectionAdapter.notifyItemRemoved(SingleSection_allStoryList.size());

                            }
                            if (Temp_SingleSection_allStoryList.size()<10){
                                IsDataPresent=false;
                            }
                            LoadingProgress.setVisibility(View.GONE);
                            SingleSection_allStoryList.addAll(Temp_SingleSection_allStoryList);
                            isLoading=false;
                            SectionAdapter.notifyDataSetChanged();

                        }
                    },2000);
                }
            }
        });
        //setupEndlessListener();
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
