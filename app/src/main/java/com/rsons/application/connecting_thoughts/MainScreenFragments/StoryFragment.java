package com.rsons.application.connecting_thoughts.MainScreenFragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.rsons.application.connecting_thoughts.Const;
import com.rsons.application.connecting_thoughts.EndlessRecyclerViewScrollListener;
import com.rsons.application.connecting_thoughts.Fetch_data.SectionDataModel;
import com.rsons.application.connecting_thoughts.Fetch_data.SingleStoryModel;
import com.rsons.application.connecting_thoughts.NativeAds.RecyclerViewAdapter;
import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.WritingActivity.EditPublishedStory;
import com.rsons.application.connecting_thoughts.WritingActivity.NonPublishedWriting;
import com.rsons.application.connecting_thoughts.WritingActivity.WriteStoryActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.android.gms.internal.zzahn.runOnUiThread;
import static com.rsons.application.connecting_thoughts.Const.IS_DIRECT_GO_BACK_FROM_STORY;

/**
 * Created by ankit on 2/9/2018.
 */

public class StoryFragment extends Fragment {

    public static FloatingActionButton AddNewStory;
    public static BottomSheetBehavior BottomSheet;
    private LinearLayout BottomlSheetLayout;

    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;
    private RecyclerView mTrendingStoryRecycler;
    RecyclerViewAdapter SectionAdapter;
    RecyclerView my_recycler_view;
    LinearLayoutManager linearLayout_manager;
    private ArrayList<Object> mRecyclerViewItems = new ArrayList<>();
    private List<NativeAd> mNativeAds = new ArrayList<>();

    private List<String> FeedRowList=new ArrayList<>();
    private int currentAdLoadedAt=0;

    public static final int NUMBER_OF_ADS = 2;

    LinearLayout CreateNewStory,EditDraftStory, Edit_PublishedStory_btn;
    //CreateNewAudioStory;

    //ArrayList<SectionDataModel> allSampleData=new ArrayList<>();

    private boolean StoryExist=false;
    private boolean IsOnScroll=true;
    //private ProgressBar LoadingProgress;
    RelativeLayout LoadingScreen;
    private ImageView MovingAlone;
    private TextView MovingAloneMessage;
    private ProgressBar LoadingCircle;
    private ImageView OfflineCloud;
    SwipeRefreshLayout swipeLayout;
    private boolean isSwipe=false;

