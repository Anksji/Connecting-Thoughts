package com.rsons.application.connecting_thoughts.ReadStoryActivity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.rsons.application.connecting_thoughts.Material_Views_classes.SwipeDetector;
import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.Single_StoryChapterListing.StoryChapterListingAdapter;
import com.rsons.application.connecting_thoughts.Single_StoryChapterListing.Story_Chapter_model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ankit on 3/2/2018.
 */

public class ChapterListingFragment extends Fragment {


    RecyclerView recyclerView;
    public static ArrayList<Story_Chapter_model> storyChapterList =new ArrayList<>();
    public static ArrayList<String> StoryChapterListId=new ArrayList<>();
    private StoryChapterListingAdapter chapter_story_list_adapter;
    LinearLayoutManager mLayoutManager;
    LinearLayout CompleteFragmentLayout;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    String StoryId_or_serverName,StoryAuthorId;
    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;
    private RelativeLayout fragmentMargin;
    private LinearLayout AddToYourLibrary;
    private ImageView AddLibraryIcon;
    private TextView AddToLibraryText;
    public static int storyNUmber=0;
    public static boolean IsAlreadyAddedToLibrary;
    private String ParentStoryId;
    public static String CurrentParentChapter;
    private String NewChapterRequest;
    private ImageView FrammentgoBack;


