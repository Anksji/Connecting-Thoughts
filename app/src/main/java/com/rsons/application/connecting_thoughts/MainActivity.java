package com.rsons.application.connecting_thoughts;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rsons.application.connecting_thoughts.AboutUs.AboutUs_Connecting_thoughts;
import com.rsons.application.connecting_thoughts.Become_A_Partner.Become_Partner_First_Activity;
import com.rsons.application.connecting_thoughts.LibraryCollection.HistoryListing_of_Previous_Read_Story;
import com.rsons.application.connecting_thoughts.LibraryCollection.LibraryListing_of_all_story;
import com.rsons.application.connecting_thoughts.MainScreenFragments.ChatFragment;
import com.rsons.application.connecting_thoughts.MainScreenFragments.FriendOrFollowerFragment;
import com.rsons.application.connecting_thoughts.MainScreenFragments.StoryFragment;
import com.rsons.application.connecting_thoughts.NavigationDrawer.CustomExpandableListAdapter;
import com.rsons.application.connecting_thoughts.NavigationDrawer.ExpandableListDataSource;
import com.rsons.application.connecting_thoughts.NewConnectionRequest.NewConnectionRequestActivity;
import com.rsons.application.connecting_thoughts.Notification.AllNotificationListing;
import com.rsons.application.connecting_thoughts.Notification.BadgeDrawable;
import com.rsons.application.connecting_thoughts.Notification.NotificationSetting;
import com.rsons.application.connecting_thoughts.ReadStoryActivity.ViewFirstPage_of_Story;
import com.rsons.application.connecting_thoughts.Report.ReportListingInsideHelpTab;
import com.rsons.application.connecting_thoughts.Search.SearchDbHepler;
import com.rsons.application.connecting_thoughts.Search.SearchProvider;
import com.rsons.application.connecting_thoughts.Search.Search_Result;
import com.rsons.application.connecting_thoughts.Search.SetKeywordsNumber;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.rsons.application.connecting_thoughts.WritingActivity.WriteStoryActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.rsons.application.connecting_thoughts.Const.IS_DIRECT_GO_BACK_FROM_CHAT;
import static com.rsons.application.connecting_thoughts.Const.IS_DIRECT_GO_BACK_FROM_FOLLOWER;
import static com.rsons.application.connecting_thoughts.Const.IS_DIRECT_GO_BACK_FROM_STORY;
import static com.rsons.application.connecting_thoughts.MainScreenFragments.StoryFragment.BottomSheet;
import static com.rsons.application.connecting_thoughts.MainScreenFragments.StoryFragment.BottomSheetCollpse;
import static com.rsons.application.connecting_thoughts.Notification.FirebaseNotificationService.NOTIFICATION_SHARED_PREF_NAME;

public class MainActivity extends AppCompatActivity implements SearchView.OnSuggestionListener{

    ArrayList<String> ReportAccesList = new ArrayList<String>() {{
        add("hBbSHb69d0VOMJEweIJZqSg9hgO2");
        add("ttFVuZ8g95bxlJ8OGZsaB1HNk5u1");
    }};

    private RelativeLayout Edit_Profile;
    private TextView UserName;
    private ImageView NavigationBaground;
    private CircleImageView UserImage;
    private int UPDATE_SEARCH_SUGGESTION_COUNTER=1;

    private FirebaseUser mCurrentUser;
    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;
   // private DatabaseReference mUserDatabase;
    Toolbar mToolbar;
    public static SearchDbHepler searchKeysSQLite;
    public static SetKeywordsNumber KeyWordPrefrence;
    //long OnlineKeyword_Number;

    private List<String>SUGGESTIONS =new ArrayList<>();
    /*private static final String[] SUGGESTIONS = {
            "Bauru", "Sao Paulo", "Rio de Janeiro",
            "Bahia", "Mato Grosso", "Minas Gerais",
            "Tocantins", "Rio Grande do Sul"
    };*/

