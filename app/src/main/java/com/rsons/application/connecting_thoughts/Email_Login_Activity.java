package com.rsons.application.connecting_thoughts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class Email_Login_Activity extends AppCompatActivity {

    private EditText LoginPassword,LoginEmail;
    private Button loginUserBtn;

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        LoginEmail= (EditText) findViewById(R.id.login_email);
        LoginPassword= (EditText) findViewById(R.id.login_passward);

        mToolbar= (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loginUserBtn = (Button) findViewById(R.id.login_user_btn);
        mProgressDialog =new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            Intent intent = new Intent(Email_Login_Activity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        //mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        loginUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=LoginEmail.getText().toString();
                String password=LoginPassword.getText().toString();
                if(!email.isEmpty()&&!password.isEmpty()){
                    mProgressDialog.show();
                    mProgressDialog.setTitle("Logging In");
                    mProgressDialog.setMessage("Please wait while we check your credentials.");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    LoginUser(email,password);
                }
            }
        });

    }

    private void LoginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("fsfs", "signInWithEmail:onComplete:" + task.isSuccessful());

                        if(task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information


                            final FirebaseUser user = task.getResult().getUser();
                            // [START_EXCLUDE]


                            final String user_id=user.getUid();
                            final DocumentReference dataRef = mDatabase.collection("Users").document(user_id);

                            dataRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                    @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {

                                        Toast.makeText(Email_Login_Activity.this,"db is empty",Toast.LENGTH_SHORT).show();
                                        Log.w("tag","Listen failed.", e);
                                        //return;
                                    }
                                    if (snapshot != null && snapshot.exists()){
                                        Intent intent = new Intent(Email_Login_Activity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        String device_Token= FirebaseInstanceId.getInstance().getToken();

                                        Map<String, Object> User = new HashMap<>();
                                        User.put("name", "");
                                        User.put("image", "default");
                                        User.put("thumb_image", "default");
                                        User.put("email","");
                                        User.put("dob","");
                                        User.put("gender","");
                                        User.put("device_token",device_Token);

                                        mDatabase.collection("Users").document(user_id).set(User, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(Email_Login_Activity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("tag", "Error writing document", e);
                                                mProgressDialog.hide();
                                                Toast.makeText(Email_Login_Activity.this, R.string.auth_failed,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });

                        }else {
                            Log.w("fsfs", "signInWithEmail:failed", task.getException());
                            mProgressDialog.hide();
                            Toast.makeText(Email_Login_Activity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}
