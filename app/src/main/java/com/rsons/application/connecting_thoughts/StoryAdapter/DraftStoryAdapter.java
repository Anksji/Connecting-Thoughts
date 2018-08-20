package com.rsons.application.connecting_thoughts.StoryAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.StoryClasses.Simple_Story;
import com.rsons.application.connecting_thoughts.WritingActivity.WriteStoryActivity;

import java.util.List;

/**
 * Created by ankit on 2/11/2018.
 */

public class DraftStoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private final int ITEM_VIEW_TYPE_BASIC = 0;
    private final int ITEM_VIEW_TYPE_FOOTER = 1;

    protected List<Simple_Story> currentArticle;
    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;
    private boolean isPublishedStory;
    ProgressDialog mProgressDialog;
    private boolean value;

    public DraftStoryAdapter(List<Simple_Story> article, Activity activity,boolean isPublisedStory){
        this.currentArticle = article;
        this.activity=activity;
        this.isPublishedStory=isPublisedStory;
        mFireStoreDatabase= FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStoreDatabase.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();
        mProgressDialog=new ProgressDialog(activity);
        mProgressDialog.setCanceledOnTouchOutside(false);

    }


    public void refreshAdapter(boolean value, List<Simple_Story> tempCardItem){
        this.value = value;
        this.currentArticle = tempCardItem;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        RecyclerView.ViewHolder vh;
        if (i == ITEM_VIEW_TYPE_BASIC) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.draft_story_card, viewGroup, false);

            vh = new DraftStoryHolderClass(v);
        } else{
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.progress_item, viewGroup, false);

            vh = new ProgressViewHolder(v);
        }


        /*View v;

            v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.draft_story_card, viewGroup, false);*/

            //return new DraftStoryHolderClass(v);
        return  vh;

    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int i) {
        //globalHolder=rholder;


        if (rholder instanceof DraftStoryHolderClass) {
            final DraftStoryHolderClass holder;

            holder = (DraftStoryHolderClass) rholder;
            DraftStoryHolderClass.setupStoryCard(holder, activity, currentArticle.get(i),isPublishedStory);

            holder.DeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(holder.DeleteButton,i);

                }
            });


        } else {
            if (!value) {
                ((ProgressViewHolder) rholder).progressBar.setVisibility(View.VISIBLE);
                ((ProgressViewHolder) rholder).progressBar.setIndeterminate(true);
            } else ((ProgressViewHolder) rholder).progressBar.setVisibility(View.GONE);
        }




    }

    @Override
    public int getItemViewType(int position) {
        return currentArticle.get(position) != null ? ITEM_VIEW_TYPE_BASIC : ITEM_VIEW_TYPE_FOOTER;
    }


    @Override
    public int getItemCount() {
        return currentArticle == null ? 0: currentArticle.size();
    }


    private void showPopupMenu(View view,int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_view_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;
        public MyMenuItemClickListener(int positon) {
            this.position=positon;
        }

        @Override
        public boolean onMenuItemClick(final MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.delete_stry:

                    AlertPopUpDialog(activity,position);

                    return true;

                default:
            }
            return false;
        }
    }

    public  void AlertPopUpDialog(final Activity activity, final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View mView = activity.getLayoutInflater().inflate(R.layout.want_do_delete_story, null);


        RelativeLayout delete_story= (RelativeLayout) mView.findViewById(R.id.delete_published_story);
        RelativeLayout CancelBtn= (RelativeLayout) mView.findViewById(R.id.cancel_btn);


        builder.setView(mView);
        final AlertDialog Warning_dialog = builder.create();

        delete_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressDialog.setMessage("Please wait ..!!");
                mProgressDialog.show();

                mFireStoreDatabase.collection("Story").document(currentArticle.get(position).getStory_id_server_name()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot=task.getResult();
                            if (documentSnapshot.exists()){

                                String ParentStoryId=documentSnapshot.getString("parent_story_id");
                                String AuthorId=documentSnapshot.getString("story_author_id");
                                Log.e("slkdjfkf","document exist parent id is "+ParentStoryId+" and author id "+AuthorId);
                                if (ParentStoryId!=null&&ParentStoryId.length()>5&&AuthorId!=null&&AuthorId.length()>5){
                                    mFireStoreDatabase.collection("Users").document(AuthorId).collection("Story").document(ParentStoryId)
                                            .collection("All_Chapter_list").document(currentArticle.get(position).getStory_id_server_name()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Log.e("slkdjfkf","first deletion is done");
                                                mFireStoreDatabase.collection("Story").document(currentArticle.get(position).getStory_id_server_name()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Log.e("slkdjfkf","second deletion is done");
                                                            mFireStoreDatabase.collection("Users").document(mAuth.getUid()).collection("Story").document(currentArticle.get(position).getStory_id_server_name())
                                                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        Log.e("slkdjfkf","final deletion is done");
                                                                        mProgressDialog.dismiss();
                                                                            currentArticle.remove(position);
                                                                            notifyItemRemoved(position);
                                                                            notifyItemRangeChanged(position, getItemCount());
                                                                            Toast.makeText(activity,activity.getString(R.string.YOUR_STORY_DELETED_SUCCESSFULLY),Toast.LENGTH_SHORT).show();
                                                                        }else {
                                                                        mProgressDialog.dismiss();
                                                                            currentArticle.remove(position);
                                                                            notifyItemRemoved(position);
                                                                            notifyItemRangeChanged(position, getItemCount());
                                                                            Toast.makeText(activity,activity.getString(R.string.SOMETHING_WENT_WRONG),Toast.LENGTH_SHORT).show();




                                        /*mFireStoreDatabase.collection("Search").document("suggestion_list").collection("List")
                                                .document(currentArticle.get(position).getStory_id_server_name()).delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            currentArticle.remove(position);
                                                            notifyItemRemoved(position);
                                                            notifyItemRangeChanged(position, getItemCount());
                                                            Toast.makeText(activity,activity.getString(R.string.YOUR_STORY_DELETED_SUCCESSFULLY),Toast.LENGTH_SHORT).show();
                                                        }else {
                                                            currentArticle.remove(position);
                                                            notifyItemRemoved(position);
                                                            notifyItemRangeChanged(position, getItemCount());
                                                            Toast.makeText(activity,activity.getString(R.string.YOUR_STORY_DELETED_SUCCESSFULLY),Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                });*/
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(activity,activity.getString(R.string.SOMETHING_WENT_WRONG),Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    }
                });


                Warning_dialog.dismiss();

            }
        });

        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Warning_dialog.dismiss();
            }
        });

        Warning_dialog.show();
    }



    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

   /* @Override
    public int getItemCount() {
        return currentArticle.size();
    }*/
}