    public ChapterListingFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chapter_listing_fragment,
                container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.story_chapter_list);
        CompleteFragmentLayout= (LinearLayout) view.findViewById(R.id.fragment_layout);
        AddLibraryIcon= (ImageView) view.findViewById(R.id.add_to_library_icon);
        AddToLibraryText= (TextView) view.findViewById(R.id.add_to_library_text);
        FrammentgoBack= (ImageView) view.findViewById(R.id.fragment_go_back);

        new SwipeDetector(CompleteFragmentLayout).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
            @Override
            public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum swipeType) {
                if(swipeType==SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT)
                    getActivity().onBackPressed();
            }
        });

        mFireStoreDatabase=FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        StoryId_or_serverName = getArguments().getString("story_Id");
        StoryAuthorId=getArguments().getString("authorId");
        NewChapterRequest=getArguments().getString("parentStoryId");
        ParentStoryId=NewChapterRequest;
        Log.e("skdfsfkflsd","this is parent id "+StoryId_or_serverName);
        Log.e("thisIs_storyId"," "+StoryId_or_serverName+" Author id "+StoryAuthorId);



        recyclerView.setHasFixedSize(true);
        chapter_story_list_adapter = new StoryChapterListingAdapter(getActivity(), storyChapterList);
        mLayoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(chapter_story_list_adapter);
        AddToYourLibrary= (LinearLayout) view.findViewById(R.id.add_to_your_library);

        fragmentMargin=(RelativeLayout)view.findViewById(R.id.fragment_margin);

        AddToYourLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsAlreadyAddedToLibrary){
                    Toast.makeText(getActivity(),getString(R.string.THIS_cREATIONaLREADYaDDED),Toast.LENGTH_SHORT).show();
                    return;
                }
                AddToLibrary(ParentStoryId);
            }
        });

        fragmentMargin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        FrammentgoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        if (storyChapterList.size()>0&&CurrentParentChapter!=null&&CurrentParentChapter.length()>5){
            if (CurrentParentChapter.equals(NewChapterRequest)){
                if (IsAlreadyAddedToLibrary){
                    AddToLibraryText.setText(getString(R.string.ALREADY_ADDED));
                    AddLibraryIcon.setImageResource(R.drawable.ic_correct);
                }
                Story_Chapter_model.setCurrent_story_id(StoryId_or_serverName);
                chapter_story_list_adapter.notifyDataSetChanged();
            }else {
                storyNUmber=0;
                IsAlreadyAddedToLibrary=false;
                CurrentParentChapter=null;
                StoryChapterListId.clear();
                storyChapterList.clear();
                LoadChapterList(NewChapterRequest);
            }

        }else {

            LoadChapterList(NewChapterRequest);
        }

        return view;
    }

    private void AddToLibrary(String parentStoryId) {

        Map loadData=new HashMap();
        final long timeInMillis = System.currentTimeMillis();
        loadData.put("story_number",storyNUmber);
        loadData.put("timestamp_added_to_library", FieldValue.serverTimestamp());

        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Library").document(parentStoryId).
                set(loadData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    IsAlreadyAddedToLibrary=true;
                    AddLibraryIcon.setImageResource(R.drawable.ic_correct);
                    AddToLibraryText.setText(getString(R.string.ALREADY_ADDED));
                    Toast.makeText(getActivity(),getString(R.string.SUCCESS_FULLY_ADDED_TO_LIBRARY),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
/*

    private void CheckForChapters(){
        mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").document(StoryId_or_serverName).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                Log.e("sdkfjksfs","if part this check for chapter is called");

                                */
/*************************************If parent is exist*******************************************//*

                                if (document.contains("parent_story_id")){

                                    ParentStoryId=document.getString("parent_story_id");
                                    mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").document(ParentStoryId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                DocumentSnapshot document = task.getResult();
                                                Log.e("sdkfjksfs","parent exist "+document.getString("story_title"));

                                                if (document.exists()){
                                                    //storyChapterList.add(new Story_Chapter_model(ParentStoryId, document.getString("story_title"),StoryId_or_serverName));

                                                    LoadChapterList(ParentStoryId);
                                                }

                                            }
                                        }
                                    });

                                }else {
*/
/*************************************************************************Individual story parent not found *************************************************************************//*


                                    ParentStoryId=StoryId_or_serverName;
                                    storyChapterList.add(new Story_Chapter_model(StoryId_or_serverName, document.getString("story_title")));
                                    Story_Chapter_model.setCurrent_story_id(StoryId_or_serverName);
                                    LoadChapterList(StoryId_or_serverName);
                                    storyNUmber++;
                                    //WriteAnotherChapter(StoryId_or_serverName);
                                }

                            }else {
                                Log.e("sdkfjksfs","else part document does not exist");
                            }
                        }
                    }
                });
    }
*/

    private void LoadChapterList(final String storyParentName) {

        Log.e("dfjskfjskf","author id "+StoryAuthorId+" story parent id "+storyParentName);
        if (StoryAuthorId!=null&&storyParentName!=null){
            mFireStoreDatabase.collection("Users").document(StoryAuthorId).collection("Story").document(storyParentName).
                    collection("All_Chapter_list").orderBy("chapter_counter", Query.Direction.ASCENDING).
                    addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            if (documentSnapshots!=null){
                                if (!documentSnapshots.isEmpty()){

                                    CurrentParentChapter=storyParentName;
                                    for (DocumentSnapshot document : documentSnapshots){

                                        if (document.getString("story_status").equals("publish")&&!document.getString("story_title").equals("First_Chapter")){
                                            if (!StoryChapterListId.contains(document.getId())){
                                                StoryChapterListId.add(document.getId());
                                                storyNUmber++;
                                                Log.e("kdjfdskfs","author id "+StoryAuthorId+" and doc id "+document.getId());
                                                storyChapterList.add(new Story_Chapter_model(document.getId(), document.getString("story_title")));
                                                Story_Chapter_model.setCurrent_story_id(StoryId_or_serverName);
                                                chapter_story_list_adapter.notifyDataSetChanged();
                                                Log.e("kflksjkd","final this is list size at last "+storyChapterList.size());
                                            }

                                        }
                                    }

                                    mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Library").document(ParentStoryId).
                                            addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(DocumentSnapshot document, FirebaseFirestoreException e) {
                                                    if (document!=null){

                                                        if (document.exists()){
                                                            Activity activity = getActivity();
                                                            if(activity != null && isAdded()){

                                                                if (document.exists()){
                                                                    IsAlreadyAddedToLibrary=true;
                                                                    AddToLibraryText.setText(getString(R.string.ALREADY_ADDED));
                                                                    AddLibraryIcon.setImageResource(R.drawable.ic_correct);
                                                                }else {
                                                                    IsAlreadyAddedToLibrary=false;
                                                                }
                                                            }

                                                        }else {
                                                            IsAlreadyAddedToLibrary=false;
                                                        }
                                                    }
                                                }
                                            });

                                }
                            }

                        }
                    });
        }


    }

}