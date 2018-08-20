package com.rsons.application.connecting_thoughts.ChatActivityAndClasses;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.rsons.application.connecting_thoughts.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ankit on 2/24/2018.
 */

public class MessageAdapterClass extends RecyclerView.Adapter<MessageAdapterClass.MessageViewHolder> {

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private Activity context;
    //public static String FriendPic;
    private ArrayList<ImageModel> ImageData=new ArrayList<>();
    //private boolean Pic;
    /*public boolean playPause;
    //MediaPlayer player = new MediaPlayer();
    private Handler mHandler = new Handler();
    private StorageReference mAudioStorage;
    private FirebaseFirestore mFireStoreDatabase;



    private static int pause=R.drawable.pause,play= R.drawable.play;

    public boolean initialStage = true;
    private static String mFileName = null;*/



    public MessageAdapterClass(List<Messages> mMessageList, Activity ctx,String friendPic) {
        this.mMessageList = mMessageList;
        mAuth=FirebaseAuth.getInstance();
        context=ctx;
        //FriendPic=friendPic;
        //Pic =true;
    }

    /*public void UpdateFriendPic(String picUrl){
        FriendPic=picUrl;
    }*/

    public MessageAdapterClass(List<Messages> mMessageList,ArrayList<ImageModel>ImageUrlList, Activity ctx ) {
        this.mMessageList = mMessageList;
        mAuth=FirebaseAuth.getInstance();
        context=ctx;
        //Pic =false;
        this.ImageData=ImageUrlList;
        //FillImageArray();
        //mAudioStorage= FirebaseStorage.getInstance().getReference();
        //mFireStoreDatabase=FirebaseFirestore.getInstance();
    }