    private SimpleCursorAdapter mAdapter;


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_man_thinking,
            R.drawable.ic_speech_bubbles_comment_option,
            R.drawable.ic_bookmark_ribbon
    };

    private ExpandableListView mExpandableListView;
    private ExpandableListAdapter mExpandableListAdapter;
    private List<String> mExpandableListTitle;
    private LinkedHashMap<String, List<String>> mExpandableListData;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    public static Context context;
    DocumentReference dataRef;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String CurrnetLang= Const.GetLanguageFromSharedPref(MainActivity.this);
        for(int i=0;i<Const.languageId.length;i++){
            Log.e("fjskfjks","inside for loop setting language "+CurrnetLang);
            if (CurrnetLang.equals(Const.languageId[i])){
                Log.e("fjskfjks","setting language "+CurrnetLang+" language iso code is "+Const.languageISO[i]);
                Const.CT_setLocale(MainActivity.this,Const.languageISO[i]);
                break;
            }
        }
/*
        String languageToLoad  = "hi"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());*/

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(mToolbar);

        mAuth = FirebaseAuth.getInstance();

        mCurrentUser = mAuth.getCurrentUser();

        mFireStoreDatabase=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);
        dataRef = mFireStoreDatabase.collection("Users").document(mAuth.getUid());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Locale current = getResources().getConfiguration().locale;
        changeLocale(MainActivity.this,current);

        Log.e("skfjskfjsfk","hai this is current local "+current);

        final SharedPreferences prefs = this.getSharedPreferences(NOTIFICATION_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String finalStoryId=prefs.getString("share_story_id","");

        if (finalStoryId.length()>1){
            mFireStoreDatabase.collection("Story").document(finalStoryId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot=task.getResult();
                        if (documentSnapshot.exists()){
                            Intent LaunchViewStoryFirstPage=new Intent(MainActivity.this, ViewFirstPage_of_Story.class);
                            LaunchViewStoryFirstPage.putExtra("storyTitle",documentSnapshot.getString("story_title"));
                            if (documentSnapshot.contains("cover_image")){
                                LaunchViewStoryFirstPage.putExtra("story_cover",documentSnapshot.getString("cover_image"));
                            }else {
                                LaunchViewStoryFirstPage.putExtra("story_cover","");
                            }

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("share_story_id","");
                            editor.apply();
                            LaunchViewStoryFirstPage.putExtra("view_count",""+documentSnapshot.get("view_count"));
                            LaunchViewStoryFirstPage.putExtra("story_info",documentSnapshot.getString("story_intro"));
                            LaunchViewStoryFirstPage.putExtra("story_rating",documentSnapshot.getString("AvgRating"));
                            LaunchViewStoryFirstPage.putExtra("story_Id",finalStoryId);
                            LaunchViewStoryFirstPage.putExtra("from_story_listing",false);
                            LaunchViewStoryFirstPage.putExtra("story_AuthorId",documentSnapshot.getString("story_author_id"));
                            LaunchViewStoryFirstPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(LaunchViewStoryFirstPage);
                        }
                    }
                }
            });
        }





        /*AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, false).setResultCallback(*//* ... *//*);
        String link = AppInviteReferral.getDeepLink(intent);

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData data) {
                        if (data == null) {
                            Log.d("kdfjkjgkggfd", "getInvitation: no data");
                            return;
                        }

                        // Get the deep link
                        Log.e("kdfjkjgkggfd","getting getUpdateAppIntent "+data.getUpdateAppIntent(MainActivity.this));
                        Uri deepLink = data.getLink();
                        Log.e("kdfjkjgkggfd","getting the link from play store "+deepLink);

                        // Extract invite
                        FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(data);
                        if (invite != null) {
                            String invitationId = invite.getInvitationId();
                            Log.e("kdfjkjgkggfd","getting the invitationId from play store "+invitationId);

                        }

                        // Handle the deep link
                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("kdfjkjgkggfd", "getDynamicLink:onFailure"+e.getMessage());
                    }
                });*/


        MobileAds.initialize(this, getString(R.string.admob_CT_app_id));

        Const.CURRENT_LANGUAGE = Const.GetLanguageFromSharedPref(MainActivity.this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        searchKeysSQLite=new SearchDbHepler(MainActivity.this);
        KeyWordPrefrence=new SetKeywordsNumber(MainActivity.this);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setupDrawer();
        mDrawerToggle.syncState();
        context=this;
        setUpNavigatinList(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setNavBar();
            }
        }, 50);






        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SetUserImageAndName();
            }
        }, 250);

        final String[] from = new String[] {"cityName"};
        final int[] to = new int[] {android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(MainActivity.this,
                R.layout.custom_search_suggestion_layout,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        //setHasOptionsMenu(true);

        /*Intent intent  = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        }*/

        //PrintcompleteSearchKeys();


    }

    public static void changeLocale(Context context, Locale locale) {
        Log.e("skjskfjks","we are setting language");
        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, res.getDisplayMetrics());
    }


    private void SetUserImageAndName(){
        Log.e("jfskljsksd","cT language "+ Locale.getDefault().getDisplayLanguage());

        dataRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {
                try {
                    if (dataSnapshot != null){
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.contains("name")){
                                String ProfileVisit_name = dataSnapshot.get("name").toString();
                                if (ProfileVisit_name.equals("")){
                                    UserName.setText("User Name");
                                }else {
                                    UserName.setText(dataSnapshot.get("name").toString());
                                }
                            }
                            String image="default";
                            if (dataSnapshot.contains("image")){
                                image= dataSnapshot.get("image").toString();
                            }


                            if (!image.equals("default")) {

                                final String finalImage = image;
                                Picasso.with(MainActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_user_).into(UserImage,
                                        new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Log.e("onerrorSection", "this is success");
                                            }

                                            @Override
                                            public void onError() {
                                                Log.e("onerrorSection", "this is error");
                                                Picasso.with(MainActivity.this).load(finalImage).placeholder(R.drawable.ic_user_).into(UserImage);
                                            }
                                        });
                                Picasso.with(MainActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_user_).into(NavigationBaground,
                                        new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Log.e("onerrorSection", "this is success");
                                            }

                                            @Override
                                            public void onError() {
                                                Log.e("onerrorSection", "this is error");
                                                Picasso.with(MainActivity.this).load(finalImage).placeholder(R.drawable.ic_user_).into(NavigationBaground);
                                            }
                                        });


                                //Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.ic_user_).into(UserImage);
                            }

                        } else {
                            Log.d("data", "No such document");
                        }
                    }
                }catch (Exception e1){

                }

            }
        });

        /*dataRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    if (task.getResult().exists()){
                        DocumentSnapshot dataSnapshot = task.getResult();

                    }

                } else {
                    Log.d("data", "get failed with ", task.getException());
                }
            }
        });*/

        if (Const.getIsFirstTime(MainActivity.this)){
            ShowSavingAlertDialog();
        }


        //RequestPermission();
        ResetNotificationParams();
        //CheckforsuggestionKeywordUpdate();
    }

    public void RequestPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
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

    private void ShowSavingAlertDialog() {
        AlertDialog.Builder builder;
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(WriteStoryActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(WriteStoryActivity.this);
        }*/
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.DO_YOU_WANT_SOME_HELP))
                .setMessage(getString(R.string.GO_TO_HELP_MESSAGE))
                .setPositiveButton(R.string.GO_TO_HELP, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Const.SetIsFirstTime(false,MainActivity.this);
                        Intent LaunchHelp=new Intent(MainActivity.this,HelpINConnectingThoughts.class);
                        startActivity(LaunchHelp);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Const.SetIsFirstTime(false,MainActivity.this);
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_help)
                .show();
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    private void setNavBar(){
        mExpandableListView = (ExpandableListView) findViewById(R.id.expandable_nav_list_view);
        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.nav_header, null, false);

        final RelativeLayout profile_container = (RelativeLayout)listHeaderView.findViewById(R.id.hb_profile_container);
        UserImage= (CircleImageView) listHeaderView.findViewById(R.id.user_image);


        //final ImageView banner_image = (ImageView) listHeaderView.findViewById(R.id.hb_banner);
        UserName = (TextView) listHeaderView.findViewById(R.id.user_name);
        NavigationBaground= (ImageView) listHeaderView.findViewById(R.id.navigation_header_background);


        View listFooterView = inflater.inflate(R.layout.nav_footer, null, false);
        UserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchProfileActivity=new Intent(MainActivity.this,EditProfile_Activity.class);
                startActivity(launchProfileActivity);
                //overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        /*NavFBIcon = (ImageView) listFooterView.findViewById(R.id.nav_footer_fb_icon);
        *//*NavTwitterIcon = (ImageView) listFooterView.findViewById(R.id.nav_footer_twitter_icon);
        NavYoutubeIcon = (ImageView) listFooterView.findViewById(R.id.nav_footer_youtube_icon);*//*
        NavCompanyName = (TextView) listFooterView.findViewById(R.id.nav_footer_company_name);

        // Handle links at the navigation footer.
        NavFBIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Goto_Web_Page(0,0);
            }
        });

       *//* NavTwitterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        NavYoutubeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });*//*

        NavCompanyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
