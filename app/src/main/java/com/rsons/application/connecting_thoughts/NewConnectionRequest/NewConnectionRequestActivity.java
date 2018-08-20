package com.rsons.application.connecting_thoughts.NewConnectionRequest;

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
import com.rsons.application.connecting_thoughts.EndlessRecyclerViewScrollListener;
import com.rsons.application.connecting_thoughts.FriendOr_Follower.FriendOrFollowerAdapter;
import com.rsons.application.connecting_thoughts.FriendOr_Follower.FriendOrFollowerModel;
import com.rsons.application.connecting_thoughts.R;

import java.util.ArrayList;

/**
 * Created by ankit on 3/12/2018.
 */

public class NewConnectionRequestActivity extends AppCompatActivity {

   // private CollectionReference FriendRequestDatabaseRef;
    private RecyclerView mFriendsRequestList;
    private FriendOrFollowerAdapter FriendRequestAdapter;
    private ArrayList<FriendOrFollowerModel> FriendRequestList =new ArrayList<>();
    private ArrayList<String> MyFriendRequestIdList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStoreDatabase;
    LinearLayoutManager mLayoutManager;
    private  String mCurrent_user_id;
    private ProgressBar LoadingCircle;
    RelativeLayout LoadingScreen;
    private ImageView MovingAlone;
    private TextView MovingAloneMessage;
    int GetDataAt_a_time=1;
    private boolean IsScroll=true;
    private Toolbar mIndividualSectionToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_connection_request);

        mFriendsRequestList = (RecyclerView) findViewById(R.id.fried_list);
        LoadingCircle= (ProgressBar) findViewById(R.id.loading_circle);
        MovingAlone= (ImageView) findViewById(R.id.moving_alone);
        LoadingScreen= (RelativeLayout) findViewById(R.id.loading);
        MovingAloneMessage= (TextView) findViewById(R.id.message_text);
        MovingAlone.setImageResource(R.drawable.ic_simple_man_in_connection);
        MovingAloneMessage.setText(R.string.NO_NEW_CONNECTION_REQUEST);

        mIndividualSectionToolbar = (Toolbar) findViewById(R.id.new_connection_request_toolbar);
        setSupportActionBar(mIndividualSectionToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.NEW_CONNECTION_reQ));

        mFireStoreDatabase= FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);

        mAuth = FirebaseAuth.getInstance();

        mFriendsRequestList.setHasFixedSize(true);
        FriendRequestAdapter = new FriendOrFollowerAdapter(NewConnectionRequestActivity.this, FriendRequestList);
        mLayoutManager=new LinearLayoutManager(NewConnectionRequestActivity.this, LinearLayoutManager.VERTICAL, false);


        mFriendsRequestList.setLayoutManager(mLayoutManager);
        mFriendsRequestList.setAdapter(FriendRequestAdapter);

        LoadAllFriendRequestList();


    }



    private void LoadAllFriendRequestList() {
        IsScroll=false;

        Query GetRequestData=mFireStoreDatabase.collection("FriendRequestData").document(mAuth.getUid()).collection("RequestList")
                .whereEqualTo("request_type","recieved")
                .orderBy("request_timestamp", Query.Direction.DESCENDING).limit(GetDataAt_a_time*10);
        GetRequestData.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    boolean IsEmpty=true;
                    for (DocumentSnapshot doc : task.getResult()) {
                        IsEmpty=false;
                        if (!MyFriendRequestIdList.contains(doc.getId())) {
                            MyFriendRequestIdList.add(doc.getId());

                            final String UserId=doc.getId();
                            mFireStoreDatabase.collection("Users").document(UserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        Log.e("dslkfdjks","inside if statement ");
                                        DocumentSnapshot document = task.getResult();
                                        FriendRequestList.add(new FriendOrFollowerModel(UserId,document.getString("image"),
                                                document.getString("name"),document.getString("status")));
                                    }
                                    FriendRequestAdapter.notifyDataSetChanged();
                                    LoadingScreen.setVisibility(View.GONE);
                                    //LoadingProgress.setVisibility(View.GONE);
                                }
                            });

                        }
                    }
                    if (IsEmpty){
                        LoadingCircle.setVisibility(View.GONE);
                        MovingAlone.setVisibility(View.VISIBLE);
                        MovingAloneMessage.setVisibility(View.VISIBLE);
                    }else {
                        setupEndlessListener();
                    }
                }
                else {
                    IsScroll=true;
                    if (FriendRequestList.size()<=0){
                        LoadingCircle.setVisibility(View.GONE);
                        MovingAlone.setVisibility(View.VISIBLE);
                        MovingAloneMessage.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        GetRequestData.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("sdkfkfjs","this is error "+e.getMessage());
            }
        });


    }

    private void setupEndlessListener(){
        mFriendsRequestList.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager, 2) {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetDataAt_a_time++;
                        LoadAllFriendRequestList();
                    }
                }, 150);
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
