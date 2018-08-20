package com.rsons.application.connecting_thoughts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ankit on 3/24/2018.
 */

public class EnterUserName extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStoreDatabase;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_user_name);

        final EditText UserName= (EditText) findViewById(R.id.user_name);

        RelativeLayout SaveUserName= (RelativeLayout) findViewById(R.id.save_user_name_btn);

        mProgressDialog=new ProgressDialog(EnterUserName.this);

        mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mAuth = FirebaseAuth.getInstance();
        mFireStoreDatabase= FirebaseFirestore.getInstance();

        SaveUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserName.getText().toString().length()>0){
                    mProgressDialog.show();
                    Map userData=new HashMap();
                    userData.put("name",UserName.getText().toString());
                    userData.put("lower_case_name",UserName.getText().toString().toLowerCase());

                    mFireStoreDatabase.collection("Users").document(mAuth.getUid()).set(userData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                PutTitle_into_Search_SuggestionList(UserName.getText().toString());
                                mProgressDialog.dismiss();
                                Intent LaunchMain=new Intent(EnterUserName.this,MainActivity.class);
                                startActivity(LaunchMain);
                                finish();
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        }
                    });
                }else {
                    Toast.makeText(EnterUserName.this,getString(R.string.PLEASE_ENTER_YOUR_NAME_BEFORE_SAVING_IT),Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

   /* public void PutTitle_into_Search_SuggestionList(final String title_keyword){

        Map loadData=new HashMap();
        loadData.put("suggestion_keyword",title_keyword);
        mFireStoreDatabase.collection("Search").document("suggestion_list").collection("List").document(mAuth.getUid()).set(loadData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    mProgressDialog.dismiss();
                    Intent LaunchMain=new Intent(EnterUserName.this,MainActivity.class);
                    startActivity(LaunchMain);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }else {
                    Toast.makeText(EnterUserName.this,getString(R.string.PLEASE_TRY_AGAIN),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/


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


            nestedData.put(mAuth.getUid(),title_keyword.replaceAll("\\s","~"));
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

}
