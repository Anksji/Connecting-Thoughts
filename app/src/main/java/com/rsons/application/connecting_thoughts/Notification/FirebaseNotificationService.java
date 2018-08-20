package com.rsons.application.connecting_thoughts.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rsons.application.connecting_thoughts.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ankit on 10/3/2017.
 */

public class FirebaseNotificationService extends FirebaseMessagingService {

    private FirebaseFirestore mFireStoreDatabase;
    private FirebaseAuth mAuth;
    public static int CHAT_NOTIFICATION_ID=1210199473;
    private static String NOTIFICATION_SPLIT_STRING="$!@%#";
    public static  String NOTIFICATION_SHARED_PREF_NAME="cONNECTING&*^(tHOUGHTS*(";
    private  String MyDefaultClickAction="com.rsons.application.connecting_thoughts_TARGET_CHAT_Friend_List_NOTIFICATION";
    private String CommentNotificationClickAction="com.rsons.application.connecting_thoughts_COMMENT_NOTIFICATION";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        /*String notificationTitle=remoteMessage.getNotification().getTitle();
        String notificationBody=remoteMessage.getNotification().getBody();
        String click_action=remoteMessage.getNotification().getClickAction();*/

        Log.e("sdfskljkfljdklsj","Notification come "+remoteMessage);

        String notificationTitle=remoteMessage.getData().get("title");
        String notificationBody=remoteMessage.getData().get("body");
        String notificationType=remoteMessage.getData().get("notification_type");

        mFireStoreDatabase=FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String click_action=remoteMessage.getData().get("click_action");

