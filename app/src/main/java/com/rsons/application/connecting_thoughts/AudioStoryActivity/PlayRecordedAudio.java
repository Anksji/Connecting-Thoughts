package com.rsons.application.connecting_thoughts.AudioStoryActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rsons.application.connecting_thoughts.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Brother's on 5/15/2016.
 */
public class PlayRecordedAudio extends Activity implements View.OnClickListener{


    public static ArrayList songList;
    final ArrayList songListData=new ArrayList<>();
    public static List<String> myList = new ArrayList<String>();
    Button stop,resume;
    private MediaPlayer mp =null;
    PlayAudioAdapter adapter;
    Boolean itemSelected=false,longClicked=false,deleteSelect=false;
    private static String[] files = null;
    //private static ListView listView;
    //private static View deleteView;
    private static int Position=-1;
    private static long i=-1;
    boolean songCompleted=false;

    private static int pause,play;

    SeekBar seek_bar;
    private  static boolean playOn=true;
    Button  pause_button,backward,farward;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    // MediaPlayer player=null;
    ImageView play_button;
    private static TextView PlayingTitleOfStory,tx1,tx2;
    Handler seekHandler = new Handler();
    private static String mediaPath,path;
    private static long finalTime;
     private static long presentTime;
    private static boolean playing=false;
    float seekPosition=0,fileLength,result;
    // DecimalFormat f = new DecimalFormat("##.00");
    //private StartAppAd startAppAd = new StartAppAd(this);
    RecyclerView mRecyclerView;
    int currentSelectedPositionRecyclerView,PreviousSelectedPositionRecyclerView;
    private FloatingActionButton AddNewAudiostory;


    public static final String MEDIA_PATH = new String(Environment.getExternalStorageDirectory().getAbsolutePath() +"/Connecting_Thoughts/");
    // public static final String MEDIA_PATH = new String( Environment.getExternalStorageDirectory() + File.separator + "/TollCulator/");

