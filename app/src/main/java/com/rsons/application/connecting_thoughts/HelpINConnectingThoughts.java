package com.rsons.application.connecting_thoughts;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ankit on 4/9/2018.
 */

public class HelpINConnectingThoughts extends AppCompatActivity {

    private TextView howToWriteAndPublish;
    private TextView howToSendRequest;
    private TextView howToAcceptRequest;
    private TextView howToSendFirstMessage;
    private TextView howToStartConversation;
    private TextView WantToSuggest;
    private Toolbar HelpToolbar;
    private TextView CT_promo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
        findViews();

        CT_promo= (TextView) findViewById(R.id.connecting_thoughts_promo);
        HelpToolbar = (Toolbar) findViewById(R.id.help_toolbar);
        setSupportActionBar(HelpToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.HELP));

        CT_promo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Const.GetLanguageFromSharedPref(HelpINConnectingThoughts.this).equals("hindi")){
                    OpenYouTubeVideoUrl("https://www.youtube.com/watch?v=34jtUDu_SMI");
                }else {
                    OpenYouTubeVideoUrl("https://www.youtube.com/watch?v=O1yokDq4mFk");
                }
            }
        });

        howToWriteAndPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenYouTubeVideoUrl("https://youtu.be/14lfbv-O3pM");
            }
        });

        howToSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenYouTubeVideoUrl("https://youtu.be/koCVt9j86KQ");
            }
        });
        howToAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenYouTubeVideoUrl("https://youtu.be/73xsJNZVSqk");
            }
        });

        howToSendFirstMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenYouTubeVideoUrl("https://youtu.be/SxP7kiVlOE4");
            }
        });
        howToStartConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenYouTubeVideoUrl("https://youtu.be/Bsd5JVlJBE8");
            }
        });

        WantToSuggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMailToUs();
            }
        });

    }


    public void OpenYouTubeVideoUrl(String HelpVideoUrl){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(HelpVideoUrl));
        intent.setPackage("com.google.android.youtube");
        startActivity(intent);
    }


    private void findViews() {
        howToWriteAndPublish = (TextView)findViewById( R.id.how_to_write_and_publish );
        howToSendRequest = (TextView)findViewById( R.id.how_to_send_request );
        howToAcceptRequest = (TextView)findViewById( R.id.how_to_accept_request );
        howToSendFirstMessage = (TextView)findViewById( R.id.how_to_send_first_message );
        howToStartConversation = (TextView)findViewById( R.id.how_to_start_conversation );
        WantToSuggest= (TextView) findViewById(R.id.other_problems_and_suggestion);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if( android.R.id.home==item.getItemId()){
            onBackPressed();
        }
        return true;
    }

    private void sendMailToUs(){
        try{
            Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "connectingthoughtsofficial@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.I_WANT_TO_SUGGEST_SOMETHING));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.MY_SUGGESTIONS));
            startActivity(intent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(HelpINConnectingThoughts.this,getString(R.string.PLEASE_INSTALL_GMAIL_TO_SEND),Toast.LENGTH_SHORT).show();
        }
    }
}
