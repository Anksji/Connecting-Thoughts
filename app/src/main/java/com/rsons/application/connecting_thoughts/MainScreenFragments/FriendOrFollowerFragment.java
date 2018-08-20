package com.rsons.application.connecting_thoughts.MainScreenFragments;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rsons.application.connecting_thoughts.Const;
import com.rsons.application.connecting_thoughts.EndlessRecyclerViewScrollListener;
import com.rsons.application.connecting_thoughts.FriendOr_Follower.FriendOrFollowerAdapter;
import com.rsons.application.connecting_thoughts.FriendOr_Follower.FriendOrFollowerModel;
import com.rsons.application.connecting_thoughts.R;

import java.util.ArrayList;

import static com.rsons.application.connecting_thoughts.Const.IS_DIRECT_GO_BACK_FROM_FOLLOWER;

/**
 * Created by ankit on 2/9/2018.
 */

public class FriendOrFollowerFragment extends Fragment {

    public FriendOrFollowerFragment() {
    // Required empty public constructor
}

    boolean isLoading,IsDataPresent=true;
    private int visibleThreshold = 10;
    int totalItemCount,lastVisibleItem;

    private RecyclerView mFriendsList;
    private FriendOrFollowerAdapter FriendFollowerAdapter;
    private ArrayList<FriendOrFollowerModel> FriendFollowerList=new ArrayList<>();
    private ArrayList<String> MyFollowerIdList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStoreDatabase;
    LinearLayoutManager mLayoutManager;
    private  String mCurrent_user_id;
    private ProgressBar LoadingCircle;
    RelativeLayout LoadingScreen;
    private ImageView MovingAlone;
    private TextView MovingAloneMessage;
    int GetDataAt_a_time=1;
    private boolean IsScroll=true;
    private ImageView OfflineCloud;

    SwipeRefreshLayout swipeLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView=inflater.inflate(R.layout.friend_or_follower_fragment, container, false);


        mFriendsList= (RecyclerView) mView.findViewById(R.id.fried_list);
        LoadingCircle= (ProgressBar) mView.findViewById(R.id.loading_circle);
        MovingAlone= (ImageView) mView.findViewById(R.id.moving_alone);
        LoadingScreen= (RelativeLayout) mView.findViewById(R.id.loading);
        MovingAloneMessage= (TextView) mView.findViewById(R.id.message_text);
        OfflineCloud= (ImageView) mView.findViewById(R.id.offline_cloud);

