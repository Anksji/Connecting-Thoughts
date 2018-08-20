package com.rsons.application.connecting_thoughts.UsersActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.rsons.application.connecting_thoughts.ChatActivityAndClasses.ChatActivity;
import com.rsons.application.connecting_thoughts.Fetch_data.SectionListDataAdapter;
import com.rsons.application.connecting_thoughts.Fetch_data.SingleStoryModel;
import com.rsons.application.connecting_thoughts.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ankit on 2/16/2018.
 */

public class AuthorProfileActivity extends AppCompatActivity{

    private FirebaseFirestore mFireStoreDatabase;

    //String Author_id;

    String AuthorVisitedName;

    private DocumentReference mUserAuthorRef;
    private FirebaseUser mCurrentUser;

    private FirebaseAuth mAuth;

    private CollectionReference FriendRequestDatabaseRef;
    private CollectionReference FriendsDatabaseRef;
    //private CollectionReference NotificationDatabaseRef;
    private  CollectionReference FriendNotificationDatabaseRef;

    private ImageView UserImage,UserImageBackground;
    private TextView UserName;
    private TextView AboutAuthor;
    private String AuthorName,AuthorImgUrl;

    private TextView Send_friendRequest,Decline_friendRequest;
    ProgressDialog mProgressDialog;