    ImageView MoreOptionsIcon,BackPressBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_recorded_audio);

       // StartAppSDK.init(this, "104130031", "204644773", true);

        // pause = (Button) findViewById(R.id.pause);
        // pause.setOnClickListener(this);
        farward = (Button) findViewById(R.id.farward);
        farward.setOnClickListener(this);
        backward = (Button) findViewById(R.id.backward);
        backward.setOnClickListener(this);
        /*delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(this);*/

        seek_bar = (SeekBar) findViewById(R.id.seek_bar);
        play_button = (ImageView) findViewById(R.id.play);
        PlayingTitleOfStory = (TextView) findViewById(R.id.playing_title_of_story);
        tx1= (TextView) findViewById(R.id.textView);
        tx2= (TextView) findViewById(R.id.textView2);
        MoreOptionsIcon= (ImageView) findViewById(R.id.more_options_publish);
        BackPressBtn= (ImageView) findViewById(R.id.back_press);
        AddNewAudiostory= (FloatingActionButton) findViewById(R.id.add_new_story);
        play_button.setOnClickListener(this);

        tx1.setText("00 : 00");
        tx2.setText("00 : 00");
        play= R.drawable.play;
        pause= R.drawable.pause;


        //listView = (ListView) findViewById(R.id.list);
        path=Environment.getExternalStorageDirectory() + "/Connecting_Thoughts/";
        Log.e("exception","Inside play recpre path is "+path);
        //path= Environment.getExternalStorageDirectory() + File.separator + "/TollCulator/";
        files = new File(Environment.getExternalStorageDirectory() + "/Connecting_Thoughts/").list();
        //files = new File(Environment.getExternalStorageDirectory() + File.separator + "/TollCulator/").list();
        files=sortAscending();

        adapter = new PlayAudioAdapter(PlayRecordedAudio.this,files);

        mRecyclerView = (RecyclerView) findViewById(R.id.recorded_files);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);

        //adapter = new CustomAdapter(PlayRecordedAudio.this,files);
        //listView.setAdapter(new CustomAdapter(PlayRecordedAudio.this,files));
        //adapter.notifyDataSetChanged();

        mp=new MediaPlayer();

        MoreOptionsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivityMoreOptionPopup(v);
            }
        });

        BackPressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        AddNewAudiostory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("sdProgress","this is current "+seekBar.getProgress()+" this is presenttime "+presentTime);
                presentTime=seekBar.getProgress();
                seek_bar.setProgress((int)presentTime);
                mp.seekTo((int)presentTime);
            }
        });


    }

    private void showActivityMoreOptionPopup(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.audio_story_publish_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemClickListener());
        popup.show();
    }

    class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;
        public MenuItemClickListener() {
            //this.position=positon;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.publish_story:
                    Toast.makeText(PlayRecordedAudio.this,"Not working right now",Toast.LENGTH_SHORT).show();
                    return true;

                default:
            }
            return false;
        }
    }

    @Override    public void onBackPressed() {
        //startAppAd.onBackPressed();
        //showInterstitial();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if(deleteSelect){

        }else {
            seekHandler.removeCallbacks(run);
            if(mp!=null){
                mp.release();
            }

            mp = null;
        }
        super.onDestroy();
    }



    Runnable run = new Runnable() {

        @Override
        public void run() {
            if(longClicked  ||deleteSelect){

            }
            else {
                seekUpdation();
            }
        }
    };

    public void seekUpdation() {
        presentTime = mp.getCurrentPosition();

        if(playing) {

            tx1.setText(String.format("%d : %d ",

                    TimeUnit.MILLISECONDS.toMinutes((long) presentTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) presentTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) presentTime))));
        }


        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                play_button.setImageResource(play);
                songCompleted = true;
               // Log.i("Completion_Listener", "array size "+fileArray.size()+" and current position is "+CurrentselectedPosition);
                if (fileArray.size()>CurrentselectedPosition+1){
                    PreviousPositionClicked=CurrentselectedPosition;
                    CurrentselectedPosition++;
                    PlayMediaFile(CurrentselectedPosition,ViewToPlayAudio);
                    adapter.notifyItemChanged(CurrentselectedPosition);
                    adapter.notifyItemChanged(PreviousPositionClicked);
                }else {
                    Toast.makeText(PlayRecordedAudio.this, "Story Completed", Toast.LENGTH_SHORT).show();
                }

                //Toast.makeText(PlayRecordedAudio.this, "Media Completed", Toast.LENGTH_SHORT).show();
            }
        });
        if(songCompleted){

        }else{
            seek_bar.setProgress((int) presentTime);
            seekHandler.postDelayed(run, 100);

        }
        Log.i("time", "revolveing loop");




    }
    private void setPositionView(int pos, View v){
        Position=pos;
    }

    private String [] sortAscending () {
        List<String> sortedList = Arrays.asList(files);
        Collections.sort(sortedList);

        return (String[]) sortedList.toArray();
    }



    private void playSong(String songPath) {
        playing=true;
        songCompleted=false;
        // play_button.setImageResource(R.drawable.pause);
        //Log.d("button","button colour changed");
        try {

            mp.reset();
            mp.setDataSource(songPath);
            mp.prepare();
            playOn=true;
            if (playOn){
                playOn=false;
                play_button.setImageResource(pause);

                mp.start();
                Log.d("button", "button colour changed");
            }

            seek_bar.setMax(mp.getDuration());
            finalTime=mp.getDuration();
            presentTime=mp.getCurrentPosition();
            setTime();

            seek_bar.setProgress((int) presentTime);
            seekHandler.postDelayed(run, 100);


        } catch (IOException e) {
            Log.v(getString(R.string.app_name), e.getMessage());
        }
    }
    public static void setTime(){
        tx2.setText(String.format("%d : %d ",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));

        tx1.setText(String.format("%d : %d ",
                TimeUnit.MILLISECONDS.toMinutes((long) presentTime),
                TimeUnit.MILLISECONDS.toSeconds((long) presentTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) presentTime))));

    }

    View ViewToPlayAudio;
    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.farward:
                if(itemSelected && mp!=null){
                    int temp = (int)presentTime;

                    if (fileArray.size()>CurrentselectedPosition+1){
                        PreviousPositionClicked=CurrentselectedPosition;
                        CurrentselectedPosition++;
                        PlayMediaFile(CurrentselectedPosition,v);
                        adapter.notifyItemChanged(CurrentselectedPosition);
                        adapter.notifyItemChanged(PreviousPositionClicked);
                    }
                    /*if((temp+forwardTime)<=finalTime){
                        presentTime = presentTime + forwardTime;
                        mp.seekTo((int) presentTime);
                    }*/
                }
                else{
                    Toast.makeText(this,"This time Not playing any file",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.play:

                if(itemSelected){
                    if(deleteSelect || songCompleted){
                        mp=new MediaPlayer();
                        //Object ob=listView.getItemAtPosition(Position);
                        //playSong(MEDIA_PATH + ob);
                        deleteSelect=false;
                    }
                    if (playOn){
                        PreviousPositionClicked=PreviousSelectedPositionRecyclerView;
                        CurrentselectedPosition =currentSelectedPositionRecyclerView;
                        adapter.notifyItemChanged(CurrentselectedPosition);
                        playOn=false;
                        play_button.setImageResource(pause);

                        mp.start();
                    }else{
                        PreviousSelectedPositionRecyclerView=PreviousPositionClicked;
                        PreviousPositionClicked=-1;
                        currentSelectedPositionRecyclerView= CurrentselectedPosition;
                        CurrentselectedPosition =-1;
                        adapter.notifyItemChanged(currentSelectedPositionRecyclerView);
                        playOn=true;
                        play_button.setImageResource(play);

                        mp.pause();
                    }

                }
                else{
                    Toast.makeText(this,"This time Not playing any file",Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.backward:

                if(itemSelected&& mp!=null) {
                    int temp = (int) presentTime;

                    /*if ((temp - backwardTime) > 0) {
                        presentTime = presentTime - backwardTime;
                        mp.seekTo((int) presentTime);
                    }*/
                    Log.e("sdffss","current positon is "+CurrentselectedPosition);
                    if (CurrentselectedPosition>0){
                        PreviousPositionClicked=CurrentselectedPosition;
                        CurrentselectedPosition--;
                        PlayMediaFile(CurrentselectedPosition,v);
                        adapter.notifyItemChanged(CurrentselectedPosition);
                        adapter.notifyItemChanged(PreviousPositionClicked);
                    }else {
                        Toast.makeText(PlayRecordedAudio.this, "No other story to play", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this,"This time Not playing any file",Toast.LENGTH_LONG).show();
                }
                break;

            case DEFAULT_KEYS_DIALER:
                Toast.makeText(this,"This is Inactive button",Toast.LENGTH_LONG).show();
                break;

        }
    }

    public void PlayMediaFile(int position, View view){
        ViewToPlayAudio=view;
        if (mp != null) {
            mp.release();
            mp = null;
        }
        mp = new MediaPlayer();
        itemSelected=true;
        Object listItem = fileArray.get(position);
        setPositionView(position, view);
        playSong(MEDIA_PATH + listItem);

        String s;
        s=""+listItem;
        PlayingTitleOfStory.setText(s.replace(".wav",""));

        mediaPath = MEDIA_PATH + listItem;
    }
    int PreviousPositionClicked=-1;int CurrentselectedPosition =-1;List<String> fileArray;
    public class PlayAudioAdapter extends RecyclerView.Adapter<PlayAudioAdapter.ViewHolder>{

        Activity context;



        public PlayAudioAdapter(Activity con , String[] fileList){
            fileArray =new LinkedList<String> (Arrays.asList(fileList));
            context=con;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_audio_book_iteam, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.StoryTitle_name.setText(fileArray.get(position));

            if(CurrentselectedPosition ==position){
               // holder.itemView.setBackgroundColor(Color.parseColor("#000000"));

                holder.BookIconImage.setImageResource(R.drawable.ic_audio_book_pause_button);
            }
            else{
                //holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
                holder.BookIconImage.setImageResource(R.drawable.ic_play_book_audio);
            }


            holder.StoryTitle_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CurrentselectedPosition =position;
                    if(PreviousPositionClicked != -1){
                        Log.e("fdfsfs","inside if previous clicked position "+PreviousPositionClicked);
                        notifyItemChanged(PreviousPositionClicked);

                    }
                    notifyItemChanged(position);
                    PreviousPositionClicked=position;
                    PlayMediaFile(position,view);

                }
            });

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(holder.deleteButton,position);

                }
            });


        }



        private void showPopupMenu(View view,int position) {
            // inflate menu
            PopupMenu popup = new PopupMenu(view.getContext(),view );
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.audio_item_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
            popup.show();
        }

        class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

            private int position;
            public MyMenuItemClickListener(int positon) {
                this.position=positon;
            }

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.rename_story:
                        RenameAudio(position);
                        return true;
                    case R.id.delete_stry:
                        File from = new File(path + fileArray.get(position));
                        from.delete();
                        fileArray.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                        return true;

                    default:
                }
                return false;
            }
        }


        @Override
        public int getItemCount() {
            return fileArray.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder {
            public TextView StoryTitle_name;
            public ImageView deleteButton;
            public ImageView BookIconImage;



            public ViewHolder(View itemView) {
                super(itemView);
                BookIconImage= (ImageView) itemView.findViewById(R.id.book_icon);
                StoryTitle_name = (TextView) itemView.findViewById(R.id.book_title);
                deleteButton=(ImageView)itemView.findViewById(R.id.more_options);
                //AudioPauseBookIcon= (ImageView)itemView.findViewById(R.id.book_icon_pause);
            }
        }
    }

    private void RenameAudio(final int position) {

        AlertDialog.Builder alert = new AlertDialog.Builder(PlayRecordedAudio.this);
        alert.setTitle("Rename");

        final EditText input = new EditText(PlayRecordedAudio.this);
        alert.setView(input);
        alert.setCancelable(false);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                //update your listview here
                // final String ext =   listView.substring(listView.indexOf(".") + 1);
                Object listItem = fileArray.get(position);

                File from = new File(path + listItem);

                String srt1 = input.getEditableText().toString();


                File changedTo = new File(path + srt1 + "." + "wav");


                boolean success = from.renameTo(changedTo);
                if (success) {
                    Toast.makeText(PlayRecordedAudio.this, "File is Renamed", Toast.LENGTH_SHORT).show();
                    //adapter.notifyDataSetChanged();
                    fileArray.set(position,srt1+ "." + "wav");
                    //sortAscending();
                    playing = false;
                    itemSelected = false;
                    adapter.notifyDataSetChanged();
                    /*Intent intent = getIntent();
                    finish();
                    startActivity(intent);*/
                }


            }


        });

        alert.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();

                        playing = false;
                        itemSelected = false;
                    }

                });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }


}
