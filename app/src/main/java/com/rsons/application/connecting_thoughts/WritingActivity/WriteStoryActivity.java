package com.rsons.application.connecting_thoughts.WritingActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.RTToolbar;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.onegravity.rteditor.api.format.RTFormat;
import com.rsons.application.connecting_thoughts.Const;
import com.rsons.application.connecting_thoughts.MainActivity;
import com.rsons.application.connecting_thoughts.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;

/**
 * Created by ankit on 2/10/2018.
 */

public class WriteStoryActivity extends AppCompatActivity{

    private static int FINAL_DRAFT_SAVE=10;
    private FirebaseUser mCurrentUser;

    private StorageReference mImageStorageRef;
    private FirebaseStorage mfirebaseStorrageRef;
    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;

    private EditText Title_of_story;
    private RelativeLayout AddCoverImageButton;
    private ImageView Image_more_options;
    private int UpdateProcessRunning=0;
    private boolean IsFinalSaveDraft=true;
   // private String CompleteStoryText;

    //Handler handler = new Handler();
    byte[] user_byte;
    ProgressDialog mProgressDialog;
    private ImageView Cover_image;
    private boolean isUpdated=true;
    private String StoryServerName="";
    private Toolbar mStoryToolbar;
    private TextView ToolBarTextView;
    private String DownloadImageUrl;

    private String mAuthorName,mUserProfilePic;

    private BottomSheetBehavior BottomSheet;
    private LinearLayout BottomlSheetLayout;

    //private EditText KeyWordsOrTags;
    private TextView generText,languageText;
    private Spinner GenreSpinner,LanguageSpinner;
    private RelativeLayout GenreLayout,LanguageLayout;
    private EditText TellAboutYourStory;

