package com.rsons.application.connecting_thoughts.ChatActivityAndClasses;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rsons.application.connecting_thoughts.Const;
import com.rsons.application.connecting_thoughts.EditProfile_Activity;
import com.rsons.application.connecting_thoughts.MainActivity;
import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.UsersActivity.AuthorProfileActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.rsons.application.connecting_thoughts.Notification.FirebaseNotificationService.CHAT_NOTIFICATION_ID;

/**
 * Created by ankit on 2/24/2018.
 */

public class ChatActivity extends AppCompatActivity {


    private static final int SELECT_IMAGE = 1002;
    private String mChatUserKey,mUserName,mUserImage;
    private Toolbar mChatToolBar;

    private TextView mLastSeenview;

    private boolean IsFriend_online;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private EditText WriteChat;
    private ImageView ChatSend;
    //private ImageView RecordAudio;
    private CollectionReference ChatNotificationDatabaseRef;
    private FirebaseFirestore mFireStoreDatabase;

    private boolean IsMovingUp=false;

    private static final int TOTAL_ITEMS_TO_LOAD=30;
    private int mCurrentPage=1;
    //private final List<Messages> old_messagesList =new ArrayList<>();
    private final List<Messages> new_messagesList=new ArrayList<>();
    private final ArrayList<ImageModel> ImageUrlList=new ArrayList<>();
    private LinearLayoutManager mlinearLayoutManagerOld;
    private MyAdapter new_messageAdapterClass,old_messageAdapterClass;
    private RecyclerView MessageList,oldMessageListUp;
    private boolean dontLoadMoreData=false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int CurrentScrollPosition=0;
   // private int currentItemPosition=0;
    private boolean loading = true;
    LinearLayoutManager linearLayoutManager;
    private final List<String > FriendMessageListId =new ArrayList<>();
    private TextView mTitleview;
    private ImageView mProfileImage;
    /*public static MediaRecorder mRecorder = null;
    private static String mFileName = null;
    Chronometer StopWatch;
    LinearLayout AudioRecordMessageLayout;
    ProgressBar AudioUploadProgress;
    TextView AudioTimeText;
    boolean RecordingStarted=false;
    boolean mBooleanIsPressed;
    private StorageReference mAudioStorage;*/
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private ProgressDialog mProgressDialog;
    private AdView mAdView;
    private ImageView SendMediaFile;
    private boolean IsAttachingImage=false;
    private boolean isImOnline=false;

    private StorageReference mImageStorageRef;
    private FirebaseStorage mfirebaseStorrageRef;

    FirebaseStorage storage;
    StorageReference storageReference;
    private int GetDataAt_a_time=3;
    private  String FriendCurrentMessage;
    //private static  String FriendName;
    private  String MyName;
    private String MyStatus,MyImageLink;
    private String MyDeviceToken;
    private String FriendDevicetoken;
    private String FriendStatus;
    private  String NOTIFICATION_SHARED_PREF_NAME="cONNECTING&*^(tHOUGHTS*(";