*/
        mExpandableListView.addHeaderView(listHeaderView);
        mExpandableListView.addFooterView(listFooterView);

        final String sections[] = {
                this.getResources().getString(R.string.Language),
                this.getResources().getString(R.string.NEW_CONNECTION_R),
                this.getResources().getString(R.string.Library),
                this.getResources().getString(R.string.History),
               /* this.getResources().getString(R.string.Setting),*/
                this.getResources().getString(R.string.PARTERS),
                this.getResources().getString(R.string.HELP),
                this.getResources().getString(R.string.ABOUT_US),
                this.getResources().getString(R.string.Share),
                this.getResources().getString(R.string.Rate_Us)};

        mExpandableListData = ExpandableListDataSource.getData(Const.language, sections);


        mExpandableListTitle = new ArrayList(mExpandableListData.keySet());
        addDrawerItems();

    }

    private void closeDrawer(){
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void addDrawerItems() {
        mExpandableListAdapter = new CustomExpandableListAdapter(this, mExpandableListTitle, mExpandableListData);
        mExpandableListView.setAdapter(mExpandableListAdapter);
        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                handleNavMenuItemClick(groupPosition);
            }
        });

        mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                handleNavMenuItemClick(groupPosition);
            }
        });


        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                if (groupPosition == 0) {
                    String language;
                    Const.setLanguage(Const.languageId[childPosition],MainActivity.this);
                    Const.CT_setLocale(MainActivity.this,Const.languageISO[childPosition]);
                    RestartActivity(MainActivity.this);

                    Log.e("sdfs", "Clicked Position "+childPosition);

                    closeDrawer();
                }



                return false;
            }
        });
    }

    private void handleNavMenuItemClick(int groupPosition){

        switch(groupPosition){
            /**
             * NAV_RATE takes the user to our app's link in the Play store,
             * so that users can review and rate our app
             */

            case Const.NEW_CONNECTION_REQUEST:
                Intent RequestList=new Intent(MainActivity.this, NewConnectionRequestActivity.class);
                startActivity(RequestList);
                closeDrawer();
                break;

            case Const.LIBRARY:
                    Intent GoToLivrary=new Intent(MainActivity.this, LibraryListing_of_all_story.class);
                    startActivity(GoToLivrary);
                closeDrawer();

                break;
            case Const.HISTORY:
                Intent GotoHistory=new Intent(MainActivity.this, HistoryListing_of_Previous_Read_Story.class);
                startActivity(GotoHistory);
                closeDrawer();

                break;
            /*case Const.SETTING:
                Intent OpenSetting=new Intent(MainActivity.this, NotificationSetting.class);
                startActivity(OpenSetting);
                //Toast.makeText(MainActivity.this,"currently setting is not working",Toast.LENGTH_SHORT).show();
                closeDrawer();

                break;*/
            case Const.BECOME_PARTERNS:
                Intent LaunchPartner=new Intent(MainActivity.this, Become_Partner_First_Activity.class);
                startActivity(LaunchPartner);

                closeDrawer();
                break;
            case Const.HELP:
                if (ReportAccesList.contains(mAuth.getUid())){
                    Intent LaunchReport=new Intent(MainActivity.this, ReportListingInsideHelpTab.class);
                    startActivity(LaunchReport);
                }else {
                    Intent LaunchReport=new Intent(MainActivity.this, HelpINConnectingThoughts.class);
                    startActivity(LaunchReport);

                }

                break;
            case Const.ABOUT_US:
                Intent LaunchReport=new Intent(MainActivity.this, AboutUs_Connecting_thoughts.class);
                startActivity(LaunchReport);


                break;
            case Const.SHARE:
                String  applink;
                try{
                    applink = ""+ Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                }catch (android.content.ActivityNotFoundException anfe){
                    applink = ""+Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                }

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Get Connecting Thoughts from Google Play Store and enjoy to explore yourself." +
                        " Get up now its time and meet peoples like you.");

                sharingIntent.putExtra(Intent.EXTRA_TEXT, applink);
                startActivity(Intent.createChooser(sharingIntent, "Share with ..."));
                closeDrawer();
                break;
            case Const.RATE:
                //Toast.makeText(MainActivity.this,"RATE_US is clicked",Toast.LENGTH_SHORT).show();
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                closeDrawer();
                break;
        }
    }



    public static void RestartActivity(Activity mConte) {
        mConte.finish();
        Intent main = new Intent(mConte, MainActivity.class);
        context.startActivity(main);
    }
    public static void setUpNavigatinList(Context ctx){
        //language

        Const.GenresMap.put("Bengali",ctx.getResources().getString(R.string.bengali));
        Const.GenresMap.put("English", ctx.getResources().getString(R.string.english));
        Const.GenresMap.put("Gujarati",ctx.getResources().getString(R.string.gujarati));
        Const.GenresMap.put("Hindi", ctx.getResources().getString(R.string.hindi));
        Const.GenresMap.put("Kannada",ctx.getResources().getString(R.string.kannada));
        Const.GenresMap.put("Malayalam",ctx.getResources().getString(R.string.malayalam));
        Const.GenresMap.put("Marathi",ctx.getResources().getString(R.string.marathi));
        Const.GenresMap.put("Oriya",ctx.getResources().getString(R.string.oriya));
        Const.GenresMap.put("Punjabi",ctx.getResources().getString(R.string.punjabi));
        Const.GenresMap.put("Sanskrit",ctx.getResources().getString(R.string.sanskrit));
        Const.GenresMap.put("Tamil",ctx.getResources().getString(R.string.tamil));
        Const.GenresMap.put("Telugu",ctx.getResources().getString(R.string.telugu));


    }


    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText(getString(R.string.STORY));
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_man_thinking, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText(getString(R.string.CHAT));
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_speech_bubbles_comment_option, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText(getString(R.string.FOLLOWERS));
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_children, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if (BottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED){
            BottomSheetCollpse();
        }
        else {

            KeyWordPrefrence.setKeyWordNumber(searchKeysSQLite.getNoOfKeyWords_In_Search_Queue());
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            IS_DIRECT_GO_BACK_FROM_STORY=true;
            IS_DIRECT_GO_BACK_FROM_CHAT=true;
            IS_DIRECT_GO_BACK_FROM_FOLLOWER=true;
            SearchUpdateRunning=false;
            //super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        PrintcompleteSearchKeys();
        Log.e("keySearchList","calling onresume SearchUpdateRunning "+SearchUpdateRunning);
        if (!SearchUpdateRunning){

            CheckforsuggestionKeywordUpdate();
        }



    }

    /* @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.main,menu);
            return true;
        }
    */
    /*public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        //menuInflater.inflate(R.menu.main, menu);

        getMenuInflater().inflate(R.menu.main, menu);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            search.setSuggestionsAdapter(mAdapter);
            search.setIconifiedByDefault(false);

            search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionClick(int position) {
                    // Your code here
                    return true;
                }

                @Override
                public boolean onSuggestionSelect(int position) {
                    // Your code here
                    return true;
                }
            });
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    populateAdapter(s);
                    return false;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }*/


    private void PrintcompleteSearchKeys(){

        Log.e("keySearchList","print complete search key call");
        ArrayList<String> SampleList=new ArrayList<>();
        SampleList= searchKeysSQLite.GetKeyWords();

        Log.e("keySearchList","Current list number "+Const.GetCurrentSearchListNumber(MainActivity.this));
        for (int i=0;i<SampleList.size();i++){
            Log.e("keySearchList","At position "+i+" keyword is "+SampleList.get(i));
        }

    }

    public static boolean SearchUpdateRunning=false;

    public void CheckforsuggestionKeywordUpdate(){

        if (SearchUpdateRunning){
            return;
        }
        //searchKeysSQLite.DeleteallSuggestionList();
        Log.e("kjfkjfsfkj","call CheckforsuggestionKeywordUpdate");
        //final long offlineKeyword_number=KeyWordPrefrence.getKeyWordNumber();
        SearchUpdateRunning=true;
        new MyAsyncTask().execute();

    }



    public class MyAsyncTask extends AsyncTask< Void, Void, String > {
        @Override
        protected String doInBackground(Void...params) {

            String CurrentListNUmber=Const.GetCurrentSearchListNumber(MainActivity.this);
            Log.e("keySearchList","we are is background process "+CurrentListNUmber);
            UpdateSearchListINTO_sqlite(CurrentListNUmber);
            /*UpdateSearchSuggestionIN_Sqlite(mFireStoreDatabase.collection("Search").document("suggestion_list").collection("List")
                    .orderBy("suggestion_keyword").limit(50));
*/

            return "";
        }
        @Override
        protected void onPostExecute(String result) {
            //SearchUpdateRunning=false;
        }
    }

    private void UpdateSearchListINTO_sqlite(final String currentListNUmber) {

        mFireStoreDatabase.collection("Search").document("suggestion_list").collection("Multi_List"+currentListNUmber).document("List"+ currentListNUmber)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Log.e("keySearchList"," UpdateSearchListINTO_sqlite currentListNUmber "+currentListNUmber);
                    DocumentSnapshot document=task.getResult();
                    if (document.exists()){

                        String JSONsEARCH_LIST=""+document.get("keywordList");
                        Log.e("keySearchList"," UpdateSearchListINTO_sqlite inside document exist "+JSONsEARCH_LIST);
                        int loopCounter=1;
                        try{

                            Log.e("keySearchList"," inside try block ");

                            JSONObject json = new JSONObject(JSONsEARCH_LIST);
                            Log.e("keySearchList"," before while loop "+json);
                            Iterator<String> iter = json.keys();

                            while (iter.hasNext()) {
                                //Log.e("keySearchList"," inside while loop "+iter.next());
                                try {
                                    String searchkeyId = iter.next();
                                    loopCounter++;
                                    String keyword = ""+json.get(searchkeyId);
                                    String RemoveSpaceActualKeyword=keyword.replaceAll("~"," ");
                                    String ActualKeyword=RemoveSpaceActualKeyword.replaceAll("`",",");
                                    Log.e("keySearchList","actual key word "+ActualKeyword);
                                   if (!searchKeysSQLite.IsKeyIdExist(searchkeyId)){
                                       if (!searchKeysSQLite.IsKeyWordExist(ActualKeyword)){
                                           searchKeysSQLite.AddSearchKeyWords(searchkeyId, ActualKeyword);
                                       }
                                   }else {
                                       searchKeysSQLite.UpdateSearchKeyValue(searchkeyId,ActualKeyword);
                                   }

                                } catch (JSONException e) {
                                    // Something went wrong!
                                    Log.e("keySearchList","we are in catch block with error "+e.getMessage());
                                }
                            }

                        }catch (JSONException e){
                            Log.e("keySearchList","we are in catch block with error "+e.getMessage());
                        }

                        if (loopCounter>=Const.SUGGESTION_LIST_BOX){
                            mFireStoreDatabase.collection("Search").document("CurrentListNumber").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        Log.e("keySearchList","hai we are inside current list number ");
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if (documentSnapshot.exists()) {
                                            int ServerCurrentListNumber = Integer.parseInt(documentSnapshot.getString("currentList_Number"));

                                            int CurrentListNumber = Integer.parseInt(currentListNUmber);
                                            Log.e("keySearchList", "ServerCurrentListNumber =" + ServerCurrentListNumber +
                                                    " CurrentListNumber " + CurrentListNumber);
                                            if (CurrentListNumber <= ServerCurrentListNumber) {
                                                Log.e("keySearchList", "document exist setting new counter value");
                                                if (CurrentListNumber == ServerCurrentListNumber) {
                                                    Log.e("keySearchList", "no increment to CurrentListNumber both are equal");
                                                    Const.setCurrentSearchListNumber((CurrentListNumber) + "", MainActivity.this);
                                                    // UpdateSearchListINTO_sqlite((CurrentListNumber)+"");
                                                }else {
                                                    Log.e("keySearchList", " increment CurrentListNumber is less then  ServerCurrentListNumber");
                                                    ++CurrentListNumber;
                                                    Const.setCurrentSearchListNumber((CurrentListNumber) + "", MainActivity.this);
                                                    UpdateSearchListINTO_sqlite((CurrentListNumber) + "");
                                                }

                                            } else {
                                                Log.e("keySearchList", "no increment both are  equal");
                                                Const.setCurrentSearchListNumber((ServerCurrentListNumber) + "", MainActivity.this);
                                                //UpdateSearchListINTO_sqlite((CurrentListNumber) + "");
                                            }
                                        }
                                    }
                                }
                            });
                        }

                    }
                }
            }
        });

    }
