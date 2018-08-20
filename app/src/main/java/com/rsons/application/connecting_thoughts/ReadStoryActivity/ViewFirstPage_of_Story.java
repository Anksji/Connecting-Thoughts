package com.rsons.application.connecting_thoughts.ReadStoryActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.rsons.application.connecting_thoughts.FormatLognValue;
import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.UsersActivity.AuthorProfileActivity;
import com.rsons.application.connecting_thoughts.WritingActivity.WriteStoryActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by ankit on 2/14/2018.
 */

public class ViewFirstPage_of_Story extends AppCompatActivity {

    TextView TitleOfStory,Info_of_Story,Rating_of_Story,ViewShip_ofStory,Author_name;
    ImageView StoryCoverImage,StoryBackgroundCover;
    String StoryTitle,StoryInfo,StoryViewCount,StoryRating,StoryCoverUrl,StoryId_or_serverName;
    CircleImageView AuthorImage;
    String StoryAuthorId;
    ImageView Go_Back;
    RelativeLayout Add_Another_Chapter;
    RippleView AddAnotherChapterRipple;
    ImageView MoreChapterList;
    ProgressDialog mProgressDialog;
    private boolean IsAuthorHide=false;

    private boolean IsFromStoryListing=false;
    private String CurrentChapterParentId;

    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;
    private boolean IsChapterListOpen=false;
    private InterstitialAd mInterstitialAd;
    private static long mInterstitialAdCounter=0;
    private String AuthorDeviceTokenId="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_first_page_of_story);
        MobileAds.initialize(this, getString(R.string.admob_CT_app_id));


        TitleOfStory= (TextView) findViewById(R.id.story_title);
        Info_of_Story= (TextView) findViewById(R.id.story_info);
        Rating_of_Story= (TextView) findViewById(R.id.star_count);
        ViewShip_ofStory= (TextView) findViewById(R.id.view_count);
        StoryCoverImage= (ImageView) findViewById(R.id.story_cover_image);
        StoryBackgroundCover= (ImageView) findViewById(R.id.story_cover_image_back);
        Author_name= (TextView) findViewById(R.id.author_name);
        AuthorImage= (CircleImageView) findViewById(R.id.author_image);
        LinearLayout AuthorLayout= (LinearLayout) findViewById(R.id.author_layout);
        Go_Back= (ImageView) findViewById(R.id.go_back);
        MoreChapterList= (ImageView) findViewById(R.id.more_options);
        Add_Another_Chapter= (RelativeLayout) findViewById(R.id.add_another_chapter_to_story);
        AddAnotherChapterRipple= (RippleView) findViewById(R.id.add_new_story_ripple);

        RelativeLayout Start_reading_btn= (RelativeLayout) findViewById(R.id.start_reading_btn);