    private ProgressBar ChatProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_activity);

        MobileAds.initialize(this, getString(R.string.admob_CT_app_id));

        mAdView = (AdView) findViewById(R.id.chat_act_ad);
        final AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ChatProgressBar= (ProgressBar) findViewById(R.id.chat_progress_loading);
        //mAdView.setAdUnitId(getString(R.string.story_chat_banner_ad));


        mChatToolBar= (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(mChatToolBar);
        final ActionBar actionBar=getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mImageStorageRef = FirebaseStorage.getInstance().getReference();
        mfirebaseStorrageRef=FirebaseStorage.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mChatUserKey = getIntent().getStringExtra("user_key");
        mUserName=getIntent().getStringExtra("user_name");
        mUserImage=getIntent().getStringExtra("user_img");



        LayoutInflater inflater= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view=inflater.inflate(R.layout.chat_custom_bar,null);

        actionBar.setCustomView(action_bar_view);


        mTitleview= (TextView) action_bar_view.findViewById(R.id.chat_user_name);
        mLastSeenview= (TextView) action_bar_view.findViewById(R.id.chat_user_last_seen);
        mProfileImage= (CircleImageView) action_bar_view.findViewById(R.id.custom_bar_profile_image);

        if (mUserName!=null&&mUserName.length()>0){
            mTitleview.setText(mUserName);

        }

        ChatSend= (ImageView) findViewById(R.id.chat_sent_icon);
        WriteChat= (EditText) findViewById(R.id.write_message);
        WriteChat.requestFocus();
        ChatSend= (ImageView) findViewById(R.id.chat_sent_icon);
        SendMediaFile= (ImageView) findViewById(R.id.attach_media_file);
        /*StopWatch=(Chronometer)findViewById(R.id.stopwatch);
        AudioRecordMessageLayout = (LinearLayout) findViewById(R.id.audio_record_message_layout);
        AudioUploadProgress= (ProgressBar) findViewById(R.id.audio_upload_progress);
        AudioTimeText= (TextVciew) findViewById(R.id.audio_time_text);*/

        ResetNotificationParams();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId=mAuth.getCurrentUser().getUid();


        mProgressDialog=new ProgressDialog(ChatActivity.this);
        mProgressDialog.setCanceledOnTouchOutside(false);

        mFireStoreDatabase=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);

        mFireStoreDatabase.collection("Users").document(mChatUserKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (documentSnapshot.exists()){
                        setUpFriendData(documentSnapshot);

                    }
                }
            }
        });


        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent FriendActivity=new Intent(ChatActivity.this, AuthorProfileActivity.class);
                FriendActivity.putExtra("user_key",mChatUserKey);
                FriendActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(FriendActivity);
            }
        });

        /*mAudioStorage= FirebaseStorage.getInstance().getReference();

        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/chat_audio_record.3gp";*/

        //new_messageAdapterClass=new MessageAdapterClass(new_messagesList,ImageUrlList,ChatActivity.this);


        MessageList = (RecyclerView) findViewById(R.id.message_list_recycler);
        MessageList.setHasFixedSize(true);
        MessageList.setItemViewCacheSize(30);
        MessageList.setDrawingCacheEnabled(true);
        mlinearLayoutManagerOld = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mlinearLayoutManagerOld.setReverseLayout(true);
        //mlinearLayoutManagerOld.setStackFromEnd(true);
        MessageList.setLayoutManager(mlinearLayoutManagerOld);
        new_messageAdapterClass=new MyAdapter(new_messagesList,ImageUrlList,ChatActivity.this,MessageList);
        MessageList.setAdapter(new_messageAdapterClass);



        new_messageAdapterClass.setOnLoadMoreListener(new MyAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add progress item
                if (new_messagesList.size()>25){
                    if (!dontLoadMoreData){
                        new_messagesList.add(null);
                        new_messageAdapterClass.notifyItemInserted(new_messagesList.size() - 1);

                        IsMovingUp=true;
                        GetDataAt_a_time++;
                        LoadOldMessages(new_messagesList.size());
                    }

                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //remove progress item

                            //add items one by one


                        }
                    }, 1000);*/

                }
            }
        });


        /*if ( checkPermission()){

        }else {
            requestPermission();
        }*/

       /* ChatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this,getString(R.string.HOLD_TO_SEND_AUDIO_MESSAGE),Toast.LENGTH_LONG).show();
                return;
            }
        });*/
        /*ChatSend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    WriteChat.setVisibility(View.GONE);
                    *//*AudioRecordMessageLayout.setVisibility(View.VISIBLE);
                    StopWatch.setVisibility(View.VISIBLE);
                    StopWatch.setBase(SystemClock.elapsedRealtime());
                    StopWatch.start();
                    mBooleanIsPressed = true;*//*
                    handler.postDelayed(runnable, 1000);


                }else if (event.getAction()==MotionEvent.ACTION_UP){
                    Log.e("kdhdjgddds","button is released "+mBooleanIsPressed);
                    if (mBooleanIsPressed){
                        mBooleanIsPressed = false;
                        handler.removeCallbacks(runnable);
                    }
                    else {
                        ResetEveryThing();
                        return false;
                    }
                    Log.e("kdhdjgddds","button is released mBooleanIsPressed= "+mBooleanIsPressed+" rdosd "+RecordingStarted);

                        String[] separated = StopWatch.getText().toString().split(":");
                        int record_time=Integer.parseInt(separated[1]);
                        Log.e("dfdgddd","value of int "+record_time+" string is "+separated[1]);
                        if (record_time>=2){
                            Log.e("dfdgddd","record time "+separated[0]+" 1 = "+separated[1]);
                            AudioTimeText.setVisibility(View.VISIBLE);
                            AudioTimeText.setText(StopWatch.getText());
                            StopWatch.setVisibility(View.GONE);
                            StopWatch.setBase(SystemClock.elapsedRealtime());
                            StopWatch.stop();
                            AudioUploadProgress.setVisibility(View.VISIBLE);
                            stopRecording();

                    }else {
                        ResetEveryThing();
                    }


                }
                return false;
            }
        });*/


        WriteChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               /* if (WriteChat.getText().length()>0){
                    ChatSend.setImageResource(R.drawable.ic_send_button);
                }else {
                    ChatSend.setImageResource(R.drawable.ic_voice);
                }*/
            }
        });

        if (mUserImage!=null){
            Picasso.with(ChatActivity.this).load(mUserImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_user_).into(mProfileImage,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.e("onerrorSection", "this is success");
                        }

                        @Override
                        public void onError() {
                            Log.e("onerrorSection", "this is error");
                            Picasso.with(ChatActivity.this).load(mUserImage).placeholder(R.drawable.ic_user_).into(mProfileImage);
                        }
                    });
        }


        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (documentSnapshot.exists()){
                        if (documentSnapshot.contains("name")){
                            MyName=documentSnapshot.getString("name");
                        }
                        if (documentSnapshot.contains("device_token")){
                            MyDeviceToken=documentSnapshot.getString("device_token");
                        }
                        if (documentSnapshot.contains("image")){
                            MyImageLink=documentSnapshot.getString("image");
                        }
                        if (documentSnapshot.contains("status")){
                            MyStatus=documentSnapshot.getString("status");
                        }
                    }
                }
            }
        });

        mFireStoreDatabase.collection("Users").document(mChatUserKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {
                if(dataSnapshot!=null){
                    if (dataSnapshot.exists()){

                        setUpFriendData(dataSnapshot);

                    }else {
                        IsFriend_online=false;
                        mLastSeenview.setText("Not Active");
                    }
                }

            }
        });



        MessageList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });


        ChatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WriteChat.length()>0){
                    FriendCurrentMessage="";
                    sendMessage();
                  if (!IsFriend_online){
                      if (FriendCurrentMessage.length()==0){
                          if (WriteChat.getText().length()>0){
                              FriendCurrentMessage=WriteChat.getText().toString();
                          }else {
                              FriendCurrentMessage="Sent a new message";
                          }

                      }
                      SendMessageNotification();
                  }
                }
            }


        });

        SendMediaFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckPermission()){
                    IsAttachingImage=true;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.SELECT_PICTURE)),SELECT_IMAGE);
                }

                /*CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ChatActivity.this);*/
            }
        });



        if (!Const.IS_MESSAGE_SHARE_COMPLETE){
            String CallingFrom=getIntent().getStringExtra("calling_from");
            if(CallingFrom!=null && CallingFrom.equals("ALL_FRIEND_LIST")){
                String ShareType=getIntent().getStringExtra("type");
                String Message=getIntent().getStringExtra("message");

              if (ShareType!=null){
                  if (ShareType.equals("text")){
                      WriteChat.setText(Message);
                      Const.IS_MESSAGE_SHARE_COMPLETE=true;
                  }else if (ShareType.equals("image")) {
                      mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT));
                      mProgressDialog.show();
                      try {
                          String imageUri=getFilePath(ChatActivity.this,Uri.parse(Message));
                          uploadImage(Uri.parse(imageUri));
                      }catch (Exception e){
                          Log.e("dfjkfjskf","upload image exception "+e.getMessage());
                          uploadImageDuringException(Uri.parse(Message));
                      }
                  }
              }else {
                  Const.IS_MESSAGE_SHARE_COMPLETE=true;
              }

            }
        }


        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(CHAT_NOTIFICATION_ID);

        //LoadOldMessages();
        setUnreadMessagesToZero();
        //LoadNewMessages();

        InitialLoadMessages();

        //LoadOldMessages();

    }

    private void setUpFriendData(DocumentSnapshot dataSnapshot){

        if (dataSnapshot.contains("online")){
            boolean online=dataSnapshot.getBoolean("online");
            if(online){
                mLastSeenview.setText("Online");
                IsFriend_online=true;
            }else {

                IsFriend_online=false;

                long lastTime;

                try{
                    Date date = (Date) dataSnapshot.get("last_active");
                    lastTime=date.getTime();
                }catch (Exception e){
                    lastTime=dataSnapshot.getLong("last_active").longValue();
                }

                GetTimeAgo getTimeAgo=new GetTimeAgo();
                String lastSeen=getTimeAgo.getTimeAgo(lastTime,getApplicationContext());
                mLastSeenview.setText(lastSeen);
            }
        }else {
            IsFriend_online=false;

            mLastSeenview.setText("Not Active");

        }

        Log.e("sdkfjksf","setting name "+dataSnapshot.getString("name"));

        if (dataSnapshot.contains("name")){
            if (dataSnapshot.getString("name").length()>0){

                mTitleview.setText(dataSnapshot.getString("name"));
                //actionBar.setTitle(dataSnapshot.getString("name"));
            }
        }

        if (dataSnapshot.contains("device_token")){
            FriendDevicetoken=dataSnapshot.getString("device_token");
        }
        if (dataSnapshot.contains("status")){
            FriendStatus=dataSnapshot.getString("status");
        }
        if (dataSnapshot.contains("image")){
            mUserImage=dataSnapshot.getString("image");
            Picasso.with(ChatActivity.this).load(mUserImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_user_).into(mProfileImage,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.e("onerrorSection", "this is success");
                        }

                        @Override
                        public void onError() {
                            Log.e("onerrorSection", "this is error");
                            Picasso.with(ChatActivity.this).load(mUserImage).placeholder(R.drawable.ic_user_).into(mProfileImage);
                        }
                    });
        }

    }

    private void ResetNotificationParams() {
        SharedPreferences prefs = this.getSharedPreferences(NOTIFICATION_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("messages_notification","");
        editor.putInt("total_messages",0);
        editor.putString("previous_user_notification_id","");
        editor.putBoolean("Is_more_then_two_user_sending",false);
        editor.apply();
    }


    private void InitialLoadMessages() {
        mFireStoreDatabase.collection("messages").document(mCurrentUserId).collection(mChatUserKey).
                orderBy("time", Query.Direction.DESCENDING).
                limit(TOTAL_ITEMS_TO_LOAD).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<Messages> currentMessageList =new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        if (!FriendMessageListId.contains(document.getId())){
                            FriendMessageListId.add(document.getId());
                            //Date date = (Date) document.get("time");

                                Log.e("jsdfskfdsd","this is time "+document.get("time"));


                                new_messagesList.add(new Messages(document.getId(),document.getString("message"),document.getString("type"),document.getString("from"),
                                        mChatUserKey,document.getString("sourceLocation"),document.getString("time_duration"),
                                        document.get("time"),true,document.getBoolean("is_downloaded")));
                            if (document.getString("type").equals("image")){
                                ImageUrlList.add(0,new ImageModel(document.getString("sourceLocation"),(new_messagesList.size()-1)+""));
                            }

                            ChatProgressBar.setVisibility(View.GONE);

                        }

                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ChatProgressBar.setVisibility(View.GONE);
                        }
                    }, 3000);

                    new_messagesList.addAll(currentMessageList);
                    new_messageAdapterClass.notifyDataSetChanged();
                    LoadNewMessages();

                    //new_messageAdapterClass.notifyDataSetChanged();
                    //MessageList.scrollToPosition(CurrentScrollPosition);
                    /*if (!IsMovingUp){
                        MessageList.scrollToPosition(0);
                    }*/
                    //MessageList.scrollToPosition(currentScrollPosition);
                }
            }
        });
    }

   /* private void setupEndlessListener(){
        MessageList.addOnScrollListener(new EndlessRecyclerViewScrollListener(mlinearLayoutManagerOld, 2) {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        IsMovingUp=true;
                        GetDataAt_a_time++;
                        LoadOldMessages();
                    }
                }, 150);
            }
        });
    }*/

    private void LoadNewMessages(){

        mFireStoreDatabase.collection("messages").document(mCurrentUserId).collection(mChatUserKey).orderBy("time", Query.Direction.DESCENDING).
               limit(1). addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        List<Messages> currentMessageList =new ArrayList<>();
                        if (documentSnapshots!=null){
                            if (!documentSnapshots.isEmpty()){
                                for (final DocumentSnapshot document : documentSnapshots) {

                                    if(document.get("time")!=null){
                                        if (!FriendMessageListId.contains(document.getId())){
                                            FriendMessageListId.add(document.getId());
                                            //Date date = (java.util.Date) document.getLong("time").longValue();
                                            Log.e("jsdfskfdsd","this is time "+document.get("time"));

                                                    new_messagesList.add(0,new Messages(document.getId(),document.getString("message"),document.getString("type"),document.getString("from"),
                                                            mChatUserKey,document.getString("sourceLocation"),document.getString("time_duration"),
                                                            document.get("time"),true,document.getBoolean("is_downloaded")));

                                            Log.e("dlkfsjksdsf","how many times new message loaded");

                                            if (document.getString("type").equals("image")){

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        RefreshImageList(document.getString("sourceLocation"));
                                                    }
                                                },50);

                                            }else {
                                                //Log.e("sdfjksdjfd","this is current item position "+currentItemPosition);
                                                RefreshImageList();
                                                //currentItemPosition++;
                                            }

                                            if (isImOnline){
                                                setUnreadMessagesToZero();
                                            }

                                        }
                                    }

                                }
                                new_messageAdapterClass.notifyDataSetChanged();
                                MessageList.scrollToPosition(0);
                                //new_messagesList.addAll(currentMessageList);
                            }
                        }

                    }
                });
        //setupEndlessListener();
    }

    private void RefreshImageList() {
        List<ImageModel> ImagedataList=new ArrayList<>();
        for(int i=ImageUrlList.size()-1;i>=0;i--){
            ImageModel obj=ImageUrlList.get(i);
            int imagePosition=Integer.parseInt(obj.getName());
            Log.e("dfjskfjsf","current images name "+imagePosition+" size of list is "+ImageUrlList.size()+" value of i is "+i);
            ImagedataList.add(0,new ImageModel(obj.getUrl(),(imagePosition+1)+""));

        }

        ImageUrlList.clear();
        ImageUrlList.addAll(ImagedataList);
    }

    private void RefreshImageList(String sourceLocation) {
    List<ImageModel> ImagedataList=new ArrayList<>();
        Log.e("dlkfsjksdsf","how many times RefreshImageList called");
        ImagedataList.add(new ImageModel(sourceLocation,"0"));

        for(int i=ImageUrlList.size()-1;i>=0;i--){
            ImageModel obj=ImageUrlList.get(i);
            int imagePosition=Integer.parseInt(obj.getName());
            Log.e("dfjskfjsf","current images name "+imagePosition+" size of list is "+ImageUrlList.size()+" value of i is "+i);
            ImagedataList.add(0,new ImageModel(obj.getUrl(),(imagePosition+1)+""));

        }
        ///currentItemPosition=0;
        ImageUrlList.clear();
        ImageUrlList.addAll(ImagedataList);

    }

    private void LoadOldMessages(final int CurrentScrollPosition){
        mFireStoreDatabase.collection("messages").document(mCurrentUserId).collection(mChatUserKey).orderBy("time", Query.Direction.DESCENDING).
                limit(GetDataAt_a_time*TOTAL_ITEMS_TO_LOAD).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    final List<Messages> currentMessageList =new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        if (!FriendMessageListId.contains(document.getId())){
                            FriendMessageListId.add(document.getId());
                            currentMessageList.add(new Messages(document.getId(),document.getString("message"),document.getString("type"),document.getString("from"),
                                    mChatUserKey,document.getString("sourceLocation"),document.getString("time_duration"),
                                    document.get("time"),true,document.getBoolean("is_downloaded")));
                            if (document.getString("type").equals("image")){
                                ImageUrlList.add(0,new ImageModel(document.getString("sourceLocation"),
                                        (new_messagesList.size()-2+currentMessageList.size())+""));
                            }

                        }

                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new_messagesList.remove(CurrentScrollPosition - 1);
                            new_messageAdapterClass.notifyItemRemoved(CurrentScrollPosition);
                            new_messagesList.addAll(currentMessageList);
                            if (currentMessageList.size()==0){
                                dontLoadMoreData=true;

                            }
                            new_messageAdapterClass.setLoaded();
                            new_messageAdapterClass.notifyDataSetChanged();
                        }
                    },2000);

                    //MessageList.scrollToPosition(CurrentScrollPosition);
                    /*if (!IsMovingUp){
                        MessageList.scrollToPosition(0);
                    }*/
                    //MessageList.scrollToPosition(currentScrollPosition);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case Const.PICK_FROM_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    IsAttachingImage=true;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.SELECT_PICTURE)),SELECT_IMAGE);

                } else {
                    Toast.makeText(ChatActivity.this,getString(R.string.PLEASE_GIVE_PERMISSIONS_FOR_IMAGE_MESSAGE),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private boolean CheckPermission() {
        boolean grantPermission;
        if (ActivityCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Const.PICK_FROM_GALLERY);
            grantPermission=false;
        } else {
            grantPermission=true;
        }
        return grantPermission;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                if (data != null)
                {
                    mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT));
                    mProgressDialog.show();
                        try {
                            String fileName=data.getData().toString();
                            if (fileName.endsWith(".3gp")||fileName.endsWith(".mp4")||
                                    fileName.endsWith(".wav")||fileName.endsWith(".mp3")||
                                    fileName.endsWith(".midi")||fileName.endsWith(".amr")){
                                mProgressDialog.dismiss();
                                Toast.makeText(ChatActivity.this,getString(R.string.ONLY_IMAGE_FILE_IS_SUPPORTED),Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Log.e("ldjfkfjsl","this is data before "+data.getData());
                            String imageUri=getFilePath(ChatActivity.this,data.getData());
                            Log.e("ldjfkfjsl","image uri is "+imageUri);
                            uploadImage(Uri.parse(imageUri));

                            if (!IsFriend_online){
                                FriendCurrentMessage="Sent an image";
                                SendMessageNotification();
                            }

                        }catch (Exception e){
                            Log.e("dfjkfjskf","upload image exception "+e.getMessage());
                            uploadImageDuringException(data.getData());
                            if (!IsFriend_online){
                                FriendCurrentMessage="Sent an image";
                                SendMessageNotification();
                            }
                        }


                }
            } else if (resultCode == Activity.RESULT_CANCELED)
            {
                Toast.makeText(ChatActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                Log.e("ldjfkfjsl","isExternalStorageDocument is true");
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                Log.e("ldjfkfjsl","isDownloadsDocument is true");
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                Log.e("ldjfkfjsl","final download url "+uri);
            } else if (isMediaDocument(uri)) {
                Log.e("ldjfkfjsl","isMediaDocument is true");
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


   /* public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    *//**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     *//*
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    *//**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     *//*
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    *//**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     *//*
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    *//**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     *//*
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    */



    byte[] user_byte;
    private void uploadImage(Uri imageUri) {

        File bitmapPath=new File(imageUri.getPath());

        Log.e("dfjkfjskf","upload image in compressed format");

        try{
            Bitmap storyBitmap = new Compressor(this).setMaxWidth(300).setMaxHeight(300).setQuality(50).
                    compressToBitmap(bitmapPath);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            storyBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            user_byte= baos.toByteArray();

        }catch (IOException e){

        }
        String CurrentUId=mAuth.getUid();
        final StorageReference thumb_filePath=mImageStorageRef.child("Chat_images").child("picture_messages").child(CurrentUId+"_"+
                getRandomString(20)+".jpg");

        UploadTask uploadTask = thumb_filePath.putBytes(user_byte);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
               String DownloadImageUrl=task.getResult().getDownloadUrl().toString();
                if(task.isSuccessful()){
                    SendImageMessage(DownloadImageUrl);
                    Log.e("dfjkfjskf","this is image download url "+DownloadImageUrl);
                }
            }
        });
    }

    private void uploadImageDuringException(Uri filePath) {

        Log.e("dfjkfjskf","upload image in original format");
        if(filePath != null)
        {


            StorageReference ref = storageReference.child("Chat_images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.e("dfjkfjskf","download url don't compress image "+taskSnapshot.getDownloadUrl().toString());
                            SendImageMessage(taskSnapshot.getDownloadUrl().toString());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss();
                            Toast.makeText(ChatActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    /*.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    })*/;
        }
    }


/*

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        public void run() {
            checkGlobalVariable();
        }
    };
    private void checkGlobalVariable(){
        if (mBooleanIsPressed){
            if (checkPermission()){
                startRecording();
            }else {
                requestPermission();
            }
            //Toast.makeText(ChatActivity.this,"this is button check global",Toast.LENGTH_LONG).show();
        }
    }
*/


    protected String getRandomString(int length) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ResetEveryThing(){
        WriteChat.setVisibility(View.VISIBLE);
        /*AudioTimeText.setVisibility(View.GONE);
        AudioRecordMessageLayout.setVisibility(View.GONE);
        AudioUploadProgress.setVisibility(View.GONE);*/
    }


    /*private void UploadAudio(){

        final String AudioName=""+System.currentTimeMillis();
        StorageReference filePath=mAudioStorage.child("AudioChatMessages").child(AudioName+".3gp");
        Uri audioUri=Uri.fromFile(new File(mFileName));
        filePath.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                WriteChat.setVisibility(View.VISIBLE);
                AudioTimeText.setVisibility(View.GONE);
                AudioRecordMessageLayout.setVisibility(View.GONE);
                AudioUploadProgress.setVisibility(View.GONE);
                sendMediaMessage(false,"audio",AudioName,AudioTimeText.getText().toString());
            }
        });
    }*/



    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }


/*
    private void startRecording() {
        Log.e("kdhdjgddds","this is start recording");
        try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setAudioSamplingRate(16000);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("dfsjkfsk", "prepare() failed");
            }

            mRecorder.start();
            RecordingStarted=true;
        }catch (Exception e){
            e.printStackTrace();
            RecordingStarted=false;
        }

    }

    */
    public static final int RequestPermissionCode = 1;
    private void requestPermission() {
        ActivityCompat.requestPermissions(ChatActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }


/*

    private void stopRecording() {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            UploadAudio();

    }
*/



   /* private void setupEndlessListener(){
        MessageList.addOnScrollListener(new EndlessRecyclerViewScrollListener(mlinearLayoutManagerOld, 2) {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        *//*GetDataAt_a_time++;
                        LoadOldMessages();*//*
                    }
                }, 150);
            }
        });
    }*/


    private void sendMediaMessage(final boolean isDownloaded,final String audio, final String downloadUrl, final String timeDuration) {
        DocumentReference FriendRef = mFireStoreDatabase.collection("FriendsData").document(mChatUserKey).collection("FriendList").document(mAuth.getUid());


        FriendRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        if (doc.contains("unread_message")){
                            String UnreadMessage_number=doc.getString("unread_message");
                            int UnreadMessage=Integer.parseInt(UnreadMessage_number);
                            setUnreadMessages(isDownloaded,++UnreadMessage,"New audio message",audio,downloadUrl,timeDuration) ;

                        }else {
                            setUnreadMessages(isDownloaded,1,"New audio message",audio,downloadUrl,timeDuration);
                        }
                    }

                }

            }
        });
    }



    private void sendMessage() {
        final String message=WriteChat.getText().toString();
        FriendCurrentMessage=message;
        WriteChat.setText("");

        if(!TextUtils.isEmpty(message)){

            DocumentReference FriendRef = mFireStoreDatabase.collection("FriendsData").document(mChatUserKey).collection("FriendList").document(mAuth.getUid());

            FriendRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()){
                            if (doc.contains("unread_message")){
                                String UnreadMessage_number=doc.getString("unread_message");
                                int UnreadMessage=Integer.parseInt(UnreadMessage_number);
                                setUnreadMessages(false,++UnreadMessage,message,"text","","") ;

                            }else {
                                setUnreadMessages(false,1,message,"text","","");
                            }
                        }

                    }

                }
            });


        }
    }

    private void setUnreadMessagesToZero(){
        Log.e("fjksjfksjf","set unread message to zero");
        DocumentReference FriendRef = mFireStoreDatabase.collection("FriendsData").document(mAuth.getUid()).collection("FriendList").document(mChatUserKey);
        final Map messageMap=new HashMap();
        messageMap.put("unread_message","0");
        FriendRef.set(messageMap,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Log.e("fjksjfksjf","set unread message to zero success");
                return;
            }
        });
    }

    private void SendImageMessage(final String image_downloadUrl){
        DocumentReference FriendRef = mFireStoreDatabase.collection("FriendsData").document(mChatUserKey).collection("FriendList").document(mAuth.getUid());


        FriendRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        if (doc.contains("unread_message")){
                            String UnreadMessage_number=doc.getString("unread_message");
                            int UnreadMessage=Integer.parseInt(UnreadMessage_number);
                            setUnreadMessages(false,++UnreadMessage,"Send New Image","image",image_downloadUrl,"") ;

                        }else {
                            setUnreadMessages(false,1,"Send New Image","image",image_downloadUrl,"");
                        }
                    }

                }

            }
        });
    }

    private void SendMessageNotification() {

            final Map messageMap=new HashMap();
            messageMap.put("my_name",MyName);

            if (FriendDevicetoken!=null&&FriendDevicetoken.length()>5){
                messageMap.put("friend_device_token",FriendDevicetoken);
            }

            messageMap.put("friend_message",FriendCurrentMessage);

            mFireStoreDatabase.collection("Message_notification").document(mCurrentUserId).collection("Notification_list").document(mChatUserKey)
                    .set(messageMap,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Log.e("ksjfsflk","thiosdfksj fdsjkthis is friend status "+IsFriend_online+" and this is friend message "+FriendCurrentMessage);
                        return;
                    }
                }
            });

    }


    private void setUnreadMessages(final boolean isDownloaded,final int unread_messages, final String message, final String messageType, final String SourceLocation, final String timeDuration){
        DocumentReference FriendRef = mFireStoreDatabase.collection("FriendsData").document(mChatUserKey).collection("FriendList").document(mAuth.getUid());
        final Map messageMap=new HashMap();
        final long timeInMillis = System.currentTimeMillis();

        messageMap.put("unread_message",""+unread_messages);
        messageMap.put("last_message",message);
        messageMap.put("last_message_sent_at", FieldValue.serverTimestamp());
        messageMap.put("time_duration",timeDuration);
        messageMap.put("friend_name",MyName);
        messageMap.put("friend_status",MyStatus);
        messageMap.put("friend_image",MyImageLink);
        if (MyDeviceToken!=null&&MyDeviceToken.length()>5){
            messageMap.put("friend_device_token",MyDeviceToken);

        }

        FriendRef.set(messageMap,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){

                    Log.e("hfsjfds","message length "+message.length()+" unread message "+unread_messages);
                    //DocumentReference user_message_push=mFireStoreDatabase.collection("messages").document(mCurrentUserId).collection(mChatUserKey).document();


                    final DocumentReference FriendRef = mFireStoreDatabase.collection("FriendsData").document(mAuth.getUid()).collection("FriendList").document(mChatUserKey);

                    final Map messageLOADMap=new HashMap();
                    messageLOADMap.put("last_message",message);
                    messageLOADMap.put("last_message_sent_at",FieldValue.serverTimestamp());
                    messageLOADMap.put("friend_name",mTitleview.getText().toString());
                    if (FriendDevicetoken!=null&&FriendDevicetoken.length()>5){
                        messageLOADMap.put("friend_device_token",FriendDevicetoken);
                    }else {
                            mFireStoreDatabase.collection("Users").document(mChatUserKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        DocumentSnapshot documentSnapshot=task.getResult();
                                        if (documentSnapshot.exists()){
                                            setUpFriendData(documentSnapshot);

                                        }
                                    }
                                }
                            });
                    }

                    messageLOADMap.put("friend_status",FriendStatus);
                    messageLOADMap.put("friend_image",mUserImage);
                    FriendRef.set(messageLOADMap,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()){
                                    //final long push_id=System.currentTimeMillis();

                                    final Map messageMap=new HashMap();
                                    messageMap.put("message",message);
                                    messageMap.put("type",messageType);
                                    messageMap.put("time", FieldValue.serverTimestamp());
                                    messageMap.put("from",mCurrentUserId);
                                    messageMap.put("is_downloaded",isDownloaded);
                                    FriendCurrentMessage=message;
                   /* if (messageType.equals("audio")){
                        messageMap.put("sourceLocation",SourceLocation);
                        messageMap.put("time_duration",timeDuration);
                    }*/
                               if (messageType.equals("image")){
                                   messageMap.put("sourceLocation",SourceLocation);
                               }

                                    mFireStoreDatabase.collection("messages").document(mCurrentUserId).collection(mChatUserKey).document(""+timeInMillis).
                                            set(messageMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()){
                                                mFireStoreDatabase.collection("messages").document(mChatUserKey).collection(mCurrentUserId).document(""+timeInMillis).
                                                        set(messageMap,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        if (task.isSuccessful()){
                                                            Const.IS_MESSAGE_SHARE_COMPLETE=true;
                                                            mProgressDialog.dismiss();
                                                            return;
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });

                                }
                        }
                    });

                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (!IsAttachingImage){
            final long timeInMillis = System.currentTimeMillis();
            Map userStatus=new HashMap();
            userStatus.put("online",false);
            userStatus.put("last_active",FieldValue.serverTimestamp());
            mFireStoreDatabase.collection("Users").document(mAuth.getUid()).set(userStatus,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Log.e("onfjskdf","offline set is called");
                }
            });
        }
        Log.e("onfjskdf","onPause is called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("onfjskdf","onStop is called");
    }

    @Override
    public void onBackPressed() {

        isImOnline=false;
        boolean IsFromNotification=getIntent().getBooleanExtra("from_notification",false);
        if (IsFromNotification){

            Intent Main=new Intent(ChatActivity.this, MainActivity.class);
            Main.putExtra("fromChat",true);
            ChatActivity.this.finish();
            startActivity(Main);
        }else {
            super.onBackPressed();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onfjskdf","ondestory is called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        isImOnline=true;
        if (!IsAttachingImage){
            Map userStatus=new HashMap();
            userStatus.put("online",true);
            mFireStoreDatabase.collection("Users").document(mAuth.getUid()).set(userStatus,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Log.e("onfjskdf","online set is called");
                }
            });
        }
        IsAttachingImage=false;
        Log.e("onfjskdf","onResume is called");
    }
}
