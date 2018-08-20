package com.rsons.application.connecting_thoughts;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.LinkedHashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ankit on 9/15/2017.
 */

public class Const {


    public static int LOAD_DATA_AT_ONCE=10;
    public static String READ_TEXT_SIZE="Read_size";
    public static String DAY_NIGHT_VISION="Day_night_vision";
    public static String IS_OPEN_FIRST_TIME="is_open_first_time";
    public static int SUGGESTION_LIST_BOX=200;

    public static boolean IS_DIRECT_GO_BACK_FROM_STORY=false, IS_DIRECT_GO_BACK_FROM_CHAT=false,IS_DIRECT_GO_BACK_FROM_FOLLOWER=false;
    public static final int REQUEST_CODE=1;

    public static boolean IS_MESSAGE_SHARE_COMPLETE=true;
    public static final int NAV_GNR_MENU_POS=0;
    public static final int TRENDING_STORY_FREQUENCY = 7;
    public static LinkedHashMap<String,String> GenresMap = new LinkedHashMap<String,String>();

    public static final int  LIBRARY =2,NEW_CONNECTION_REQUEST=1,HISTORY=3,SETTING=4,BECOME_PARTERNS=4,HELP=5,ABOUT_US=6,SHARE=7,RATE=8;
    public static String PrivateShairedPref="@C@ONN#E)TI$G%";
    public static String CURRENT_LANGUAGE;
    public static String TestLanguage="hindi";

    public static int currentLanguageIndex = -1;

    ///public static String[] language = {"Hindi", "English"};



    public static String[] languageISO = {"bn", "en", "gu",
            "hi", "kn", "ml",
            "mr", "or", "pa","sa",
            "ta", "te"};


    public static String[] languages = {"বাংলা", "English", "ગુજરાતી",
            "हिंदी", "ಕನ್ನಡ", "മലയാളം",
            "मराठी", "ନୀୟ", "ਪੰਜਾਬੀ","संस्कृत",
            "தமிழ்", "తెలుగు"};
    public static String[] languageId = {"bengali", "english", "gujarati",
            "hindi", "kannada", "malayalam",
            "marathi", "oriya", "punjabi","sanskrit",
            "tamil", "telugu"};

    public static String[] language = {"Bengali", "English", "Gujarati",
            "Hindi", "Kannada", "Malayalam",
            "Marathi", "Oriya", "Punjabi","Sanskrit",
            "Tamil", "Telugu"};


    public static final int PICK_FROM_GALLERY = 1;
    public static String USER_ID="";

    public static String [] ThoughtGenre={"Motivational","Struggle","Memories","Dream","Opinion","Patriotic","Your Day Story","Comedy","Love","Confession",
            "Poetry","Remarkable Creation"};
    public static String [] ThoughtGenreHindi={"प्रेरणात्मक","संघर्ष","पुरानी यादें","स्वप्न","विषय पर राय","देशभक्ति","आपके दिन की कहानी",
    "हास्य","प्यार","कॉन्फेशन","कविता","उल्लेखनीय रचना"};

    public static String [] ThoughtGenreId={"motivational","struggle","memories","dream","opinion","patriotic","your_day_story",
            "comedy","love","confession",
            "poetry","remarkable_creation"};



    public static String getFreshCapatibleStringToJson(String keyword){
        String first,Second;
        first=keyword.replaceAll("\\{"," ");
        Second=first.replaceAll("\\}"," ");
        first=Second.replaceAll("\\["," ");
        Second=first.replaceAll("\\]"," ");
        first=Second.replaceAll("\\("," ");
        Second=first.replaceAll("\\)"," ");
        first=Second.replaceAll("\\s","~");
        Second=first.replaceAll(",","`");
        return Second;
    }


    public static void setCurrentSearchListNumber(String list_number, Context context){
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PrivateShairedPref, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("current_search_list_number",list_number);
        editor.commit();
    }

    public static String GetCurrentSearchListNumber(Context context){
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PrivateShairedPref, MODE_PRIVATE);
        String current_listNUmber= pref.getString("current_search_list_number","1");
        return current_listNUmber;
    }


    public static void  SetDayNightMode(boolean isDay,Context context){
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PrivateShairedPref, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(DAY_NIGHT_VISION,isDay);
        editor.commit();
    }
    public static void SetIsFirstTime(boolean isFirstTime,Context context){
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PrivateShairedPref, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_OPEN_FIRST_TIME,isFirstTime);
        editor.commit();
    }
    public static boolean getIsFirstTime(Context context){
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PrivateShairedPref, MODE_PRIVATE);
        boolean first_Time= pref.getBoolean(IS_OPEN_FIRST_TIME,true);
        return first_Time;
    }

    public static boolean IsDayVision(Context context){
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PrivateShairedPref, MODE_PRIVATE);
        boolean IsDay= pref.getBoolean(DAY_NIGHT_VISION,true);
        return IsDay;
    }

    public static void SetReadTextSize(int Textsize,Context context){
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PrivateShairedPref, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(READ_TEXT_SIZE,Textsize);
        editor.commit();
    }

    public static int GetReadTextSize(Context context){
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PrivateShairedPref, MODE_PRIVATE);
        int text_size= pref.getInt(READ_TEXT_SIZE,16);
        return text_size;
    }

    public static void setLanguage(String language, Context context){
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PrivateShairedPref, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("language",language);
        editor.commit();
    }


    public static String GetLanguageFromSharedPref(Context context){
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PrivateShairedPref, MODE_PRIVATE);
        String language= pref.getString("language","default");
        return language;
    }

    public static void CT_setLocale(Context ctx, String languageISO){
        Configuration cfg = new Configuration();

        if (!languageISO.isEmpty()) {
            cfg.locale = new Locale(languageISO);
        } else {
            cfg.locale = Locale.getDefault();
        }

        Locale.setDefault(cfg.locale);
        ctx.getResources().updateConfiguration(cfg, null);
    }

}