    /*private void FillImageArray() {
        for (int i=0;i<mMessageList.size();i++)
        if (!TimeDuration.contains(mMessageList.get(i).getTime_duration())){
            TimeDuration.add(mMessageList.get(i).getTime_duration());
            ImageData.add(0,new ImageModel(mMessageList.get(i).getSourceLocation()));
        }

    }*/


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_chat_layout,parent,false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, final int position) {

        final String current_u_id=mAuth.getCurrentUser().getUid();


        if (position==mMessageList.size()-1){
            holder.Message_progress_bar.setVisibility(View.VISIBLE);
        }else {
            holder.Message_progress_bar.setVisibility(View.GONE);
            final Messages messages=mMessageList.get(position);

            final String From_user=messages.getFrom();
            //long timeinMilles=messages.getTime();
            long timeINlong=Long.parseLong(messages.getTime()+"");
            String DateString= android.text.format.DateFormat.format("dd MMM  hh:mm a",new Date(timeINlong)).toString();
            //String dateString= DateFormat.format("MM/dd/yyyy",new Date(messages.getTime()))+"";
            if (messages.getType().equals("text")){
                holder.TextMessage_layout.setVisibility(View.VISIBLE);
               // holder.ImageMessageLayout.setVisibility(View.GONE);
                if(current_u_id.equals(From_user)){
                    //holder.AudioMessageLayout.setVisibility(View.GONE);

                    holder.Friend_messageText.setVisibility(View.GONE);
                    //holder.profileImage.setVisibility(View.GONE);
                    holder.My_messageText.setVisibility(View.VISIBLE);
                    holder.My_messageText.setText(messages.getMessage());
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
         /*   else if (messages.getType().equals("image")){

                holder.TextMessage_layout.setVisibility(View.GONE);
                holder.ImageMessageLayout.setVisibility(View.VISIBLE);

                if(current_u_id.equals(From_user)){
                    holder.FirendImageMessage.setVisibility(View.GONE);
                    holder.MyImageMessage.setVisibility(View.VISIBLE);

                    final Picasso picasso = Picasso.with(context);
                    picasso.setLoggingEnabled(false);
                    picasso.setDebugging(false);

                    picasso.load(messages.getSourceLocation()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.card_defalt_back_cover).into(holder.MyImageMessage,
                            new Callback() {
                                @Override
                                public void onSuccess() {
                                    Log.e("onerrorSection", "this is success");
                                }

                                @Override
                                public void onError() {
                                    Log.e("onerrorSection", "this is error");
                                    picasso.load(messages.getSourceLocation()).placeholder(R.drawable.card_defalt_back_cover).into(holder.MyImageMessage);
                                }
                            });

                    holder.My_Pic_message_time.setVisibility(View.VISIBLE);
                    holder.My_Pic_message_time.setText(DateString);
                    holder.Friend_Pic_messageTime.setVisibility(View.GONE);
                }else {

                    holder.FirendImageMessage.setVisibility(View.VISIBLE);
                    holder.MyImageMessage.setVisibility(View.GONE);
                    holder.My_Pic_message_time.setVisibility(View.GONE);
                    holder.Friend_Pic_messageTime.setVisibility(View.VISIBLE);

                    final Picasso picasso = Picasso.with(context);
                    picasso.setLoggingEnabled(false);
                    picasso.setDebugging(false);

                    picasso.load(messages.getSourceLocation()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.card_defalt_back_cover).into(holder.FirendImageMessage,
                            new Callback() {
                                @Override
                                public void onSuccess() {
                                    Log.e("onerrorSection", "this is success");
                                }

                                @Override
                                public void onError() {
                                    Log.e("onerrorSection", "this is error");
                                    picasso.load(messages.getSourceLocation()).placeholder(R.drawable.card_defalt_back_cover).into(holder.FirendImageMessage);
                                }
                            });

                    holder.My_Pic_message_time.setVisibility(View.GONE);
                    holder.Friend_Pic_messageTime.setVisibility(View.VISIBLE);
                    holder.Friend_Pic_messageTime.setText(DateString);
                }

            }

            holder.MyImageMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageDetailActivity.class);
                    intent.putParcelableArrayListExtra("data", ImageData);

                    if (ImageData.size()<=position){
                        intent.putExtra("pos", ImageData.size()-1);
                    }else {
                        intent.putExtra("pos", position);
                    }
                    Log.e("fjshjkfsf","current image url is "+ImageData.get(position).getUrl());
                    context.startActivity(intent);
                }
            });
            holder.FirendImageMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, ImageDetailActivity.class);
                    intent.putParcelableArrayListExtra("data", ImageData);

                    if (ImageData.size()<=position){
                        intent.putExtra("pos", ImageData.size()-1);
                    }else {
                        intent.putExtra("pos", position);
                    }
                    Log.e("fjshjkfsf","current image url is "+ImageData.get(position).getUrl());
                    context.startActivity(intent);
                }
            });*/

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


    /*public void seekUpdation(MessageViewHolder holder) {
        holder.presentTime = holder.mediaPlayer.getCurrentPosition();

        if (holder.playing) {

            holder.tx1.setText(String.format("%d : %d ",

                    TimeUnit.MILLISECONDS.toMinutes((long) holder.presentTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) holder.presentTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) holder.presentTime))));
        }
    }
*/


   /* public static void setTime(MessageViewHolder holder){
        holder.total_time_of_my_audio.setText(String.format("%d : %d ",
                TimeUnit.MILLISECONDS.toMinutes((long) holder.finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) holder.finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) holder.finalTime))));

        holder.tx1.setText(String.format("%d : %d ",
                TimeUnit.MILLISECONDS.toMinutes((long) holder.presentTime),
                TimeUnit.MILLISECONDS.toSeconds((long) holder.presentTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) holder.presentTime))));

    }*/



    @Override
    public int getItemViewType(int position) {

        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
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
       /* public LinearLayout ImageMessageLayout;
        public TextView Friend_Pic_messageTime,My_Pic_message_time;
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
            Message_progress_bar= (ProgressBar) itemView.findViewById(R.id.message_progress_bar);
            /*FirendImageMessage= (ImageView) itemView.findViewById(R.id.friend_picture_message);
            MyImageMessage= (ImageView) itemView.findViewById(R.id.my_picture_message);
            Friend_Pic_messageTime= (TextView) itemView.findViewById(R.id.friend_pic_message_time);
            My_Pic_message_time = (TextView) itemView.findViewById(R.id.my__pic_message_time);
            ImageMessageLayout= (LinearLayout) itemView.findViewById(R.id.image_layout);*/
            MyMessageTime= (TextView) itemView.findViewById(R.id.my_message_time);
            FriendMessageTime= (TextView) itemView.findViewById(R.id.friend_message_time);
            TextMessage_layout= (LinearLayout) itemView.findViewById(R.id.text_layout);
            Friend_messageText = (TextView) itemView.findViewById(R.id.other_user_message);
            My_messageText= (TextView) itemView.findViewById(R.id.my_message_txt);




        }
    }
/*

    public void SettingsForAudioMessages(final MessageViewHolder holder, final Messages messages){

        */
/*************** listen friend messages************//*


        holder.mediaPlayer = new MediaPlayer();
        holder.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        holder.total_time_of_my_audio.setText(messages.getTime_duration());

        holder.ProgressBarMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String[] separated = messages.getSourceLocation().split("/");
                if (messages.IsDownloaded()){
                    holder.mediaPlayer.reset();
                    final File localFile  = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS), messages.getSourceLocation()+".3gp");
                    Log.e("fjskfsdf","Play from download section ");
                    try {
                        holder.ProgressBarMy.setMax(20);
                        holder.mediaPlayer.setDataSource(String.valueOf(localFile));
                        //(player.getDuration());
                        holder.mediaPlayer.prepare();
                        holder.mediaPlayer.start();
                        Log.e("dfjkdfs","duration is "+(holder.mediaPlayer.getDuration()));
                        context.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if(holder.mediaPlayer != null){
                                    int mCurrentPosition = holder.mediaPlayer.getCurrentPosition() / 1000;
                                    holder.ProgressBarMy.setProgress(mCurrentPosition);
                                }
                                mHandler.postDelayed(this, 1000);
                            }
                        });

                        holder.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                holder.mediaPlayer.reset();
                                holder.ProgressBarMy.setProgress(20);
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    try {
                        Log.e("sdfskfksf","file name is "+messages.getSourceLocation());
                        mAudioStorage=mAudioStorage.child("AudioChatMessages").child(messages.getSourceLocation()+".3gp");
                        final File localFile  = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS), messages.getSourceLocation()+".3gp");
                        localFile .createNewFile();
                        mAudioStorage.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {

                                Log.e("fjskfsdf","file get from "+messages.getFrom()+" to "+messages.getTo());

                                final Map messageMap=new HashMap();
                                messageMap.put("is_downloaded",true);
                                mFireStoreDatabase.collection("messages").document(messages.getFrom()).collection(messages.getTo()).document(messages.getMessageId())
                                        .set(messageMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){
                                            try {
                                                Log.e("fjskfsdf","bolean value changed");
                                                holder.mediaPlayer.reset();
                                                holder.ProgressBarMy.setMax(20);
                                                holder.mediaPlayer.setDataSource(String.valueOf(localFile));
                                                //(player.getDuration());
                                                holder.mediaPlayer.prepare();
                                                holder.mediaPlayer.start();
                                                //Log.e("dfjkdfs","duration is "+(player.getDuration()));
                                                context.runOnUiThread(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        if(holder.mediaPlayer != null){
                                                            int mCurrentPosition = holder.mediaPlayer.getCurrentPosition() / 1000;
                                                            holder.ProgressBarMy.setProgress(mCurrentPosition);
                                                        }
                                                        mHandler.postDelayed(this, 1000);
                                                    }
                                                });
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });

                            }
                        });

                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
            }
        });

        */
/*************** listen own messages************//*


        holder.PlayBtn_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //String[] separated = messages.getSourceLocation().split("/");
                if (messages.IsDownloaded()){
                    holder.mediaPlayer.reset();
                    final File localFile  = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS), messages.getSourceLocation()+".3gp");
                    Log.e("fjskfsdf","Play from download section ");
                    try {
                        holder.ProgressBarMy.setMax(20);
                        holder.mediaPlayer.setDataSource(String.valueOf(localFile));
                        //(player.getDuration());
                        holder.mediaPlayer.prepare();
                        holder.mediaPlayer.start();
                        Log.e("dfjkdfs","duration is "+(holder.mediaPlayer.getDuration()));
                        context.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if(holder.mediaPlayer != null){
                                    int mCurrentPosition = holder.mediaPlayer.getCurrentPosition() / 1000;
                                    holder.ProgressBarMy.setProgress(mCurrentPosition);
                                }
                                mHandler.postDelayed(this, 1000);
                            }
                        });

                        holder.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                holder.mediaPlayer.reset();
                                holder.ProgressBarMy.setProgress(20);
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    try {
                        Log.e("sdfskfksf","file name is "+messages.getSourceLocation());
                        mAudioStorage=mAudioStorage.child("AudioChatMessages").child(messages.getSourceLocation()+".3gp");
                        final File localFile  = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS), messages.getSourceLocation()+".3gp");
                        localFile .createNewFile();
                        mAudioStorage.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {

                                Log.e("fjskfsdf","file get from "+messages.getFrom()+" to "+messages.getTo());

                                final Map messageMap=new HashMap();
                                messageMap.put("is_downloaded",true);
                                mFireStoreDatabase.collection("messages").document(messages.getFrom()).collection(messages.getTo()).document(messages.getMessageId())
                                        .set(messageMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){
                                            try {
                                                Log.e("fjskfsdf","bolean value changed");
                                                holder.mediaPlayer.reset();
                                                holder.ProgressBarMy.setMax(20);
                                                holder.mediaPlayer.setDataSource(String.valueOf(localFile));
                                                //(player.getDuration());
                                                holder.mediaPlayer.prepare();
                                                holder.mediaPlayer.start();
                                                //Log.e("dfjkdfs","duration is "+(player.getDuration()));
                                                context.runOnUiThread(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        if(holder.mediaPlayer != null){
                                                            int mCurrentPosition = holder.mediaPlayer.getCurrentPosition() / 1000;
                                                            holder.ProgressBarMy.setProgress(mCurrentPosition);
                                                        }
                                                        mHandler.postDelayed(this, 1000);
                                                    }
                                                });
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });

                            }
                        });

                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
            }
        });
    }
*/

}
