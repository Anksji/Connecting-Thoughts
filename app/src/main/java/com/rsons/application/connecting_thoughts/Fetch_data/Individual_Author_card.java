package com.rsons.application.connecting_thoughts.Fetch_data;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rsons.application.connecting_thoughts.R;
import com.rsons.application.connecting_thoughts.UsersActivity.AuthorProfileActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ankit on 3/7/2018.
 */

public class Individual_Author_card extends RecyclerView.ViewHolder{

    CircleImageView AuthorImage;
    TextView AuthorName,AuthorDescription;
    //AuthorWriting;
    RelativeLayout ViewProfile;



    public Individual_Author_card(final View view) {
        super(view);
        this.AuthorImage= (CircleImageView) view.findViewById(R.id.author_image);
        this.AuthorName= (TextView) view.findViewById(R.id.author_name);
        //this.AuthorWriting= (TextView) view.findViewById(R.id.writing_number);
        this.ViewProfile= (RelativeLayout) view.findViewById(R.id.view_profile);
        this.AuthorDescription= (TextView) view.findViewById(R.id.author_description);

    }

    public static void setupIndividualAuthorCard(final Individual_Author_card holder, final Activity activity,
                                                final AuthorModel singleItem) {


        holder.AuthorName.setText(singleItem.getAuthorName());
        //holder.AuthorWriting.setText(singleItem.getAuthorWriting());
        holder.AuthorDescription.setText(""+singleItem.getAuthorDescription());
        final Picasso picasso = Picasso.with(activity);
        picasso.setLoggingEnabled(false);
        picasso.setDebugging(false);

        picasso.load(singleItem.getAuthorImageUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_user_).into(holder.AuthorImage,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("onerrorSection", "this is success");
                    }

                    @Override
                    public void onError() {
                        Log.e("onerrorSection", "this is error");
                        picasso.load(singleItem.getAuthorImageUrl()).placeholder(R.drawable.ic_user_).into(holder.AuthorImage);
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profilePage=new Intent(activity, AuthorProfileActivity.class);
                profilePage.putExtra("user_key",singleItem.getAuthorId());
                activity.startActivity(profilePage);
                //mContext.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });

        holder.ViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profilePage=new Intent(activity, AuthorProfileActivity.class);
                profilePage.putExtra("user_key",singleItem.getAuthorId());
                activity.startActivity(profilePage);
            }
        });


    }
}
