package com.rsons.application.connecting_thoughts.ChatActivityAndClasses;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.rsons.application.connecting_thoughts.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ankit on 3/22/2018.
 */

public class MyAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_IMAGE = 2;
    private final int VIEW_PROG = 0;
    private List<Messages> mMessageList;
    //private List<T> mDataset;
    private FirebaseAuth mAuth;
    private Activity context;
    private ArrayList<ImageModel> ImageData=new ArrayList<>();

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public MyAdapter(List<Messages> mMessageList,  ArrayList<ImageModel> ImageUrlList, Activity ctx, RecyclerView recyclerView) {
        this.mMessageList = mMessageList;
        mAuth= FirebaseAuth.getInstance();
        context=ctx;
        this.ImageData=ImageUrlList;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessageList.get(position)==null){
            return VIEW_PROG;
        }else if (mMessageList.get(position).getType().equals("image")){
            return VIEW_IMAGE;
        }else {
            return VIEW_ITEM ;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_chat_layout, parent, false);

            vh = new MessageViewHolder(v);
        } else if (viewType == VIEW_IMAGE){
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_chat_image_layout, parent, false);
            vh = new IndividualImageMessageView(v);
        }
        else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder itemRowHolder, final int position) {

        int viewType = getItemViewType(position);

        switch (viewType) {
            case VIEW_PROG:
                ((ProgressViewHolder) itemRowHolder).progressBar.setIndeterminate(true);
                break;

            case VIEW_IMAGE:
                final IndividualImageMessageView Mholder;
                final String current_user_id=mAuth.getCurrentUser().getUid();
                Mholder=(IndividualImageMessageView) itemRowHolder;
                IndividualImageMessageView.setupIndividualMessageView(Mholder, context, mMessageList.get(position),position,
                        current_user_id);

                Mholder.MyImageMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ImageDetailActivity.class);
                        //Collections.reverse(ImageData);
                        intent.putParcelableArrayListExtra("data", ImageData);
                        Log.e("fjskjsd","array size is "+ImageData.size()+" and clicked position is "+position);
                        intent.putExtra("pos", position);
                    /*if (ImageData.size()<=position){
                        intent.putExtra("pos", ImageData.size()-1);
                    }else {

                    }*/
                        context.startActivity(intent);
                    }
                });
                Mholder.FirendImageMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ImageDetailActivity.class);
                        //ArrayList<ImageModel> imgList=reverse(ImageData);
                        //Collections.reverse(ImageData);
                        intent.putParcelableArrayListExtra("data", ImageData);
                        Log.e("fjskjsd","array size is "+ImageData.size()+" and clicked position is "+position);
                        intent.putExtra("pos", position);
                    /*if (ImageData.size()<=position){
                        intent.putExtra("pos", ImageData.size()-1);
                    }else {

                    }*/
                        context.startActivity(intent);
                    }
                });
                break;

            case VIEW_ITEM:
                // fall through
            default:
                Log.d("jdkfhdjfsd", " calling view method");
                final MessageViewHolder  holder;
                holder=(MessageViewHolder) itemRowHolder;


                final String current_u_id=mAuth.getCurrentUser().getUid();

                holder.Message_progress_bar.setVisibility(View.GONE);
                final Messages messages=mMessageList.get(position);

                final String From_user=messages.getFrom();

                String DateString="";
                Log.d("jdkfhdjfsd", " outside  try date =" + messages.getTime());
                try{

                    Date date = (Date) messages.getTime();

                    DateString = android.text.format.DateFormat.format("dd MMM  hh:mm a",new Date(date.getTime())).toString();
                    Log.d("jdkfhdjfsd", " inside  try date =" + DateString);


                }catch (ClassCastException e){
                    Log.e("error","this is error comming "+e.getMessage());
                    try{

                        long timeINlong=Long.parseLong(messages.getTime()+"");

                        DateString = android.text.format.DateFormat.format("dd MMM  hh:mm a",new Date(timeINlong)).toString();

                        Log.d("jdkfhdjfsd", "catch block inside inner try date =" + DateString);
                        /*DateString=messages.getTime().toString();
                        DateString=DateString.substring(3,DateString.length());
                        String day=DateString.substring(5,7);
                        String year=DateString.substring((DateString.length()-5),DateString.length());
                        DateString=day+" "+DateString.substring(0,4)+" "+year;

                        Log.e("jdkfhdjfsd","this is date "+DateString);

                        Date date = (Date) messages.getTime();
                        Log.d("jdkfhdjfsd", "date=" + date);
                        Log.d("jdkfhdjfsd", "time=" + date.getTime());*/
                        //DateString=android.text.format.DateFormat.format("dd MMM  hh:mm a",new Date( messages.getTime()+"")).toString();

                    }catch (NumberFormatException exp){
                        DateString=""+messages.getTime();
                        Log.d("jdkfhdjfsd", "last catch block date =" + DateString);
                       /* DateString=messages.getTime().toString();
                        DateString=DateString.substring(3,DateString.length());
                        String day=DateString.substring(5,7);
                        String year=DateString.substring((DateString.length()-5),DateString.length());
                        DateString=day+" "+DateString.substring(0,4)+" "+year;
                                Log.e("kdlfjslkfj","complete date "+DateString+ " year = "+year+" day is "+day);*/
                    }

                }

                if (messages.getType().equals("text")){
                    holder.TextMessage_layout.setVisibility(View.VISIBLE);
                    if(current_u_id.equals(From_user)){
                        //holder.AudioMessageLayout.setVisibility(View.GONE);

                        holder.Friend_messageText.setVisibility(View.GONE);
                        //holder.profileImage.setVisibility(View.GONE);
                        holder.My_messageText.setVisibility(View.VISIBLE);
                        holder.My_messageText.setText(messages.getMessage());

                        /*holder.My_messageText.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                Toast.makeText(context,"hai long pressed clicked",Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        });*/

                        holder.MyMessageTime.setVisibility(View.VISIBLE);
                        holder.MyMessageTime.setText(DateString);
                        holder.FriendMessageTime.setVisibility(View.GONE);
                    }else {
                        holder.My_messageText.setVisibility(View.GONE);
                        holder.Friend_messageText.setVisibility(View.VISIBLE);
                        holder.Friend_messageText.setText(messages.getMessage());
                        holder.MyMessageTime.setVisibility(View.GONE);
                        holder.FriendMessageTime.setVisibility(View.VISIBLE);
                        holder.FriendMessageTime.setText(DateString);
                    }


                }


        /*else if (messages.getType().equals("audio")){

            holder.TextMessage_layout.setVisibility(View.GONE);
            holder.AudioMessageLayout.setVisibility(View.VISIBLE);


            if ( current_u_id.equals(From_user)) {
                holder.Friend_Chat_Audio.setVisibility(View.GONE);
                holder.My_chat_Audio.setVisibility(View.VISIBLE);



            }else {
                holder.Friend_Chat_Audio.setVisibility(View.VISIBLE);
                holder.My_chat_Audio.setVisibility(View.GONE);
            }
            SettingsForAudioMessages(holder,messages);
        }else {
            Toast.makeText(context,"this is not audio and text",Toast.LENGTH_SHORT).show();
        }*/
                /*holder.ProgressBarMy.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        holder.presentTime=seekBar.getProgress();
                        holder.ProgressBarMy.setProgress((int)holder.presentTime);
                        holder.mediaPlayer.seekTo((int)holder.presentTime);
                    }
                });*/

        }
    }

    /*public ArrayList<ImageModel> reverse(ArrayList<ImageModel> list) {
        ArrayList ImgList=new ArrayList();
        for(int i = 0, j = list.size() - 1; i < j; i++) {
            ImageModel value=list.remove(j);
            ImgList.add(i, value);
        }
        return ImgList;
    }*/

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        /*public boolean playOn=true;
        public Handler seekHandler = new Handler();
        public TextView tx1, total_time_of_my_audio;
        public long finalTime;
        public boolean playing,songCompleted;
        public long presentTime;*/
        public ProgressBar Message_progress_bar;
        public TextView Friend_messageText,My_messageText,FriendMessageTime,MyMessageTime;
        public LinearLayout TextMessage_layout;
        //public LinearLayout ImageMessageLayout;
        /*public TextView Friend_Pic_messageTime,My_Pic_message_time;
        public ImageView FirendImageMessage,MyImageMessage;*/


        //public LinearLayout AudioMessageLayout,My_chat_Audio,Friend_Chat_Audio;
        /*public ImageView PlayBtn_my;
        public ProgressBar ProgressBarMy;
        public MediaPlayer mediaPlayer;
        public ImageView PlayBtn_friend;
        public ProgressBar ProgressBar_friend;*/


        public MessageViewHolder(View itemView) {
            super(itemView);
            /*tx1= (TextView) itemView.findViewById(R.id.textView_my);
            total_time_of_my_audio = (TextView) itemView.findViewById(R.id.textView2_my);
            PlayBtn_my= (ImageView) itemView.findViewById(R.id.play_my);
            ProgressBarMy= (ProgressBar) itemView.findViewById(R.id.progressBar2_my);
            PlayBtn_friend= (ImageView) itemView.findViewById(R.id.play);
            ProgressBar_friend= (ProgressBar) itemView.findViewById(R.id.progressBar2);
            AudioMessageLayout= (LinearLayout) itemView.findViewById(R.id.audio_message_layout);
            My_chat_Audio= (LinearLayout) itemView.findViewById(R.id.my_chat_audio);
            Friend_Chat_Audio= (LinearLayout) itemView.findViewById(R.id.friend_chat_audio);*/
           /* FirendImageMessage= (ImageView) itemView.findViewById(R.id.friend_picture_message);
            MyImageMessage= (ImageView) itemView.findViewById(R.id.my_picture_message);
            Friend_Pic_messageTime= (TextView) itemView.findViewById(R.id.friend_pic_message_time);
            My_Pic_message_time = (TextView) itemView.findViewById(R.id.my__pic_message_time);
            ImageMessageLayout= (LinearLayout) itemView.findViewById(R.id.image_layout);*/
            Message_progress_bar= (ProgressBar) itemView.findViewById(R.id.message_progress_bar);
            MyMessageTime= (TextView) itemView.findViewById(R.id.my_message_time);
            FriendMessageTime= (TextView) itemView.findViewById(R.id.friend_message_time);
            TextMessage_layout= (LinearLayout) itemView.findViewById(R.id.text_layout);
            Friend_messageText = (TextView) itemView.findViewById(R.id.other_user_message);
            My_messageText= (TextView) itemView.findViewById(R.id.my_message_txt);




        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }
}