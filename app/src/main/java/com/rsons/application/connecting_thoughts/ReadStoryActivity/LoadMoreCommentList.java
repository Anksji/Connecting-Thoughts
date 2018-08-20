package com.rsons.application.connecting_thoughts.ReadStoryActivity;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rsons.application.connecting_thoughts.Const;
import com.rsons.application.connecting_thoughts.Fetch_data.CommentDataAdapter;
import com.rsons.application.connecting_thoughts.Fetch_data.CommentModel;
import com.rsons.application.connecting_thoughts.R;

import java.util.ArrayList;

/**
 * Created by ankit on 4/18/2018.
 */

public class LoadMoreCommentList extends AppCompatActivity {

    String StoryTitle,StoryId,StoryAuthorId;
    private FirebaseFirestore mFireStoreDatabase;
    private Toolbar mCommentToolbar;
    private FirebaseAuth mAuth;
    private ProgressBar LoadingProgress;
    private ArrayList<CommentModel> CommentArrayList=new ArrayList<>();
    private ArrayList<String> CommentIdList=new ArrayList<>();
    CommentDataAdapter CommentAdapter;
    RecyclerView comment_Recycler_View;
    LinearLayoutManager section_linearLayout_manager;
    int GetDataAt_a_time=1;
    boolean isLoading,IsDataPresent=true;
    private int visibleThreshold = 10;
    int totalItemCount,lastVisibleItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_more_comment);

        StoryTitle=getIntent().getStringExtra("story_title");
        StoryId=getIntent().getStringExtra("story_id");
        StoryAuthorId=getIntent().getStringExtra("storyAuthorId");


        mFireStoreDatabase= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //GetSection_Data_query= ;

        mCommentToolbar = (Toolbar) findViewById(R.id.comment_toolbar);
        setSupportActionBar(mCommentToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Comments on "+StoryTitle);

        LoadingProgress= (ProgressBar) findViewById(R.id.loading_progress);
        comment_Recycler_View = (RecyclerView) findViewById(R.id.comment_list);
        comment_Recycler_View.setHasFixedSize(true);
        CommentAdapter = new CommentDataAdapter(LoadMoreCommentList.this, CommentArrayList);
        section_linearLayout_manager=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        comment_Recycler_View.setLayoutManager(section_linearLayout_manager);
        comment_Recycler_View.setAdapter(CommentAdapter);

        comment_Recycler_View.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        LoadAllComments();
                        isLoading = true;
                    }

                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                LoadAllComments();
            }
        }, 150);

    }


    private void LoadAllComments(){

        if (StoryAuthorId==null||StoryId==null){
            return;
        }
        if (CommentArrayList.size()>0&&IsDataPresent){
            CommentArrayList.add(null);
            CommentAdapter.notifyItemInserted(CommentArrayList.size() - 1);
        }


        Log.e("kfjskfjskfd","inside load comment Author id "+StoryAuthorId+" story id "+StoryId);
        CollectionReference comentRef = mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").
                document(StoryId).collection("Comments");

         final ArrayList<CommentModel> TempCommentArrayList=new ArrayList<>();
        comentRef.orderBy("comment_timestamp_update", Query.Direction.DESCENDING).limit(GetDataAt_a_time* Const.LOAD_DATA_AT_ONCE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d("sdfhsdj", document.getId() + " => " + document.getData());
                        if (!document.getId().equals(mAuth.getUid())){
                            if (!CommentIdList.contains(document.getId())){
                                CommentIdList.add(document.getId());
                                //Log.d("kfjskfjskfd", "Comment user name "+document.getString("comment_user_name")+" user rating is "+document.get("star_rating")+"");

                                TempCommentArrayList.add(new CommentModel(document.getId(), document.getString("comment_user_image"), document.get("comment_timestamp_update"),
                                        document.get("starRating")+"",document.getString("comment"),document.getString("comment_user_name")));

                            }

                        }

                        Log.d("dfsfjskff", "Comment user name "+document.getString("comment_user_name")+" user rating is "+document.get("star_rating")+"");
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (CommentArrayList.size()>0&&IsDataPresent){
                                if (CommentArrayList.get(CommentArrayList.size() - 1)==null){
                                    CommentArrayList.remove(CommentArrayList.size() - 1);
                                    CommentAdapter.notifyItemRemoved(CommentArrayList.size());
                                }
                            }
                            if (TempCommentArrayList.size()==0){
                                IsDataPresent=false;
                            }
                            CommentArrayList.addAll(TempCommentArrayList);
                            if (CommentArrayList.size()>0){
                                LoadingProgress.setVisibility(View.GONE);
                            }
                            isLoading=false;
                            CommentAdapter.notifyDataSetChanged();

                        }
                    },2000);


                } else {
                    Log.d("sdfhsdj", "Error getting documents: ", task.getException());
                }
                //CommentAdapter.notifyDataSetChanged();

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
