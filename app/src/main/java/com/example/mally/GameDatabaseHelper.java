package com.example.mally;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mally_game.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "solitaire_scores";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_TIME = "time";

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_SCORE + " INTEGER, " +
                COLUMN_TIME + " INTEGER)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void saveSolitaireScore(String username, int score, long time) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT MAX(" + COLUMN_SCORE + ") FROM " + TABLE_NAME, null);
        int bestScore = 0;
        if(c.moveToFirst()) bestScore = c.getInt(0);
        c.close();

        if(score > bestScore){
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_SCORE, score);
            values.put(COLUMN_TIME, time);

            db.delete(TABLE_NAME,null,null);
            db.insert(TABLE_NAME,null,values);
        }
        db.close();
    }

    public int getBestSolitaireScore() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT MAX(" + COLUMN_SCORE + ") FROM " + TABLE_NAME,null);
        int score=0;
        if(c.moveToFirst()) score=c.getInt(0);
        c.close();
        db.close();
        return score;
    }

    public String getBestSolitairePlayer(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT "+COLUMN_USERNAME+" FROM "+TABLE_NAME+" LIMIT 1",null);
        String player = "—";
        if(c.moveToFirst()) player=c.getString(0);
        c.close();
        db.close();
        return player;
    }

    public long getBestTime(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT "+COLUMN_TIME+" FROM "+TABLE_NAME+" LIMIT 1",null);
        long time=0;
        if(c.moveToFirst()) time=c.getLong(0);
        c.close();
        db.close();
        return time;
    }
}
