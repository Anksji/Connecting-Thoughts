package com.rsons.application.connecting_thoughts.AboutSection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.rsons.application.connecting_thoughts.R;

/**
 * Created by ankit on 4/9/2018.
 */

public class AboutScreen extends AppCompatActivity {

    private TextView Privacy_policy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_list);

        Privacy_policy= (TextView) findViewById(R.id.privacy_policy);

        Privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


}
