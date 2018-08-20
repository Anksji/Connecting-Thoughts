package com.rsons.application.connecting_thoughts.Search;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rsons.application.connecting_thoughts.EndlessRecyclerViewScrollListener;
import com.rsons.application.connecting_thoughts.Fetch_data.AuthorListAdapter;
import com.rsons.application.connecting_thoughts.Fetch_data.AuthorModel;
import com.rsons.application.connecting_thoughts.Fetch_data.Display_Single_Section_list_Adapter;
import com.rsons.application.connecting_thoughts.Fetch_data.SingleStoryModel;
import com.rsons.application.connecting_thoughts.R;

import java.util.ArrayList;

/**
 * Created by ankit on 3/6/2018.
 */

public class Search_Result extends AppCompatActivity {

    RecyclerView recyclerView_Writings;

    private ArrayList<SingleStoryModel> searchStoryList =new ArrayList<>();
    Display_Single_Section_list_Adapter StoryAdapter;
    LinearLayoutManager mLayoutManager;
    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;
    //private ProgressBar LoadingProgress;
    int GetDataItemStory=1;
    //ArrayList<String> StoryIdList=new ArrayList<>();
    private String SearchKeyword;
    Query GetSearchedStory_query;

    RelativeLayout LoadingScreen;
    private ImageView MovingAlone;
    private TextView MovingAloneMessage;
    private ProgressBar LoadingCircle;
    private ImageView OfflineCloud;