    private RecyclerView AuthorSuggestionList;
    SectionListDataAdapter AuthorSuggestionListAdapter;
    private ArrayList<SingleStoryModel> SuggestionStoryList=new ArrayList<>();
    private int mCurrent_state=0;
    //**** mCurrent_state=0 means No Friends mCurrent_state=1 means Friend Request Sent
    //**** mCurrent_state=2 means Recieved Friend Request mCurrent_state=3 means they are friends;
    String Profile_visit_id;
    RelativeLayout AuthorWritingLayout;
    String Prof_Visit_name;
    ImageView GoBack,MoreOption;
    LinearLayout NewButtonLayout,NewConnectionRequest, Inconnection_Layout,ConnectLayoutAndMore;
    ImageView AddUserImage;
    LinearLayout AcceptRequest,RejectRequest,MassageLayout;
    private int IsRequestSentOrRecieve=5;
    private  String AuthorDeviceToken;
    private  String AuthorStatus;
    private  String CurrentUserName;
    private  String CurrentUserImage;
    private  String CurrentUserDeviceToken;
    private  String CurrentUserStatus;
    private ImageView LoadMoreAuthorCreation;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authors_profile_activity);

        LoadMoreAuthorCreation= (ImageView) findViewById(R.id.btnMore);
        UserImage= (ImageView) findViewById(R.id.request_profile_image);
        UserImageBackground= (ImageView) findViewById(R.id.user_image_background);
        UserName= (TextView) findViewById(R.id.request_user_name);
        AboutAuthor= (TextView) findViewById(R.id.request_user_status);
        GoBack= (ImageView) findViewById(R.id.go_back);
        MoreOption= (ImageView) findViewById(R.id.more_options);
        AuthorWritingLayout= (RelativeLayout) findViewById(R.id.writings);
        NewButtonLayout = (LinearLayout) findViewById(R.id.new_button_layout);
        NewConnectionRequest = (LinearLayout) findViewById(R.id.new_request_arrive);
        AddUserImage = (ImageView) findViewById(R.id.add_user_image);
        Inconnection_Layout = (LinearLayout) findViewById(R.id.inconnection_layout);
        AcceptRequest = (LinearLayout) findViewById(R.id.accept_request);
        RejectRequest = (LinearLayout) findViewById(R.id.reject_request);
        MassageLayout = (LinearLayout) findViewById(R.id.message_layout);
        ConnectLayoutAndMore = (LinearLayout) findViewById(R.id.connect_layout);

        Send_friendRequest= (TextView) findViewById(R.id.send_frnd_request);
        Decline_friendRequest= (TextView) findViewById(R.id.decline_frnd_request);
        AuthorSuggestionList = (RecyclerView) findViewById(R.id.recycler_view_list);


        mProgressDialog=new ProgressDialog(this);
        /*mProgressDialog.setTitle(getString(R.string.PLEASE_WAIT));*/
        mProgressDialog.setMessage(getString(R.string.LOADING_USER_DATA));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


        mFireStoreDatabase=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);

        mAuth = FirebaseAuth.getInstance();

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        Profile_visit_id = getIntent().getStringExtra("user_key");
        //=mCurrentUser.getUid();
        Log.e("sdfss","Profile visit id "+Profile_visit_id);
        Log.e("sdfss","Current user id is "+mCurrentUser.getUid());
        if(Profile_visit_id.equals(mCurrentUser.getUid())){
            NewButtonLayout.setVisibility(View.GONE);
            /*Send_friendRequest.setVisibility(View.GONE);
            Decline_friendRequest.setVisibility(View.GONE);*/
        }

        mUserAuthorRef = mFireStoreDatabase.collection("Users").document(Profile_visit_id);

        FriendRequestDatabaseRef=mFireStoreDatabase.collection("FriendRequestData");
        FriendsDatabaseRef=mFireStoreDatabase.collection("FriendsData");
        FriendNotificationDatabaseRef=mFireStoreDatabase.collection("NotificationData");

        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (documentSnapshot.exists()){
                        if (documentSnapshot.contains("name")){
                            CurrentUserName=documentSnapshot.getString("name");
                        }
                        if (documentSnapshot.contains("device_token")){
                            CurrentUserDeviceToken=documentSnapshot.getString("device_token");
                        }
                        if (documentSnapshot.contains("image")){
                            CurrentUserImage=documentSnapshot.getString("image");

                        }
                        if (documentSnapshot.contains("status")){
                            CurrentUserStatus=documentSnapshot.getString("status");
                        }
                    }
                }
            }
        });


        LoadMoreAuthorCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent AuthorCreation=new Intent(AuthorProfileActivity.this,LoadAuthorWritings.class);
                AuthorCreation.putExtra("user_name",AuthorName);
                AuthorCreation.putExtra("author_id",Profile_visit_id);
                startActivity(AuthorCreation);
            }
        });

        GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        MoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(MoreOption);
            }
        });
        MassageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ChatMessages=new Intent(AuthorProfileActivity.this, ChatActivity.class);
                ChatMessages.putExtra("user_key",Profile_visit_id);
                ChatMessages.putExtra("user_name",AuthorName);
                ChatMessages.putExtra("user_img",AuthorImgUrl);
                ChatMessages.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ChatMessages);
            }
        });


        FriendsDatabaseRef.document(mCurrentUser.getUid()).collection("FriendList").document(Profile_visit_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {
                if (dataSnapshot != null) {
                    if (dataSnapshot.exists()){
                       // mProgressDialog.dismiss();
                        mCurrent_state=3;
                        AddUserImage.setImageResource(R.drawable.ic_delete_male_user);
                        AddUserImage.setColorFilter(ContextCompat.getColor(AuthorProfileActivity.this, R.color.grey_500),
                                android.graphics.PorterDuff.Mode.SRC_IN);
                        Send_friendRequest.setText(getString(R.string.disconnect));
                        Inconnection_Layout.setVisibility(View.VISIBLE);
                        NewConnectionRequest.setVisibility(View.GONE);
                        //Decline_friendRequest.setVisibility(View.GONE);
                        MassageLayout.setVisibility(View.VISIBLE);
                    }else {
                        CheckFriendRequestStatus();
                        /*if (IsRequestSentOrRecieve>2){
                            Send_friendRequest.setText(getString(R.string.CONNECT));
                        }
                        Send_friendRequest.setText(getString(R.string.CONNECT));
                        MassageLayout.setVisibility(View.GONE);
                        Inconnection_Layout.setVisibility(View.GONE);*/
                    }
                }
            }
        });

        mUserAuthorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    if (task.getResult().exists()) {
                        final DocumentSnapshot dataSnapshot = task.getResult();

                        if (dataSnapshot.contains("name")) {
                            AuthorName = dataSnapshot.get("name").toString();
                            UserName.setText(AuthorName);
                        }
                        if (dataSnapshot.contains("device_token")){
                            AuthorDeviceToken=dataSnapshot.getString("device_token");
                        }else {
                            Toast.makeText(AuthorProfileActivity.this,"Something went wrong with author device token",Toast.LENGTH_SHORT).show();
                        }

                        if (dataSnapshot.contains("status")){
                            AuthorStatus=dataSnapshot.getString("status");
                        }


                        if (dataSnapshot.contains("image")) {
                            AuthorImgUrl=dataSnapshot.get("image").toString();
                            Picasso.with(AuthorProfileActivity.this).load(AuthorImgUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_user_).into(UserImage,
                                    new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.e("onerrorSection", "this is success");
                                        }

                                        @Override
                                        public void onError() {
                                            Log.e("onerrorSection", "this is error");
                                            Picasso.with(AuthorProfileActivity.this).load(AuthorImgUrl).placeholder(R.drawable.ic_user_).into(UserImage);
                                        }
                                    });

                            Picasso.with(AuthorProfileActivity.this).load(AuthorImgUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.card_defalt_back_cover).into(UserImageBackground,
                                    new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.e("onerrorSection", "this is success");
                                        }

                                        @Override
                                        public void onError() {
                                            Log.e("onerrorSection", "this is error");
                                            Picasso.with(AuthorProfileActivity.this).load(AuthorImgUrl).placeholder(R.drawable.card_defalt_back_cover).into(UserImageBackground);
                                        }
                                    });
                        }

                        if (dataSnapshot.contains("status")){
                            AboutAuthor.setText(dataSnapshot.get("status").toString());
                        }

                    }
                    mProgressDialog.dismiss();
                }

            }
        });

                RejectRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IsRequestSentOrRecieve=3;
                        NewConnectionRequest.setVisibility(View.GONE);
                        mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT));
                        mProgressDialog.show();
                        DecreaseNotificationCounter_by_one();
                        DeleteCurrentFriendRequestFromList(Profile_visit_id);
                        RejectRequest.setEnabled(false);
                        Log.e("kkjkcxcvx","Reject request clicked");
                        DocumentReference docRef = FriendRequestDatabaseRef.document(mCurrentUser.getUid()).collection("RequestList").document(Profile_visit_id);

                        docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    DocumentReference docRef = FriendRequestDatabaseRef.document(Profile_visit_id).collection("RequestList").document(mCurrentUser.getUid());
                                    docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                Log.e("kkjkcxcvx","hai this is calling delete notification on reject request");
                                                DocumentReference docRef = FriendNotificationDatabaseRef.document(mCurrentUser.getUid()).collection("RequestAndStory").document(Profile_visit_id);

                                                docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            mProgressDialog.dismiss();
                                                            AddUserImage.setImageResource(R.drawable.ic_add_male_user);
                                                            AddUserImage.setColorFilter(ContextCompat.getColor(AuthorProfileActivity.this, R.color.grey_500),
                                                                    android.graphics.PorterDuff.Mode.SRC_IN);

                                                            Send_friendRequest.setTextColor(Color.parseColor("#9E9E9E"));
                                                            Send_friendRequest.setText(getString(R.string.CONNECT));
                                                            Inconnection_Layout.setVisibility(View.GONE);
                                                            MassageLayout.setVisibility(View.GONE);
                                                            mCurrent_state = 0;
                                                            ConnectLayoutAndMore.setEnabled(true);
                                                            Send_friendRequest.setEnabled(true);
                                                            //Decline_friendRequest.setVisibility(View.GONE);
                                                        } else {
                                                            Log.w("fsfs", "signInWithEmail:failed", task.getException());
                                                            NewConnectionRequest.setVisibility(View.VISIBLE);
                                                            RejectRequest.setEnabled(true);
                                                           ErrorMessageHandle(task);
                                                        }
                                                    }
                                                });
                                            }else {
                                                ErrorMessageHandle(task);
                                            }
                                        }
                                    });
                                }else {
                                    ErrorMessageHandle(task);
                                }
                            }
                        });

                    }
                });


                AcceptRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IsRequestSentOrRecieve=2;
                        NewConnectionRequest.setVisibility(View.GONE);
                        AcceptRequest.setEnabled(false);
                        mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT));
                        mProgressDialog.show();
                        DecreaseNotificationCounter_by_one();
                        DeleteCurrentFriendRequestFromList(Profile_visit_id);
                            DocumentReference docRef = FriendsDatabaseRef.document(mCurrentUser.getUid()).collection("FriendList")
                                    .document(Profile_visit_id);
                        final long timeInMillis = System.currentTimeMillis();
                            //Map<String, Object> docData = new HashMap<>();
                            Map<String, Object> nestedData = new HashMap<>();
                            nestedData.put("date", timeInMillis);
                            nestedData.put("friend_name",UserName.getText().toString());
                            nestedData.put("friend_status",AuthorStatus);
                            nestedData.put("friend_image",AuthorImgUrl);
                            nestedData.put("friend_device_token",AuthorDeviceToken);
                            //docData.put(Profile_visit_id, nestedData);
                            docRef.set(nestedData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        final long timeInMillis = System.currentTimeMillis();
                                        Map<String, Object> nestedData = new HashMap<>();
                                        nestedData.put("date", timeInMillis);
                                        nestedData.put("friend_name",CurrentUserName);
                                        nestedData.put("friend_device_token",CurrentUserDeviceToken);
                                        nestedData.put("friend_status",CurrentUserStatus);
                                        nestedData.put("friend_image",CurrentUserImage);
                                        DocumentReference docRef = FriendsDatabaseRef.document(Profile_visit_id).collection("FriendList")
                                                .document(mCurrentUser.getUid());
                                        docRef.set(nestedData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){

                                                    FriendNotificationDatabaseRef.document(mCurrentUser.getUid()).collection("RequestAndStory").document(Profile_visit_id)
                                                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                mProgressDialog.dismiss();
                                                                AcceptRequest.setEnabled(true);
                                                                NewConnectionRequest.setVisibility(View.GONE);
                                                                AddUserImage.setImageResource(R.drawable.ic_delete_male_user);
                                                                AddUserImage.setColorFilter(ContextCompat.getColor(AuthorProfileActivity.this, R.color.grey_500),
                                                                        android.graphics.PorterDuff.Mode.SRC_IN);
                                                                Log.e("skjfsdksdf","line no. 496");
                                                                Send_friendRequest.setText(getString(R.string.disconnect));
                                                                Inconnection_Layout.setVisibility(View.VISIBLE);
                                                                MassageLayout.setVisibility(View.VISIBLE);

                                                                //Decline_friendRequest.setVisibility(View.GONE);
                                                                mCurrent_state = 3;
                                                                ConnectLayoutAndMore.setEnabled(true);
                                                                Send_friendRequest.setEnabled(true);
                                                            } else {
                                                                AcceptRequest.setEnabled(true);
                                                                NewConnectionRequest.setVisibility(View.GONE);
                                                                ErrorMessageHandle(task);
                                                            }
                                                        }
                                                    });

                                                }else {
                                                    ErrorMessageHandle(task);
                                                }

                                            }
                                        });

                                    }else {
                                        ErrorMessageHandle(task);
                                    }
                                }
                            });
                    }
                });



        ConnectLayoutAndMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ConnectLayoutAndMore.setEnabled(false);
                            Send_friendRequest.setEnabled(false);
                            mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT));
                            mProgressDialog.show();
                            //****************----------No Friend State-----------**************//

                            if (mCurrent_state == 0) {
                                //Map<String, Object> docData = new HashMap<>();
                                final long timeInMillis = System.currentTimeMillis();
                                Map<String, Object> nestedData = new HashMap<>();
                                nestedData.put("request_type", "sent");
                                nestedData.put("request_timestamp",timeInMillis);
                               // docData.put(Profile_visit_id, nestedData);

                                FriendRequestDatabaseRef.document(mCurrentUser.getUid()).collection("RequestList").document(Profile_visit_id)
                                        .set(nestedData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            final long timeInMillis = System.currentTimeMillis();
                                            //Map<String, Object> docData = new HashMap<>();
                                            Map<String, Object> nestedData = new HashMap<>();
                                            nestedData.put("request_type", "recieved");
                                            nestedData.put("request_timestamp",timeInMillis);
                                            //docData.put(mCurrentUser.getUid(), nestedData);
                                            FriendRequestDatabaseRef.document(Profile_visit_id).collection("RequestList").document(mCurrentUser.getUid()).
                                                    set(nestedData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        final long timeInMillis = System.currentTimeMillis();
                                                        Map<String, Object> notificationData = new HashMap<>();
                                                        notificationData.put("from", mCurrentUser.getUid());
                                                        notificationData.put("notification_type", "request");
                                                        notificationData.put("sending_user_name",CurrentUserName);
                                                        notificationData.put("user_image",CurrentUserImage);
                                                        notificationData.put("device_token",AuthorDeviceToken);
                                                        notificationData.put("notification_timestamp",timeInMillis);

                                                        FriendNotificationDatabaseRef.document(Profile_visit_id).collection("RequestAndStory").document(mCurrentUser.getUid())
                                                                .set(notificationData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    mProgressDialog.dismiss();
                                                                    CheckNotificationCounter();
                                                                    Log.e("skjfsdksdf","line no. 368");
                                                                    /*AddUserImage.setImageResource(R.drawable.ic_delete_male_user);
                                                                    AddUserImage.setColorFilter(ContextCompat.getColor(AuthorProfileActivity.this, R.color.grey_500),
                                                                            android.graphics.PorterDuff.Mode.SRC_IN);
                                                                    Send_friendRequest.setText(getString(R.string.disconnect));
                                                                    mCurrent_state = 1;*/
                                                                    ConnectLayoutAndMore.setEnabled(true);
                                                                    Send_friendRequest.setEnabled(true);

                                                                } else {
                                                                    ErrorMessageHandle(task);
                                                                }
                                                            }
                                                        });
                                                    }else {
                                                        ErrorMessageHandle(task);
                                                    }
                                                }
                                            });

                                        }else {
                                            ErrorMessageHandle(task);
                                        }
                                    }
                                });
                            }

                            //****************----------Cancel Friend Request State-----------**************//

                            if (mCurrent_state == 1) {
                                DocumentReference docRef = FriendRequestDatabaseRef.document(mCurrentUser.getUid()).collection("RequestList").document(Profile_visit_id);

                                docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            DocumentReference docRef = FriendRequestDatabaseRef.document(Profile_visit_id).collection("RequestList").document(mCurrentUser.getUid());
                                            docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){

                                                        DocumentReference docRef = FriendNotificationDatabaseRef.document(Profile_visit_id).collection("RequestAndStory").document(mCurrentUser.getUid());

                                                        docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    mProgressDialog.dismiss();
                                                                    AddUserImage.setImageResource(R.drawable.ic_add_male_user);
                                                                    AddUserImage.setColorFilter(ContextCompat.getColor(AuthorProfileActivity.this, R.color.grey_500),
                                                                            android.graphics.PorterDuff.Mode.SRC_IN);

                                                                    Send_friendRequest.setTextColor(Color.parseColor("#9E9E9E"));
                                                                    Send_friendRequest.setText(getString(R.string.CONNECT));
                                                                    Inconnection_Layout.setVisibility(View.GONE);
                                                                    MassageLayout.setVisibility(View.GONE);
                                                                    mCurrent_state = 0;
                                                                    ConnectLayoutAndMore.setEnabled(true);
                                                                    Send_friendRequest.setEnabled(true);
                                                                    //Decline_friendRequest.setVisibility(View.GONE);
                                                                } else {
                                                                    ErrorMessageHandle(task);
                                                                    /*Log.w("fsfs", "signInWithEmail:failed", task.getException());
                                                                    mProgressDialog.hide();
                                                                    Toast.makeText(AuthorProfileActivity.this, R.string.PLEASE_TRY_AGAIN,
                                                                            Toast.LENGTH_SHORT).show();*/
                                                                }
                                                            }
                                                        });
                                                    }else {
                                                        ErrorMessageHandle(task);
                                                    }
                                                }
                                            });
                                        }else {
                                            ErrorMessageHandle(task);
                                        }
                                    }
                                });

                            }

                            //****************----------Accept Friend Request State-----------**************//



                            //****************----------Remove From Friend List-----------**************//

                            if (mCurrent_state == 3) {
                                IsRequestSentOrRecieve=3;

                                DocumentReference docRef = FriendRequestDatabaseRef.document(mCurrentUser.getUid()).collection("RequestList").document(Profile_visit_id);
                                docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            DocumentReference docRef = FriendRequestDatabaseRef.document(Profile_visit_id).collection("RequestList").document(mCurrentUser.getUid());
                                            docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    DocumentReference docRef = FriendsDatabaseRef.document(mCurrentUser.getUid()).collection("FriendList").document(Profile_visit_id);
                                                    docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                DocumentReference docRef = FriendsDatabaseRef.document(Profile_visit_id).collection("FriendList").document(mCurrentUser.getUid());
                                                                docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){
                                                                            mProgressDialog.dismiss();
                                                                            mCurrent_state = 0;
                                                                            AddUserImage.setImageResource(R.drawable.ic_add_male_user);
                                                                            AddUserImage.setColorFilter(ContextCompat.getColor(AuthorProfileActivity.this, R.color.grey_500),
                                                                                    android.graphics.PorterDuff.Mode.SRC_IN);
                                                                            Log.e("skjfsdksdf","line no. 547");
                                                                            Send_friendRequest.setText(getString(R.string.CONNECT));
                                                                            Inconnection_Layout.setVisibility(View.GONE);
                                                                            MassageLayout.setVisibility(View.GONE);
                                                                            ConnectLayoutAndMore.setEnabled(true);
                                                                            Send_friendRequest.setEnabled(true);
                                                                        }else {
                                                                            ErrorMessageHandle(task);
                                                                        }
                                                                    }
                                                                });
                                                            }else {
                                                                ErrorMessageHandle(task);
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        }else {
                                            ErrorMessageHandle(task);
                                        }
                                    }
                                });
                            }
                        }
                    });

        AuthorSuggestionList.setHasFixedSize(true);
        AuthorSuggestionListAdapter = new SectionListDataAdapter(AuthorProfileActivity.this, SuggestionStoryList);
        AuthorSuggestionList.setLayoutManager(new LinearLayoutManager(AuthorProfileActivity.this, LinearLayoutManager.HORIZONTAL, false));
        AuthorSuggestionList.setAdapter(AuthorSuggestionListAdapter);

        PutSuggestionList();

    }


    private void ErrorMessageHandle(Task task){
        mProgressDialog.dismiss();
        Log.w("fsfs", "signInWithEmail:failed", task.getException());
        mProgressDialog.hide();
        Toast.makeText(AuthorProfileActivity.this, R.string.PLEASE_TRY_AGAIN,
                Toast.LENGTH_SHORT).show();
    }

    private void DeleteCurrentFriendRequestFromList(String FriendId){
        mFireStoreDatabase.collection("FriendRequestData").document(mAuth.getUid()).collection("RequestList").document(FriendId)
                .delete();
    }

    private void DecreaseNotificationCounter_by_one() {

        mFireStoreDatabase.collection("NotificationData").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (documentSnapshot.exists()){
                        if (documentSnapshot.contains("notification_counter")){
                            long NotificationCounter=documentSnapshot.getLong("notification_counter").longValue();

                            Map loadData=new HashMap();
                            if (NotificationCounter<=1){
                                loadData.put("notification_counter",0);
                            }else {
                                loadData.put("notification_counter",--NotificationCounter);
                            }
                            mFireStoreDatabase.collection("NotificationData").document(mAuth.getUid()).set(loadData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()){
                                        return;
                                    }
                                }
                            });

                        }
                    }
                }
            }
        });

    }



    private void CheckNotificationCounter() {
        mFireStoreDatabase.collection("NotificationData").document(Profile_visit_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (documentSnapshot.exists()){
                        if (documentSnapshot.contains("notification_counter")){
                            SetNotificationCounter((int)documentSnapshot.getLong("notification_counter").longValue());
                        }else {
                            SetNotificationCounter(0);
                        }
                    }else {
                        SetNotificationCounter(0);
                    }
                }else {
                    SetNotificationCounter(0);
                }
            }
        });
    }


    public void SetNotificationCounter(int Counter){
        Map loadData=new HashMap();
        loadData.put("notification_counter",++Counter);
        mFireStoreDatabase.collection("NotificationData").document(Profile_visit_id).set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    return;
                }
            }
        });
    }




    private void CheckFriendRequestStatus(){
        FriendRequestDatabaseRef.document(mCurrentUser.getUid()).collection("RequestList").document(Profile_visit_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                        if (dataSnapshot != null) {
                            mProgressDialog.dismiss();
                            if (dataSnapshot.exists()){

                                //Log.e("sdfskss","document id is "+dataSnapshot.get(mCurrentUser.getUid())+" author id "+dataSnapshot.contains(Author_id));

                                IsRequestSentOrRecieve=1;
                               /* Log.e("jsfskfjksd","this is other user response "+dataSnapshot.get(dataSnapshot)+
                                "\n this is current user "+mCurrentUser.getUid());*/
                                //JSONObject requestType=new JSONObject(""+dataSnapshot.get(Profile_visit_id));
                                Send_friendRequest.setText("");
                                String req_type=dataSnapshot.getString("request_type");
                                if(req_type.equals("recieved")){
                                    NewConnectionRequest.setVisibility(View.VISIBLE);
                                    mCurrent_state=2;
                                    AddUserImage.setImageResource(R.drawable.ic_add_male_user);
                                    AddUserImage.setColorFilter(ContextCompat.getColor(AuthorProfileActivity.this, R.color.colorPrimary),
                                            android.graphics.PorterDuff.Mode.SRC_IN);
                                    Send_friendRequest.setTextColor(Color.parseColor("#009688"));
                                    Send_friendRequest.setText(getString(R.string.NEW_REQUEST));
                                    //Decline_friendRequest.setVisibility(View.VISIBLE);

                                }
                                else if(req_type.equals("sent")){
                                    NewConnectionRequest.setVisibility(View.GONE);
                                    mCurrent_state=1;
                                    AddUserImage.setColorFilter(ContextCompat.getColor(AuthorProfileActivity.this, R.color.colorPrimary),
                                            android.graphics.PorterDuff.Mode.SRC_IN);
                                    Send_friendRequest.setTextColor(Color.parseColor("#009688"));
                                    Send_friendRequest.setText(getString(R.string.CANCEL_REQUEST));

                                }


                            }else {
                                MassageLayout.setVisibility(View.GONE);
                                Inconnection_Layout.setVisibility(View.GONE);
                                NewConnectionRequest.setVisibility(View.GONE);
                                Send_friendRequest.setText(getString(R.string.CONNECT));
                            }
                        }
                    }
                });
    }


    public void PutSuggestionList(){
        CollectionReference storyRef = mFireStoreDatabase.collection("Users").document(Profile_visit_id).collection("Story");


        storyRef.whereEqualTo("is_hide",false).orderBy("all_time_rank", Query.Direction.DESCENDING).limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {

                        if (document.getString("story_status").equals("publish")){
                            AuthorWritingLayout.setVisibility(View.VISIBLE);
                            SuggestionStoryList.add(new SingleStoryModel(1,document.getString("story_title"), document.getString("cover_image"), document.getString("story_intro"),
                                    document.get("view_count")+"",document.getString("AvgRating"),document.getId(),Profile_visit_id,document.getBoolean("is_hide").booleanValue()));

                        }

                    }
                } else {
                    Log.d("sdfhsdj", "Error getting documents: ", task.getException());
                }


                AuthorSuggestionListAdapter.notifyDataSetChanged();
            }
        });
    }


    FirebaseUser currentUser;

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.author_page_menu_option_first, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;
        public MyMenuItemClickListener() {
            //this.position=positon;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.writing_list:
                    Toast.makeText(AuthorProfileActivity.this,"Not working right now",Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.send_message:
                    Toast.makeText(AuthorProfileActivity.this,"Not working right now",Toast.LENGTH_SHORT).show();
                    return true;

                default:
            }
            return false;
        }
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (currentUser!=null){
            Log.e("sfdsdf","Profile on stop is called");

            //mDevUserRef.child("lastSeen").setValue();
        }
    }
}
