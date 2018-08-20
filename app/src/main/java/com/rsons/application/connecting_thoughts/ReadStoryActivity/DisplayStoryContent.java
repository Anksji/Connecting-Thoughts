package com.rsons.application.connecting_thoughts.ReadStoryActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.rsons.application.connecting_thoughts.Const;
import com.rsons.application.connecting_thoughts.EndlessRecyclerViewScrollListener;
import com.rsons.application.connecting_thoughts.Fetch_data.CommentDataAdapter;
import com.rsons.application.connecting_thoughts.Fetch_data.CommentModel;
import com.rsons.application.connecting_thoughts.Fetch_data.SectionListDataAdapter;
import com.rsons.application.connecting_thoughts.Fetch_data.SingleStoryModel;
import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.Report.ReportForStoryScreen;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ankit on 2/15/2018.
 */

public class DisplayStoryContent extends AppCompatActivity{

    TextView Rating_of_Story,ViewShip_ofStory;
    //TitleOfStory
    ImageView StoryCoverImage,StoryBackgroundCover;
    String StoryTitle,StoryViewCount,StoryRating,StoryCoverUrl,StoryId_or_serverName,StoryIntro;
    TextView StoryContentDisplay;
    //private long NumberOF_userRatedStory=0;
    //private long TotalRatingSum=0;
    //private long AllTimeRank=0;
        private int TEXT_SIZE_INC_DEC_BY=5;
    String StoryAuthorId;
    CircleImageView CurrentUserImage;
    TextView SubmitTextView;
    EditText UserCommentEditText;
    ProgressDialog mProgressDialog,mloadContentProgressDialog;
    //ProgressDialog NOn_cancelableProgressDialog;
    private static RatingBar UserRateToStory;
    TextView UsercommentTExtView;
    SectionListDataAdapter SuggestionListAdapter;
    RecyclerView SuggestionList,CommentList;
    LinearLayoutManager mCommentLayoutManager;
    private FirebaseFirestore mFireStoreDatabase;
    private String CurrentUserImageUrl,CurrentUserName;
    CommentDataAdapter CommentListAdapter;
    TextView CurrentCommentUserName;

