package com.rsons.application.connecting_thoughts.Search;


import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Brother on 4/1/2017.
 */
public class SearchProvider extends ContentProvider {

    SearchDbHepler MyDB;
     public static ArrayList<String> SuggetionWord;
    @Override
    public boolean onCreate() {
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Log.d("Search","We are inside provider query");
        MyDB=new SearchDbHepler(getContext());
        String word;
        Log.d("Searchkfjskd","We are inside provider query "+uri.getLastPathSegment().toString());
        String suggetionQuery=uri.getLastPathSegment().toString();
        SuggetionWord=MyDB.getSearchedWord(suggetionQuery);

        MatrixCursor matrixCursor =new MatrixCursor(new String []{
                BaseColumns._ID,SearchManager.SUGGEST_COLUMN_TEXT_1,SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID});

        //int limit=Integer.parseInt(uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT));
        int SuggestionLimit=20;
        int length= SuggetionWord.size();


        for (int i=0;i<length&&matrixCursor.getCount()<SuggestionLimit;i++){
            String Sword=SuggetionWord.get(i);
            matrixCursor.addRow(new Object[]{i, Sword,i});
            //mySuggestion.MySuggetionWord.add(Sword);

        }
        return matrixCursor;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