/*

    private void UpdateSearchSuggestionIN_Sqlite(Query query) {

        query.get().

                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (!documentSnapshots.isEmpty()){


                            for (int i=0;i<documentSnapshots.size();i++){
                                DocumentSnapshot document=documentSnapshots.getDocuments().get(i);
                                if (!searchKeysSQLite.IsKeyWordExist(document.getString("suggestion_keyword"))){
                                    searchKeysSQLite.AddSearchKeyWords(document.getString("suggestion_keyword"));
                                }

                                Log.e("kjfkjfsfkj","add search words "+document.getId());
                            }


                            DocumentSnapshot lastVisible = documentSnapshots.getDocuments()
                                    .get(documentSnapshots.size() -1);


                            Query next = mFireStoreDatabase.collection("Search").document("suggestion_list").collection("List")
                                    .orderBy("suggestion_keyword")
                                    .startAfter(lastVisible)
                                    .limit(50);
                            UpdateSearchSuggestionIN_Sqlite(next);
                        }else {

                            //KeyWordPrefrence.setKeyWordNumber(OnlineKeyword_Number);

                            Log.e("kjfkjfsfkj","completed search update");

                        }
                    }
                });


    }
*/

  /*  @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.action_search));

        // Getting selected (clicked) item suggestion

        return false;
    }
*/




    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new StoryFragment(), "ONE");
        adapter.addFrag(new ChatFragment(), "TWO");
        adapter.addFrag(new FriendOrFollowerFragment(), "THREE");
        viewPager.setAdapter(adapter);

        boolean FromChat=getIntent().getBooleanExtra("fromChat",false);
        if (FromChat){
            viewPager.setCurrentItem(1);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }


        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
        }
    }

    public  void setBadgeCount(Context context, LayerDrawable icon) {

        final BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        mFireStoreDatabase.collection("NotificationData").document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("notification_counter")){
                        int notification_count=(int)documentSnapshot.getLong("notification_counter").longValue();
                        Log.e("fsjfskfsd","this is notification counter "+notification_count);
                        if (notification_count>0){
                            if (notification_count>9){
                                badge.setCount("9+");
                            }else {
                                badge.setCount(""+notification_count);
                            }

                        }


                    }

                }
            }
        });



        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);

    }


    public  void setBadgeCount(Context context, LayerDrawable icon,String Count) {

        final BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(Count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.notification_icon){
            LayerDrawable icon = (LayerDrawable) item.getIcon();
            setBadgeCount(this, icon,"0");
            Intent NotificationListing=new Intent(MainActivity.this, AllNotificationListing.class);
            startActivity(NotificationListing);
        }

       /* if(item.getItemId()==R.id.account_setting){
            Intent SettingActivityintent=new Intent(MainActivity.this, EditProfile_Activity.class);
            startActivity(SettingActivityintent);
        }
        else if(item.getItemId()==R.id.all_users){
            *//*Intent SettingActivityintent=new Intent(MainActivity.this, AllUsers_List.class);
            startActivity(SettingActivityintent);*//*
        }*//*else if (item.getItemId()==R.id.action_search){
            Intent SettingActivityintent=new Intent(MainActivity.this, SearchActivity.class);
            startActivity(SettingActivityintent);
        }*/

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        activateSearchVeiw(menu);

        MenuItem itemCart = menu.findItem(R.id.notification_icon);
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        setBadgeCount(this, icon);



        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                Log.d("ThisQuery","Ye query submit ho rahi hai "+query);
                Intent in=new Intent(MainActivity.this,Search_Result.class);
                in.putExtra("Search_keyWord",query);
                startActivity(in);

                //WordMeaning word= mysearchDataBase.getExactSearchedWord(query);
                //Toast.makeText(MainTopicActivity.this,"Submitted query  "+word.getWord(),Toast.LENGTH_SHORT).show();
                return true;

            }
        };
        MySearch.setOnQueryTextListener(queryTextListener);


        return true;
    }

    SearchView MySearch;



    @Override
    public boolean onSuggestionSelect(int position) {
        return true;
    }

    @Override
    public boolean onSuggestionClick(int position) {

        SearchProvider mySearch=new SearchProvider();
        Intent in=new Intent(MainActivity.this,Search_Result.class);
        String  MySearchValue=mySearch.SuggetionWord.get(position);
        in.putExtra("Search_keyWord",MySearchValue);
        startActivity(in);

        return true;
    }


    private void activateSearchVeiw(Menu menu) {

        Log.e("dfskfjslsf","activate search view");
        MenuItem searchItem=menu.findItem(R.id.mysearch);
        MySearch= (SearchView) MenuItemCompat.getActionView(searchItem);

        SearchView.SearchAutoComplete autoCompleteTextView = (SearchView.SearchAutoComplete) MySearch.findViewById(R.id.search_src_text);
        if (autoCompleteTextView != null) {
            //autoCompleteTextView.setD

            autoCompleteTextView.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.ic_rectangle_white));
        }

        final SearchManager searchManager= (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MySearch.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this,MainActivity.class)
        ));

        MySearch.setOnSuggestionListener(this);
    }


}
