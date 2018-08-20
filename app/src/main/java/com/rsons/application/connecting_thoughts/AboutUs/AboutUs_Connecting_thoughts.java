package com.rsons.application.connecting_thoughts.AboutUs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rsons.application.connecting_thoughts.HelpINConnectingThoughts;
import com.rsons.application.connecting_thoughts.MainActivity;
import com.rsons.application.connecting_thoughts.R;

/**
 * Created by ankit on 4/14/2018.
 */

public class AboutUs_Connecting_thoughts extends AppCompatActivity {


    private TextView TermsAndCondition;
    private TextView PrivacyPolicy;
    private TextView AboutconnectingThoughts;
    private Toolbar AboutUsToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about_us_list);

        AboutUsToolbar = (Toolbar) findViewById(R.id.about_toolbar);
        setSupportActionBar(AboutUsToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.ABOUT_US));
        TermsAndCondition= (TextView) findViewById(R.id.terms_and_condition);
        PrivacyPolicy = (TextView) findViewById(R.id.privacy_policy);


        TermsAndCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LaunchAbout=new Intent(AboutUs_Connecting_thoughts.this, About_Us_Info_Text.class);
                LaunchAbout.putExtra("load_string",0);
                startActivity(LaunchAbout);
            }
        });

        PrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LaunchAbout=new Intent(AboutUs_Connecting_thoughts.this, About_Us_Info_Text.class);
                LaunchAbout.putExtra("load_string",1);
                startActivity(LaunchAbout);
            }
        });

        /*AboutconnectingThoughts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LaunchAbout=new Intent(AboutUs_Connecting_thoughts.this, About_Us_Info_Text.class);
                LaunchAbout.putExtra("load_string",2);
                startActivity(LaunchAbout);
            }
        });*/


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if( android.R.id.home==item.getItemId()){
            onBackPressed();
        }
        return true;
    }
}
