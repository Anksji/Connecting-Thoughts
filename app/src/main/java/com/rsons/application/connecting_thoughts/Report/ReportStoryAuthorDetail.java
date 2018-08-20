package com.rsons.application.connecting_thoughts.Report;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.rsons.application.connecting_thoughts.EditProfile_Activity;
import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.UsersActivity.AuthorProfileActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by ankit on 5/1/2018.
 */

public class ReportStoryAuthorDetail extends AppCompatActivity {

    private ImageView authorImage;
    private TextView authorId;
    private TextView authorName;
    private TextView phoneNumber;
    private TextView emailId;
    private TextView status;
    private TextView authorGender;
    private TextView authorDob;
    private FirebaseFirestore mFireStoreDatabase;
    private String AuthorId;
    private TextView OpenPublicProfile;

    private Toolbar AuthorDetailToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.author_detail);
        findViews();

        AuthorDetailToolbar = (Toolbar) findViewById(R.id.author_detail_toolbar);
        setSupportActionBar(AuthorDetailToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.AUTHOR_DETAIL));

        AuthorId=getIntent().getStringExtra("author_id");

        mFireStoreDatabase=FirebaseFirestore.getInstance();

        OpenPublicProfile= (TextView) findViewById(R.id.open_author_public_profile);
        OpenPublicProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LaunchReporterProfile=new Intent(ReportStoryAuthorDetail.this, AuthorProfileActivity.class);
                LaunchReporterProfile.putExtra("user_key",AuthorId);
                LaunchReporterProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                LaunchReporterProfile.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(LaunchReporterProfile);
            }
        });


        if (AuthorId!=null){
            authorId.setText(AuthorId);
            mFireStoreDatabase.collection("Users").document(AuthorId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot dataSnapshot=task.getResult();
                        if (dataSnapshot.contains("name")) {
                            String name = dataSnapshot.get("name").toString();
                            authorName.setText(name);
                        }else {
                            authorName.setText("NOT AVAILABLE");
                        }
                        String image=null;
                        if (dataSnapshot.contains("image")) {
                            image = dataSnapshot.get("image").toString();
                        }

                        if (dataSnapshot.contains("email")) {
                            String Email = dataSnapshot.get("email").toString();
                            emailId.setText(Email);
                        }else {
                            emailId.setText("NOT AVAILABLE");
                        }
                        if (dataSnapshot.contains("status")){
                            status.setText( dataSnapshot.get("status").toString());
                        }else {
                            status.setText("NOT AVAILABLE");
                        }

                        if (dataSnapshot.contains("gender")) {
                            String gender = dataSnapshot.get("gender").toString();
                            authorGender.setText(gender);
                        }else {
                            authorGender.setText("NOT AVAILABLE");
                        }

                        if (dataSnapshot.contains("dob")) {
                            if (dataSnapshot.getString("dob").toString().length()>4){
                                String birthday = dataSnapshot.get("dob").toString();
                                authorDob.setText(birthday);
                            }else {
                                authorDob.setText("NOT AVAILABLE");
                            }
                        }else {
                            authorDob.setText("NOT AVAILABLE");
                        }

                        if (dataSnapshot.contains("phone_no")) {
                            String phone_no = dataSnapshot.get("phone_no").toString();
                            phoneNumber.setText(phone_no);
                        }else {
                            phoneNumber.setText("NOT AVAILABLE");
                        }

                        if (image!=null&&!image.equals("default")) {

                            final String finalImage = image;
                            Picasso.with(ReportStoryAuthorDetail.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_user_).into(authorImage,
                                    new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.e("onerrorSection", "this is success");
                                        }

                                        @Override
                                        public void onError() {
                                            Log.e("onerrorSection", "this is error");
                                            Picasso.with(ReportStoryAuthorDetail.this).load(finalImage).placeholder(R.drawable.ic_user_).into(authorImage);
                                        }
                                    });
                        }
                    }
                }
            });
        }else {
            authorId.setText("Author Id is null");
        }


    }

    private void findViews() {
        authorImage = (ImageView)findViewById( R.id.author_image );
        authorId = (TextView)findViewById( R.id.author_id );
        authorName = (TextView)findViewById( R.id.author_name );
        phoneNumber = (TextView)findViewById( R.id.phone_number );
        emailId = (TextView)findViewById( R.id.email_id );
        status = (TextView)findViewById( R.id.status );
        authorGender = (TextView)findViewById( R.id.author_gender );
        authorDob = (TextView)findViewById( R.id.author_dob );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (android.R.id.home == item.getItemId()) {
            onBackPressed();
        }
        return true;
    }

}
