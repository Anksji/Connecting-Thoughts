package com.rsons.application.connecting_thoughts;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


/**
 * Created by ankit on 2/6/2018.
 */


public class EditProfile_Activity extends AppCompatActivity {

    private Spinner spinner;
    private TextView BirthDay;
    private int YearEndDate;
    private int MonthEndDate;
    private int DayEndDate;

    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private StorageReference mImageStorageRef;
    private EditText Phone_No;
    private EditText StatusText;

    //private DatabaseReference mUserDatabase;
    private FirebaseFirestore mFireStoreDatabase;

    private CircleImageView UserProfileImage;
    private String image;
    private GoogleApiClient mGoogleApiClient;
    private View Spinnerbottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_);

        ImageView GoBack= (ImageView) findViewById(R.id.go_back);

        GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });

        spinner= (Spinner) findViewById(R.id.gender_spinner);
        StatusText= (EditText) findViewById(R.id.status_text);
        Spinnerbottom=findViewById(R.id.bottom_til_view);



        String[] years = {"Male","Female"};
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(EditProfile_Activity.this,
                R.layout.spinner_text, years );
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_drop_down);
        spinner.setAdapter(langAdapter);
        final TextView GenderText= (TextView) findViewById(R.id.gener_text);

        GenderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinnerbottom.setVisibility(View.VISIBLE);
                GenderText.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
                spinner.performClick();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient=new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



        final EditText editUserName= (EditText) findViewById(R.id.edit_user_name);
        Phone_No= (EditText) findViewById(R.id.phone_number);
        UserProfileImage= (CircleImageView) findViewById(R.id.user_profile_image);

        final TextInputLayout User_Name= (TextInputLayout) findViewById(R.id.user_name);

        final EditText Email_id= (EditText) findViewById(R.id.emai_edit_text);

        BirthDay= (TextView) findViewById(R.id.selected_date_text);
        LinearLayout SelectDate= (LinearLayout) findViewById(R.id.select_date);

        final ImageView UserBackgroundImage= (ImageView) findViewById(R.id.user_image_background);

        mFireStoreDatabase=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);


        //RelativeLayout logOut_btn= (RelativeLayout) findViewById(R.id.logout_btn);

        /*logOut_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mAuth=FirebaseAuth.getInstance();
                        FirebaseUser currentUser=mAuth.getCurrentUser();
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(@NonNull Status status) {
                                        FirebaseAuth.getInstance().signOut();
                                        Intent startIntent=new Intent(EditProfile_Activity.this,LoginActivity.class);
                                        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(startIntent);
                                    }
                                });

                *//*if(currentUser!=null){
                    mUserDatabase= FirebaseDatabase.getInstance().getReference().child("status").
                            child(mAuth.getCurrentUser().getUid());
                    mUserDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.e("sfskfskfs","value set disconnected");
                            if(dataSnapshot!=null){
                                mUserDatabase.setValue("offline").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                                    new ResultCallback<Status>() {
                                                        @Override
                                                        public void onResult(@NonNull Status status) {
                                                            FirebaseAuth.getInstance().signOut();
                                                            Intent startIntent=new Intent(EditProfile_Activity.this,LoginActivity.class);
                                                            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(startIntent);
                                                        }
                                                    });

                                            //overridePendingTransition(R.anim.from_right, R.anim.to_right);
                                        }
                                    }
                                });

                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }*//*

            }
        });
*/


        mImageStorageRef = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String Current_uid=mAuth.getUid();

        Log.e("this_is_id","get authentication id "+mAuth.getUid());

        final DocumentReference dataRef = mFireStoreDatabase.collection("Users").document(mAuth.getUid());


        dataRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {
                if (dataSnapshot != null) {
                    if (!dataSnapshot.exists()){
                        return;
                    }
                    if (dataSnapshot.contains("name")) {
                        String name = dataSnapshot.get("name").toString();
                        editUserName.setText(name);
                    }
                    if (dataSnapshot.contains("image")) {
                        image = dataSnapshot.get("image").toString();
                    }

                    if (dataSnapshot.contains("email")) {
                        String Email = dataSnapshot.get("email").toString();
                        Email_id.setText(Email);
                    }
                    if (dataSnapshot.contains("status")){
                        StatusText.setText( dataSnapshot.get("status").toString());
                    }

                    if (dataSnapshot.contains("gender")) {
                        String gender = dataSnapshot.get("gender").toString();
                        Spinnerbottom.setVisibility(View.VISIBLE);
                        GenderText.setVisibility(View.GONE);
                        spinner.setVisibility(View.VISIBLE);
                        spinner.setSelection(getIndex(spinner, gender));
                    }

                    if (dataSnapshot.contains("dob")) {
                        if (dataSnapshot.getString("dob").toString().length()>4){
                            String birthday = dataSnapshot.get("dob").toString();
                            BirthDay.setTextColor(Color.BLACK);
                            BirthDay.setText(birthday);
                        }

                    }

                    if (dataSnapshot.contains("phone_no")) {
                        String phone_no = dataSnapshot.get("phone_no").toString();
                        Phone_No.setText(phone_no);
                    }

                    if (image!=null&&!image.equals("default")) {

                        Picasso.with(EditProfile_Activity.this)
                                .load(image)
                                .into(UserBackgroundImage);

                        Picasso.with(EditProfile_Activity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_user_).into(UserProfileImage,
                                new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.e("onerrorSection", "this is success");
                                    }

                                    @Override
                                    public void onError() {
                                        Log.e("onerrorSection", "this is error");
                                        Picasso.with(EditProfile_Activity.this).load(image).placeholder(R.drawable.ic_user_).into(UserProfileImage);
                                    }
                                });
                    }
                }else {
                    Log.d("data", "No such document");
                }
            }
        });

       /* dataRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot dataSnapshot = task.getResult();

            }
        });*/


        final Calendar calendarEndDate = Calendar.getInstance();
        calendarEndDate.add(Calendar.YEAR, -10);

        YearEndDate = calendarEndDate.get(Calendar.YEAR);
        MonthEndDate = calendarEndDate.get(Calendar.MONTH);
        DayEndDate = calendarEndDate.get(Calendar.DAY_OF_MONTH);

        SelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog mDate = new DatePickerDialog(EditProfile_Activity.this, date, YearEndDate, MonthEndDate, DayEndDate);
                mDate.getDatePicker().setMaxDate(calendarEndDate.getTimeInMillis());
                mDate.show();
            }
        });

        RelativeLayout CaptureImage= (RelativeLayout) findViewById(R.id.capter_image);

        CaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckPermission()){
                    ImagePicker();
                }
            }
        });

        RippleView Save_profile= (RippleView) findViewById(R.id.save_profile);

        Save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email=Email_id.getText().toString();
                final String UserName=editUserName.getText().toString();
                Log.e("kfjskfjsf","this is user name  "+UserName);
                String Birthday=BirthDay.getText().toString();
                String Gender=spinner.getSelectedItem().toString();


                if (Email.equals("")||UserName.equals("")||Birthday.equals("")||Gender.equals("")){
                    Toast.makeText(EditProfile_Activity.this,getString(R.string.PLEASE_FILL_ALL),Toast.LENGTH_SHORT).show();
                }else {

                    mProgressDialog=new ProgressDialog(EditProfile_Activity.this);
                    mProgressDialog.setTitle(getString(R.string.UPLOAD_DATA_TITLE));
                    mProgressDialog.setMessage(getString(R.string.UPLOADING_DATA));
                    mProgressDialog.show();
                    mProgressDialog.setCanceledOnTouchOutside(false);

                    Map loadData=new HashMap();
                    loadData.put("email",Email);
                    loadData.put("dob",Birthday);
                    loadData.put("gender",Gender);
                    loadData.put("name",UserName);
                    loadData.put("lower_case_name",UserName.toLowerCase());
                    loadData.put("status",StatusText.getText().toString());
                    loadData.put("phone_no",Phone_No.getText().toString());
                    Log.e("kfjskfjsf","during update this is user name  "+UserName);
                    mFireStoreDatabase.collection("Users").document(mAuth.getUid())
                            .set(loadData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                PutTitle_into_Search_SuggestionList(UserName);
                                mProgressDialog.dismiss();
                                Toast.makeText(EditProfile_Activity.this,getString(R.string.PROFILE_UPDATED),Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
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


            String NameOf_user=Const.getFreshCapatibleStringToJson(title_keyword);
            nestedData.put(mAuth.getUid(),NameOf_user);
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



    /*public void PutTitle_into_Search_SuggestionList(final String title_keyword){

        Map loadData=new HashMap();
        loadData.put("suggestion_keyword",title_keyword);
        mFireStoreDatabase.collection("Search").document("suggestion_list").collection("List").document(mAuth.getUid()).set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    *//*
                    mFireStoreDatabase.collection("Search").document("total_keyWords").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                long keywordNumber=0;
                                if (document.exists()){
                                    if (document.contains("keyword_number")){
                                        keywordNumber=document.getLong("keyword_number").longValue();
                                    }
                                }else {
                                    keywordNumber=0;
                                }

                                Map loadData=new HashMap();
                                loadData.put("keyword_number",++keywordNumber);

                                mFireStoreDatabase.collection("Search").document("total_keyWords").set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                    }
                                });
                            }
                        }
                    });*//*

                }else {
                    Toast.makeText(EditProfile_Activity.this,getString(R.string.PLEASE_TRY_AGAIN),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/

   /* public String getFreshCapatibleStringToJson(String keyword) {
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
    }
*/

    private void ImagePicker(){
        CropImage.activity().setAspectRatio(1,1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(EditProfile_Activity.this);
    }

    private boolean CheckPermission() {
        boolean grantPermission;
        if (ActivityCompat.checkSelfPermission(EditProfile_Activity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditProfile_Activity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Const.PICK_FROM_GALLERY);
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
                    Toast.makeText(EditProfile_Activity.this,getString(R.string.PLEASE_GIVE_PERMISSIONS),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            BirthDay.setTextColor(Color.BLACK);
            BirthDay.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
            /*Toast.makeText(SchedulePickUp.this,"selected date is "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year,Toast.LENGTH_LONG).show();*/
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri imageUri = result.getUri();
                Log.e("dfjkfjskf","this is image uri "+imageUri);
                UploadImageToFirebase(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    byte[] user_byte;
    ProgressDialog mProgressDialog;

    public void UploadImageToFirebase(Uri imageUri){

        mProgressDialog=new ProgressDialog(EditProfile_Activity.this);
        mProgressDialog.setTitle("Uploading Image");
        mProgressDialog.setMessage("Please wait while we upload and process the image.");
        mProgressDialog.show();
        mProgressDialog.setCanceledOnTouchOutside(false);
        //Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));

        File bitmapPath=new File(imageUri.getPath());

        try{
            Bitmap userBitmap = new Compressor(this).setMaxWidth(200).setMaxHeight(200).setQuality(50).
                    compressToBitmap(bitmapPath);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            userBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            user_byte= baos.toByteArray();

        }catch (IOException e){

        }


        String CurrentUId=mAuth.getUid();
        //StorageReference ImageFile = mImageStorageRef.child("profile_images").child(CurrentUId+".jpg");
        final StorageReference thumb_filePath=mImageStorageRef.child("profile_images").child("thumbs").child(CurrentUId+".jpg");

        /*ImageFile.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    final String img_Download_Url=task.getResult().getDownloadUrl().toString();*/

        UploadTask uploadTask = thumb_filePath.putBytes(user_byte);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                String thumb_downloadURL=task.getResult().getDownloadUrl().toString();
                if(task.isSuccessful()){

                    Map<String, Object> data = new HashMap<>();
                    data.put("image", thumb_downloadURL);

                    mFireStoreDatabase.collection("Users").document(mAuth.getUid())
                            .set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mProgressDialog.dismiss();
                                Toast.makeText(EditProfile_Activity.this,getString(R.string.IMAGE_UPLOADED),Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
                else{
                    mProgressDialog.dismiss();
                    Toast.makeText(EditProfile_Activity.this,"Error in uploading",Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.from_left, R.anim.to_right);

    }

}
