package com.rsons.application.connecting_thoughts.LibraryCollection;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rsons.application.connecting_thoughts.EndlessRecyclerViewScrollListener;
import com.rsons.application.connecting_thoughts.Fetch_data.Display_Single_Section_list_Adapter;
import com.rsons.application.connecting_thoughts.Fetch_data.SingleStoryModel;
import com.rsons.application.connecting_thoughts.R;

import java.util.ArrayList;

/**
 * Created by ankit on 3/8/2018.
 */

public class LibraryListing_of_all_story extends AppCompatActivity {
    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;
    //private ProgressBar LoadingProgress;
    Display_Single_Section_list_Adapter LibraryAdapter;
    RecyclerView library_Recycler_View;
    LinearLayoutManager library_linearLayout_manager;
    int GetDataAt_a_time=1;

    RelativeLayout LoadingScreen;
    private ImageView MovingAlone;
    private TextView MovingAloneMessage;
    private ProgressBar LoadingCircle;
    private ImageView OfflineCloud;

    boolean isLoading,IsDataPresent=true;
    private int visibleThreshold = 10;
    int totalItemCount,lastVisibleItem;


    ArrayList<SingleStoryModel> Library_allStoryList =new ArrayList<>();
    ArrayList<String> StoryIdList=new ArrayList<>();
    private Toolbar mLibraryToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.librarylisting_of_story);
        mFireStoreDatabase= FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();

        mLibraryToolbar = (Toolbar) findViewById(R.id.library_toolbar);
        setSupportActionBar(mLibraryToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(getString(R.string.YOUR_COLLECTION));
        //LoadingProgress= (ProgressBar) findViewById(R.id.loading_progress);
        library_Recycler_View = (RecyclerView) findViewById(R.id.library_story_listing);
        library_Recycler_View.setHasFixedSize(true);
        LibraryAdapter = new Display_Single_Section_list_Adapter(this, Library_allStoryList,true,false);
        library_linearLayout_manager =new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        library_Recycler_View.setLayoutManager(library_linearLayout_manager);
        library_Recycler_View.setAdapter(LibraryAdapter);

        LoadingCircle= (ProgressBar) findViewById(R.id.loading_circle);
        MovingAlone= (ImageView) findViewById(R.id.moving_alone);
        LoadingScreen= (RelativeLayout) findViewById(R.id.loading);
        MovingAloneMessage= (TextView) findViewById(R.id.message_text);
        OfflineCloud= (ImageView) findViewById(R.id.offline_cloud);


        library_Recycler_View.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // total number of items in the data set held by the adapter
                totalItemCount =library_linearLayout_manager.getItemCount();
                //adapter position of the first visible view.
                lastVisibleItem =library_linearLayout_manager.findLastVisibleItemPosition();

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

        /*if (checkNetConnection()){*/
            getStoryData();
        /*}else {
            OfflineCloud.setVisibility(View.VISIBLE);
            MovingAloneMessage.setText(getString(R.string.NO_INTERNET_CONNECTION));
            MovingAloneMessage.setVisibility(View.VISIBLE);
            LoadingCircle.setVisibility(View.GONE);
        }*/

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

    /*private void setupEndlessListener(){
        library_Recycler_View.addOnScrollListener(new EndlessRecyclerViewScrollListener(library_linearLayout_manager, 2) {
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


        if (Library_allStoryList.size()>0&&IsDataPresent){
            Library_allStoryList.add(null);
            LibraryAdapter.notifyItemInserted(Library_allStoryList.size() - 1);
        }

        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Library").orderBy("timestamp_added_to_library", Query.Direction.DESCENDING).
                orderBy("story_number").limit(GetDataAt_a_time*10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    boolean IsEmpty=true;
                    Log.e("dkfkssdd","outside for loop this is story id "+task.isSuccessful());
                    final ArrayList<SingleStoryModel> Temp_Library_allStoryList =new ArrayList<>();
                    for (final DocumentSnapshot document : task.getResult()){
                        IsEmpty=false;
                        Log.e("dkfkssdd","this is story id "+document.getId());
                        mFireStoreDatabase.collection("Story").document(document.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                if (document.exists()){
                                    if (!StoryIdList.contains(document.getId())){
                                        StoryIdList.add(document.getId());
                                        Log.e("dkfkssddsfsfk","most inneer side this is story id "+document.getId());
                                        Temp_Library_allStoryList.add(new SingleStoryModel(1,document.getString("story_title"), document.getString("cover_image"), document.getString("story_intro"),
                                                document.get("view_count")+"",""+document.get("AvgRating"),document.getId(),document.getString("story_author_id"),document.getBoolean("is_hide").booleanValue()));

                                        /*LoadingScreen.setVisibility(View.GONE);
                                        LibraryAdapter.notifyDataSetChanged();*/
                                    }

                                }
                                }
                            }
                        });
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Library_allStoryList.size()>0&&IsDataPresent){
                                Library_allStoryList.remove(Library_allStoryList.size() - 1);
                                LibraryAdapter.notifyItemRemoved(Library_allStoryList.size());

                            }
                             if (Temp_Library_allStoryList.size()==0){
                                IsDataPresent=false;
                            }
                            Library_allStoryList.addAll(Temp_Library_allStoryList);
                            isLoading=false;
                            LibraryAdapter.notifyDataSetChanged();

                            if (Library_allStoryList.size()==0){
                                MovingAlone.setVisibility(View.VISIBLE);
                                MovingAloneMessage.setText(getString(R.string.YOU_DID_NOT_READ_ANY_STORY_YET));
                                MovingAloneMessage.setVisibility(View.VISIBLE);
                                LoadingCircle.setVisibility(View.GONE);
                                isLoading=false;
                            }else {

                                LoadingScreen.setVisibility(View.GONE);
                            }

                        }
                    },2000);

                    /*if (IsEmpty){
                        MovingAlone.setVisibility(View.VISIBLE);
                        MovingAloneMessage.setText(getString(R.string.YOU_COLLECTION_IS_EMPTY));
                        MovingAloneMessage.setVisibility(View.VISIBLE);
                        LoadingCircle.setVisibility(View.GONE);
                    }else {
                        setupEndlessListener();
                    }*/
                }
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.library_option_menus, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if( android.R.id.home==item.getItemId()){
            onBackPressed();
        }
        if (R.id.clear_library==item.getItemId()){
            LoadingScreen.setVisibility(View.VISIBLE);
            LoadingCircle.setVisibility(View.VISIBLE);

            for (int i=0; i<StoryIdList.size();i++){
                final int finalI = i;
                mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Library").document(StoryIdList.get(i))
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (finalI >=StoryIdList.size()-1){

                            Library_allStoryList.clear();
                            LibraryAdapter.notifyDataSetChanged();
                            MovingAlone.setVisibility(View.VISIBLE);
                            MovingAloneMessage.setVisibility(View.VISIBLE);
                            LoadingCircle.setVisibility(View.GONE);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("kfjsklfj", "Error deleting document"+ e.getMessage());
                    }
                });
            }

        }
        return true;
    }

}