        mAuth = FirebaseAuth.getInstance();
        mFireStoreDatabase=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);
        mCurrent_user_id=mAuth.getCurrentUser().getUid();


        mFriendsList.setHasFixedSize(true);
        FriendFollowerAdapter = new FriendOrFollowerAdapter(getActivity(), FriendFollowerList);
        mLayoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);


        mFriendsList.setLayoutManager(mLayoutManager);
        mFriendsList.setAdapter(FriendFollowerAdapter);

        swipeLayout= (SwipeRefreshLayout) mView.findViewById(R.id.swipeToRefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FriendFollowerList.clear();
                //FriendFollowerAdapter.notifyDataSetChanged();
                //NotifyDataSetChange(my_recycler_view.getContext());
                //LoadingScreen.setVisibility(View.VISIBLE);
                MyFollowerIdList.clear();
                LoadAllFriendList();
            }
        });


        mFriendsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // total number of items in the data set held by the adapter
                totalItemCount =mLayoutManager.getItemCount();
                //adapter position of the first visible view.
                lastVisibleItem =mLayoutManager.findLastVisibleItemPosition();

                //if it isn't currently loading, we check to see if it reached
                //the visibleThreshold(ex,5) and need to reload more data,
                //if it need to reload some more data, execute loadData() to
                //fetch the data (showed next)
                if (!isLoading && totalItemCount <=
                        (lastVisibleItem+visibleThreshold)) {
                    if (IsDataPresent){
                        GetDataAt_a_time++;
                        LoadAllFriendList();
                        isLoading = true;
                    }

                }
            }
        });


        Log.e("dslkfdjks","inside onCreateView function ");

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("dslkfdjks","inside onActivityCreated function ");

    }


    @Override
    public void onResume() {
        super.onResume();
        if (IS_DIRECT_GO_BACK_FROM_FOLLOWER){
            IS_DIRECT_GO_BACK_FROM_FOLLOWER=false;
            FriendFollowerList.clear();
            FriendFollowerAdapter.notifyDataSetChanged();
            //NotifyDataSetChange(my_recycler_view.getContext());
            LoadingScreen.setVisibility(View.VISIBLE);
            MyFollowerIdList.clear();
            LoadAllFriendList();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (IsScroll){
            /*if (checkNetConnection()){*/
                LoadAllFriendList();
            /*}else {
                OfflineCloud.setVisibility(View.VISIBLE);
                LoadingCircle.setVisibility(View.GONE);
                MovingAloneMessage.setText(getString(R.string.NO_INTERNET_CONNECTION));
                MovingAloneMessage.setVisibility(View.VISIBLE);
            }*/

        }else {
            LoadingScreen.setVisibility(View.GONE);
            //LoadingProgress.setVisibility(View.GONE);
        }
        Log.e("dslkfdjks","inside onStart function ");
    }

    private void LoadAllFriendList() {

        if (FriendFollowerList.size()>0&&IsDataPresent){
            FriendFollowerList.add(null);
            FriendFollowerAdapter.notifyItemInserted(FriendFollowerList.size() - 1);
        }

        IsScroll=false;
        CollectionReference FriendRef = mFireStoreDatabase.collection("FriendsData").document(mAuth.getUid()).collection("FriendList");

        Query query=FriendRef.orderBy("date", Query.Direction.DESCENDING).limit(GetDataAt_a_time* Const.LOAD_DATA_AT_ONCE);
        Log.e("dslkfdjks","inside LoadAllFriendList function ");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean IsEmpty=true;
                     final ArrayList<FriendOrFollowerModel> Temp_FriendFollowerList=new ArrayList<>();
                    for (DocumentSnapshot doc : task.getResult()) {
                        IsEmpty=false;
                        if (!MyFollowerIdList.contains(doc.getId())) {
                            MyFollowerIdList.add(doc.getId());

                            Temp_FriendFollowerList.add(new FriendOrFollowerModel(doc.getId(),doc.getString("friend_image"),
                                    doc.getString("friend_name"),doc.getString("friend_status")));


                        }
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (FriendFollowerList.size()>0&&IsDataPresent){
                                if (FriendFollowerList.get(FriendFollowerList.size() - 1)==null){
                                    FriendFollowerList.remove(FriendFollowerList.size() - 1);
                                    FriendFollowerAdapter.notifyItemRemoved(FriendFollowerList.size());
                                }

                            }

                            if (Temp_FriendFollowerList.size()==0){
                                Log.e("dkfjskfdssdsfsf","Stop condition temp size is "+Temp_FriendFollowerList.size());
                                IsDataPresent=false;
                            }
                            FriendFollowerList.addAll(Temp_FriendFollowerList);
                            isLoading=false;
                            FriendFollowerAdapter.notifyDataSetChanged();

                            Log.e("dkfjskfdssdsfsf","this is friend list size "+FriendFollowerList.size());
                            Log.e("dkfjskfdssdsfsf","this is temp list size "+Temp_FriendFollowerList.size());
                            swipeLayout.setRefreshing(false);
                            if (FriendFollowerList.size()<=0){
                                LoadingCircle.setVisibility(View.GONE);
                                MovingAlone.setVisibility(View.VISIBLE);
                                MovingAloneMessage.setVisibility(View.VISIBLE);
                                LoadingScreen.setVisibility(View.VISIBLE);
                                isLoading=false;

                            }else {
                                LoadingScreen.setVisibility(View.GONE);
                            }

                        }
                    },2000);

                   /* swipeLayout.setRefreshing(false);
                    FriendFollowerAdapter.notifyDataSetChanged();
                    LoadingScreen.setVisibility(View.GONE);
                    if (IsEmpty){
                        swipeLayout.setRefreshing(false);
                        LoadingCircle.setVisibility(View.GONE);
                        MovingAlone.setVisibility(View.VISIBLE);
                        MovingAloneMessage.setVisibility(View.VISIBLE);
                    }else {
                        setupEndlessListener();
                    }*/
                }
                else {
                    IsScroll=true;
                    if (FriendFollowerList.size()<=0){
                        swipeLayout.setRefreshing(false);
                        LoadingCircle.setVisibility(View.GONE);
                        MovingAlone.setVisibility(View.VISIBLE);
                        MovingAloneMessage.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        query.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("sdfjsklsd","this is error "+e.getMessage());
            }
        });

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
/*
    private void setupEndlessListener(){
        mFriendsList.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager, 2) {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetDataAt_a_time++;
                        LoadAllFriendList();
                    }
                }, 150);
            }
        });
    }*/


}