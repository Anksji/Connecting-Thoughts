package com.rsons.application.connecting_thoughts.Notification;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.rsons.application.connecting_thoughts.EndlessRecyclerViewScrollListener;
import com.rsons.application.connecting_thoughts.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ankit on 3/13/2018.
 */

public class AllNotificationListing extends AppCompatActivity{

    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;
    //Query GetSection_Data_query;
    int GetDataAt_a_time=1;
    private ProgressBar LoadingProgress;
    NotificationAdapterClass NotificationAdapter;
    RecyclerView notification_Recycler_View;
    LinearLayoutManager notification_linearLayout_manager;
    private Toolbar mNotificationListToolbar;
    ArrayList<NotificationModel_class> Notification_Model_list =new ArrayList<>();
    ArrayList<String> NotificationIdList =new ArrayList<>();
    ProgressDialog mProgressDialog;
    private ImageView EmptyNotification;

    boolean isLoading,IsDataPresent=true;
    private int visibleThreshold = 10;
    int totalItemCount,lastVisibleItem;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_notification_listing);

        mFireStoreDatabase= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        //GetSection_Data_query= ;

        mProgressDialog=new ProgressDialog(AllNotificationListing.this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage(getString(R.string.PLEASE_WAIT));


        mNotificationListToolbar = (Toolbar) findViewById(R.id.notification_list_toolbar);
        setSupportActionBar(mNotificationListToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.NOTIFICATION));

        EmptyNotification= (ImageView) findViewById(R.id.empty_notification_image);
        LoadingProgress= (ProgressBar) findViewById(R.id.loading_progress);
        notification_Recycler_View = (RecyclerView) findViewById(R.id.notification_listing);
        notification_Recycler_View.setHasFixedSize(true);
        NotificationAdapter = new NotificationAdapterClass(this, Notification_Model_list);
        notification_linearLayout_manager =new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        notification_Recycler_View.setLayoutManager(notification_linearLayout_manager);
        notification_Recycler_View.setAdapter(NotificationAdapter);

        notification_Recycler_View.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // total number of items in the data set held by the adapter
                totalItemCount =notification_linearLayout_manager.getItemCount();
                //adapter position of the first visible view.
                lastVisibleItem =notification_linearLayout_manager.findLastVisibleItemPosition();

                //if it isn't currently loading, we check to see if it reached
                //the visibleThreshold(ex,5) and need to reload more data,
                //if it need to reload some more data, execute loadData() to
                //fetch the data (showed next)
                if (!isLoading && totalItemCount <=
                        (lastVisibleItem+visibleThreshold)) {
                    if (IsDataPresent){
                        GetDataAt_a_time++;
                        getNotificationData();
                        isLoading = true;
                    }

                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getNotificationData();
            }
        }, 50);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SetNotificationCounter_to_Zero();

            }
        }, 1000);
    }

    private void SetNotificationCounter_to_Zero() {
        Map loadData=new HashMap();
        loadData.put("notification_counter",0);
        mFireStoreDatabase.collection("NotificationData").document(mAuth.getUid()).set(loadData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    return;
                }
            }
        });
    }


    /*private void setupEndlessListener(){
        notification_Recycler_View.addOnScrollListener(new EndlessRecyclerViewScrollListener(notification_linearLayout_manager, 2) {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetDataAt_a_time++;
                        getStoryData();
                    }
                }, 150);
            }
        });
    }*/

    public void getNotificationData() {

        if (Notification_Model_list.size()>0&&IsDataPresent){
            Notification_Model_list.add(null);
            NotificationAdapter.notifyItemInserted(Notification_Model_list.size() - 1);
        }

        mFireStoreDatabase.collection("NotificationData").document(mAuth.getUid()).collection("RequestAndStory").orderBy("notification_timestamp", Query.Direction.DESCENDING)
                .limit(GetDataAt_a_time*20).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    final ArrayList<NotificationModel_class> Temp_Notification_Model_list =new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        if (!NotificationIdList.contains(document.getId())){

                            NotificationIdList.add(document.getId());
                            if (document.getString("notification_type").equals("publish_story")){
                                Temp_Notification_Model_list.add(new NotificationModel_class(document.getString("story_author_name"), document.getId(), document.getString("story_Title"),
                                        document.getString("story_intro"),document.get("story_published_time"),document.getString("cover_image"),"publish_story"));

                            }else if (document.getString("notification_type").equals("request")){
                                Temp_Notification_Model_list.add(new NotificationModel_class(document.getString("sending_user_name"), document.get("notification_timestamp"), document.getString("user_image"),
                                        document.getString("from"),"request"));

                            }

                        }
                    }


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Notification_Model_list.size()>0&&IsDataPresent){
                                if (Notification_Model_list.get(Notification_Model_list.size() - 1)==null){
                                    Notification_Model_list.remove(Notification_Model_list.size() - 1);
                                    NotificationAdapter.notifyItemRemoved(Notification_Model_list.size());
                                }

                            }

                            if (Temp_Notification_Model_list.size()==0){
                                Log.e("dkfjskfdssdsfsf","Stop condition temp size is "+Temp_Notification_Model_list.size());
                                IsDataPresent=false;
                            }
                            Notification_Model_list.addAll(Temp_Notification_Model_list);
                            isLoading=false;
                            NotificationAdapter.notifyDataSetChanged();

                            Log.e("dkfjskfdssdsfsf","this is friend list size "+Notification_Model_list.size());
                            Log.e("dkfjskfdssdsfsf","this is temp list size "+Temp_Notification_Model_list.size());

                            if (Notification_Model_list.size()>0){
                                LoadingProgress.setVisibility(View.GONE);
                                NotificationAdapter.notifyDataSetChanged();
                            }else {
                                LoadingProgress.setVisibility(View.GONE);
                                EmptyNotification.setVisibility(View.VISIBLE);
                            }

                        }
                    },2000);



                }
            }
        });

       // setupEndlessListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.notification_more_options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if( android.R.id.home==item.getItemId()){
            onBackPressed();
        }
        else if (item.getItemId()==R.id.clear_notification){

            ClearNotification();

        }
        return true;
    }

    private void ClearNotification() {
        mProgressDialog.show();

        for (int i=0;i<NotificationIdList.size();i++){
            final int finalI = i;
            Log.e("sdfsklf","hai this is size of notification id "+NotificationIdList.size()+" and notification id "+NotificationIdList.get(i));

            mFireStoreDatabase.collection("NotificationData").document(mAuth.getUid()).collection("RequestAndStory").
                    document(NotificationIdList.get(i))
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("kfjsklfj", "DocumentSnapshot successfully deleted!");
                            if (finalI >=NotificationIdList.size()-1){
                                EmptyNotification.setVisibility(View.VISIBLE);
                                mProgressDialog.dismiss();
                                Notification_Model_list.clear();
                                NotificationAdapter.notifyDataSetChanged();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("kfjsklfj", "Error deleting document"+ e.getMessage());
                        }
                    });

        }



    }

}
