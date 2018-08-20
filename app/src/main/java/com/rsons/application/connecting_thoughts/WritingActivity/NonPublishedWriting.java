package com.rsons.application.connecting_thoughts.WritingActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rsons.application.connecting_thoughts.EndlessRecyclerViewScrollListener;
import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.StoryAdapter.DraftStoryAdapter;
import com.rsons.application.connecting_thoughts.StoryClasses.Simple_Story;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankit on 2/10/2018.
 */

public class NonPublishedWriting extends AppCompatActivity {

    List<Simple_Story> Story_list= new ArrayList<>();
    private RecyclerView mStoryRecyclerView;
    private ArrayList<String> StoryIdList=new ArrayList<>();
    int GetDataAt_a_time=1;
    private LinearLayoutManager llm;

    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;
    RelativeLayout LoadingScreen;
    private ImageView MovingAlone;
    private TextView MovingAloneMessage;
    private ProgressBar LoadingCircle;


    boolean isLoading,IsDataPresent=true;
    private int visibleThreshold = 10;
    int totalItemCount,lastVisibleItem;

    private DraftStoryAdapter mDraftAdapter;
    private Toolbar mStoryToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.non_published_story);
        mStoryRecyclerView = (RecyclerView) findViewById(R.id.story_list);

        LoadingCircle= (ProgressBar) findViewById(R.id.loading_circle);
        MovingAlone= (ImageView) findViewById(R.id.empty_folder);
        LoadingScreen= (RelativeLayout) findViewById(R.id.loading);
        MovingAloneMessage= (TextView) findViewById(R.id.message_text);
        mStoryToolbar= (Toolbar) findViewById(R.id.non_published_toolbar);
        setSupportActionBar(mStoryToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(getString(R.string.DRAFT));


        mStoryRecyclerView.setNestedScrollingEnabled(true);
        llm = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mStoryRecyclerView.setHasFixedSize(true);
        mStoryRecyclerView.setItemViewCacheSize(20);
        mStoryRecyclerView.setDrawingCacheEnabled(true);
        mStoryRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mStoryRecyclerView.setLayoutManager(llm);

        mStoryRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mDraftAdapter =new DraftStoryAdapter(Story_list,NonPublishedWriting.this,false);
        mStoryRecyclerView.setAdapter(mDraftAdapter);

        mStoryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // total number of items in the data set held by the adapter
                totalItemCount =llm.getItemCount();
                //adapter position of the first visible view.
                lastVisibleItem =llm.findLastVisibleItemPosition();

                //if it isn't currently loading, we check to see if it reached
                //the visibleThreshold(ex,5) and need to reload more data,
                //if it need to reload some more data, execute loadData() to
                //fetch the data (showed next)
                if (!isLoading && totalItemCount <=
                        (lastVisibleItem+visibleThreshold)) {
                    if (IsDataPresent){
                        GetDataAt_a_time++;
                        loadStories();
                        isLoading = true;
                    }
                }
            }
        });


        mFireStoreDatabase= FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();


        loadStories();
    }

    private void loadStories() {

        if (Story_list.size()>0&&IsDataPresent){
            Story_list.add(null);
            mDraftAdapter.notifyItemInserted(Story_list.size() - 1);
        }
        final List<Simple_Story> Temp_Story_list= new ArrayList<>();
        CollectionReference storyRef = mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story");

        Query getData=storyRef.whereEqualTo("story_status","draft").orderBy("draft_story_update_time", Query.Direction.DESCENDING);
        getData.limit(GetDataAt_a_time*10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()) {
                        if (!StoryIdList.contains(document.getId())){
                            StoryIdList.add(document.getId());
                            Temp_Story_list.add(new Simple_Story(document.getId(),document.getString("story_title"),document.getString("cover_image"),
                                    document.get("draft_story_update_time")));
                        }

                    }
                    mDraftAdapter.notifyDataSetChanged();
                }


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Story_list.size()>0&&IsDataPresent){
                            Story_list.remove(Story_list.size() - 1);
                            mDraftAdapter.notifyItemRemoved(Story_list.size());
                        }

                        if (Temp_Story_list.size()<10){
                            IsDataPresent=false;
                        }

                        Story_list.addAll(Temp_Story_list);
                        isLoading=false;
                        mDraftAdapter.notifyDataSetChanged();

                        Log.e("dkfjskfsf","this is temp list size "+Temp_Story_list.size());
                        if (Story_list.size()==0){
                            MovingAlone.setVisibility(View.VISIBLE);
                            MovingAloneMessage.setVisibility(View.VISIBLE);
                            isLoading=false;
                            MovingAloneMessage.setText(getString(R.string.YOU_HAVE_NOT_PUBLISHED_STORY_YET));
                            LoadingCircle.setVisibility(View.GONE);
                        }else {

                            LoadingScreen.setVisibility(View.GONE);
                        }

                    }
                },2000);

            }
        });

        getData.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("dkfskffsf","this is current exception "+e.getMessage());
            }
        });
        //setupEndlessListener();


    }

   /* private void setupEndlessListener(){
        mStoryRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(llm, 2) {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetDataAt_a_time++;
                        loadStories();
                    }
                }, 150);
            }
        });
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.draft_screen_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if( android.R.id.home==item.getItemId()){
            onBackPressed();
        }
        if(item.getItemId()==R.id.new_story){
            Intent LaunchWriteStory=new Intent(NonPublishedWriting.this, WriteStoryActivity.class);
            startActivity(LaunchWriteStory);
            //overridePendingTransition(R.anim.from_right, R.anim.to_right);
        }

        return true;
    }
}
