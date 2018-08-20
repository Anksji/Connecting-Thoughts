package com.rsons.application.connecting_thoughts.ChatActivityAndClasses;

import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rsons.application.connecting_thoughts.ChatFragFriends.ChatFriendAdapter;
import com.rsons.application.connecting_thoughts.ChatFragFriends.ChatFriendsModel;
import com.rsons.application.connecting_thoughts.Const;
import com.rsons.application.connecting_thoughts.EndlessRecyclerViewScrollListener;
import com.rsons.application.connecting_thoughts.MainActivity;
import com.rsons.application.connecting_thoughts.R;

import java.util.ArrayList;

/**
 * Created by ankit on 3/21/2018.
 */

public class AllFriendList extends AppCompatActivity {

    private RecyclerView mFriendsList;
    private ChatFriendAdapter FriendFollowerAdapter;
    private ArrayList<ChatFriendsModel> ChatFriendList =new ArrayList<>();
    private ArrayList<String> MyFriendIdList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStoreDatabase;
    LinearLayoutManager mLayoutManager;
    private  String mCurrent_user_id;
    //private ProgressBar LoadingProgress;
    private boolean IsScroll=true;
    int GetDataAt_a_time=1;
    RelativeLayout LoadingScreen;
    private ImageView MovingAlone;
    private TextView MovingAloneMessage;
    private ProgressBar LoadingCircle;
    private ImageView OfflineCloud;
    private Toolbar mAllfriendtoolbar;

    private String ShareType;
    private String Message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_friend_list);


        mAllfriendtoolbar= (Toolbar) findViewById(R.id.all_friends_toolbar);
        setSupportActionBar(mAllfriendtoolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        mFriendsList= (RecyclerView) findViewById(R.id.fried_list);
        //LoadingProgress= (ProgressBar) mView.findViewById(R.id.loading_progress);
        LoadingCircle= (ProgressBar) findViewById(R.id.loading_circle);
        MovingAlone= (ImageView) findViewById(R.id.moving_alone);
        LoadingScreen= (RelativeLayout) findViewById(R.id.loading);
        MovingAloneMessage= (TextView) findViewById(R.id.message_text);
        OfflineCloud= (ImageView) findViewById(R.id.offline_cloud);

        mAuth = FirebaseAuth.getInstance();
        mFireStoreDatabase=FirebaseFirestore.getInstance();
        mCurrent_user_id=mAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            actionBar.setTitle(getString(R.string.SHARE_WITH));
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        }else {
            actionBar.setTitle(getString(R.string.FRIENDS));
        }

        Const.IS_MESSAGE_SHARE_COMPLETE=false;


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFriendsList.setHasFixedSize(true);
                FriendFollowerAdapter = new ChatFriendAdapter(AllFriendList.this, ChatFriendList,true,ShareType,Message);
                mLayoutManager=new LinearLayoutManager(AllFriendList.this, LinearLayoutManager.VERTICAL, false);


                mFriendsList.setLayoutManager(mLayoutManager);
                mFriendsList.setAdapter(FriendFollowerAdapter);

                LoadAllFriendList();
            }
        }, 500);




    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            ShareType="image";
            Message=""+imageUri;
        }
    }
    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            Message=sharedText;
            ShareType="text";
            // Update UI to reflect text being shared
        }
    }

    private void setupEndlessListener(){
        mFriendsList.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager, 2) {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetDataAt_a_time++;
                        LoadAllFriendList();
                    }
                }, 150);
            }
        });
    }


    private void LoadAllFriendList() {
        IsScroll=false;
        CollectionReference FriendRef = mFireStoreDatabase.collection("FriendsData").
                document(mAuth.getUid()).collection("FriendList");

        //orderBy("last_message_sent_at").
        Query query=FriendRef.orderBy("last_message_sent_at", Query.Direction.DESCENDING).limit(GetDataAt_a_time*10);
        Log.e("chatfragment_","inside LoadAllFriendList function ");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean IsEmpty=true;
                    for (DocumentSnapshot doc : task.getResult()) {
                        IsEmpty=false;
                        final String UnreadMessages=doc.getString("unread_message");
                        final String LastMessage=doc.getString("last_message");
                        Log.e("chatfragment_","inside task successfull");
                        if (!MyFriendIdList.contains(doc.getId())){
                            MyFriendIdList.add(doc.getId());
                            final String UserId=doc.getId();
                            mFireStoreDatabase.collection("Users").document(UserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        Log.e("dslkfdjks","inside if statement ");
                                        DocumentSnapshot document = task.getResult();
                                        ChatFriendList.add(new ChatFriendsModel(UserId,document.getString("image"),
                                                document.getString("name"),LastMessage,UnreadMessages));
                                    }
                                    FriendFollowerAdapter.notifyDataSetChanged();
                                    //LoadingProgress.setVisibility(View.GONE);
                                    LoadingScreen.setVisibility(View.GONE);
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
                    if (ChatFriendList.size()<=0){
                        LoadingCircle.setVisibility(View.GONE);
                        MovingAlone.setVisibility(View.VISIBLE);
                        MovingAloneMessage.setVisibility(View.VISIBLE);
                    }

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

    @Override
    public void onBackPressed() {
        Intent Main=new Intent(AllFriendList.this, MainActivity.class);
        AllFriendList.this.finish();
        startActivity(Main);
    }
}