    AuthorListAdapter AuthorAdapter;
    LinearLayoutManager mAuthorLinearLayoutManager;
    private ArrayList<AuthorModel> SearchAuthorList =new ArrayList<>();
    RecyclerView RecyclerView_author;
    int GetDataItemAuthor=1;
    LinearLayout SearchedAuthorLayout,SearchedStoryLayout;
    ArrayList<String>SearchedAuthorIdsList=new ArrayList<>();
    ArrayList<String> SearchedStoryIdsList=new ArrayList<>();
    private boolean isItFirstTime=true;
    private Toolbar mSearchToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serach_result);

        mSearchToolbar= (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(mSearchToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SearchedAuthorLayout= (LinearLayout) findViewById(R.id.author_layout);
        SearchedStoryLayout= (LinearLayout) findViewById(R.id.story_seached_layout);
        //LoadingProgress= (ProgressBar) findViewById(R.id.loading_progress);
        recyclerView_Writings = (RecyclerView) findViewById(R.id.search_result_story);
        RecyclerView_author = (RecyclerView) findViewById(R.id.serach_result_authors);

        LoadingCircle= (ProgressBar) findViewById(R.id.loading_circle);
        MovingAlone= (ImageView) findViewById(R.id.moving_alone);
        LoadingScreen= (RelativeLayout) findViewById(R.id.loading);
        MovingAloneMessage= (TextView) findViewById(R.id.message_text);
        OfflineCloud= (ImageView) findViewById(R.id.offline_cloud);


        SearchKeyword=getIntent().getStringExtra("Search_keyWord");

        actionBar.setTitle(getString(R.string.SEARCH_RESULT_FOR)+" "+SearchKeyword);
        SearchKeyword=SearchKeyword.toLowerCase();
        mFireStoreDatabase= FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();

        Log.e("fdggdgdgdg","searched keyword "+SearchKeyword);


        RecyclerView_author.setHasFixedSize(true);
        AuthorAdapter= new AuthorListAdapter(this, SearchAuthorList);
        mAuthorLinearLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView_author.setLayoutManager(mAuthorLinearLayoutManager);
        RecyclerView_author.setAdapter(AuthorAdapter);

        recyclerView_Writings.setHasFixedSize(true);
        StoryAdapter = new Display_Single_Section_list_Adapter(this, searchStoryList,false,false);
        mLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_Writings.setLayoutManager(mLayoutManager);
        recyclerView_Writings.setAdapter(StoryAdapter);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkNetConnection()){
                    getAuthorData();
                }else {
                    OfflineCloud.setVisibility(View.VISIBLE);
                    MovingAloneMessage.setText(getString(R.string.NO_INTERNET_CONNECTION));
                    MovingAloneMessage.setVisibility(View.VISIBLE);
                    LoadingCircle.setVisibility(View.GONE);
                }

            }
        }, 50);

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getStoryData();
            }
        }, 100);*/



    }

    public boolean checkNetConnection(){
        boolean isConnection;
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null){
            isConnection=false;
        }else{
            isConnection=true;
        }
        return isConnection;
    }

    private void getAuthorData(){
        if (SearchKeyword!=null) {
            GetSearchedStory_query = mFireStoreDatabase.collection("Users").orderBy("lower_case_name").startAt(SearchKeyword).
                    endAt(SearchKeyword + "\uf8ff");
            GetSearchedStory_query.limit(GetDataItemAuthor*10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        ArrayList<AuthorModel> singleItem=new ArrayList<AuthorModel>();
                        Log.e("fisdfsdldfsf","task outside for loop successfull "+task.getResult().size());
                        for (DocumentSnapshot document : task.getResult()) {
                            Log.e("fisdfsdldfsf","task inside for loop successfull "+document.getId());
                            if (!SearchedAuthorIdsList.contains(document.getId())){
                                SearchedAuthorIdsList.add(document.getId());
                                SearchedAuthorLayout.setVisibility(View.VISIBLE);
                                singleItem.add(new AuthorModel(document.getId(), document.getString("name"), document.getString("image"),
                                        "20",document.getString("status")));
                            }


                        }

                        SearchAuthorList.addAll(singleItem);
                        AuthorAdapter.notifyDataSetChanged();
                        if (SearchAuthorList.size()>0){
                            LoadingScreen.setVisibility(View.GONE);
                        }

                        if (isItFirstTime){
                            getStoryData();
                        }

                    }else {
                        Log.e("fisdfsdldfsf"," task unsuccessful ");
                        if (isItFirstTime){
                            getStoryData();
                        }
                    }
                }
            });
            setupAuthorEndlessListener();
        }
    }

    private void setupStoryEndlessListener(){
        recyclerView_Writings.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager, 2) {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetDataItemStory++;
                        getStoryData();
                    }
                }, 150);
            }
        });
    }
    private void setupAuthorEndlessListener(){
        RecyclerView_author.addOnScrollListener(new EndlessRecyclerViewScrollListener(mAuthorLinearLayoutManager, 2) {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetDataItemAuthor++;
                        getAuthorData();
                    }
                }, 150);
            }
        });
    }
    public void getStoryData() {
        isItFirstTime=false;
        if (SearchKeyword!=null){
            GetSearchedStory_query= mFireStoreDatabase.collection("Story").orderBy("story_title_lower_case").startAt(SearchKeyword).
                    endAt(SearchKeyword+"\uf8ff");
            /// whereEqualTo("story_title",SearchKeyword);

            GetSearchedStory_query.limit(GetDataItemStory*10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        ArrayList<SingleStoryModel> singleItem=new ArrayList<SingleStoryModel>();
                        Log.e("fisdfsdl","task outside for loop successfull "+task.getResult().size());
                        for (DocumentSnapshot document : task.getResult()) {
                            Log.e("fisdfsdl","task inside for loop successfull "+document.getId());

                            if (!SearchedAuthorIdsList.contains(document.getId())){
                                SearchedAuthorIdsList.add(document.getId());
                                if (document.getString("story_status").equals("publish")){
                                    SearchedStoryLayout.setVisibility(View.VISIBLE);
                                    singleItem.add(new SingleStoryModel(1,document.getString("story_title"), document.getString("cover_image"), document.getString("story_intro"),
                                            document.get("view_count")+"",""+document.get("AvgRating"),document.getId(),document.getString("story_author_id"),document.getBoolean("is_hide").booleanValue()));

                                }
                            }

                        }


                        //LoadingProgress.setVisibility(View.GONE);
                        searchStoryList.addAll(singleItem);
                        StoryAdapter.notifyDataSetChanged();
                        if (searchStoryList.size()>0){
                            LoadingCircle.setVisibility(View.GONE);
                            LoadingScreen.setVisibility(View.GONE);
                        }


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (searchStoryList.size()<=0&&SearchAuthorList.size()<=0){
                                    MovingAlone.setImageResource(R.drawable.ic_cat);
                                    MovingAloneMessage.setText(getString(R.string.NO_RESULT_FOUND));
                                    LoadingScreen.setVisibility(View.VISIBLE);
                                    LoadingCircle.setVisibility(View.GONE);
                                    MovingAlone.setVisibility(View.VISIBLE);
                                    MovingAloneMessage.setVisibility(View.VISIBLE);
                                }else {
                                    setupStoryEndlessListener();
                                }

                            }
                        }, 5000);

                    }else {
                        Log.e("fisdfsdl"," task unsuccesful ");
                    }
                }
            });


        }
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
