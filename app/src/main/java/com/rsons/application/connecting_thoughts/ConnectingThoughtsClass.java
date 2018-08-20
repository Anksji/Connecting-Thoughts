package com.rsons.application.connecting_thoughts;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by ankit on 2/25/2018.
 */

public class ConnectingThoughtsClass extends Application {

    //private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();
        /*FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE ));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        FontManager.preLoadFonts(getApplicationContext());
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            mUserDatabase=FirebaseDatabase.getInstance().getReference().child("status").
                    child(mAuth.getCurrentUser().getUid());
            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null){
                        mUserDatabase.onDisconnect().setValue("offline");

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
*/


    }
}