    private CardView AdCardview1,AdCardView2;
    int CommentCounter=1;
    TextView UserCommentDate;
    boolean IsUserAlreadyCommented=false;
    RelativeLayout UserAlreadyCommented,NoCommentTillNOw;
    ImageView firstStart,secondStar,thirdStar,fourthStar,fifthStar;
    String AlreadyRatedStar="0",AlreadyUserComment;
    ImageView MoreOptionForComment;
    private FirebaseAuth mAuth;
   // private ImageView GoBack,StoryReadMoreOption;
    private ArrayList<CommentModel> CommentArrayList=new ArrayList<>();
    private TextView LoadMore_comment_text;
    private ArrayList<String> CommentIdList=new ArrayList<>();
    private ArrayList<SingleStoryModel> SuggestionStoryList=new ArrayList<>();
    private ArrayList<String>SuggestionListIds=new ArrayList<>();
    private static  String CurrentReadingGenre;
    private static String ParentStoryId;
    private long CurrentChapterNumber;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private AdView mAdView,mAdview2;
    private CardView SuggestionLayout;
    private InterstitialAd mInterstitialAd;
    private static long mInterstitialAdCounter=0;
    private NestedScrollView StoryScrollRead;
    //private ScrollView StoryContentScroll;
    private RelativeLayout BottomCommentLayout;
    public static BottomSheetBehavior BottomSheet;
    private LinearLayout BottomlSheetLayout;
    private RelativeLayout SaveReadSetting;
    private RelativeLayout DaySelection,NightSelection;
    private RelativeLayout IncreaseFont,DecreaseFont;
    public static int MAX_FONT_LIMIT=28;
    private CoordinatorLayout MainReadingCoordinator;
    private RelativeLayout CurrenUserRating;
    private String AuthorDeviceTokenId="";

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_story_content);

        AdCardview1= (CardView) findViewById(R.id.adView_in_card);
        AdCardView2= (CardView) findViewById(R.id.adView_in_card_bottom);
        mToolbar= (Toolbar) findViewById(R.id.story_read_toolbar);
        mCollapsingToolbar= (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        StoryScrollRead= (NestedScrollView) findViewById(R.id.scroll);
        BottomCommentLayout= (RelativeLayout) findViewById(R.id.bottom_comment);
        CurrenUserRating= (RelativeLayout) findViewById(R.id.current_user_comment_and_rating);
        //StoryContentScroll= (ScrollView) findViewById(R.id.story_content_scroll);

        LoadMore_comment_text = (TextView) findViewById(R.id.load_more_comment);
        BottomlSheetLayout= (LinearLayout) findViewById(R.id.bottom_sheet);
        BottomSheet = BottomSheetBehavior.from(BottomlSheetLayout);
        SaveReadSetting= (RelativeLayout) findViewById(R.id.save_setting);
        DaySelection= (RelativeLayout) findViewById(R.id.day_vision);
        NightSelection= (RelativeLayout) findViewById(R.id.night_vision);
        IncreaseFont= (RelativeLayout) findViewById(R.id.increase_font);
        DecreaseFont= (RelativeLayout) findViewById(R.id.decrease_font);
        MainReadingCoordinator= (CoordinatorLayout) findViewById(R.id.main_reading_co_ordinator_layout);


        MobileAds.initialize(this, getString(R.string.admob_CT_app_id));


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.story_read_interstial_ad));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        mInterstitialAdCounter++;

        Log.e("dfklkfs","this is interstital counter value "+mInterstitialAdCounter+
                " and division result "+mInterstitialAdCounter%5);
        if (mInterstitialAdCounter%5==0){
            mInterstitialAdCounter=0;
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }

        SuggestionLayout= (CardView) findViewById(R.id.suggestion_layout);
        mAdView = (AdView) findViewById(R.id.story_view_ad);
        mAdview2= (AdView) findViewById(R.id.story_view_ad2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdview2.loadAd(adRequest2);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //TitleOfStory= (TextView) findViewById(R.id.story_title);
        Rating_of_Story= (TextView) findViewById(R.id.star_count);
        ViewShip_ofStory= (TextView) findViewById(R.id.view_count);
        StoryCoverImage= (ImageView) findViewById(R.id.story_cover_image);
        StoryBackgroundCover= (ImageView) findViewById(R.id.story_cover_image_back);
        StoryContentDisplay= (TextView) findViewById(R.id.story_content);
        SubmitTextView= (TextView) findViewById(R.id.submit_comment);
        UserCommentEditText= (EditText) findViewById(R.id.comment_edit_text);
        UserRateToStory= (RatingBar) findViewById(R.id.story_rating_given_by_user);
        UsercommentTExtView= (TextView) findViewById(R.id.user_comment_textview);
        SuggestionList= (RecyclerView) findViewById(R.id.suggestion_list);
        CommentList= (RecyclerView) findViewById(R.id.comment_list);
        firstStart= (ImageView) findViewById(R.id.first_star);
        secondStar= (ImageView) findViewById(R.id.second_star);
        thirdStar= (ImageView) findViewById(R.id.third_star);
        fourthStar= (ImageView) findViewById(R.id.fourth_star);
        fifthStar= (ImageView) findViewById(R.id.fifth_star);
        UserAlreadyCommented= (RelativeLayout) findViewById(R.id.user_already_commented);
        NoCommentTillNOw= (RelativeLayout) findViewById(R.id.no_comment_till_now);
        UserCommentDate= (TextView) findViewById(R.id.user_comment_date);
        MoreOptionForComment= (ImageView) findViewById(R.id.comment_more_option);


        mloadContentProgressDialog=new ProgressDialog(DisplayStoryContent.this);
        mloadContentProgressDialog.setMessage(getString(R.string.PLEASE_WAIT));
        mloadContentProgressDialog.setCanceledOnTouchOutside(false);
        mloadContentProgressDialog.show();

        SetBottomSheetElements();

        DaySelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutElementChangeToBlackAndWhite(true);
                Const.SetDayNightMode(true,DisplayStoryContent.this);

                    DaySelection.setBackgroundResource(R.drawable.selection_rectangle);
                    NightSelection.setBackgroundResource(R.drawable.rectangle_border);
            }
        });

        NightSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutElementChangeToBlackAndWhite(false);

                Const.SetDayNightMode(false,DisplayStoryContent.this);
                DaySelection.setBackgroundResource(R.drawable.rectangle_border);
                NightSelection.setBackgroundResource(R.drawable.selection_rectangle);
            }
        });

        IncreaseFont.setOnTouchListener( new View.OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                switch(event.getAction())
                {

                    case MotionEvent.ACTION_DOWN:
                        if (Const.GetReadTextSize(DisplayStoryContent.this)<MAX_FONT_LIMIT){
                            IncreaseFont.setBackgroundResource(R.drawable.selection_rectangle);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        if (Const.GetReadTextSize(DisplayStoryContent.this)<MAX_FONT_LIMIT){
                            IncreaseFont.setBackgroundResource(R.drawable.rectangle_border);
                            DecreaseFont.setBackgroundResource(R.drawable.rectangle_border);
                            Const.SetReadTextSize((Const.GetReadTextSize(DisplayStoryContent.this)+TEXT_SIZE_INC_DEC_BY),DisplayStoryContent.this);
                            StoryContentDisplay.setTextSize(Const.GetReadTextSize(DisplayStoryContent.this));
                        }else {
                            IncreaseFont.setBackgroundResource(R.drawable.disable_rectangle_selection);
                            Toast.makeText(DisplayStoryContent.this,getString(R.string.SORRY_TEXT_SIZE_NOT_INCREASES),Toast.LENGTH_SHORT).show();

                        }

                        break;
                }
                return true;
            }
        });

       /* IncreaseFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });*/

        DecreaseFont.setOnTouchListener( new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        if (Const.GetReadTextSize(DisplayStoryContent.this)>10){
                            DecreaseFont.setBackgroundResource(R.drawable.selection_rectangle);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        if (Const.GetReadTextSize(DisplayStoryContent.this)>10){
                            DecreaseFont.setBackgroundResource(R.drawable.rectangle_border);
                            IncreaseFont.setBackgroundResource(R.drawable.rectangle_border);
                            Const.SetReadTextSize((Const.GetReadTextSize(DisplayStoryContent.this)-TEXT_SIZE_INC_DEC_BY),DisplayStoryContent.this);
                            StoryContentDisplay.setTextSize(Const.GetReadTextSize(DisplayStoryContent.this));
                        }else {
                            DecreaseFont.setBackgroundResource(R.drawable.disable_rectangle_selection);
                            Toast.makeText(DisplayStoryContent.this,getString(R.string.SORRY_TEXT_SIZE_NOT_DECREASES),Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
                return true;
            }
        });


       /* DecreaseFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        SaveReadSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        BottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        //btnBottomSheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        // btnBottomSheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        /*StoryScrollRead.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                Log.v("kdfhsjkfsk","parent touch");
                findViewById(R.id.story_content_scroll).getParent().requestDisallowInterceptTouchEvent(false);
                findViewById(R.id.scroll).getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });*/

        /*StoryContentScroll.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event)
            {
                Log.v("kdfhsjkfsk","parent child touch");
                // Disallow the touch request for parent scroll on touch of child view
                findViewById(R.id.scroll).getParent().requestDisallowInterceptTouchEvent(false);
                findViewById(R.id.story_content_scroll).getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });*/

        /*
        GoBack= (ImageView) findViewById(R.id.go_back);
        StoryReadMoreOption= (ImageView) findViewById(R.id.more_option);*/

        CurrentCommentUserName= (TextView) findViewById(R.id.user_name);


        CurrentUserImage= (CircleImageView) findViewById(R.id.current_user_image);


        mFireStoreDatabase=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);

        mAuth = FirebaseAuth.getInstance();

        mProgressDialog=new ProgressDialog(DisplayStoryContent.this);
        mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        //mProgressDialog.show();

        /*NOn_cancelableProgressDialog=new ProgressDialog(DisplayStoryContent.this);
        NOn_cancelableProgressDialog.setMessage(getString(R.string.PLEASE_WAIT));
        NOn_cancelableProgressDialog.setCanceledOnTouchOutside(false);
        NOn_cancelableProgressDialog.setCancelable(false);*/

        Typeface TitleFont = Typeface.createFromAsset(getAssets(),  "fonts/Playfair.ttf");
        Typeface storyFont = Typeface.createFromAsset(getAssets(),  "fonts/Playfair_Regular.ttf");
        //TitleOfStory.setTypeface(TitleFont);
        StoryContentDisplay.setTypeface(storyFont);
        mCollapsingToolbar.setCollapsedTitleTypeface(TitleFont);
        mCollapsingToolbar.setExpandedTitleTypeface(TitleFont);

        boolean isFromFirstPage=getIntent().getBooleanExtra("from_first_page",false);

        if (isFromFirstPage){
            StoryTitle=getIntent().getStringExtra("storyTitle");
            StoryIntro=getIntent().getStringExtra("story_intro");
            StoryViewCount=getIntent().getStringExtra("view_count");
            StoryRating=getIntent().getStringExtra("story_rating");
            StoryCoverUrl=getIntent().getStringExtra("story_cover");
            StoryId_or_serverName=getIntent().getStringExtra("story_Id");
            StoryAuthorId=getIntent().getStringExtra("story_AuthorId");
            AuthorDeviceTokenId=getIntent().getStringExtra("story_Author_device_token");
            SetReadPage();
        }else {
            StoryId_or_serverName=getIntent().getStringExtra("story_Id");
            Log.e("sdlkfjsklfj","this is story id in story content page "+StoryId_or_serverName);
            mFireStoreDatabase.collection("Story").document(StoryId_or_serverName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot dataSnapshot=task.getResult();
                        if (dataSnapshot.exists()){
                            StoryTitle=dataSnapshot.getString("story_title");
                            StoryIntro=dataSnapshot.getString("story_intro");
                            StoryViewCount=""+dataSnapshot.get("view_count");
                            StoryRating=dataSnapshot.getString("AvgRating");
                            StoryCoverUrl=dataSnapshot.getString("cover_image");
                            StoryAuthorId=dataSnapshot.getString("story_author_id");
                            SetReadPage();
                        }else {
                            Toast.makeText(DisplayStoryContent.this,getString(R.string.SOMETHING_WENT_WRONG),Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(DisplayStoryContent.this,getString(R.string.SOMETHING_WENT_WRONG),Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        LoadMore_comment_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoadMOreComment=new Intent(DisplayStoryContent.this,LoadMoreCommentList.class);
                LoadMOreComment.putExtra("storyAuthorId",StoryAuthorId);
                LoadMOreComment.putExtra("story_id",StoryId_or_serverName);
                LoadMOreComment.putExtra("story_title",StoryTitle);
                startActivity(LoadMOreComment);
            }
        });

    }

    private void SetReadPage(){

        //TitleOfStory.setText(StoryTitle);
        Rating_of_Story.setText(StoryRating);
        ViewShip_ofStory.setText(StoryViewCount);
        CreateDynamicLink();


        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(StoryTitle);
        }

        Picasso.with(this).load(StoryCoverUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.card_defalt_back_cover).into(StoryBackgroundCover,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("onerrorSection", "this is success");
                    }

                    @Override
                    public void onError() {
                        Log.e("onerrorSection", "this is error");
                        Picasso.with(DisplayStoryContent.this).load(StoryCoverUrl).placeholder(R.drawable.card_defalt_back_cover).into(StoryBackgroundCover);
                    }
                });



        if (StoryAuthorId!=null&&StoryId_or_serverName!=null&&StoryId_or_serverName.length()>5){
            final DocumentReference dataRef = mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").document(StoryId_or_serverName);
            dataRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().exists()) {
                            final DocumentSnapshot dataSnapshot = task.getResult();
                            if (dataSnapshot != null) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                    StoryContentDisplay.setText(Html.fromHtml(dataSnapshot.get("story_content").toString()));
                                }else {
                                    StoryContentDisplay.setText(Html.fromHtml(dataSnapshot.get("story_content").toString(),Html.FROM_HTML_MODE_COMPACT));
                                }


                                //StoryScrollRead.scrollTo(0, mAdView.getBottom());

                                Log.e("jkhfjksfs","Total word  count is "+getTotalNumberOfWordCount(StoryContentDisplay.getText().toString()));

                                /*if (dataSnapshot.contains("Total_user_rated_to_story")){
                                    NumberOF_userRatedStory=dataSnapshot.getLong("Total_user_rated_to_story").longValue();
                                }else {
                                    NumberOF_userRatedStory=0;
                                }
                                if (dataSnapshot.contains("TotalRatingSum")){
                                    TotalRatingSum=dataSnapshot.getLong("TotalRatingSum").longValue();
                                }else {
                                    TotalRatingSum=0;
                                }
                                if (dataSnapshot.contains("all_time_rank")){
                                    AllTimeRank=dataSnapshot.getLong("all_time_rank").longValue();
                                }else {
                                    AllTimeRank=0;
                                }*/

                                if (dataSnapshot.contains("parent_story_id")){
                                    ParentStoryId=dataSnapshot.getString("parent_story_id");
                                }
                                if (dataSnapshot.contains("chapter_counter")){
                                    CurrentChapterNumber =dataSnapshot.getLong("chapter_counter").longValue();
                                }

                                CurrentReadingGenre=dataSnapshot.getString("story_genre");
                                Log.e("kfjskjkdd","this is current genre "+CurrentReadingGenre);
                                //PutSuggestionList();
                                PutInToSuggestionListNextParts();


                            }

                            final DocumentReference comentRef = mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").
                                    document(StoryId_or_serverName).collection("Comments").document(mAuth.getUid());

                            comentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        final DocumentSnapshot dataSnapshot = task.getResult();
                                        if (dataSnapshot.exists()) {

                                            if (dataSnapshot.contains("starRating")){
                                                IsUserAlreadyCommented=true;
                                                AlreadyRatedStar=""+dataSnapshot.get("starRating");
                                            }
                                            if (dataSnapshot.contains("comment")){
                                                IsUserAlreadyCommented=true;
                                                AlreadyUserComment=dataSnapshot.getString("comment");
                                            }
                                            if (dataSnapshot.contains("comment_timestamp_update")){
                                                long commentdate;

                                                try{
                                                    Log.e("jkhdksjfh","try part of timestamp published");
                                                    Date date = (Date)dataSnapshot.get("comment_timestamp_update");
                                                    commentdate=date.getTime();
                                                }catch (Exception e){
                                                    Log.e("jkhdksjfh","catch part of timestamp published");
                                                    commentdate=Long.parseLong(""+dataSnapshot.getLong("comment_timestamp_update").longValue());
                                                }

                                                String DateString= android.text.format.DateFormat.format("MM/dd/yyyy",new Date(commentdate)).toString();

                                                UserCommentDate.setText(DateString);
                                            }
                                            if(dataSnapshot.contains("last_read_position")){
                                                //mProgressDialog.dismiss();
                                                mloadContentProgressDialog.dismiss();
                                                StoryScrollRead.scrollTo(0,Integer.parseInt(dataSnapshot.getString("last_read_position")));
                                            }else {
                                                mloadContentProgressDialog.dismiss();
                                            }

                                            if (IsUserAlreadyCommented){
                                                MoreOptionForComment.setVisibility(View.VISIBLE);
                                                UserAlreadyCommented.setVisibility(View.VISIBLE);
                                                NoCommentTillNOw.setVisibility(View.GONE);
                                                UserCommentDate.setVisibility(View.VISIBLE);
                                                MoreOptionForComment.setVisibility(View.VISIBLE);
                                                UsercommentTExtView.setVisibility(View.VISIBLE);
                                                UsercommentTExtView.setText(AlreadyUserComment);
                                                UserCommentEditText.setText(AlreadyUserComment);
                                                Float tempStar=Float.parseFloat(AlreadyRatedStar);
                                                UserRateToStory.setRating(tempStar);
                                                ShowAlreadyRatedStars(tempStar);
                                            }else {
                                                NoCommentTillNOw.setVisibility(View.VISIBLE);
                                            }

                                               /* loadData.put("starRating",""+UserRateToStory.getRating());
                                                loadData.put("comment",UserCommentEditText.getText().toString());
                                                loadData.put("sorting_key",getCommentSortingKeyId());
                                                loadData.put("comment_date",formattedDate);
                                                loadData.put("comment_user_name",CurrentUserName);
                                                loadData.put("comment_user_image",CurrentUserImageUrl);
                                                sdfsfsffd*/

                                            if (dataSnapshot.contains("UserRatingRankAlgoInsideComment")) {

                                            }
                                            else {
                                                mloadContentProgressDialog.dismiss();
                                               // mProgressDialog.dismiss();
                                                AddUserRatingRankInsideComment();
                                            }
                                        }else {
                                            mloadContentProgressDialog.dismiss();
                                            //mProgressDialog.dismiss();
                                            AddUserRatingRankInsideComment();
                                        }
                                    }else {
                                        mloadContentProgressDialog.dismiss();
                                        //mProgressDialog.dismiss();
                                    }
                                }
                            });

                        }

                    }
                }
            });

            final long timeInMillis = System.currentTimeMillis();
            Map<String, Object> history = new HashMap<>();
            history.put("story_read_history_timestamp", FieldValue.serverTimestamp());
            //history.put("story_read_history_timestamp", FieldValue.serverTimestamp());
            mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("History").document(StoryId_or_serverName).
                    set(history,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                    }
                }
            });


        }else {
            mProgressDialog.dismiss();
            Toast.makeText(DisplayStoryContent.this,"Sorry Something went wrong",Toast.LENGTH_SHORT).show();
        }


        final DocumentReference userDataRef = mFireStoreDatabase.collection("Users").document(mAuth.getUid());
        userDataRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()) {
                        DocumentSnapshot dataSnapshot = task.getResult();
                        if (dataSnapshot != null) {
                            if (dataSnapshot.contains("image")) {
                                CurrentUserImageUrl=dataSnapshot.get("image").toString();

                                Picasso.with(DisplayStoryContent.this).load(CurrentUserImageUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_user_).into(CurrentUserImage,
                                        new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Log.e("onerrorSection", "this is success");
                                            }

                                            @Override
                                            public void onError() {
                                                Log.e("onerrorSection", "this is error");
                                                Picasso.with(DisplayStoryContent.this).load(CurrentUserImageUrl).placeholder(R.drawable.ic_user_).into(CurrentUserImage);
                                            }
                                        });
                            }if (dataSnapshot.contains("name")){
                                CurrentUserName=dataSnapshot.getString("name");
                                CurrentCommentUserName.setText(CurrentUserName);
                            }
                        }
                    }
                }
            }
        });

        /*GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        StoryReadMoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadStoryMainPopupMenu(v);
            }
        });*/
        MoreOptionForComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        SubmitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserRateToStory.getRating()>0){
                    mProgressDialog.show();
                    //NOn_cancelableProgressDialog.show();

                    if (!AlreadyRatedStar.equals(""+UserRateToStory.getRating())){
                        Log.e("ffdggerdfssfd","alredy rated if part ");
                        UpdateTotalRatingSumOuter();
                    }else {
                        Log.e("sdfhjks","alredy rated else part ");
                    }
                    AddCommentSection();
                }else {
                    Toast.makeText(DisplayStoryContent.this,R.string.PLEASE_RATE_FIRST,Toast.LENGTH_SHORT).show();
                }

            }
        });





        SuggestionList.setHasFixedSize(true);
        SuggestionListAdapter = new SectionListDataAdapter(DisplayStoryContent.this, SuggestionStoryList);
        SuggestionList.setLayoutManager(new LinearLayoutManager(DisplayStoryContent.this, LinearLayoutManager.HORIZONTAL, false));
        SuggestionList.setAdapter(SuggestionListAdapter);


        CommentList.setHasFixedSize(true);
        CommentListAdapter = new CommentDataAdapter(DisplayStoryContent.this, CommentArrayList);
        mCommentLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        CommentList.setLayoutManager(mCommentLayoutManager);
        CommentList.setAdapter(CommentListAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                LoadAllComments();
            }
        }, 500);


    }

    private void LayoutElementChangeToBlackAndWhite(boolean isBlack) {
        if (isBlack){
            CurrenUserRating.setBackgroundColor(getResources().getColor(R.color.grey_200));
            MainReadingCoordinator.setBackgroundColor(Color.WHITE);
            StoryContentDisplay.setTextColor(Color.BLACK);
            AdCardView2.setBackgroundColor(Color.WHITE);
            AdCardview1.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params1.setMargins(0, 0, 0, 30);
            AdCardview1.setLayoutParams(params1);
            params2.setMargins(0,30,0,0);
            AdCardView2.setLayoutParams(params2);
            SuggestionLayout.setBackgroundColor(Color.WHITE);
            UserAlreadyCommented.setBackgroundColor(getResources().getColor(R.color.grey_200));
            BottomCommentLayout.setBackgroundColor(getResources().getColor(R.color.grey_200));
            CommentList.setBackgroundColor(getResources().getColor(R.color.grey_100));
        }
        else {
            SuggestionLayout.setBackgroundColor(getResources().getColor(R.color.grey_900));
            AdCardView2.setBackgroundColor(getResources().getColor(R.color.grey_900));
            AdCardview1.setBackgroundColor(getResources().getColor(R.color.grey_900));
            StoryContentDisplay.setTextColor(Color.GRAY);
            CurrenUserRating.setBackgroundColor(getResources().getColor(R.color.grey_900));
            UserAlreadyCommented.setBackgroundColor(getResources().getColor(R.color.grey_900));
            MainReadingCoordinator.setBackgroundColor(Color.BLACK);
            CommentList.setBackgroundColor(Color.BLACK);
            BottomCommentLayout.setBackgroundColor(getResources().getColor(R.color.grey_900));
        }
    }

    private void SetBottomSheetElements() {
        int currentFontsize=Const.GetReadTextSize(DisplayStoryContent.this);
        boolean isDay=Const.IsDayVision(DisplayStoryContent.this);
        StoryContentDisplay.setTextSize(currentFontsize);
        if (currentFontsize>MAX_FONT_LIMIT){
            IncreaseFont.setBackgroundResource(R.drawable.disable_rectangle_selection);
        }
        if (currentFontsize<10){
            DecreaseFont.setBackgroundResource(R.drawable.disable_rectangle_selection);
        }
        LayoutElementChangeToBlackAndWhite(isDay);
        if (isDay){
            DaySelection.setBackgroundResource(R.drawable.selection_rectangle);
        } else{
            NightSelection.setBackgroundResource(R.drawable.selection_rectangle);
        }
    }

    private int getTotalNumberOfWordCount(String word) {
        if (word == null || word.isEmpty()) {
            return 0;
        }
        int count = 0; char ch[] = new char[word.length()];
        for (int i = 0; i < word.length(); i++) {
            ch[i] = word.charAt(i);
            if (((i > 0) && (ch[i] != ' ') && (ch[i - 1] == ' ')) || ((ch[0] != ' ') && (i == 0))) {
                count++;
            }
        }
        return count;
    }

    String DynamicShareLink="";
    public String  CreateDynamicLink(){
        final DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://"+StoryId_or_serverName+".com/"))
                .setDynamicLinkDomain("hcz7n.app.goo.gl")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();



        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLink.getUri())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            DynamicShareLink=""+shortLink;
                            if (isShare){
                                if (DynamicShareLink==null||DynamicShareLink.length()<=1){
                                    mProgressDialog.dismiss();
                                    ShareStory(DynamicShareLink);
                                }else {
                                    CreateDynamicLink();
                                }
                            }


                        } else {
                           DynamicShareLink=""+dynamicLink.getUri();
                        }
                    }
                });

        return DynamicShareLink;
    }

    public int getDaysDifference(long publishDate, long currentDate) {
        long diff=currentDate-publishDate;
        float dayCount = (float) diff / (24 * 60 * 60 * 1000);
        return  (int) dayCount;
    }

    private void UpdateTotalRatingSumOuter() {
        Log.e("ffdggerdfssfd","UpdateTotalRatingSumOuter is called ");
        final DocumentReference dataRef = mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").document(StoryId_or_serverName);

        dataRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    long TotalUserRatedToStory=1;
                    final DocumentSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()){

                        if (dataSnapshot.contains("Total_user_rated_to_story")){
                            TotalUserRatedToStory=dataSnapshot.getLong("Total_user_rated_to_story").longValue();
                            if (AlreadyRatedStar==null||AlreadyRatedStar.equals("0")){
                                TotalUserRatedToStory+=1;
                            }
                        }
                        if (dataSnapshot.contains("TotalRatingSum")){
                            Log.e("dfssdf","total number of story total rating sum "+dataSnapshot.getLong("TotalRatingSum").longValue()+" "+
                                    Math.round(UserRateToStory.getRating())+" "+
                                    Math.round(Float.parseFloat(AlreadyRatedStar)));

                            UpdateTotalRatingSum(dataSnapshot.getLong("TotalRatingSum").longValue()+
                                    Math.round(UserRateToStory.getRating())-
                                    Math.round(Float.parseFloat(AlreadyRatedStar)),(TotalUserRatedToStory));
                        }else {
                            UpdateTotalRatingSum(0+Math.round(UserRateToStory.getRating()),(TotalUserRatedToStory));
                        }
                    }else {
                        UpdateTotalRatingSum(0+Math.round(UserRateToStory.getRating()),(TotalUserRatedToStory));
                    }
                }
            }
        });
    }

    private void AddCommentSection() {
        Log.e("sdfhjks","AddCommentSection is called");
        final DocumentReference comentRef = mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").
                document(StoryId_or_serverName).collection("Comments").document(mAuth.getUid());

        /*Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c);*/

        final long timeInMillis = System.currentTimeMillis();

        Map loadData=new HashMap();
        loadData.put("starRating",""+UserRateToStory.getRating());
        loadData.put("comment",UserCommentEditText.getText().toString());
        loadData.put("sorting_key",getCommentSortingKeyId());
        //loadData.put("comment_date",formattedDate);
        //loadData.put("comment_timestamp_update",timeInMillis);
        loadData.put("comment_timestamp_update",FieldValue.serverTimestamp());
        loadData.put("comment_user_name",CurrentUserName);
        loadData.put("comment_user_image",CurrentUserImageUrl);
        comentRef.set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    comentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (AuthorDeviceTokenId==null||AuthorDeviceTokenId.length()<5){
                                mFireStoreDatabase.collection("Users").document(StoryAuthorId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            DocumentSnapshot documentSnapshot=task.getResult();
                                            AuthorDeviceTokenId=documentSnapshot.getString("device_token");
                                            CommentNotification(timeInMillis);
                                        }
                                    }
                                });
                            }else {
                                CommentNotification(timeInMillis);
                            }

                            Log.e("dkjfsklfsf","Already rated "+AlreadyRatedStar+" current user rating "+UserRateToStory.getRating());
                            if (AlreadyRatedStar.equals(""+UserRateToStory.getRating())){
                                SetVisibilityAndDismissProgress();
                                return;
                            }

                            final DocumentSnapshot dataSnapshot = task.getResult();
                            if (dataSnapshot.contains("UserRatingRankAlgoInsideComment")){
                                try {

                                    JSONObject ratingObj=new JSONObject(dataSnapshot.get("UserRatingRankAlgoInsideComment").toString());
                                    /************************************************Rate for first Time***********************/

                                    if (ratingObj.getString("rating_type").equals("null")
                                            &&ratingObj.getString("rating_number").equals("null")){
                                        int CurrentRating=(int)UserRateToStory.getRating();
                                        int UserRatingForRank=0;
                                        String RatingType="1";
                                        if (CurrentRating==1||CurrentRating==2){
                                            //RatingType="negative";
                                            if (CurrentRating==1){
                                                UserRatingForRank=-2;
                                                RatingType="1";

                                            }else if (CurrentRating==2){
                                                UserRatingForRank=-1;
                                                RatingType="2";
                                            }
                                        }else if(CurrentRating==3){
                                            //RatingType="neutral";
                                            RatingType="3";
                                            UserRatingForRank=0;
                                        }else {
                                            //RatingType="positive";
                                            if (CurrentRating==4){
                                                RatingType="4";
                                                UserRatingForRank=2;
                                            }else if (CurrentRating==5){
                                                RatingType="5";
                                                UserRatingForRank=3;
                                            }
                                        }
                                        Map<String, Object> docData = new HashMap<>();
                                        Map<String, Object> nestedData = new HashMap<>();
                                        nestedData.put("User_viewed", "true");
                                        nestedData.put("rating_type",RatingType);
                                        nestedData.put("rating_number",""+UserRatingForRank);
                                        docData.put("UserRatingRankAlgoInsideComment", nestedData);

                                        final int finalUserRatingForRank = UserRatingForRank;
                                        comentRef.set(docData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                final DocumentReference StoryRef = mFireStoreDatabase.collection("Story").document(StoryId_or_serverName);
                                                StoryRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()){
                                                            final DocumentSnapshot dataSnapshot = task.getResult();
                                                            long StoryAlltimeRank=0;
                                                            long PublishDate=0;
                                                            //String UserRatedforStory="0";
                                                            if(dataSnapshot.contains("all_time_rank")){
                                                                Log.e("hjsksd","this all time rank "+dataSnapshot.get("all_time_rank"));
                                                                StoryAlltimeRank=dataSnapshot.getLong("all_time_rank").longValue();

                                                                try{
                                                                    Log.e("jkhdksjfh","try part of timestamp published");
                                                                    Date date = (Date) dataSnapshot.get("story_timestamp_published");
                                                                    PublishDate=date.getTime();
                                                                }catch (Exception e){
                                                                    Log.e("jkhdksjfh","catch part of timestamp published");
                                                                    PublishDate=dataSnapshot.getLong("story_timestamp_published").longValue();
                                                                }
                                                                //PublishDate=dataSnapshot.getLong("story_timestamp_published").longValue();
                                                            }
                                                                           /* if (dataSnapshot.contains("NO_of_user_rated")){
                                                                                UserRatedforStory=dataSnapshot.getString("NO_of_user_rated");

                                                                            }*/
                                                            //final int finalUserRatedNo=Integer.parseInt(UserRatedforStory)+1;
                                                            final long finalRank=finalUserRatingForRank+StoryAlltimeRank;
                                                            final Map<String, Object> StoryRank = new HashMap<>();
                                                            StoryRank.put("all_time_rank", finalRank);
                                                            final long timeInMillis = System.currentTimeMillis();
                                                            if (getDaysDifference(PublishDate,timeInMillis)<Const.TRENDING_STORY_FREQUENCY){
                                                                StoryRank.put("trending_story_rank",finalRank);
                                                            }
                                                            //StoryData.put("NO_of_user_rated",""+finalUserRatedNo);

                                                            StoryRef.set(StoryRank,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        final DocumentReference dataRef = mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").document(StoryId_or_serverName);

                                                                        //StoryData.put("NO_of_user_rated",""+finalUserRatedNo);
                                                                        dataRef.set(StoryRank,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){
                                                                                        SetVisibilityAndDismissProgress();
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
                                        });

                                    }else {

                                        int CurrentRating=(int)UserRateToStory.getRating();
                                        int UserRatingForRank=0;
                                        final String PreviousRatingType=ratingObj.getString("rating_type");
                                        final String Previousrating=""+ratingObj.get("rating_number");
                                        String CurrentRatingType="";
                                        if (CurrentRating==1||CurrentRating==2){
                                            //CurrentRatingType="negative";
                                            if (CurrentRating==1){
                                                CurrentRatingType="1";
                                                UserRatingForRank=-2;
                                            }

                                            if (CurrentRating==2){
                                                UserRatingForRank=-1;
                                                CurrentRatingType="2";
                                            }
                                        }else if(CurrentRating==3){
                                            //CurrentRatingType="neutral";
                                            CurrentRatingType="3";
                                            UserRatingForRank=0;
                                        }else {
                                            //CurrentRatingType="positive";
                                            if (CurrentRating==4){
                                                UserRatingForRank=2;
                                                CurrentRatingType="4";
                                            }
                                            if (CurrentRating==5){
                                                UserRatingForRank=3;
                                                CurrentRatingType="5";
                                            }
                                        }
/******************************************If previous rating and current rating type is not equal***************************************************/

                                        if (!PreviousRatingType.equals(CurrentRatingType)){
                                            Map<String, Object> docData = new HashMap<>();
                                            Map<String, Object> nestedData = new HashMap<>();
                                            nestedData.put("User_viewed", "true");
                                            nestedData.put("rating_type",CurrentRatingType);
                                            nestedData.put("rating_number",""+UserRatingForRank);
                                            docData.put("UserRatingRankAlgoInsideComment", nestedData);
                                            final int finalUserRatingForRank = UserRatingForRank;

                                            comentRef.set(docData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){

                                                        final DocumentReference StoryRef = mFireStoreDatabase.collection("Story").document(StoryId_or_serverName);

                                                        StoryRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()){
                                                                    final DocumentSnapshot dataSnapshot = task.getResult();
                                                                    long StoryAlltimeRank=1;
                                                                    long PublishDate=1;
                                                                    if(dataSnapshot.contains("all_time_rank")){
                                                                        Log.e("hjsksd","this all time rank "+dataSnapshot.get("all_time_rank"));
                                                                        StoryAlltimeRank=dataSnapshot.getLong("all_time_rank").longValue();

                                                                        try{
                                                                            Log.e("jkhdksjfh","try part of timestamp published");
                                                                            Date date = (Date) dataSnapshot.get("story_timestamp_published");
                                                                            PublishDate=date.getTime();
                                                                        }catch (Exception e){
                                                                            Log.e("jkhdksjfh","catch part of timestamp published");
                                                                            PublishDate=dataSnapshot.getLong("story_timestamp_published").longValue();
                                                                        }

                                                                        //PublishDate=dataSnapshot.getLong("story_timestamp_published").longValue();
                                                                    }

                                                                                /*Log.e("execute_all_time","previous rating "+(-Integer.parseInt(Previousrating))+
                                                                                        "\n Story All time rank "+Integer.parseInt(StoryAlltimeRank)+" current User rating "+finalUserRatingForRank);
                                                                                */
                                                                    final long finalRank=(-Integer.parseInt(Previousrating))+StoryAlltimeRank+ finalUserRatingForRank;
                                                                    final Map<String, Object> StoryData = new HashMap<>();
                                                                    StoryData.put("all_time_rank", finalRank);
                                                                    final long timeInMillis = System.currentTimeMillis();
                                                                    if (getDaysDifference(PublishDate,timeInMillis)<Const.TRENDING_STORY_FREQUENCY){
                                                                        StoryData.put("trending_story_rank",finalRank);
                                                                    }

                                                                    StoryRef.set(StoryData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            final DocumentReference dataRef = mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").document(StoryId_or_serverName);

                                                                            dataRef.set(StoryData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    SetVisibilityAndDismissProgress();
                                                                                   /* UsercommentTExtView.setText(UserCommentEditText.getText().toString());
                                                                                    NoCommentTillNOw.setVisibility(View.GONE);
                                                                                    UserAlreadyCommented.setVisibility(View.VISIBLE);
                                                                                    MoreOptionForComment.setVisibility(View.VISIBLE);
                                                                                    Log.e("dkjfsklfsf","updating already rated stars "+AlreadyRatedStar);
                                                                                    new Handler().postDelayed(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            ShowAlreadyRatedStars(UserRateToStory.getRating());
                                                                                        }
                                                                                    }, 100);

                                                                                    mProgressDialog.dismiss();*/
                                                                                }
                                                                            });
                                                                        }
                                                                    });

                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });

                                        }else {
                                            Log.e("fsfsddd","dfksfkl_ddf");
                                        }



                                    }



                                }catch (JSONException e){

                                }
                            }
                        }
                    });



                            /*final DocumentReference UpdateRankCounter = mFireStoreDatabase.collection("Story").document(StoryId_or_serverName);

                            UpdateRankCounter.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        final DocumentSnapshot dataSnapshot = task.getResult();
                                        if (dataSnapshot.contains("view_count")&&dataSnapshot.contains("all_time_rank")) {
                                            int AllTimeRank = Integer.parseInt(dataSnapshot.getString("all_time_rank"));
                                            JSONObject ratingObj=new JSONObject(dataSnapshot.get("UserRatingRank").toString());
                                        }
                                        }
                                }
                            });
*/



                            /*

                            Map loadData=new HashMap();
                            loadData.put("all_time_rank",+UserRateToStory.getRating());
                            UpdateRankCounter.set(loadData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()){

                                    }
                                }
                            });*/

                }
            }
        });
    }

    private void SetVisibilityAndDismissProgress(){
        UsercommentTExtView.setText(UserCommentEditText.getText().toString());
        NoCommentTillNOw.setVisibility(View.GONE);
        UserAlreadyCommented.setVisibility(View.VISIBLE);
        MoreOptionForComment.setVisibility(View.VISIBLE);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(c);
        UsercommentTExtView.setVisibility(View.VISIBLE);
        UserCommentDate.setVisibility(View.VISIBLE);
        UserCommentDate.setText(formattedDate);

        Log.e("dkjfsklfsf","updating already rated stars "+AlreadyRatedStar);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ShowAlreadyRatedStars(UserRateToStory.getRating());
            }
        }, 100);

        mProgressDialog.dismiss();
    }

    private void CommentNotification(long timeInMillis){
        if(!StoryAuthorId.equals(mAuth.getUid())){

            Map loadData=new HashMap();
            loadData.put("comment_on_user",CurrentUserName+" commented on "+StoryTitle);
            loadData.put("comment_user_id",mAuth.getUid());
            if (UserCommentEditText.getText().toString().length()>1){
                loadData.put("message"," Says : " +UserCommentEditText.getText().toString());
            }else {
                loadData.put("message",CurrentUserName+" share his opinion about " +StoryTitle);
            }

            loadData.put("story_id",StoryId_or_serverName);
            loadData.put("author_device_token_id",AuthorDeviceTokenId);

            mFireStoreDatabase.collection("CommentNotificationData").document(StoryAuthorId).collection("CommentNotification")
                    .document(timeInMillis+"").set(loadData,SetOptions.merge());

        }
    }

    private void AddUserRatingRankInsideComment() {

        final DocumentReference comentRef = mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").
                document(StoryId_or_serverName).collection("Comments").document(mAuth.getUid());

        Log.e("ratingObject", "No user rating object ");
        Map<String, Object> docData = new HashMap<>();
        Map<String, Object> nestedData = new HashMap<>();
        nestedData.put("User_viewed", "true");
        nestedData.put("rating_type", "null");
        nestedData.put("rating_number", "null");
        docData.put("UserRatingRankAlgoInsideComment", nestedData);
        comentRef.set(docData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    final DocumentReference StorydataRef = mFireStoreDatabase.collection("Story").document(StoryId_or_serverName);
                    StorydataRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                final DocumentSnapshot dataSnapshot = task.getResult();
                                if (dataSnapshot.contains("view_count") && dataSnapshot.contains("all_time_rank")) {
                                    final long ViewCount = dataSnapshot.getLong("view_count").longValue();
                                    final long AllTimeRank = dataSnapshot.getLong("all_time_rank").longValue();
                                    long PublishDate;
                                    try{
                                        Date date = (Date) dataSnapshot.get("story_timestamp_published");
                                        Log.e("jkhdksjfh","try part of timestamp published");
                                        PublishDate=date.getTime();
                                    }catch (Exception e){
                                        Log.e("jkhdksjfh","catch part of timestamp published");
                                        PublishDate=dataSnapshot.getLong("story_timestamp_published").longValue();
                                    }

                                    final Map<String, Object> updateViewCount = new HashMap<>();
                                    updateViewCount.put("view_count", (ViewCount + 1));
                                    updateViewCount.put("all_time_rank",  (AllTimeRank + 1));

                                    final long timeInMillis = System.currentTimeMillis();
                                    if (getDaysDifference(PublishDate,timeInMillis)< Const.TRENDING_STORY_FREQUENCY){
                                        updateViewCount.put("trending_story_rank",(AllTimeRank + 1));
                                    }

                                    StorydataRef.set(updateViewCount, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    final DocumentReference storyRef = mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").
                                                            document(StoryId_or_serverName);

                                                    storyRef.set(updateViewCount,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                return;

                                                            }
                                                        }
                                                    });
                                                }
                                        }
                                    });

                                } else {
                                    Toast.makeText(DisplayStoryContent.this, "Story options are not completed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
        NoCommentTillNOw.setVisibility(View.VISIBLE);
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    private void UpdateTotalRatingSum(long totalRatingSum,long TotalUserRatedToStory) {
        //Log.e("sdfhjks","update total rating sum");
        final DocumentReference dataRef = mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").document(StoryId_or_serverName);

        double  AvgRating=((double)totalRatingSum/TotalUserRatedToStory);
        //totalRatingSum=38;
        //

            if (AvgRating>5.0){
                while (AvgRating>5.0){

                    TotalUserRatedToStory++;
                    AvgRating = ((double)totalRatingSum/TotalUserRatedToStory);
                    /*Log.e("sfjkfjsdkfs","total rating sum "+totalRatingSum+" total user rated user "+
                            TotalUserRatedToStory+" avgrating "+AvgRating);*/
                }
            }


        final Map loadData=new HashMap();
        Log.e("ffdggerdfssfd","in UpdateTotalRatingSum Total user rated "+TotalUserRatedToStory+" avg rating "+AvgRating+" totalRatingSum = "+totalRatingSum);
        //Log.e("sdfhjksSDDSF","AVG RATING ONLY update total rating sum "+AvgRating+" totalRatingSum = "+totalRatingSum+" TotalUserRatedToStory = "+TotalUserRatedToStory);
        if (TotalUserRatedToStory>0){
            loadData.put("TotalRatingSum",totalRatingSum);
            loadData.put("Total_user_rated_to_story",TotalUserRatedToStory);
            loadData.put("AvgRating",String.format("%.1f", AvgRating));

        }


        dataRef.set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    final DocumentReference dataRef = mFireStoreDatabase.collection("Story").document(StoryId_or_serverName);

                    dataRef.set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                return;
                            }
                        }
                    });
                }
            }
        });
    }

    /*private void updateUserRatingCountTostory() {
        final DocumentReference dataRef = mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").document(StoryId_or_serverName);
            Log.e("sdfhjks","updateUserRatingCountTostory is called ");
        dataRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()){
                        if (dataSnapshot.contains("Total_user_rated_to_story")){
                            UpdateUserRatingCounterInner(dataSnapshot.getLong("Total_user_rated_to_story").longValue());
                        }else {
                            UpdateUserRatingCounterInner(0);
                        }
                    }else {
                        UpdateUserRatingCounterInner(0);
                    }
                }
            }
        });

    }*/

  /*
    private void UpdateUserRatingCounterInner(final long countNumber){
        final DocumentReference dataRef = mFireStoreDatabase.collection("Users").document(StoryAuthorId)
                .collection("Story").document(StoryId_or_serverName);

        Log.e("sdfhjks","UpdateUserRatingCounterInner user rating is called "+countNumber);
        final Map loadData=new HashMap();
        loadData.put("Total_user_rated_to_story",(countNumber+1));

        dataRef.set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    mFireStoreDatabase.collection("Story").document(StoryId_or_serverName).set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){

                                if (!AlreadyRatedStar.equals(""+UserRateToStory.getRating())){
                                    Log.e("sdfhjks","UpdateUserRatingCounterInner user rating inner side");
                                    UpdateTotalRatingSumOuter();
                                }
                                AddCommentSection();
                            }
                        }
                    });


                }else {
                    Log.e("sdfhjks","UpdateUserRatingCounterInner user rating else part" );
                    UpdateUserRatingCounterInner(countNumber);
                }
            }
        });

    }*/

    private void ReadStoryMainPopupMenu(View view){
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.read_story_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMainMenuItemClickListener());
        popup.show();
    }

    class MyMainMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;
        public MyMainMenuItemClickListener() {
            //this.position=positon;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.report:

                    return true;

                case R.id.delete_comment:
                    return true;
                default:
            }
            return false;
        }
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.comment_more_options, popup.getMenu());
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

                case R.id.edit_comment:
                    UserAlreadyCommented.setVisibility(View.GONE);
                    NoCommentTillNOw.setVisibility(View.VISIBLE);
                    return true;

                case R.id.delete_comment:
                    mProgressDialog.show();

                    Map<String,Object> innerAlgo=new HashMap<>();

                    innerAlgo.put("User_viewed",true);
                    innerAlgo.put("rating_number",null);
                    innerAlgo.put("rating_type",null);

                    Map<String,Object> updates = new HashMap<>();
                    updates.put("starRating",FieldValue.delete());
                    updates.put("comment",FieldValue.delete());
                    updates.put("sorting_key",FieldValue.delete());
                    //updates.put("comment_date",FieldValue.delete());
                    updates.put("comment_timestamp_update",FieldValue.delete());
                    updates.put("comment_user_name",FieldValue.delete());
                    updates.put("comment_user_image",FieldValue.delete());
                    updates.put("UserRatingRankAlgoInsideComment",innerAlgo);

                   DocumentReference updateDelete= mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").
                            document(StoryId_or_serverName).collection("Comments").document(mAuth.getUid());

                    updateDelete.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                final float TempRating=UserRateToStory.getRating();
                                int finalActionRating=0;
                                if (TempRating==1){
                                    finalActionRating=2;
                                }
                                else if (TempRating==2){
                                    finalActionRating=1;
                                }
                                else if (TempRating==3){
                                    finalActionRating=0;
                                }
                                else if (TempRating==4){
                                    finalActionRating=-2;
                                }
                                else if (TempRating==5){
                                    finalActionRating=-3;
                                }


                                final int finalActionRating1 = finalActionRating;
                                mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").
                                        document(StoryId_or_serverName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            long NumberOF_userRatedStory,TotalRatingSum,AllTimeRank;
                                            DocumentSnapshot dataSnapshot=task.getResult();
                                            if (dataSnapshot.contains("Total_user_rated_to_story")){
                                                NumberOF_userRatedStory=dataSnapshot.getLong("Total_user_rated_to_story").longValue();
                                            }else {
                                                NumberOF_userRatedStory=0;
                                            }
                                            if (dataSnapshot.contains("TotalRatingSum")){
                                                TotalRatingSum=dataSnapshot.getLong("TotalRatingSum").longValue();
                                            }else {
                                                TotalRatingSum=0;
                                            }
                                            if (dataSnapshot.contains("all_time_rank")){
                                                AllTimeRank=dataSnapshot.getLong("all_time_rank").longValue();
                                            }else {
                                                AllTimeRank=0;
                                            }

                                            final Map<String,Object> updatesGlobalCommentStatus = new HashMap<>();

                                            if (NumberOF_userRatedStory>1){
                                                final double  AvgRating=((double)((TotalRatingSum-TempRating)/(NumberOF_userRatedStory-1)));
                                                Log.e("jskldjsk","Avg Rating "+AvgRating+" Total Rating sum "+TotalRatingSum+" temp rating is "+
                                                        TempRating+ " no of user rated "+NumberOF_userRatedStory);
                                                updatesGlobalCommentStatus.put("Total_user_rated_to_story",(NumberOF_userRatedStory-1));
                                                updatesGlobalCommentStatus.put("TotalRatingSum",(TotalRatingSum-TempRating));
                                                updatesGlobalCommentStatus.put("AvgRating",String.format("%.1f", AvgRating));
                                                updatesGlobalCommentStatus.put("all_time_rank",(AllTimeRank+ finalActionRating1));
                                            }else {
                                                updatesGlobalCommentStatus.put("Total_user_rated_to_story",0);
                                                updatesGlobalCommentStatus.put("TotalRatingSum",0);
                                                updatesGlobalCommentStatus.put("AvgRating",0);
                                                updatesGlobalCommentStatus.put("all_time_rank",(AllTimeRank+ finalActionRating1));
                                            }

                                            mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").
                                                    document(StoryId_or_serverName).set(updatesGlobalCommentStatus,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        mFireStoreDatabase.collection("Story").document(StoryId_or_serverName).set(updatesGlobalCommentStatus,SetOptions.merge())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){

                                                                            AlreadyRatedStar="0";
                                                                            mProgressDialog.dismiss();
                                                                            UserRateToStory.setRating(0);
                                                                            UserCommentEditText.setText("");
                                                                            UserAlreadyCommented.setVisibility(View.GONE);
                                                                            NoCommentTillNOw.setVisibility(View.VISIBLE);
                                                                            Toast.makeText(DisplayStoryContent.this,getString(R.string.COMMENT_DELETED),Toast.LENGTH_SHORT).show();

                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });




                                        }
                                    }
                                });



                            }else {
                                mProgressDialog.dismiss();
                            }
                        }
                    });
                    return true;

                default:
            }
            return false;
        }
    }

    private void ShowAlreadyRatedStars(float alreadyRatedStar) {
        Log.e("currentRatingStar","current rating "+alreadyRatedStar);
        AlreadyRatedStar=""+UserRateToStory.getRating();
        if (alreadyRatedStar==1){
            firstStart.setVisibility(View.VISIBLE);
            secondStar.setVisibility(View.GONE);
            thirdStar.setVisibility(View.GONE);
            fifthStar.setVisibility(View.GONE);
            fourthStar.setVisibility(View.GONE);
        }
        else if (alreadyRatedStar==2){
            firstStart.setVisibility(View.VISIBLE);
            secondStar.setVisibility(View.VISIBLE);
            thirdStar.setVisibility(View.GONE);
            fifthStar.setVisibility(View.GONE);
            fourthStar.setVisibility(View.GONE);
        }
        else if (alreadyRatedStar==3){
                firstStart.setVisibility(View.VISIBLE);
            secondStar.setVisibility(View.VISIBLE);
            thirdStar.setVisibility(View.VISIBLE);
            fifthStar.setVisibility(View.GONE);
            fourthStar.setVisibility(View.GONE);
        }
        else if (alreadyRatedStar==4){
            firstStart.setVisibility(View.VISIBLE);
            secondStar.setVisibility(View.VISIBLE);
            thirdStar.setVisibility(View.VISIBLE);
            fourthStar.setVisibility(View.VISIBLE);
            fifthStar.setVisibility(View.GONE);
        }
        else if (alreadyRatedStar==5){
            firstStart.setVisibility(View.VISIBLE);
            secondStar.setVisibility(View.VISIBLE);
            thirdStar.setVisibility(View.VISIBLE);
            fourthStar.setVisibility(View.VISIBLE);
            fifthStar.setVisibility(View.VISIBLE);
        }
    }
    public void PutInToSuggestionListNextParts(){

        Log.e("dfjsksjf","current parent id "+ParentStoryId);
               if (ParentStoryId!=null&&ParentStoryId.length()>5){
                  mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").document(ParentStoryId).
                          collection("All_Chapter_list").whereGreaterThan("chapter_counter",CurrentChapterNumber).orderBy("chapter_counter")
                          .limit(6).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                      @Override
                      public void onComplete(@NonNull Task<QuerySnapshot> task) {
                          if (task.isSuccessful()){

                              final int[] chaptercounterInside = {0};
                              final ArrayList<String> chapterIds=new ArrayList<String>();
                              for (DocumentSnapshot document : task.getResult()) {
                                  chapterIds.add(document.getId());
                              }

                              for (int i=0;i<chapterIds.size();i++) {

                                  mFireStoreDatabase.collection("Story").document(chapterIds.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                      @Override
                                      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                          if (task.isSuccessful()){
                                              chaptercounterInside[0]++;
                                              Log.e("dfjsksjf","calling inside for loop subsiquent chapters");
                                              DocumentSnapshot document = task.getResult();
                                              if (document.exists()){
                                                  if (document.getString("story_status").toString().equals("publish")&&
                                                          !document.getId().equals(StoryId_or_serverName)){
                                                      if (!SuggestionListIds.contains(document.getId())){
                                                          SuggestionListIds.add(document.getId());
                                                          SuggestionStoryList.add(new SingleStoryModel(1,document.getString("story_title"), document.getString("cover_image"), document.getString("story_intro"),
                                                                  document.get("view_count")+"",document.getString("AvgRating"),document.getId(),document.getString("story_author_id"),document.getBoolean("is_hide").booleanValue()));

                                                      }
                                                  }
                                              }

                                              /*if (chapterIds.size() -2<= chaptercounterInside[0]){

                                              }*/
                                              SuggestionListAdapter.notifyDataSetChanged();
                                          }
                                      }
                                  });
                              }
                              Log.e("dfjksfdf","outside for loop completed");

                              new Handler().postDelayed(new Runnable() {
                                  @Override
                                  public void run() {
                                      Log.e("dfjsksjf","go after for loop completed");
                                      PutSuggestionList();
                                  }
                              }, 700);

                          }
                      }
                  });
               }else {
                   Log.e("dfjsksjf","go else after for loop completed");
                   PutSuggestionList();
               }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("jkhfjksfs","inside on resume height of scroll view "+(StoryScrollRead.getChildAt(0).getMeasuredWidth()-
                getWindowManager().getDefaultDisplay().getHeight()));
    }

    public void PutSuggestionList(){
        Log.e("dfjsksjf","calling PutSuggestionList next suggestion");
        CollectionReference storyRef = mFireStoreDatabase.collection("Story");

        final ArrayList<SingleStoryModel> singleItem = new ArrayList<SingleStoryModel>();

        Log.e("kfjskjkdd","this is current language "+Const.CURRENT_LANGUAGE
                +" and story genre "+CurrentReadingGenre);
        Query getData=storyRef.whereEqualTo("story_genre",CurrentReadingGenre)
                .whereEqualTo("story_language",Const.CURRENT_LANGUAGE)
                .orderBy("all_time_rank", Query.Direction.DESCENDING)
                .limit(10);
        getData.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    Log.d("sdfhsdj", "TAsk successfull ", task.getException());

                    for (DocumentSnapshot document : task.getResult()) {
                            Log.d("sdfhsdj", document.getId() + " => " + document.getData());
                            if (document.getString("story_status").toString().equals("publish")&&
                                    !document.getId().equals(StoryId_or_serverName)){
                                if (!SuggestionListIds.contains(document.getId())){
                                    SuggestionListIds.add(document.getId());
                                    SuggestionStoryList.add(new SingleStoryModel(1,document.getString("story_title"), document.getString("cover_image"), document.getString("story_intro"),
                                            document.get("view_count")+"",document.getString("AvgRating"),document.getId(),document.getString("story_author_id"),document.getBoolean("is_hide").booleanValue()));

                                }

                            }
                    }
                } else {
                    Log.d("sdfhsdj", "Error getting documents: ", task.getException());
                }
                if (SuggestionListIds.size()==0){
                    SuggestionLayout.setVisibility(View.GONE);
                    AdCardView2.setVisibility(View.GONE);
                }else {
                    AdCardView2.setVisibility(View.VISIBLE);
                    SuggestionLayout.setVisibility(View.VISIBLE);
                    mAdview2.setVisibility(View.VISIBLE);
                    SuggestionListAdapter.notifyDataSetChanged();
                }

            }
        });
        getData.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("jfskfjskf","this is error "+e.getMessage());
                Toast.makeText(DisplayStoryContent.this,getString(R.string.SOMETHING_WENT_WRONG),Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void LoadAllComments(){
         CollectionReference comentRef = mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").
                document(StoryId_or_serverName).collection("Comments");


        comentRef.orderBy("comment_timestamp_update", Query.Direction.DESCENDING).limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d("sdfhsdj", document.getId() + " => " + document.getData());
                         if (!document.getId().equals(mAuth.getUid())){
                             if (!CommentIdList.contains(document.getId())){
                                 CommentIdList.add(document.getId());

                                 CommentArrayList.add(new CommentModel(document.getId(), document.getString("comment_user_image"), document.get("comment_timestamp_update"),
                                         document.get("starRating")+"",document.getString("comment"),document.getString("comment_user_name")));

                             }

                            }

                        Log.d("dfsfjskff", "Comment user name "+document.getString("comment_user_name")+" user rating is "+document.get("star_rating")+"");
                    }
                } else {
                    Log.d("sdfhsdj", "Error getting documents: ", task.getException());
                }
                CommentListAdapter.notifyDataSetChanged();
                if (CommentArrayList.size()>=9){
                    LoadMore_comment_text.setVisibility(View.VISIBLE);
                }

            }

        });

    }

    private void setupCommentEndlessListener(){
        CommentList.addOnScrollListener(new EndlessRecyclerViewScrollListener(mCommentLayoutManager, 2) {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CommentCounter++;
                        LoadAllComments();
                    }
                }, 150);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read_time_more_options, menu);
        return true;
    }

   /* private void onInviteClicked() {
        String  applink;
        if (DynamicShareLink==null||DynamicShareLink.length()<=1){
            applink=CreateDynamicLink();
        }else {
            applink=DynamicShareLink;
        }

        Uri uri = Uri.parse("android.resource://com.developers.rsons.connectingthoughts/drawable/card_defalt_back_cover");

        Map share_story=new HashMap();
        share_story.put("story_id",StoryId_or_serverName);

        Intent intent = new AppInviteInvitation.IntentBuilder("check connecting thoughts")
                .setMessage("Check out this story in connecting thoughts")
                .setDeepLink(Uri.parse(applink))
                .setCustomImage(uri)
                .setAdditionalReferralParameters(share_story)
                .setCallToActionText("TARGET_SHARE")
                .build();
        startActivityForResult(intent, 121);
    }*/

   private boolean isShare=false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if( android.R.id.home==item.getItemId()){
            onBackPressed();
        }else if (item.getItemId()==R.id.read_setting){

            if (BottomSheet.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                BottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);

            } else {
                BottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        }
        else if(item.getItemId()==R.id.share){

            //onInviteClicked();
            String  applink;
            if (DynamicShareLink==null||DynamicShareLink.length()<=1){
                isShare=true;
                mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT));
                mProgressDialog.show();
                CreateDynamicLink();
            }else {

                ShareStory(DynamicShareLink);
            }


        }else if (item.getItemId()==R.id.report){
            Intent LaunchReport=new Intent(DisplayStoryContent.this, ReportForStoryScreen.class);
            LaunchReport.putExtra("story_id",StoryId_or_serverName);
            startActivity(LaunchReport);
        }
        else if (item.getItemId()==R.id.add_to_libray){
            AddToLibrary();
        }

        return true;
    }


    private void AddToLibrary() {

        Map loadData=new HashMap();
        int storyNUmber=1;
        final long timeInMillis = System.currentTimeMillis();
        loadData.put("story_number",storyNUmber);
        loadData.put("timestamp_added_to_library", FieldValue.serverTimestamp());

        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Library").document(StoryId_or_serverName).
                set(loadData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(DisplayStoryContent.this,getString(R.string.SUCCESS_FULLY_ADDED_TO_LIBRARY),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        UpdateReadPosition();
    }

    @Override
    public void onBackPressed() {
        if (BottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            BottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

        } else {
            super.onBackPressed();
            Log.e("jkhfjksfs","total word count in back press "+getTotalNumberOfWordCount(StoryContentDisplay.getText().toString()));
            Log.e("jkhfjksfs","current scroll position "+StoryScrollRead.getScrollY());
            UpdateReadPosition();
        }

    }

    public void UpdateReadPosition(){
        final DocumentReference comentRef = mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").
                document(StoryId_or_serverName).collection("Comments").document(mAuth.getUid());

        Map loadData=new HashMap();
        Log.e("djsfksklfjs","this is current scroll position "+StoryScrollRead.getScrollY());
        loadData.put("last_read_position",""+StoryScrollRead.getScrollY());
        comentRef.set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

            }
        });
    }
    public void ShareStory(String applink){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.CHECK_OUT_CONNECTING_THOUGHTS));
        String ShareMessage="Hai I love " +StoryTitle+
                " story \n\n"+StoryIntro+"\n\n Read it completely free on connecting thoughts...";

        sharingIntent.putExtra(Intent.EXTRA_TEXT,ShareMessage+"\n\n"+ applink);
        startActivity(Intent.createChooser(sharingIntent, "Share with ..."));
    }


    public String getCommentSortingKeyId(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);

        String CT_Id= sdf.format(cal.getTime());
        long ctId=Long.parseLong(CT_Id);
        ctId=99999999999999L-ctId;
        CT_Id="CT"+ctId;
        return CT_Id;
    }
}
