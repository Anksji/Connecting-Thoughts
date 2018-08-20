package com.rsons.application.connecting_thoughts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by ankit on 3/19/2018.
 */

public class ChooseLanguage extends AppCompatActivity {

    private Spinner LanguageSpinner;
    private TextView languageText;
    private FirebaseAuth mAuth;
    private RelativeLayout SelectLanguageLayout;
    private RelativeLayout SaveLanguageBtn;
    private View SpinnerBottom;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_language);
        mAuth = FirebaseAuth.getInstance();
        languageText= (TextView) findViewById(R.id.language_spinner_text);
        LanguageSpinner= (Spinner) findViewById(R.id.language_spinner);
        SelectLanguageLayout= (RelativeLayout) findViewById(R.id.select_language);
        SaveLanguageBtn= (RelativeLayout) findViewById(R.id.save_language_btn);
        SpinnerBottom= (View) findViewById(R.id.bottom_til_view);

        ArrayAdapter<CharSequence> LanguageAdapter = new ArrayAdapter<CharSequence>(ChooseLanguage.this,
                R.layout.language_spinner_text_view, Const.languages);
        LanguageAdapter.setDropDownViewResource(R.layout.language_spinner_drop_down);
        LanguageSpinner.setAdapter(LanguageAdapter);

        SelectLanguageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpinnerBottom.setVisibility(View.VISIBLE);
                languageText.setVisibility(View.GONE);
                LanguageSpinner.setVisibility(View.VISIBLE);
                LanguageSpinner.performClick();
            }
        });


        SaveLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LanguageSpinner.getVisibility()==View.VISIBLE){
                    Const.setLanguage(Const.languageId[LanguageSpinner.getSelectedItemPosition()],ChooseLanguage.this);
                    Const.CT_setLocale(ChooseLanguage.this,Const.languageISO[LanguageSpinner.getSelectedItemPosition()]);
                    Const.CURRENT_LANGUAGE = Const.GetLanguageFromSharedPref(ChooseLanguage.this);
                    LaunchActivity();
                }

            }
        });

        /*LanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(ChooseLanguage.this,getString(R.string.PLEASE_CHOOSE_LANGUGE),Toast.LENGTH_SHORT).show();
            }

        });*/


    }

    public void LaunchActivity(){

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser==null){
            Intent LaunchLogin=new Intent(ChooseLanguage.this,LoginActivity.class);
            startActivity(LaunchLogin);
            finish();
        }else {
            Intent LaunchChooseLanguage=new Intent(ChooseLanguage.this,MainActivity.class);
            startActivity(LaunchChooseLanguage);
            finish();
        }
    }

}