        mFireStoreDatabase=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);

        mAuth = FirebaseAuth.getInstance();
        mProgressDialog=new ProgressDialog(ViewFirstPage_of_Story.this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT));


        IsFromStoryListing=getIntent().getBooleanExtra("from_story_listing",false);
        StoryId_or_serverName=getIntent().getStringExtra("story_Id");
        Log.e("fdskjfskf","this is from_story_listing = "+IsFromStoryListing+" story_Id = "+StoryId_or_serverName);
        if (IsFromStoryListing){
            mProgressDialog.show();
            LoadRawDataOfStory(StoryId_or_serverName);
        }else {
            IsAuthorHide=getIntent().getBooleanExtra("Is_hide",false);
            StoryTitle=getIntent().getStringExtra("storyTitle");
            StoryInfo=getIntent().getStringExtra("story_info");
            StoryViewCount=getIntent().getStringExtra("view_count");
            StoryRating=getIntent().getStringExtra("story_rating");
            StoryCoverUrl=getIntent().getStringExtra("story_cover");
            StoryAuthorId=getIntent().getStringExtra("story_AuthorId");
            SetUpAllData();
        }


        Start_reading_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ReadStory=new Intent(ViewFirstPage_of_Story.this,DisplayStoryContent.class);
                ReadStory.putExtra("storyTitle",StoryTitle);
                ReadStory.putExtra("story_cover",StoryCoverUrl);
                ReadStory.putExtra("story_intro",StoryInfo);
                ReadStory.putExtra("view_count",StoryViewCount);
                ReadStory.putExtra("story_rating",StoryRating);
                ReadStory.putExtra("story_Id",StoryId_or_serverName);
                ReadStory.putExtra("story_AuthorId",StoryAuthorId);
                ReadStory.putExtra("from_first_page",true);
                ReadStory.putExtra("story_Author_device_token",AuthorDeviceTokenId);
                startActivity(ReadStory);
            }
        });
        Go_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        MoreChapterList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("story_Id", StoryId_or_serverName);
                bundle.putString("authorId",StoryAuthorId);
                Log.e("skdfsfkflsd","this is we sending "+CurrentChapterParentId);
                bundle.putString("parentStoryId",CurrentChapterParentId);
                ChapterListingFragment fragobj = new ChapterListingFragment();
                fragobj.setArguments(bundle);

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                //ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left);
                ft.add(R.id.fragment_holder, fragobj,"chapter_list_frag");
                ft.addToBackStack("chapter_list_frag");
                ft.commit();
                //fragmentMargin.setVisibility(View.VISIBLE);
                IsChapterListOpen=true;

            }
        });

        Log.e("fskflfkdsf","story author id "+StoryAuthorId+" user id "+mAuth.getUid());
        Log.e("skdfsfkflsd","this is story id "+StoryId_or_serverName);




        Add_Another_Chapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final String[] ParentStoryId = new String[1];

                mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT));
                mProgressDialog.show();
                Add_Another_Chapter.setEnabled(false);
                    DocumentReference getData=mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(StoryId_or_serverName);
                    getData.get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()){
                                            if (document.contains("parent_story_id")){
                                                WriteAnotherChapter(document.getString("parent_story_id"));

                                            }else {
                                                WriteAnotherChapter(StoryId_or_serverName);
                                            }
                                        }
                                    }
                                }
                            });
                    getData.get().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ViewFirstPage_of_Story.this,getString(R.string.SOMETHING_WENT_WRONG),Toast.LENGTH_SHORT).show();
                        }
                    });


            }
        });


        AuthorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!IsAuthorHide){
                    Intent profilePage=new Intent(ViewFirstPage_of_Story.this, AuthorProfileActivity.class);
                    profilePage.putExtra("user_key",StoryAuthorId);
                    profilePage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(profilePage);
                }else {
                    Toast.makeText(ViewFirstPage_of_Story.this,getString(R.string.SORRY_AUTHOR_OF_THIS_STORY_IS_UNKNOWN),Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void SetUpAllData(){
        Log.e("fdskjfskf","setup all data is called");
        TitleOfStory.setText(StoryTitle);
        Info_of_Story.setText(StoryInfo);
        Rating_of_Story.setText(StoryRating);
        String formattedViewCount = FormatLognValue.format(Long.parseLong(StoryViewCount));
        ViewShip_ofStory.setText(formattedViewCount);

        mProgressDialog.dismiss();
        if (!mAuth.getUid().equals(StoryAuthorId)){
            Add_Another_Chapter.setVisibility(View.GONE);
            AddAnotherChapterRipple.setVisibility(View.GONE);
            Add_Another_Chapter.setEnabled(false);
        }


        Picasso.with(this).load(StoryCoverUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.card_defalt_back_cover).into(StoryCoverImage,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("onerrorSection", "this is success");
                    }

                    @Override
                    public void onError() {
                        Log.e("onerrorSection", "this is error");
                        Picasso.with(ViewFirstPage_of_Story.this).load(StoryCoverUrl).placeholder(R.drawable.card_defalt_back_cover).into(StoryCoverImage);
                    }
                });
        Picasso.with(this).load(StoryCoverUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.card_defalt_back_cover).into(StoryBackgroundCover,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("onerrorSection", "this is success");
                    }

                    @Override
                    public void onError() {
                        Log.e("onerrorSection", "this is error");
                        Picasso.with(ViewFirstPage_of_Story.this).load(StoryCoverUrl).placeholder(R.drawable.card_defalt_back_cover).into(StoryBackgroundCover);
                    }
                });


        if (!IsAuthorHide){
            final DocumentReference dataRef = mFireStoreDatabase.collection("Users").document(StoryAuthorId);
            dataRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().exists()) {
                            final DocumentSnapshot dataSnapshot = task.getResult();
                            if (dataSnapshot != null) {
                                if (dataSnapshot.contains("name")) {
                                    String name = dataSnapshot.get("name").toString();
                                    Author_name.setText(name);
                                }
                                if (dataSnapshot.contains("device_token")){
                                    AuthorDeviceTokenId=dataSnapshot.getString("device_token");
                                }
                                if (dataSnapshot.contains("image")) {

                                    Picasso.with(ViewFirstPage_of_Story.this).load(dataSnapshot.get("image").toString()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_user_).into(AuthorImage,
                                            new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    Log.e("onerrorSection", "this is success");
                                                }

                                                @Override
                                                public void onError() {
                                                    Log.e("onerrorSection", "this is error");
                                                    Picasso.with(ViewFirstPage_of_Story.this).load(dataSnapshot.get("image").toString()).placeholder(R.drawable.ic_user_).into(AuthorImage);
                                                }
                                            });
                                }
                            }
                        }
                    }
                }
            });
        }else {
            Author_name.setText(getString(R.string.UNKNOWN));
        }



        mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").document(StoryId_or_serverName)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        if (document.contains("parent_story_id")){
                            CurrentChapterParentId=document.getString("parent_story_id");
                        }else {
                            CurrentChapterParentId=StoryId_or_serverName;
                        }
                    }

                }
            }
        });

    }

    private void LoadRawDataOfStory(String storyId_or_serverName) {
        Log.e("fdskjfskf","calling raw data story id is "+storyId_or_serverName);
        DocumentReference getdata=mFireStoreDatabase.collection("Story").document(storyId_or_serverName);
        getdata.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()){
                        Log.e("fdskjfskf","inside raw data loading");
                        StoryTitle=dataSnapshot.getString("story_title");
                        StoryInfo=dataSnapshot.getString("story_intro");
                        StoryViewCount=""+dataSnapshot.get("view_count");
                        StoryRating=dataSnapshot.getString("AvgRating");
                        StoryCoverUrl=dataSnapshot.getString("cover_image");
                        StoryAuthorId=dataSnapshot.getString("story_author_id");
                        IsAuthorHide=dataSnapshot.getBoolean("is_hide").booleanValue();
                        SetUpAllData();
                    }else {
                        Toast.makeText(ViewFirstPage_of_Story.this,getString(R.string.SOMETHING_WENT_WRONG),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ViewFirstPage_of_Story.this,getString(R.string.SOMETHING_WENT_WRONG),Toast.LENGTH_SHORT).show();
                }
            }
        });
        getdata.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fdskjfskf","this is failure message "+e.getMessage());
            }
        });
    }

    private void WriteAnotherChapter(String parentId){
        mProgressDialog.dismiss();
        Intent LaunchWriteStory=new Intent(ViewFirstPage_of_Story.this, WriteStoryActivity.class);
        LaunchWriteStory.putExtra("addNewChapter",true);
        LaunchWriteStory.putExtra("parent_story_Id",parentId);
        startActivity(LaunchWriteStory);
        //overridePendingTransition(R.anim.from_right, R.anim.to_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Add_Another_Chapter.setEnabled(true);
    }

    @Override
    public void onBackPressed() {

        //super.onBackPressed();
        Log.e("sdfkjfksds","on back pressed call count = "+IsChapterListOpen);
        //overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right);
        if (!IsChapterListOpen) {
            finish();
            Log.e("sdfkjfksds","inside if statement = "+IsChapterListOpen);
            //additional code
        } else {
            Log.e("sdfkjfksds","else statement = "+IsChapterListOpen);
            //fragmentMargin.setVisibility(View.GONE);
            Fragment fragment = getFragmentManager().findFragmentByTag("chapter_list_frag");
            IsChapterListOpen=false;
            Log.e("sdfsfksdfsds","chapter fragment is "+fragment);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            //FragmentManager fm = getFragmentManager();
            //fm.popBackStack("chapter_list_frag",FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.setCustomAnimations(R.animator.in_from_left, R.animator.in_from_right, R.animator.in_from_left, R.animator.in_from_right);
            ft.remove(fragment).commit();

        }

    }


}
