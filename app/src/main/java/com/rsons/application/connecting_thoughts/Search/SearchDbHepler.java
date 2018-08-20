package com.rsons.application.connecting_thoughts.Search;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ankit on 3/5/2018.
 */

public class SearchDbHepler extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="Searching.db";
    private static final String TABLE_SEARCH_KEYWORDS ="Searching_suggestion";
    private static final String KEY_ID="id";
    private static final String SEARCH_KEYWORD="Search_keywords";
    private static final String SEARCH_KEY_ID="Search_keyId";


    private static final String CREATE_TABLE="CREATE TABLE " + TABLE_SEARCH_KEYWORDS + " (" +KEY_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            SEARCH_KEY_ID + " TEXT, "+SEARCH_KEYWORD+" TEXT);";



    private Context context;
    private SQLiteDatabase dbase;


    public SearchDbHepler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        dbase = getWritableDatabase();
    }

        @Override
        public void onCreate(SQLiteDatabase db){
            dbase=db;
            db.execSQL(CREATE_TABLE);

        }

        public void  DeleteallSuggestionList(){
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from "+ TABLE_SEARCH_KEYWORDS);
        }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_KEYWORDS);
        onCreate(db);
    }

    public void AddSearchKeyWords(String searchkeyId,String search_keyword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SEARCH_KEYWORD,search_keyword);
        values.put(SEARCH_KEY_ID,searchkeyId);
        db.insert(TABLE_SEARCH_KEYWORDS, null, values);
    }

    public boolean IsKeyWordExist(String search_keyword){
        SQLiteDatabase db = getWritableDatabase();

        String [] columns={KEY_ID};
        Cursor cursor;

        cursor = db.query(TABLE_SEARCH_KEYWORDS, columns, SEARCH_KEYWORD+" = ?", new String[]{search_keyword }, null, null, null, null);
        boolean isPresent = false;
        if(cursor.moveToFirst()){
            isPresent = true;
        }
        cursor.close();
        db.close();
        return isPresent;
    }

    public int UpdateSearchKeyValue(String searchKeyId,String newSearchKey){
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(SEARCH_KEYWORD,""+newSearchKey);
        String [] whereArgs={searchKeyId};
        int count= database.update(TABLE_SEARCH_KEYWORDS,contentValues,SEARCH_KEY_ID+" = ?",whereArgs);

        return count;
    }

    public boolean IsKeyIdExist(String searchKeyId){
        SQLiteDatabase db = getWritableDatabase();

        String [] columns={KEY_ID};
        Cursor cursor;
        cursor = db.query(TABLE_SEARCH_KEYWORDS, columns, SEARCH_KEY_ID+" = ?", new String[]{searchKeyId}, null, null, null, null);
        boolean isPresent = false;
        if(cursor.moveToFirst()){
            isPresent = true;
        }
        cursor.close();
        db.close();
        return isPresent;
    }



    public ArrayList<String> GetKeyWords(){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> sample=new ArrayList<>();
        String [] columns={SEARCH_KEYWORD};
        Cursor cursor;
        cursor = db.query(TABLE_SEARCH_KEYWORDS, columns, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                sample.add(cursor.getString(0));

            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return sample;
    }

    public long getNoOfKeyWords_In_Search_Queue(){
        SQLiteDatabase db = getWritableDatabase();
        String [] columns={KEY_ID};
        Cursor cursor;
        cursor = db.query(TABLE_SEARCH_KEYWORDS, columns, null, null, null, null, null, null);
        long noOfRows = 0;
        if(cursor.moveToFirst()){
            do{
                noOfRows++;
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return noOfRows;
    }


    public ArrayList<String> getSearchedWord(String selectionArgs){

        Log.d("Searchkdfsk","this is getSearchedWord "+selectionArgs);
        String selection = SEARCH_KEYWORD + " like ? ";
        ArrayList<String> WordList = new ArrayList<String>();
        String [] columns={KEY_ID,SEARCH_KEYWORD};


        selectionArgs = "%"+selectionArgs + "%";


        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        //String sqlTables = "word";
        qb.setTables(TABLE_SEARCH_KEYWORDS);
        Cursor cursor;

        try {

            Log.d("Search","We are in try block");
            cursor = db.query(TABLE_SEARCH_KEYWORDS, columns, selection,new String[] {selectionArgs} , null, null, SEARCH_KEYWORD + " ASC ", "10");

            if(cursor.moveToFirst()){
                Log.d("Search", "we are inside if statement cursor.movetofirst");
                do {
                    String wordn=cursor.getString(1);
                    WordList.add(wordn);

                }while (cursor.moveToNext());

            }
            else {
                Log.d("Search","No Search value is found");
            }
        }catch (Throwable e){
            e.printStackTrace();
            Log.d("Search", "we are inside catch block "+e);
        }
        return WordList;
    }


}