        if (notificationType.equals("friend_request")){
            String  getUserId=remoteMessage.getData().get("from_user_id");
            String UserName=remoteMessage.getData().get("user_name");

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this,"12739410sara")
                            .setSmallIcon(R.drawable.ct_icon_blue)
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationBody)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            mBuilder.setAutoCancel(true);

            Intent resultIntent = new Intent(click_action);
            resultIntent.putExtra("user_key",getUserId);
            resultIntent.putExtra("user_name",UserName);
            //resultIntent.putExtra("user_img",UserImg);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);


            int mNotificationId = (int) System.currentTimeMillis();
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(getString(R.string.app_name),mNotificationId, mBuilder.build());


        }else if (notificationType.equals("comment_notification")){

            String  getUserId=remoteMessage.getData().get("Commented_user_id");
            String storyId=remoteMessage.getData().get("Comment_story_id");

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this,"12739410sara")
                            .setSmallIcon(R.drawable.ct_icon_blue)
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationBody)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            mBuilder.setAutoCancel(true);
            Log.e("sdlkfjsklfj","this is story id in Notification page "+storyId);
            Intent resultIntent = new Intent(click_action);
            resultIntent.putExtra("user_key",getUserId);
            resultIntent.putExtra("story_Id",storyId);
            resultIntent.putExtra("from_first_page",false);
            //resultIntent.putExtra("user_img",UserImg);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);


            int mNotificationId = (int) System.currentTimeMillis();
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(getString(R.string.app_name),mNotificationId, mBuilder.build());


        }

        else if (notificationType.equals("story_publish")){




            mFireStoreDatabase.collection("NotificationData").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot=task.getResult();
                        if (documentSnapshot.exists()){
                            if (documentSnapshot.contains("notification_counter")){
                                SetNotificationCounter((int)documentSnapshot.getLong("notification_counter").longValue());
                            }else {
                                SetNotificationCounter(0);
                            }
                        }else {
                            SetNotificationCounter(0);
                        }
                    }else {
                        SetNotificationCounter(0);
                    }
                }
            });

            String  StoryName=remoteMessage.getData().get("storyName");
            String StoryAuthorName=remoteMessage.getData().get("story_author_Name");
            String  getStoryId=remoteMessage.getData().get("story_id");
            String StoryImageUrl=remoteMessage.getData().get("story_img_url");
            String StoryPublishedTime=remoteMessage.getData().get("story_Publish_time");
            final long timeInMillis = System.currentTimeMillis();
            Map loadData=new HashMap();
            loadData.put("notification_type","publish_story");
            loadData.put("notification_timestamp",timeInMillis);
            loadData.put("story_intro",notificationBody);
            loadData.put("story_Title",StoryName);
            loadData.put("story_author_name",StoryAuthorName);
            loadData.put("story_published_time",StoryPublishedTime);
            loadData.put("cover_image",StoryImageUrl);
           DocumentReference UploadData= mFireStoreDatabase.collection("NotificationData").document(mAuth.getUid()).collection("RequestAndStory")
                   .document(getStoryId);
            UploadData.set(loadData, SetOptions.merge())
            .addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("kldjfsklf","this is error "+e.getMessage());
                }
            });


            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this,"12739410sara")
                            .setSmallIcon(R.drawable.ct_icon_blue)
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationBody)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            mBuilder.setAutoCancel(true);

            Intent resultIntent = new Intent(click_action);
            resultIntent.putExtra("story_Id",getStoryId);
            resultIntent.putExtra("from_story_listing",true);
            //resultIntent.putExtra("user_img",UserImg);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);


            int mNotificationId = (int) System.currentTimeMillis();
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(getString(R.string.app_name),mNotificationId, mBuilder.build());

        }else if (notificationType.equals("friend_message")){

            String  getUserId=remoteMessage.getData().get("from_user_id");




            /*Notification.Builder mBuilder=new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ct_icon_blue)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationBody)
                    .setContentIntent(resultPendingIntent);
*/

            SharedPreferences preferences = getSharedPreferences(NOTIFICATION_SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String Messages = preferences.getString("messages_notification", "");
            int total_Message_count = preferences.getInt("total_messages",0);
            String previousUserId=preferences.getString("previous_user_notification_id","");
            boolean IsMoreThenTwoUserSending=preferences.getBoolean("Is_more_then_two_user_sending",false);

            if (!IsMoreThenTwoUserSending){
                if (previousUserId.length()>5){
                    if (previousUserId.equals(getUserId)){
                        IsMoreThenTwoUserSending=false;
                    }else {
                        IsMoreThenTwoUserSending=true;
                    }
                }
            }



            Messages+=notificationTitle+" : "+notificationBody+"\\|";
            total_Message_count++;

            SharedPreferences prefs = this.getSharedPreferences(NOTIFICATION_SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("messages_notification",Messages);
            editor.putInt("total_messages",total_Message_count);
            editor.putString("previous_user_notification_id",getUserId);
            editor.putBoolean("Is_more_then_two_user_sending",IsMoreThenTwoUserSending);
            editor.apply();



            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            List<String> messages = Arrays.asList(Messages.split("\\|"));
            //String[] messagesList = Messages.split("\\|");
            Log.e("jskfjsks","this is message list size "+messages.size());
            if (messages.size()<9){
                for (int i = messages.size() - 1; i >= 0; i--) {
                    inboxStyle.addLine(messages.get(i).substring(0,messages.get(i).length()-1));
                }
            }else {total_Message_count-=messages.size()-8;
                for (int i = messages.size() - 1; i >= total_Message_count; i--) {
                    inboxStyle.addLine(messages.get(i).substring(0,messages.get(i).length()-1));
                }

                inboxStyle.setSummaryText("+"+total_Message_count+" more");
            }
            Intent resultIntent;
            if (IsMoreThenTwoUserSending){
                resultIntent=new Intent(MyDefaultClickAction);
            }else {
                resultIntent= new Intent(click_action);
                resultIntent.putExtra("user_key",getUserId);
                resultIntent.putExtra("from_notification",true);
            }
/*
            Intent MaingIntent = new Intent(this, MainActivity.class);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
// Get the PendingIntent containing the entire back stack
          */

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this,"12739410sara");
            Notification notification;
            notification = mBuilder.setSmallIcon(R.drawable.ct_icon_blue).setTicker(getString(R.string.app_name))
                    .setWhen(0).setAutoCancel(true)
                    .setSound(alarmSound)
                    .setContentTitle(getString(R.string.app_name))
                            .setContentIntent(resultPendingIntent).
                            setSound(alarmSound).setStyle(inboxStyle)
                    .setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.ct_icon_blue)
                    .setContentText("Messages")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();

            /*mBuilder.setSound(alarmSound);
            mBuilder.setAutoCancel(true);*/

            /*Notification notification = new Notification.InboxStyle(mBuilder)

                    .addLine(notificationTitle+" : "+notificationBody)

                    .setBigContentTitle(getString(R.string.app_name))
                    .setSummaryText("+"+totalMessages+" more")
                    .build();*/




            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(CHAT_NOTIFICATION_ID,notification);
            /*
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(121,notification);
*/

           /* NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ct_icon_blue)
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationBody);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            mBuilder.setAutoCancel(true);

            Intent resultIntent = new Intent(click_action);
            resultIntent.putExtra("user_key",getUserId);
            //resultIntent.putExtra("user_img",UserImg);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);



            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(getString(R.string.app_name),CHAT_NOTIFICATION_ID, mBuilder.build());
*/








        }

    }


    public void SetNotificationCounter(int Counter){
        Map loadData=new HashMap();
        loadData.put("notification_counter",++Counter);
        mFireStoreDatabase.collection("NotificationData").document(mAuth.getUid()).set(loadData,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    return;
                }
            }
        });
    }


}