    public StoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("kjsfdhjf","on onCreate is called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView=inflater.inflate(R.layout.first_story_frag_with_coordinate_layout, container, false);

        //LoadingProgress= (ProgressBar) mView.findViewById(R.id.loading_progress);
        my_recycler_view = (RecyclerView) mView.findViewById(R.id.trending_story);
        LoadingCircle= (ProgressBar) mView.findViewById(R.id.loading_circle);
        MovingAlone= (ImageView) mView.findViewById(R.id.empty_folder);
        LoadingScreen= (RelativeLayout) mView.findViewById(R.id.loading);
        MovingAloneMessage= (TextView) mView.findViewById(R.id.message_text);
        OfflineCloud= (ImageView) mView.findViewById(R.id.offline_cloud);




        swipeLayout= (SwipeRefreshLayout) mView.findViewById(R.id.swipeToRefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FeedRowList.clear();
                mRecyclerViewItems.clear();
                SectionAdapter.notifyDataSetChanged();
                isAdsLoaded=0;
                NotifyDataSetChange(my_recycler_view.getContext());
                LoadingScreen.setVisibility(View.VISIBLE);
                LoadFeedData(my_recycler_view.getContext(),1);
            }
        });

        Log.e("kjsfdhjf","on onCreateView is called");

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MobileAds.initialize(getActivity(), getString(R.string.admob_CT_app_id));

        AddNewStory= (FloatingActionButton) getActivity().findViewById(R.id.add_new_story);
        mTrendingStoryRecycler= (RecyclerView) getActivity().findViewById(R.id.trending_story);
        BottomlSheetLayout= (LinearLayout) getActivity().findViewById(R.id.bottom_sheet);
        BottomSheet = BottomSheetBehavior.from(BottomlSheetLayout);

        CreateNewStory= (LinearLayout) getActivity().findViewById(R.id.create_new_story);
        EditDraftStory= (LinearLayout) getActivity().findViewById(R.id.edit_draft_story);
        Edit_PublishedStory_btn = (LinearLayout) getActivity().findViewById(R.id.edit_published_story);
        //CreateNewAudioStory= (LinearLayout) getActivity().findViewById(R.id.add_new_audio_story);

        CreateNewStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewStory.setVisibility(View.VISIBLE);
                BottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Intent LaunchDraft=new Intent(getActivity(), WriteStoryActivity.class);
                LaunchDraft.putExtra("storyId","NO");
                startActivity(LaunchDraft);
                //getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_right);

            }
        });

        EditDraftStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewStory.setVisibility(View.VISIBLE);
                BottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Intent LaunchDraft=new Intent(getActivity(), NonPublishedWriting.class);
                startActivity(LaunchDraft);
                //getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_right);
            }
        });

        Edit_PublishedStory_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent LaunchDraft=new Intent(getActivity(), EditPublishedStory.class);
                startActivity(LaunchDraft);
                //getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_right);
                AddNewStory.setVisibility(View.VISIBLE);
                BottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });



        mFireStoreDatabase=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();

        /*Map <String , Object> sampleTime=new HashMap<>() ;
        sampleTime.put("timestamp",FieldValue.serverTimestamp());
        mFireStoreDatabase.collection("SampleTimeStamp").document("Time").set(sampleTime,SetOptions.merge());*/


        my_recycler_view.setHasFixedSize(true);
        SectionAdapter= new RecyclerViewAdapter(getActivity(), mRecyclerViewItems);
        linearLayout_manager =new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        my_recycler_view.setLayoutManager(linearLayout_manager);
        my_recycler_view.setAdapter(SectionAdapter);

        /*mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story")*/

        CollectionReference storyRef = mFireStoreDatabase.collection("Users").document(mAuth.getUid())
                .collection("Story");

        try {

            storyRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    int count = 0;
                    if (documentSnapshots!=null&&!documentSnapshots.isEmpty()){
                        for (DocumentSnapshot document : documentSnapshots) {
                            count++;
                        }
                        Log.e("sfhskjs","value of count is "+count);
                        if (count>0){
                            StoryExist=true;
                        }
                    }

                }
            });

        }catch (Exception e){

        }

        AddNewStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("sfhskjs","boolean value is "+StoryExist);
                if (StoryExist){
                    if (BottomSheet.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        BottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                        AddNewStory.setVisibility(View.GONE);

                    } else {
                        BottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

                    }
                }else {
                    Intent LaunchWriteStory=new Intent(getActivity(), WriteStoryActivity.class);
                    LaunchWriteStory.putExtra("addNewChapter",false);
                    startActivity(LaunchWriteStory);
                    //getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_right);
                }

            }
        });

        /*CreateNewAudioStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewStory.setVisibility(View.VISIBLE);
                BottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Intent LaunchAudioRecorder=new Intent(getActivity(), RecordNewAudioStory.class);
                startActivity(LaunchAudioRecorder);
            }
        });*/

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
                        AddNewStory.setVisibility(View.VISIBLE);
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

        if (IsOnScroll){
            //if (checkNetConnection()){
            LoadFeedData(my_recycler_view.getContext(),1);
            //loadNativeAd();
            /*}else {
                OfflineCloud.setVisibility(View.VISIBLE);
                MovingAloneMessage.setText(getString(R.string.NO_INTERNET_CONNECTION));
                MovingAloneMessage.setVisibility(View.VISIBLE);
                LoadingCircle.setVisibility(View.GONE);
            }*/

        }else {

            if (mRecyclerViewItems.size()>0){
                LoadingScreen.setVisibility(View.GONE);
                SectionAdapter.notifyDataSetChanged();
            }else {
                mRecyclerViewItems.clear();
                LoadFeedData(my_recycler_view.getContext(),1);
            }

        }

    }

    @Override
    public void onResume() {
        if (BottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED){
            BottomSheetCollpse();
        }
        if (IS_DIRECT_GO_BACK_FROM_STORY){
            IS_DIRECT_GO_BACK_FROM_STORY=false;
            mRecyclerViewItems.clear();
            FeedRowList.clear();
            SectionAdapter.notifyDataSetChanged();
            isAdsLoaded=0;
            NotifyDataSetChange(my_recycler_view.getContext());
            LoadingScreen.setVisibility(View.VISIBLE);
            LoadFeedData(my_recycler_view.getContext(),1);
        }
        Log.e("fskfjksf","resume method is called");
        super.onResume();
    }

    public static void BottomSheetCollpse(){
        AddNewStory.setVisibility(View.VISIBLE);
        BottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

   /* private void insertAdsInMenuItems() {
        Log.e("kjsfdhjf","insertAdsInMenuItems is called ");
        if (mNativeAds.size() <= 0) {
            return;
        }
        currentAdLoadedAt=2;
        //int offset = (mRecyclerViewItems.size() / mNativeAds.size()) + 1;
        int index = 0;
        for (NativeAd ad: mNativeAds) {

            mRecyclerViewItems.add(currentAdLoadedAt, ad);
            currentAdLoadedAt+=2;
            //index = index + offset;
        }
        loadMenu();
    }*/
    private void loadNativeAd() {
        loadMenu();
       // loadNativeAd(0);
    }

    private void loadNativeAd(final int adLoadCount) {

        /*if (adLoadCount >= NUMBER_OF_ADS) {
            //insertAdsInMenuItems();
            loadMenu();
            return;
        }*/

      /*  AdLoader.Builder builder = new AdLoader.Builder(getActivity(), getString(R.string.ad_unit_id));
        AdLoader adLoader = builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
            @Override
            public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
                // An app install ad loaded successfully, call this method again to
                // load the next ad in the items list.
                mNativeAds.add(ad);
                loadNativeAd(adLoadCount + 1);

            }
        }).forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
            @Override
            public void onContentAdLoaded(NativeContentAd ad) {
                // A content ad loaded successfully, call this method again to
                // load the next ad in the items list.
                mNativeAds.add(ad);
                loadNativeAd(adLoadCount + 1);
            }
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // A native ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e("MainActivity", "The previous native ad failed to load. Attempting to" +
                        " load another.");
                loadNativeAd(adLoadCount + 1);
            }
        }).build();

        // Load the Native Express ad.
        adLoader.loadAd(new AdRequest.Builder().build());*/
    }
    private void loadMenu(){


    }

    public boolean checkNetConnection(){
        boolean isConnection;
        ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null){
            isConnection=false;
        }else{
            isConnection=true;
        }
        return isConnection;
    }



    @Override
    public void onStart() {
        super.onStart();
        Log.e("kjsfdhjf","on Start is called");

    }


    private int FeedLoopCounter;
   /* private void setupEndlessListener(){
        my_recycler_view.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayout_manager, 2) {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        LoadFeedData(my_recycler_view.getContext(),FeedLoopCounter);
                    }
                }, 150);
            }
        });
    }

*/

    int innerCounter=1;
    public void LoadFeedData(final Context ctx, final int loopcounter) {

        IsOnScroll=false;
        if (loopcounter<16){
            //final SectionDataModel dm = new SectionDataModel();
            //mRecyclerViewItems.add(new SectionDataModel());
            /*if (i==2||i==4||i==6){
                mRecyclerViewItems.add(new SectionDataModel());

            }*/
            Log.e("dsjkjksfsds","Current language is "+Const.CURRENT_LANGUAGE );
                CollectionReference storyRef = mFireStoreDatabase.collection("Story");

                final ArrayList<SingleStoryModel> singleItem = new ArrayList<SingleStoryModel>();
            String SectionName;

            Query query;
            if (loopcounter==1){
                /***************************************Trending Story***********************************/
                SectionName=ctx.getString(R.string.NEW_STORY_on_ct);
                query=storyRef.whereEqualTo("story_language",Const.CURRENT_LANGUAGE).whereEqualTo("story_status","publish").
                        orderBy("story_timestamp_published", Query.Direction.DESCENDING).limit(10);

            }else if (loopcounter==2){
                /***************************************New Story Story***********************************/

                SectionName=ctx.getString(R.string.TRENDING_STORY_on_ct);
                query=storyRef.whereEqualTo("story_language",Const.CURRENT_LANGUAGE).whereEqualTo("story_status","publish").
                        orderBy("trending_story_rank", Query.Direction.DESCENDING)
                        .limit(10);
            }
            else if (loopcounter==3){
                /***************************************All Time Best Story***********************************/
                SectionName=ctx.getString(R.string.ALL_TIME_BEST_STORY_ON_CT);
                query=storyRef.whereEqualTo("story_language",Const.CURRENT_LANGUAGE).whereEqualTo("story_status","publish").
                        orderBy("all_time_rank", Query.Direction.DESCENDING).limit(10);

            }
            else if (loopcounter==4){
                /***************************************Motivation day Best Story***********************************/
                SectionName=ctx.getString(R.string.STORY_ON_MOTIVATION);
                query=storyRef.whereEqualTo("story_language",Const.CURRENT_LANGUAGE).whereEqualTo("story_genre",Const.ThoughtGenreId[0])
                        .whereEqualTo("story_status","publish")
                        .orderBy("story_timestamp_published", Query.Direction.DESCENDING).limit(10);

            }else if (loopcounter==5){
                /***************************************Struggle Best Story***********************************/
                SectionName=ctx.getString(R.string.STORY_ON_STRUGGLE);
                query=storyRef.whereEqualTo("story_language",Const.CURRENT_LANGUAGE).whereEqualTo("story_genre",Const.ThoughtGenreId[1])
                        .whereEqualTo("story_status","publish")
                        .orderBy("story_timestamp_published", Query.Direction.DESCENDING).limit(10);

            }else if (loopcounter==7){
                /***************************************Opinion Best Story***********************************/
                SectionName=ctx.getString(R.string.STORY_ON_MEMORIES);
                query=storyRef.whereEqualTo("story_language",Const.CURRENT_LANGUAGE).whereEqualTo("story_genre",Const.ThoughtGenreId[2])
                        .whereEqualTo("story_status","publish")
                        .orderBy("story_timestamp_published", Query.Direction.DESCENDING).limit(10);

            }

            else if (loopcounter==6){
                /***************************************Dream Best Story***********************************/
                SectionName=ctx.getString(R.string.STORY_ON_DREAM);
                query=storyRef.whereEqualTo("story_language",Const.CURRENT_LANGUAGE).whereEqualTo("story_genre",Const.ThoughtGenreId[3])
                        .whereEqualTo("story_status","publish")
                        .orderBy("story_timestamp_published", Query.Direction.DESCENDING).limit(10);

            }


            else if (loopcounter==8){
                /***************************************Opinion Best Story***********************************/
                SectionName=ctx.getString(R.string.FOLKS_OPINION);
                query=storyRef.whereEqualTo("story_language",Const.CURRENT_LANGUAGE).whereEqualTo("story_genre",Const.ThoughtGenreId[4])
                        .whereEqualTo("story_status","publish")
                        .orderBy("story_timestamp_published", Query.Direction.DESCENDING).limit(10);

            }else if (loopcounter==9){
                /***************************************Your Day Story Best Story***********************************/
                SectionName=ctx.getString(R.string.PATRIOTISM);
                Log.e("kdhfsjf","this is folks story day "+Const.ThoughtGenreId[4]);
                query=storyRef.whereEqualTo("story_language",Const.CURRENT_LANGUAGE).whereEqualTo("story_genre",Const.ThoughtGenreId[5])
                        .whereEqualTo("story_status","publish")
                        .orderBy("story_timestamp_published", Query.Direction.DESCENDING).limit(10);

            }
            else if (loopcounter==10){
                /***************************************Your Day Story Best Story***********************************/
                SectionName=ctx.getString(R.string.STORY_ON_folks_DAY);
                query=storyRef.whereEqualTo("story_language",Const.CURRENT_LANGUAGE).whereEqualTo("story_genre",Const.ThoughtGenreId[6])
                        .whereEqualTo("story_status","publish")
                        .orderBy("story_timestamp_published", Query.Direction.DESCENDING).limit(10);

            } else if (loopcounter==11){
                /***************************************Comedy Best Story***********************************/
                SectionName=ctx.getString(R.string.STORY_ON_COMEDY);
                query=storyRef.whereEqualTo("story_language",Const.CURRENT_LANGUAGE).whereEqualTo("story_genre",Const.ThoughtGenreId[7])
                        .whereEqualTo("story_status","publish")
                        .orderBy("story_timestamp_published", Query.Direction.DESCENDING).limit(10);

            }else if (loopcounter==12){
                /***************************************Love Best Story***********************************/
                SectionName=ctx.getString(R.string.STORY_ON_Love);
                query=storyRef.whereEqualTo("story_language",Const.CURRENT_LANGUAGE).whereEqualTo("story_genre",Const.ThoughtGenreId[8])
                        .whereEqualTo("story_status","publish")
                        .orderBy("story_timestamp_published", Query.Direction.DESCENDING).limit(10);

            }else if (loopcounter==13){
                /***************************************Confession Best Story***********************************/
                SectionName=ctx.getString(R.string.STORY_ON_CONFESSION);
                query=storyRef.whereEqualTo("story_language",Const.CURRENT_LANGUAGE).whereEqualTo("story_genre",Const.ThoughtGenreId[9])
                        .whereEqualTo("story_status","publish")
                        .orderBy("story_timestamp_published", Query.Direction.DESCENDING).limit(10);

            }else if (loopcounter==14){
                /***************************************Remarkable Creation Best Story***********************************/
                SectionName=ctx.getString(R.string.POETRY);
                query=storyRef.whereEqualTo("story_language",Const.CURRENT_LANGUAGE).whereEqualTo("story_genre",Const.ThoughtGenreId[10])
                        .whereEqualTo("story_status","publish")
                        .orderBy("story_timestamp_published", Query.Direction.DESCENDING).limit(10);

            }
            else if (loopcounter==15){
                /***************************************Remarkable Creation Best Story***********************************/
                SectionName=ctx.getString(R.string.STORY_ON_Remarkabele_Creation);
                query=storyRef.whereEqualTo("story_language",Const.CURRENT_LANGUAGE).whereEqualTo("story_genre",Const.ThoughtGenreId[11])
                        .whereEqualTo("story_status","publish")
                        .orderBy("story_timestamp_published", Query.Direction.DESCENDING).limit(10);

            }
            else {
                SectionName=ctx.getString(R.string.ALL_TIME_BEST_STORY_ON_CT);
                query=storyRef.whereEqualTo("story_status","publish").whereEqualTo("story_language",Const.CURRENT_LANGUAGE).
                        orderBy("all_time_rank", Query.Direction.DESCENDING).limit(10);

            }

            final SectionDataModel dm = new SectionDataModel();
            dm.setHeaderTitle(SectionName);
                query.get().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("kdhfsjf","this is failed to load the data link is "+e.getMessage());
                    }
                });


            final String finalSectionName = SectionName;
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            Log.e("dsjkjksfsds","task is successful");

                            if (!FeedRowList.contains(loopcounter+"")){
                                FeedRowList.add(loopcounter+"");
                                for (DocumentSnapshot document : task.getResult()) {

                                    singleItem.add(new SingleStoryModel(1,document.getString("story_title"), document.getString("cover_image"), document.getString("story_intro"),
                                            document.get("view_count")+"",""+document.get("AvgRating"),document.getId(),document.getString("story_author_id"),document.getBoolean("is_hide").booleanValue()));

                                }
                            }

                            //dm.setAllItemsInSection(singleItem);



                            //mRecyclerViewItems.add(new SectionDataModel(finalSectionName,singleItem));


                            if (singleItem.size()!=0){

                                dm.setAllItemsInSection(singleItem);
                                mRecyclerViewItems.add(dm);
                                SectionAdapter.notifyDataSetChanged();
                                NotifyDataSetChange(ctx);
                                LoadingScreen.setVisibility(View.GONE);
                                //singleItem.clear();
                            }
                            FeedLoopCounter=loopcounter+1;
                            LoadFeedData(ctx,FeedLoopCounter);


                            if (FeedLoopCounter>=14){
                                swipeLayout.setRefreshing(false);
                                if (mRecyclerViewItems.size()==0){
                                    MovingAlone.setVisibility(View.VISIBLE);
                                    MovingAloneMessage.setVisibility(View.VISIBLE);
                                    MovingAloneMessage.setText(getString(R.string.NOTHING_TO_SHOW));
                                    LoadingCircle.setVisibility(View.GONE);
                                }
                            }



                        } else {
                            Log.d("dsjkjksfsds", "Error getting documents: ", task.getException());
                        }

                    }
                });
            //}
            //setupEndlessListener();
        }else {


            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    //LoadingCircle.setVisibility(View.GONE);
                }
            }, 1000);*/

            return;
        }

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRecyclerViewItems.size()==0){
                    LoadingCircle.setVisibility(View.GONE);
                    MovingAlone.setVisibility(View.VISIBLE);
                    MovingAloneMessage.setVisibility(View.VISIBLE);
                    MovingAloneMessage.setText(getString(R.string.NO_REPORT_YET));
                }else {
                    LoadingScreen.setVisibility(View.GONE);
                }
            }
        }, 5000);*/

    }

    public int isAdsLoaded=0;

    private void NotifyDataSetChange(Context ctx){

        if (isAdsLoaded<5){
            //isAdsLoaded=true;

            if (mRecyclerViewItems.size()==2||mRecyclerViewItems.size()==5||
                    mRecyclerViewItems.size()==8||mRecyclerViewItems.size()==11||mRecyclerViewItems.size()==14) {
                isAdsLoaded++;
                mRecyclerViewItems.add(new AdView(ctx));
                SectionAdapter.notifyDataSetChanged();
                //LoadingCircle.setVisibility(View.GONE);
            }else {
                return;
            }

            //mRecyclerViewItems.add(4,new SectionDataModel("Sample",new ArrayList<SingleStoryModel>()));
            //mRecyclerViewItems.add(6,new SectionDataModel("Sample",new ArrayList<SingleStoryModel>()));
        }



    }


}

