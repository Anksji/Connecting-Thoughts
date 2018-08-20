package com.rsons.application.connecting_thoughts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

import java.util.Locale;

/**
 * Created by ankit on 3/19/2018.
 */

public class SplashScreen extends AppCompatActivity {

    private boolean trans=true;
    Thread splashTread;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStoreDatabase;
    private  String NOTIFICATION_SHARED_PREF_NAME="cONNECTING&*^(tHOUGHTS*(";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);

        String CurrnetLang= Const.GetLanguageFromSharedPref(SplashScreen.this);

        for(int i=0;i<Const.languageId.length;i++){
            Log.e("fjskfjks","inside for loop setting language "+CurrnetLang);
            if (CurrnetLang.equals(Const.languageId[i])){
                Log.e("fjskfjks","setting language "+CurrnetLang+" language iso code is "+Const.languageISO[i]);
                Locale locale = new Locale(Const.languageISO[i]);
                setLocale(locale);
                /*String languageToLoad  = Const.languageISO[i]; // your language
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());*/
                break;
            }
        }

        Const.CURRENT_LANGUAGE = Const.GetLanguageFromSharedPref(SplashScreen.this);
        FirebaseApp.initializeApp(this);


        mFireStoreDatabase=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();



        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, null)
                .addApi(AppInvite.API)
                .build();

        Log.e("kdfjkjgkggfd","getting intent data from play store "+getIntent());
        boolean autoLaunchDeepLink = false;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (result.getStatus().isSuccess()) {
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);

                                    Log.e("kdfjkjgkggfd","getting the link from play store "+deepLink);

                                    String storyId=deepLink.substring(8);
                                    final String finalStoryId=storyId.substring(0,storyId.length()-5);

                                    SharedPreferences prefs = SplashScreen.this.getSharedPreferences(NOTIFICATION_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("share_story_id",finalStoryId);
                                    editor.apply();


                                    Log.e("kdfjkjgkggfd","getting the final storyid from play store "+finalStoryId);
                                } else {
                                    Log.d("kdfjkjgkggfd", "Oops, looks like there was no deep link found!");
                                }
                            }
                        });


        if (mAuth.getCurrentUser()!=null){
            ResetNotificationParams();
        }

        StartAnimations();
    }

    @SuppressWarnings("deprecation")
    private void setLocale(Locale locale){
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            configuration.setLocale(locale);
        } else{
            configuration.locale=locale;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            getApplicationContext().createConfigurationContext(configuration);
        } else {
            resources.updateConfiguration(configuration,displayMetrics);
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

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        RelativeLayout l=(RelativeLayout) findViewById(R.id.ct_splash);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();


        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 500) {
                        sleep(100);
                        waited += 100;
                    }

                } catch (InterruptedException e) {
                    // do nothing
                } finally {

                    runOnUiThread(new Runnable() {
                        public void run() {

                            VersionCheck();

                        }
                    });
                }

            }
        };
        splashTread.start();

    }
    private void VersionCheck() {

        if (mAuth.getCurrentUser()!=null){
            final int App_versionCode = BuildConfig.VERSION_CODE;
            //String versionName = BuildConfig.VERSION_NAME;
            Log.e("skjfskfslfjskl","current application  version is "+App_versionCode);

            DocumentReference query= mFireStoreDatabase.collection("VersionCheck").document("Current_version");
            query.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot document=task.getResult();
                        if (document.exists()){
                            boolean IsUpdateRequire=document.getBoolean("Is_update_require").booleanValue();
                            Long Current_version=document.getLong("version_number").longValue();

                            if(IsUpdateRequire){
                                if (App_versionCode<Current_version){
                                    ShowUpdateDialogPopUp(SplashScreen.this);
                                }else {
                                    LaunchActivity();
                                }
                            }else {
                                LaunchActivity();
                            }


                            Log.e("skjfskfslfjskl","current server  version is "+Current_version);
                        }
                    }
                }
            });
            query.get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    LaunchActivity();
                }
            });
        }else {
            LaunchActivity();
        }

        Log.e("skjfskfslfjskl","Calling version check ");
    }

    private void ShowUpdateDialogPopUp(Activity activity) {


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View mView = activity.getLayoutInflater().inflate(R.layout.update_dialog, null);

        RelativeLayout Update_Application= (RelativeLayout) mView.findViewById(R.id.update_application);

        builder.setView(mView);
        final AlertDialog Warning_dialog = builder.create();
        Warning_dialog.setCanceledOnTouchOutside(false);


        Update_Application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Warning_dialog.dismiss();
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        Warning_dialog.show();
    }

    public void LaunchActivity(){

        if (Const.CURRENT_LANGUAGE.equals("default")){
            Intent LaunchChooseLanguage=new Intent(SplashScreen.this,ChooseLanguage.class);
            startActivity(LaunchChooseLanguage);
            finish();

        }else {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser==null){
                Intent LaunchLogin=new Intent(SplashScreen.this,LoginActivity.class);
                startActivity(LaunchLogin);
                finish();
            }else {
                mFireStoreDatabase.collection("Users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot=task.getResult();

                            if (documentSnapshot.exists()){
                                if (documentSnapshot.contains("name")){
                                    String UserName=documentSnapshot.getString("name");
                                    if (UserName.equals("New User")||UserName.length()==0){
                                        Log.e("slkdfjskf","this is if part ");
                                        Intent LaunchMain=new Intent(SplashScreen.this,EnterUserName.class);
                                        startActivity(LaunchMain);
                                        finish();

                                    }else {
                                        Log.e("slkdfjskf","this is if part UserName "+UserName);
                                        Intent LaunchMain=new Intent(SplashScreen.this,MainActivity.class);
                                        startActivity(LaunchMain);
                                        finish();
                                    }
                                }
                            }
                            else {
                                Intent LaunchMain=new Intent(SplashScreen.this,EnterUserName.class);
                                startActivity(LaunchMain);
                                finish();
                            }
                        }
                    }
                });

            }
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }




    @Override
    public void finish()
    {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.finish();
        Log.e("skdf","finish method is called");

    }

    @Override
    protected void onPause() {
        if (trans) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        trans=true;
    }


}
