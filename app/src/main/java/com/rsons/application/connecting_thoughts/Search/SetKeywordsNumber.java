package com.rsons.application.connecting_thoughts.Search;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ankit on 3/5/2018.
 */

public class SetKeywordsNumber {


    public Context context;

    public SetKeywordsNumber(Context ctx){
        context=ctx;
    }


    public void setKeyWordNumber(long number){
        SharedPreferences.Editor editor = context.getSharedPreferences("Connecting_thoughts_search_suggestion", MODE_PRIVATE).edit();
        editor.putLong("keyword_number",number);
        editor.commit();
    }

    public long getKeyWordNumber() {
        SharedPreferences prefs = context.getSharedPreferences("Connecting_thoughts_search_suggestion", MODE_PRIVATE);
        return prefs.getLong("keyword_number", 0);

    }

}