    //private boolean IsAddingAnotherChapter=false;
    private String ParentStoryId;
    public static RTEditText CompleteStory;
    public static RTManager rtManager;
    private boolean IsStoryHasParent=false;
    private TextView PublishText;
    private boolean IsPublishedStory=false;
    private Switch HideIdentity;
    private boolean IsHideIdentity=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.My_custom_Theme);
        setContentView(R.layout.write_and_publish_story_cordinator_layout);
        HideIdentity = (Switch) findViewById(R.id.hide_identity);

        mStoryToolbar= (Toolbar) findViewById(R.id.write_story_toolbar);
        setSupportActionBar(mStoryToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle("");

        RTApi rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
        rtManager = new RTManager(rtApi, savedInstanceState);

        ViewGroup toolbarContainer = (ViewGroup) findViewById(R.id.edit_text_toolbar_container);
        RTToolbar rtToolbar = (RTToolbar) findViewById(R.id.rte_toolbar_character);
        RTToolbar paragraph=(RTToolbar) findViewById(R.id.toolbar_paragraph);
        if (rtToolbar != null&&paragraph!=null) {
            Log.e("dfsdf","we are registering toolbar");
            rtManager.registerToolbar(toolbarContainer, rtToolbar);
            rtManager.registerToolbar(toolbarContainer, paragraph);
        }


        ToolBarTextView= (TextView) mStoryToolbar.findViewById(R.id.toolbar_title);
        Title_of_story= (EditText) findViewById(R.id.title_of_story);
        CompleteStory= (RTEditText) findViewById(R.id.content_of_story);
        AddCoverImageButton= (RelativeLayout) findViewById(R.id.add_cover_image_icon);
        Cover_image = (ImageView) findViewById(R.id.cover_image);
        Image_more_options= (ImageView) findViewById(R.id.image_more_options);
        TellAboutYourStory= (EditText) findViewById(R.id.tell_about_story);

        Typeface TitleFont = Typeface.createFromAsset(getAssets(),  "fonts/Playfair.ttf");
        Typeface storyFont = Typeface.createFromAsset(getAssets(),  "fonts/Playfair_Regular.ttf");

        Title_of_story.setTypeface(TitleFont);
        CompleteStory.setTypeface(storyFont);
        ToolBarTextView.setVisibility(View.VISIBLE);
        ToolBarTextView.setTypeface(TitleFont);

        rtManager.registerEditor(CompleteStory, true);



        mFireStoreDatabase=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);

        mImageStorageRef = FirebaseStorage.getInstance().getReference();
        mfirebaseStorrageRef=FirebaseStorage.getInstance();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();

        mProgressDialog=new ProgressDialog(WriteStoryActivity.this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

       // IsAddingAnotherChapter=getIntent().getBooleanExtra("addNewChapter",false);
        ParentStoryId=getIntent().getStringExtra("parent_story_Id");
       /* if (IsAddingAnotherChapter){

            Log.e("sdfjsklj","this is parent id "+ParentStoryId);
        }*/


        /*********************** Bottom_Sheet_ ids**************************************************************/
        BottomlSheetLayout= (LinearLayout) findViewById(R.id.bottom_sheet);
        BottomSheet = BottomSheetBehavior.from(BottomlSheetLayout);

        //KeyWordsOrTags= (EditText) findViewById(R.id.keyword_edit_text);
        generText= (TextView) findViewById(R.id.gener_text);
        languageText= (TextView) findViewById(R.id.language_spinner_text);
        LanguageSpinner= (Spinner) findViewById(R.id.language_spinner);
        GenreSpinner= (Spinner) findViewById(R.id.genre_spinner);
        GenreLayout= (RelativeLayout) findViewById(R.id.genre_spinner_layout);
        LanguageLayout= (RelativeLayout) findViewById(R.id.language_spinner_layout);

        PublishText = (TextView) findViewById(R.id.publish_text);

        ArrayAdapter<CharSequence> LanguageAdapter = new ArrayAdapter<CharSequence>(WriteStoryActivity.this,
                R.layout.spinner_text, Const.languages);
        LanguageAdapter.setDropDownViewResource(R.layout.simple_spinner_drop_down);
        LanguageSpinner.setAdapter(LanguageAdapter);

        String [] StoryCategoryArray;

        if (Const.GetLanguageFromSharedPref(WriteStoryActivity.this).equals(Const.languageId[3])){
            StoryCategoryArray=Const.ThoughtGenreHindi;
        }else {
            StoryCategoryArray=Const.ThoughtGenre;
        }
        ArrayAdapter<CharSequence> GenreAdapter = new ArrayAdapter<CharSequence>(WriteStoryActivity.this,
                R.layout.spinner_text, StoryCategoryArray);
        GenreAdapter.setDropDownViewResource(R.layout.simple_spinner_drop_down);
        GenreSpinner.setAdapter(GenreAdapter);


        generText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generText.setVisibility(View.GONE);
                GenreSpinner.setVisibility(View.VISIBLE);
                GenreSpinner.performClick();
            }
        });

        languageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                languageText.setVisibility(View.GONE);
                LanguageSpinner.setVisibility(View.VISIBLE);
                LanguageSpinner.performClick();
            }
        });
        LanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (CheckEveryThing()){
                    PublishText.setTextColor(Color.parseColor("#00BCD4"));
                }else {
                    Toast.makeText(WriteStoryActivity.this,getString(R.string.PLEASE_SELECT_language_and_genre),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                if (CheckEveryThing()){
                    PublishText.setTextColor(Color.parseColor("#00BCD4"));
                }else {
                    Toast.makeText(WriteStoryActivity.this,getString(R.string.PLEASE_SELECT_language_and_genre),Toast.LENGTH_SHORT).show();
                }
            }

        });
        GenreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (CheckEveryThing()){
                    PublishText.setTextColor(Color.parseColor("#00BCD4"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                if (CheckEveryThing()){
                    PublishText.setTextColor(Color.parseColor("#00BCD4"));
                }
            }

        });
        PublishText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckEveryThing()){

                    PublishText.setEnabled(false);

                    PublishText.setTextColor(Color.parseColor("#00BCD4"));
                    final String[] Genre = new String[1];
                    final String[] Language = new String[1];

                    Log.e("kfhsjfs"," parent story id is "+ParentStoryId);
                    if(IsPublishedStory){
                       PublishAlreadyPublishedStory();

                    }
                    else  if (IsStoryHasParent){

                        mFireStoreDatabase.collection("Story").document(ParentStoryId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                            if (document.contains("is_hide")){
                                                IsHideIdentity=document.getBoolean("is_hide");
                                            }
                                    if (document.contains("story_genre"))
                                    Genre[0] =document.getString("story_genre");
                                    if (document.contains("story_language"))
                                        Language[0] = document.getString("story_language");
                                            PublishStory(Genre[0], Language[0],"");
                                            Log.e("kfhsjfs"," inside genre is "+Genre[0]+" language is "+Language[0]);
                                }
                            }
                        });

                    }else {
                        Genre[0] =Const.ThoughtGenreId[GenreSpinner.getSelectedItemPosition()];
                        Language[0] =Const.languageId[LanguageSpinner.getSelectedItemPosition()];
                        Log.e("kfhsjfs"," else genre is "+Genre[0]+" language is "+Language[0]);
                        PublishStory(Genre[0], Language[0],"");
                    }
                    Log.e("kfhsjfs"," outside genre is "+Genre[0]+" language is "+Language[0]);

                }
            }
        });

        CompleteStory.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                    BottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

                return false;
            }
        });

        Title_of_story.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                BottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

                return false;
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

        AddCoverImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckPermission()){
                    ImagePicker();
                }
            }
        });

        Title_of_story.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ToolBarTextView.setText(s.toString());
            }
        });

       /* CompleteStory.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction()==KeyEvent.ACTION_DOWN || key == KeyEvent.KEYCODE_ENTER) {
                    //operation that you want on key press
                    Log.e("fjsklfs","we press space");
                    return true;
                }
                return false;

            }
        });*/

        CompleteStory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isUpdated=false;
                IsFinalSaveDraft=false;
                Log.e("isUpdateValue","setting value inside of text watcher of boleean variable "+isUpdated);

            }
        });

        StoryServerName=getIntent().getStringExtra("storyId");


        if (StoryServerName!=null&&StoryServerName.length()>5){
            DocumentReference StoryRef= mFireStoreDatabase.collection("Story").document(StoryServerName);

            /*StoryRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                    }
                }
            });*/

            StoryRef=mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(StoryServerName);
            StoryRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()){
                        DocumentSnapshot dataSnapshot = task.getResult();

                        if (dataSnapshot.contains("cover_image")) {

                            if (!dataSnapshot.get("cover_image").toString().equals("default")){
                                DownloadImageUrl=dataSnapshot.get("cover_image").toString();
                                Picasso.with(WriteStoryActivity.this)
                                        .load(DownloadImageUrl)
                                        .into(Cover_image);

                                AddCoverImageButton.setVisibility(View.GONE);
                                Image_more_options.setVisibility(View.VISIBLE);
                            }


                        }
                        if (dataSnapshot.contains("parent_story_id")){
                            ParentStoryId=dataSnapshot.getString("parent_story_id");
                            if (ParentStoryId!=null&&!ParentStoryId.equals(StoryServerName)){
                                IsStoryHasParent=true;
                                PublishText.setTextColor(Color.parseColor("#00BCD4"));
                                GenreLayout.setVisibility(View.GONE);
                                LanguageLayout.setVisibility(View.GONE);
                            }else {
                                ParentStoryId=StoryServerName;
                            }
                        }else {
                            ParentStoryId=StoryServerName;
                        }
                        if (dataSnapshot.contains("story_intro")||dataSnapshot.contains("story_genre")){
                            PublishText.setTextColor(Color.parseColor("#00BCD4"));
                            TellAboutYourStory.setText(dataSnapshot.getString("story_intro"));
                            IsPublishedStory=true;

                            generText.setVisibility(View.GONE);
                            GenreSpinner.setVisibility(View.VISIBLE);
                            languageText.setVisibility(View.GONE);
                            LanguageSpinner.setVisibility(View.VISIBLE);

                            String CurrentGenre=dataSnapshot.getString("story_genre");
                            String CurrentLanguage=dataSnapshot.getString("story_language");

                            boolean IsAuthorHideForStory=dataSnapshot.getBoolean("is_hide").booleanValue();
                            if (IsAuthorHideForStory){
                                HideIdentity.setChecked(true);
                            }

                            for(int i=0; i < Const.ThoughtGenreId.length; i++){

                                if(Const.ThoughtGenreId[i].equals(CurrentGenre)){
                                    GenreSpinner.setSelection(i);

                                    break;
                                }
                            }
                            for(int i=0; i < Const.languageId.length; i++){
                                if(Const.languageId[i].equals(CurrentLanguage)){
                                    LanguageSpinner.setSelection(i);
                                    break;
                                }
                            }

                            /*GenreLayout.setVisibility(View.GONE);
                            LanguageLayout.setVisibility(View.GONE);*/
                        }else {
                            IsPublishedStory=false;
                        }

                        if (dataSnapshot.contains("story_title")){
                            Title_of_story.setText(dataSnapshot.get("story_title").toString());
                            ToolBarTextView.setText(dataSnapshot.get("story_title").toString());
                        }


                        if (dataSnapshot.contains("story_content")) {
                            if (dataSnapshot.get("story_content")==null){
                                CompleteStory.setRichTextEditing(true,"");
                                isUpdated=true;
                                IsFinalSaveDraft=true;
                            }else {

                                CompleteStory.setRichTextEditing(true,dataSnapshot.get("story_content").toString());
                                isUpdated=true;
                                IsFinalSaveDraft=true;
                            }

                            //CompleteStory.setText(dataSnapshot.get("story_content").toString());
                            mProgressDialog.dismiss();
                        }
                    }
                }
            });

        }else {
            mProgressDialog.dismiss();
        }

        DocumentReference UserRef=mFireStoreDatabase.collection("Users").document(mAuth.getUid());

        UserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    DocumentSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.contains("name")) {
                        mAuthorName = dataSnapshot.get("name").toString();
                        Log.e("thisOs","name is "+dataSnapshot.get("name").toString());
                    }
                    if (dataSnapshot.contains("image")) {
                        mUserProfilePic = dataSnapshot.get("image").toString();
                    }
                }
            }
        });



        Image_more_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(Image_more_options);

            }
        });


        /*final int delay = 3000;

        handler.postDelayed(new Runnable(){
            public void run(){

                if (UpdateProcessRunning==3){
                    handler.postDelayed(this, delay);
                    return;
                }
                if (!isUpdated){
                    UpdateProcessRunning=3;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (CompleteStory.getText().toString().length()>1){
                                if (CompleteStory.getText().toString().substring(CompleteStory.getText().toString().length() - 1).equals(" ")){


                                    CompleteStoryText=CompleteStory.getText(RTFormat.HTML).toString();
                                    CompleteStory.getText().delete(CompleteStory.getText().toString().length() - 1,
                                            CompleteStory.getText().toString().length());
                                    Log.e("sdfjskfjsdf","This is complete story is "+CompleteStoryText);

                                }
                            }
                           // CompleteStoryText=CompleteStory.getText().toString();
                        }
                    });

                    new MyAsyncTask().execute();
                }else {
                    handler.postDelayed(this, delay);
                    return;
                }

                handler.postDelayed(this, delay);

            }
        }, delay);*/


    }

    private void PublishAlreadyPublishedStory() {
        mProgressDialog.setTitle(getString(R.string.PUBLISHING_STORY));
        mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT_WILE_WE_PUBLISH_STORY));
        mProgressDialog.show();



        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c);


        final Map publishdata=new HashMap();

        publishdata.put("story_status","publish");
        publishdata.put("story_title",Title_of_story.getText().toString());
        publishdata.put("story_intro",TellAboutYourStory.getText().toString());
        publishdata.put("story_genre",Const.ThoughtGenreId[GenreSpinner.getSelectedItemPosition()]);
        publishdata.put("story_language",Const.languageId[LanguageSpinner.getSelectedItemPosition()]);
        if (HideIdentity.getVisibility()==View.GONE){
            publishdata.put("is_hide",IsHideIdentity);
        }else {
            publishdata.put("is_hide",HideIdentity.isChecked());
        }

        mFireStoreDatabase.collection("Story").document(StoryServerName)
                .set(publishdata, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    final long timeInMillis = System.currentTimeMillis();
                    Map loadData=new HashMap();
                    loadData.put("story_title",Title_of_story.getText().toString());
                    loadData.put("story_status","publish");
                    loadData.put("draft_story_update_time",FieldValue.serverTimestamp());
                    loadData.put("story_content",CompleteStory.getText(RTFormat.HTML).toString());
                    loadData.put("story_intro",TellAboutYourStory.getText().toString());
                    loadData.put("story_genre",Const.ThoughtGenreId[GenreSpinner.getSelectedItemPosition()]);
                    loadData.put("story_language",Const.languageId[LanguageSpinner.getSelectedItemPosition()]);
                    if (HideIdentity.getVisibility()==View.GONE){
                        loadData.put("is_hide",IsHideIdentity);
                    }else {
                        loadData.put("is_hide",HideIdentity.isChecked());
                    }

                    mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(StoryServerName)
                            .set(loadData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                UpdateAuthorWritingNumber();

                                FinalPublishChapterListing_();
                                StoryTitleString=Title_of_story.getText().toString();
                                new MySearchFillAsyncTask().execute();

                            }
                        }
                    });

                }
            }
        });

    }

    private void UpdateAuthorWritingNumber() {

        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    if (document.contains("Writing_number")){
                        long Writings=document.getLong("Writing_number").longValue();
                        SetWritingValue(Writings);
                    }else {
                        SetWritingValue(0);
                    }
                }
            }
        });

    }

    private void SetWritingValue(long writing_number) {
        Map loadData=new HashMap();
        loadData.put("Writing_number",writing_number);

        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).set(loadData,SetOptions.merge());

    }


    public void PutTitle_into_Search_AfterEdit_SuggestionList(final String title_keyword){

        Map loadData=new HashMap();
        loadData.put("suggestion_keyword",title_keyword);
        mFireStoreDatabase.collection("Search").document("suggestion_list").collection("List").document(StoryServerName).set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    FinalPublishChapterListing_();

                }else {
                    //FlushAllGoToMain();
                    Toast.makeText(WriteStoryActivity.this,getString(R.string.PLEASE_TRY_AGAIN),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        rtManager.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        rtManager.onDestroy(isFinishing());
    }


    private void PublishStory(String genre, String language, String keywords) {
        mProgressDialog.setTitle(getString(R.string.PUBLISHING_STORY));
        mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT_WILE_WE_PUBLISH_STORY));
        mProgressDialog.show();
        UpdateProcessRunning=8;//going to publish so that update procees doesn't effect it.
        //CompleteStoryText=CompleteStory.getText(RTFormat.HTML).toString();
        UpdateDataToFirebase(StoryServerName,true,genre,language,keywords);
    }

    private boolean CheckEveryThing(){
        boolean check;
        if (IsStoryHasParent||IsPublishedStory)
            return true;
        if (GenreSpinner.getVisibility()==View.GONE){
            check=false;
            return check;
        }else {
            check=true;
        }
        if (LanguageSpinner.getVisibility()==View.GONE){
            check=false;
            return check;
        }else {
            check=true;
        }
        return check;
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.story_cover_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /*public String getFreshCapatibleStringToJson(String keyword) {
        String first,Second;
        first=keyword.replaceAll("\\{"," ");
        Second=first.replaceAll("\\}"," ");
        first=Second.replaceAll("\\["," ");
        Second=first.replaceAll("\\]"," ");
        first=Second.replaceAll("\\("," ");
        Second=first.replaceAll("\\)"," ");
        first=Second.replaceAll("\\s","~");
        Second=first.replaceAll(",","`");
        return Second;
    }*/

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;
        public MyMenuItemClickListener() {
            //this.position=positon;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.remove_cover:
                    RemovecoverImage(true);
                    return true;

                case R.id.change_cover:
                    RemovecoverImage(false);
                    Toast.makeText(WriteStoryActivity.this,getString(R.string.WAIT_FOR_MOMENT),Toast.LENGTH_SHORT).show();

                    return true;
                default:
            }
            return false;
        }
    }

    private void RemovecoverImage(final boolean isRemoveCoverOnly) {

        Log.e("dfjkdjfd","this is image url "+DownloadImageUrl);

        if (DownloadImageUrl!=null&&DownloadImageUrl.length()>5){
            //String fileName = DownloadImageUrl.substring(DownloadImageUrl.lastIndexOf('/') + 1);
            //Log.e("dfjkdjfd","this is image name "+fileName);

            final StorageReference thumb_filePath = mfirebaseStorrageRef.getReferenceFromUrl(DownloadImageUrl);
            //final StorageReference thumb_filePath=mImageStorageRef.child("story_images").child("cover").child(fileName+".jpg");

            thumb_filePath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    HashMap<String,String> imageData=new HashMap<>();
                    imageData.put("cover_image","default");

                    mFireStoreDatabase.collection("Story").document(StoryServerName).set(imageData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                if (isRemoveCoverOnly){
                                    AddCoverImageButton.setVisibility(View.VISIBLE);
                                    Image_more_options.setVisibility(View.GONE);
                                    Cover_image.setImageDrawable(null);
                                    Toast.makeText(WriteStoryActivity.this,getString(R.string.STORY_COVER_DELETED_SUCCESSFULLY),Toast.LENGTH_SHORT).show();

                                }else {
                                    ImagePicker();
                                }
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //
                    Log.e("dfjkdjfd","this is exception "+exception.getMessage());
                    Toast.makeText(WriteStoryActivity.this,getString(R.string.PLEASE_TRY_AGAIN),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public class MyAsyncTask extends AsyncTask< Void, Void, String > {
        @Override
        protected String doInBackground(Void...params) {
            UpdateDataToFirebase(StoryServerName,false,"","","");

            return "";
        }
        @Override
        protected void onPostExecute(String result) {
            UpdateProcessRunning=2;
        }
    }

    private void AddingAnotherChapterToStorySettingParent(){

        final int[] chapterCounter = {1};

        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(ParentStoryId).collection("All_Chapter_list").
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()) {
                        chapterCounter[0]++;
                        Log.e("fskldjf","this is chapter counter "+chapterCounter[0]);
                    }
                    AddAnotherChapterWithCounter(chapterCounter[0]);
                    Log.e("fskldjf","outside is chapter counter "+chapterCounter[0]);
                }else {
                    AddAnotherChapterWithCounter(chapterCounter[0]+1);
                    Log.e("fskldjf","no change is chapter counter "+chapterCounter[0]);
                }
            }
        });

    }

    private void AddAnotherChapterWithCounter(final int Chaptercouter){

        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(ParentStoryId).
                collection("All_Chapter_list").document(StoryServerName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (documentSnapshot.exists()){
                        Map loadData=new HashMap();
                        loadData.put("story_title",Title_of_story.getText().toString());
                        loadData.put("story_status","draft");
                        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(ParentStoryId).
                                collection("All_Chapter_list").document(StoryServerName)
                                .set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                return;
                            }
                        });
                    }else {
                        Map loadData=new HashMap();
                        loadData.put("story_title",Title_of_story.getText().toString());
                        loadData.put("story_status","draft");
                        loadData.put("chapter_counter",Chaptercouter);

                        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(ParentStoryId).
                                collection("All_Chapter_list").document(StoryServerName)
                                .set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()){

                                    Map loadData=new HashMap();
                                    loadData.put("chapter_counter",Chaptercouter);
                                    loadData.put("parent_story_id",ParentStoryId);
                                    mFireStoreDatabase.collection("Story").document(StoryServerName).set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            return;
                                        }
                                    });
                                }
                            }
                        });

                    }
                }
            }
        });


    }


    private void FinalPublishChapterListing_(){

        Log.e("jfkjfsdlfk","call final publish chapter "+Title_of_story.getText().toString());
        final Map loadData=new HashMap();
        loadData.put("story_title",Title_of_story.getText().toString());
        loadData.put("story_status","publish");

        if (ParentStoryId.equals(StoryServerName)){
            loadData.put("chapter_counter",1);
        }

        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").
                document(ParentStoryId).collection("All_Chapter_list").document(StoryServerName)
                .set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){

                    mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").
                            document(ParentStoryId).collection("All_Chapter_list").document(StoryServerName)
                            .set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                Log.e("jfkjfsdlfk","final publish after success call flush to main "+Title_of_story.getText().toString());
                                FlushAllGoToMain();
                            }
                        }
                    });


                }
            }
        });

        /*
        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(StoryServerName).
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        if (document.contains("parent_story_id")){

                            ParentStoryId=document.getString("parent_story_id");
                            if (ParentStoryId.length()>5){

                            }
                        }
                    }else {
                        FlushAllGoToMain();
                    }

                }
            }
        });*/
    }

    /*private void SetParentToParent(){
        Map loadData=new HashMap();
        loadData.put("parent_story_id",StoryServerName);
        loadData.put("chapter_counter",0);
        mFireStoreDatabase.collection("Story").document(StoryServerName).set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                FlushAllGoToMain();
            }
        });
    }*/

    private void FlushAllGoToMain(){
        mProgressDialog.dismiss();
        isUpdated=true;
        Log.e("isUpdateValue","setting value inside FlushAllGoToMain of boleean variable "+isUpdated);
        UpdateProcessRunning=10;
        Intent LaunchOrderStatus=new Intent(WriteStoryActivity.this,MainActivity.class);
        finish();
        LaunchOrderStatus.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LaunchOrderStatus);
        //overridePendingTransition(R.anim.from_left, R.anim.to_right);
    }

    private void UpdateDataToFirebase(String serverStoryNme, final boolean IsPublish, final String genre, final String language, final String keywords){
        Map loadData=new HashMap();

        loadData.put("story_title",Title_of_story.getText().toString());
        loadData.put("story_title_lower_case",Title_of_story.getText().toString().toLowerCase());
        loadData.put("story_author_id",mAuth.getUid());
        loadData.put("story_status","draft");
        if (ParentStoryId!=null){

        }else {
            ParentStoryId=StoryServerName;
        }
        loadData.put("parent_story_id",ParentStoryId);

        final long timeInMillis = System.currentTimeMillis();
        if (StoryServerName==null||StoryServerName.length()<5){
            String RemoverCommaTitleOfStory=Const.getFreshCapatibleStringToJson(Title_of_story.getText().toString());
            final String storyName="Story_name_"+RemoverCommaTitleOfStory+"_"+getRandomString(16)+"_"+mAuth.getUid();
            mFireStoreDatabase.collection("Story").document(storyName)
                    .set(loadData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        StoryServerName=storyName;

                        Map loadData=new HashMap();
                        loadData.put("draft_story_update_time",FieldValue.serverTimestamp());
                        loadData.put("story_content",CompleteStory.getText(RTFormat.HTML).toString());
                        loadData.put("story_title",Title_of_story.getText().toString());
                        loadData.put("story_status","draft");
                        if(ParentStoryId == null ||ParentStoryId.length()<5){
                            ParentStoryId=StoryServerName;
                        }
                        loadData.put("parent_story_id",ParentStoryId);


                        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(StoryServerName)
                                .set(loadData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    if (ParentStoryId!=null&&!ParentStoryId.equals(StoryServerName)){
                                        AddingAnotherChapterToStorySettingParent();
                                    }else {
                                        AddingAnotherChapterMenuToParent();
                                    }
                                        if (IsPublish){
                                            PutStoryIntoServer(StoryServerName,genre,language,keywords);
                                        }

                                }
                            }
                        });

                        if (UpdateProcessRunning==FINAL_DRAFT_SAVE){

                            IsFinalSaveDraft=true;
                            if (!IsGoForPublish){
                                Toast.makeText(WriteStoryActivity.this,getString(R.string.DATA_SAVED),Toast.LENGTH_SHORT).show();
                            }

                            if (isFromAlertDialog){
                                mProgressDialog.dismiss();
                                finalBackFinish();
                            }
                            else if (IsGoForPublish){

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(WriteStoryActivity.this,getString(R.string.DATA_SAVED),Toast.LENGTH_SHORT).show();
                                        openPublishBottomSheet();
                                    }
                                }, 1500);
                            }else {
                                mProgressDialog.dismiss();
                            }
                        }

                        isUpdated=true;
                        Log.e("isUpdateValue","setting value inside UpdateDataToFirebase boleean variable "+isUpdated);
                        UpdateProcessRunning=2;
                    }
                    else {
                        //Log.e("thisisData","this is else part");
                    }
                }
            });
        }else {
            //Log.e("thisisData","after story update is dfgfddg else part"+" and UpdateProcessRunning "+UpdateProcessRunning);
            mFireStoreDatabase.collection("Story").document(serverStoryNme)
                    .set(loadData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        //Log.e("thisisData","after story update is else part"+" and UpdateProcessRunning "+UpdateProcessRunning);
                        Map loadData=new HashMap();
                        Log.e("thereIsStoryklsdfkd","setting as draft UpdateProcessRunning "+UpdateProcessRunning+" and isUpdate = "+isUpdated);
                        loadData.put("draft_story_update_time",FieldValue.serverTimestamp());
                        loadData.put("story_content",CompleteStory.getText(RTFormat.HTML).toString());
                        loadData.put("story_title",Title_of_story.getText().toString());
                        loadData.put("story_status","draft");

                        if(ParentStoryId == null ||ParentStoryId.length()<5){
                            ParentStoryId=StoryServerName;
                        }
                        loadData.put("parent_story_id",ParentStoryId);


                        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(StoryServerName)
                                .set(loadData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    if (ParentStoryId!=null&&!ParentStoryId.equals(StoryServerName)){
                                        AddingAnotherChapterToStorySettingParent();
                                    }else {
                                        AddingAnotherChapterMenuToParent();
                                    }

                                    Log.e("dkfjskfjskld","save story after publish as draft here ");
                                    isUpdated=true;
                                    Log.e("isUpdateValue","setting value inside UpdateDataToFirebase else part boleean variable "+isUpdated);
                                    Log.e("thisisData","after final story update is else part "+IsPublish);
                                    if (IsPublish){
                                        PutStoryIntoServer(StoryServerName,genre,language,keywords);
                                    }else if (UpdateProcessRunning==FINAL_DRAFT_SAVE){
                                        UpdateProcessRunning=2;
                                        IsFinalSaveDraft=true;

                                        Toast.makeText(WriteStoryActivity.this,getString(R.string.DATA_SAVED),Toast.LENGTH_SHORT).show();
                                        if (isFromAlertDialog){
                                            mProgressDialog.dismiss();
                                            finalBackFinish();
                                        }else if (IsGoForPublish){
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mProgressDialog.dismiss();
                                                    openPublishBottomSheet();
                                                }
                                            }, 1000);

                                        }else {
                                            mProgressDialog.dismiss();
                                        }
                                    }

                                }else {
                                    isUpdated=true;
                                    Log.e("isUpdateValue","setting value inside UpdateDataToFirebase task unsuccessful else boleean variable "+isUpdated);
                                    UpdateProcessRunning=2;
                                }
                            }
                        });

                    }else {
                        isUpdated=true;
                        Log.e("isUpdateValue","setting value inside UpdateDataToFirebase last else part boleean variable "+isUpdated);
                        UpdateProcessRunning=2;
                    }
                }
            });

        }

    }

    private void AddingAnotherChapterMenuToParent() {

        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(StoryServerName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                        DocumentSnapshot doc=task.getResult();
                    if (doc.exists()){
                        Map loadData=new HashMap();
                        loadData.put("story_title","First_Chapter");
                        loadData.put("story_status","publish");
                        loadData.put("chapter_counter",1);

                        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(StoryServerName).
                                collection("All_Chapter_list").document(StoryServerName).set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
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
        });
    }

    private void PutStoryIntoServer(final String storyName, final String genre, final String language, String keywords) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c);

        Random rand = new Random();
        final int pickedNumber =0;
                //= rand.nextInt(2000);

        final Map publishdata=new HashMap();
        final long timeInMillis = System.currentTimeMillis();
        publishdata.put("story_status","publish");
        publishdata.put("story_genre",genre);
        publishdata.put("story_language",language);
        publishdata.put("keyword_tags",keywords);
        publishdata.put("uploaded_time", formattedDate);
        publishdata.put("monthly_rank","0");
        publishdata.put("all_time_rank",pickedNumber);
        publishdata.put("view_count",pickedNumber);
        publishdata.put("AvgRating","0");
        if (HideIdentity.getVisibility()==View.GONE){
            publishdata.put("is_hide",IsHideIdentity);
        }else {
            publishdata.put("is_hide",HideIdentity.isChecked());
        }
        publishdata.put("Author_Name", mAuthorName);
        publishdata.put("story_timestamp_published", FieldValue.serverTimestamp());
        publishdata.put("Total_user_rated_to_story",0);


        if (ParentStoryId == null ||ParentStoryId.length()<5||ParentStoryId.equals(StoryServerName)) {
            publishdata.put("parent_story_id",storyName);
            publishdata.put("chapter_counter",1);
        }

        publishdata.put("story_intro",TellAboutYourStory.getText().toString());

        mFireStoreDatabase.collection("Story").document(storyName)
                .set(publishdata, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){

                    Map loadData=new HashMap();
                    loadData.put("story_title",Title_of_story.getText().toString());
                    loadData.put("story_status","publish");
                    loadData.put("story_intro",TellAboutYourStory.getText().toString());
                    loadData.put("all_time_rank",pickedNumber);
                    loadData.put("story_genre",genre);
                    loadData.put("story_language",language);
                    if (HideIdentity.getVisibility()==View.GONE){
                        loadData.put("is_hide",IsHideIdentity);
                    }else {
                        loadData.put("is_hide",HideIdentity.isChecked());
                    }

                    loadData.put("story_timestamp_published",FieldValue.serverTimestamp());
                    loadData.put("view_count",pickedNumber);
                    loadData.put("AvgRating","0");
                    if (ParentStoryId == null ||ParentStoryId.length()<5||ParentStoryId.equals(StoryServerName)) {
                        loadData.put("parent_story_id",storyName);
                        loadData.put("chapter_counter",1);
                    }

                    mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(StoryServerName)
                            .set(loadData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                FinalPublishChapterListing_();
                                StoryTitleString=Title_of_story.getText().toString();
                                new MySearchFillAsyncTask().execute();
                            }
                        }
                    });

                }
            }
        });

    }
    public static String StoryTitleString;

    public class MySearchFillAsyncTask extends AsyncTask< Void, Void, String > {
        @Override
        protected String doInBackground(Void...params) {

            PutTitle_into_Search_SuggestionList(StoryTitleString);
            return "";
        }
        @Override
        protected void onPostExecute(String result) {

        }
    }


    private void ImagePicker(){
        CropImage.activity().setAspectRatio(1,1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(WriteStoryActivity.this);
    }

    private boolean CheckPermission() {
        boolean grantPermission;
        if (ActivityCompat.checkSelfPermission(WriteStoryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WriteStoryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Const.PICK_FROM_GALLERY);
            grantPermission=false;
        } else {
            grantPermission=true;
        }
        return grantPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case Const.PICK_FROM_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker();

                } else {
                    Toast.makeText(WriteStoryActivity.this,getString(R.string.PLEASE_GIVE_PERMISSIONS),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    Uri CoverImageUri;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                 CoverImageUri = result.getUri();
                Log.e("sdkskfjsd"," image url from write story "+result.getUri());
                Image_more_options.setVisibility(View.VISIBLE);
                AddCoverImageButton.setVisibility(View.GONE);
                Cover_image.setImageURI(CoverImageUri);
                mProgressDialog.setTitle(getString(R.string.UPLOADING_IMAGE));
                mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT_FOR_IMAGE));
                mProgressDialog.show();

                UploadImageToFirebase(CoverImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    public void UploadImageToFirebase(Uri imageUri){

       /* mProgressDialog=new ProgressDialog(WriteStoryActivity.this);
        mProgressDialog.setTitle("Uploading Image");
        mProgressDialog.setMessage("Please wait while we upload and process the image.");
        mProgressDialog.show();
        mProgressDialog.setCanceledOnTouchOutside(false);*/

        File bitmapPath=new File(imageUri.getPath());

        try{
            Bitmap storyBitmap = new Compressor(this).setMaxWidth(200).setMaxHeight(200).setQuality(50).
                    compressToBitmap(bitmapPath);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            storyBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            user_byte= baos.toByteArray();

        }catch (IOException e){

        }

        String CurrentUId=mAuth.getUid();
        final StorageReference thumb_filePath=mImageStorageRef.child("story_images").child("cover").child(CurrentUId+"_"+
                getRandomString(20)+".jpg");

        UploadTask uploadTask = thumb_filePath.putBytes(user_byte);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                DownloadImageUrl=task.getResult().getDownloadUrl().toString();
                if(task.isSuccessful()){

                    final Map<String, Object> data = new HashMap<>();
                    data.put("cover_image", DownloadImageUrl);

                    if (StoryServerName==null||StoryServerName.length()<5){

                        String RemoverCommaTitleOfStory=Const.getFreshCapatibleStringToJson(Title_of_story.getText().toString());
                        final String storyName="Story_name_"+RemoverCommaTitleOfStory+"_"+getRandomString(16)+"_"+mAuth.getUid();
                        mFireStoreDatabase.collection("Story").document(storyName)
                                .set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(storyName)
                                            .set(data,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                StoryServerName=storyName;
                                                if (ParentStoryId!=null){
                                                    AddingAnotherChapterToStorySettingParent();

                                                }
                                                mProgressDialog.dismiss();
                                                Toast.makeText(WriteStoryActivity.this,getString(R.string.IMAGE_UPLOADED_SUCCESSFULLY),Toast.LENGTH_SHORT).show();

                                            }else {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(WriteStoryActivity.this,getString(R.string.SOMETHING_WENT_WRONG),Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }else {
                        mFireStoreDatabase.collection("Story").document(StoryServerName)
                                .set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(StoryServerName)
                                            .set(data,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                if (ParentStoryId!=null){
                                                    AddingAnotherChapterToStorySettingParent();
                                                }
                                                mProgressDialog.dismiss();
                                                Toast.makeText(WriteStoryActivity.this,getString(R.string.IMAGE_UPLOADED_SUCCESSFULLY),Toast.LENGTH_SHORT).show();

                                            }else {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(WriteStoryActivity.this,getString(R.string.SOMETHING_WENT_WRONG),Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }


                }
                else{
                    //mProgressDialog.dismiss();
                    Toast.makeText(WriteStoryActivity.this,"Error in uploading image",Toast.LENGTH_SHORT).show();
                }
            }
        });


               /* }
                else{
                    mProgressDialog.dismiss();
                    Toast.makeText(EditProfile_Activity.this,"Error in uploading",Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.writing_menu,menu);
        return true;
    }

    private boolean IsGoForPublish=false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if( android.R.id.home==item.getItemId()){
            onBackPressed();
        }
        if(item.getItemId()==R.id.publish_story){
        Log.e("jkdsfhsfdsksl","this is publishing story IsFinalSaveDraft "+IsFinalSaveDraft);
            if (!IsFinalSaveDraft){
                mProgressDialog.setTitle(getString(R.string.SAVING_DATA));
                mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT_TEXT));
                mProgressDialog.show();
                IsGoForPublish=true;
                UpdateProcessRunning=10;
                //CompleteStoryText=;
                UpdateDataToFirebase(StoryServerName,false,"","","");
            }else {
                mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT));
                mProgressDialog.show();
                openPublishBottomSheet();
            }
        }
        else if(item.getItemId()==R.id.save_as_draft){
            if (!IsFinalSaveDraft){
                mProgressDialog.setTitle(getString(R.string.SAVING_DATA));
                mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT_TEXT));
                mProgressDialog.show();
                UpdateProcessRunning=10;
                //CompleteStoryText=CompleteStory.getText(RTFormat.HTML).toString();
                UpdateDataToFirebase(StoryServerName,false,"","","");
            }else {
                Toast.makeText(WriteStoryActivity.this,getString(R.string.NO_UPDATE_TO_SAVE),Toast.LENGTH_SHORT).show();
            }


        }else if(item.getItemId()==R.id.delete_story){
            ProcessDeleteTask();
            //Toast.makeText(WriteStoryActivity.this,"publishing story",Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    private void openPublishBottomSheet(){
        if (CompleteStory.getText().toString().length()>5&&Title_of_story.getText().toString().length()>0){
            CheckOutForParentStory();

        }else {
            if (CompleteStory.getText().toString().length()<=5){
                mProgressDialog.dismiss();
                Toast.makeText(WriteStoryActivity.this,getString(R.string.PLEASE_WRITE_SOME_THING_BEFORE_PUBLISH),Toast.LENGTH_SHORT).show();

            }else if (Title_of_story.getText().toString().length()==0){
                mProgressDialog.dismiss();
                Toast.makeText(WriteStoryActivity.this,getString(R.string.PLEASE_GIVE_TITLE_TO_YOUR_STORY),Toast.LENGTH_SHORT).show();

            }
        }
    }


    private void CheckOutForParentStory() {

        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(StoryServerName).
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()){
                            if (document.contains("parent_story_id")) {
                                if (document.get("parent_story_id")!=null&&!document.getString("parent_story_id").equals(StoryServerName)){
                                    IsStoryHasParent=true;
                                    PublishText.setTextColor(Color.parseColor("#00BCD4"));
                                    ParentStoryId=document.getString("parent_story_id");
                                    GenreLayout.setVisibility(View.GONE);
                                    LanguageLayout.setVisibility(View.GONE);
                                    HideIdentity.setVisibility(View.GONE);
                                    OpenAndCloseBottomSheet();
                                    mProgressDialog.dismiss();
                                }else {
                                    OpenAndCloseBottomSheet();
                                    mProgressDialog.dismiss();
                                }

                            }else {
                                OpenAndCloseBottomSheet();
                                mProgressDialog.dismiss();
                            }
                        }else {
                            OpenAndCloseBottomSheet();
                            mProgressDialog.dismiss();
                        }
                    }else {
                        OpenAndCloseBottomSheet();
                        mProgressDialog.dismiss();
                    }
            }
        });
    }

    private void OpenAndCloseBottomSheet(){
        if (BottomSheet.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            BottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else {
            BottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }
    }

    private void ProcessDeleteTask(){

        if (DownloadImageUrl!=null&&DownloadImageUrl.length()>5){
            final StorageReference thumb_filePath = mfirebaseStorrageRef.getReferenceFromUrl(DownloadImageUrl);
            /*String fileName = DownloadImageUrl.substring(DownloadImageUrl.lastIndexOf('/') + 1);
            final StorageReference thumb_filePath=mImageStorageRef.child("story_images").child("cover").child(fileName+".jpg");
*/
            thumb_filePath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    DeleteCompleteData();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //
                    Toast.makeText(WriteStoryActivity.this,getString(R.string.PLEASE_TRY_AGAIN),Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            DeleteCompleteData();
        }

    }

    private void DeleteCompleteData(){
        mFireStoreDatabase.collection("Story").document(StoryServerName)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(StoryServerName)
                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(WriteStoryActivity.this,getString(R.string.DELETE_SUCCESSFULL),Toast.LENGTH_SHORT).show();
                                FlushAllGoToMain();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WriteStoryActivity.this,getString(R.string.PLEASE_TRY_AGAIN),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void PutTitle_into_Search_SuggestionList(final String title_keyword){
        final String[] listNo = new String[1];
        mFireStoreDatabase.collection("Search").document("CurrentListNumber").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (documentSnapshot.exists()){
                        if (documentSnapshot.contains("currentList_Number")){

                            listNo[0] = documentSnapshot.getString("currentList_Number");

                            getJSONobject(listNo[0],title_keyword);
                        }else {
                            getJSONobject("1",title_keyword);
                        }
                    }else {
                        getJSONobject("1",title_keyword);
                    }
                }
            }
        });

        /*Map loadData=new HashMap();
        loadData.put("suggestion_keyword",title_keyword);

        mFireStoreDatabase.collection("Search").document("suggestion_list").collection("List").
                document(StoryServerName).set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){


                }else {
                    FinalPublishChapterListing_();
                    Toast.makeText(WriteStoryActivity.this,getString(R.string.PLEASE_TRY_AGAIN),Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void getJSONobject(final String listNo, final String title_keyword) {
        Log.e("ksldjks","we are inside current list number "+listNo);
        mFireStoreDatabase.collection("Search").document("suggestion_list").collection("Multi_List"+listNo).
                document("List"+ listNo).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (documentSnapshot.exists()){
                        if (documentSnapshot.contains("keywordList")){
                            String KeyWordJSON=""+documentSnapshot.get("keywordList");
                            Log.e("jfslkfl","this is json string "+KeyWordJSON);
                            SetSuggestionJSON(listNo,KeyWordJSON,title_keyword);

                        }else {
                            SetSuggestionJSON(listNo,null,title_keyword);
                        }
                    }else {
                        SetSuggestionJSON(listNo,null,title_keyword);
                    }
                }else {
                    SetSuggestionJSON(listNo,null,title_keyword);
                }
            }
        });
    }

    private void SetSuggestionJSON(String listNo,String keyWordJSON,String title_keyword) {
        Map<String, Object> docData = new HashMap<>();
        try{
            Map<String, Object> nestedData = new HashMap<>();
            if (keyWordJSON!=null) {
                Log.e("jfslkfl","this is json string "+keyWordJSON+" length of string is "+keyWordJSON.length());
                JSONObject json = new JSONObject(keyWordJSON);
                Log.e("sjkfjksf","size of json obje "+json.length());

                    Iterator<String> iter = json.keys();
                //Log.e("jfslkfl","this is key string "+iter);
                while (iter.hasNext()) {
                    String key = iter.next();

                    try {
                        Object value = json.get(key);
                        Log.e("jfslkfl","this is value string "+value);
                        nestedData.put(key, value);
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }
                if (json.length() >= Const.SUGGESTION_LIST_BOX) {
                    Map<String, Object> ListCounterData = new HashMap<>();
                    ListCounterData.put("currentList_Number", "" + (Integer.parseInt(listNo) + 1));
                    mFireStoreDatabase.collection("Search").document("CurrentListNumber").set(ListCounterData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }
            }


            String FinalStoryName = Const.getFreshCapatibleStringToJson(title_keyword);
            //Log.e("after_removing_commas","string is "+FinalStoryName);
            nestedData.put(StoryServerName,FinalStoryName);
            docData.put("keywordList", nestedData);

            mFireStoreDatabase.collection("Search").document("suggestion_list").collection("Multi_List"+listNo).
                    document("List"+ listNo).set(docData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            return;
                            //FinalPublishChapterListing_();
                        }else {
                            return;
                            //FinalPublishChapterListing_();
                            //Toast.makeText(WriteStoryActivity.this,getString(R.string.PLEASE_TRY_AGAIN),Toast.LENGTH_SHORT).show();
                        }
                }
            });

        }catch (JSONException e){
            Log.e("jfslkfl","this is json exception "+e.getMessage());
        }
    }

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
    public void onBackPressed() {
        //super.onBackPressed();
        Log.e("sdfsklfjskl","this is bolean value "+isUpdated);

        if (IsFinalSaveDraft){
            finalBackFinish();
        }else {
            ShowSavingAlertDialog();
        }


        /*if(isUpdated){
            handler.removeCallbacksAndMessages(null);
            WriteStoryActivity.this.finish();
            overridePendingTransition(R.anim.from_right, R.anim.to_right);
        }else {


        }*/

    }


    boolean isFromAlertDialog=false;
    private void ShowSavingAlertDialog() {
        AlertDialog.Builder builder;
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(WriteStoryActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(WriteStoryActivity.this);
        }*/
        builder = new AlertDialog.Builder(WriteStoryActivity.this);
        builder.setTitle(getString(R.string.SAVE_THE_CHANGES))
                .setMessage(getString(R.string.DO_YOU_WANT_TO_SAVE_CHANGES_IN_STORY))
                .setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mProgressDialog.setTitle(getString(R.string.SAVING_DATA));
                        mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT_TEXT));
                        mProgressDialog.show();
                        isFromAlertDialog=true;
                        UpdateProcessRunning=10;
                        //CompleteStoryText=;
                        UpdateDataToFirebase(StoryServerName,false,"","","");
                    }
                })
                .setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finalBackFinish();
                    }
                })
                .setIcon(R.drawable.ic_warning)
                .show();
    }

    public void finalBackFinish(){
        //handler.removeCallbacksAndMessages(null);
        WriteStoryActivity.this.finish();
        //overridePendingTransition(R.anim.from_right, R.anim.to_right);
    }


}
