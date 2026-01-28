package com.example.mally;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mally_game.db";
    private static final int DATABASE_VERSION = 3; // ⚠️ Passage à la version 3 pour créer la table Sudoku

    // Tables
    private static final String TABLE_SOLITAIRE = "table_solitaire";
    private static final String TABLE_HANGMAN = "table_hangman";
    private static final String TABLE_SUDOKU = "table_sudoku"; // Nouvelle table

    // Colonnes communes
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_TIME = "time"; // Uniquement pour solitaire

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table Solitaire
        String CREATE_SOLITAIRE = "CREATE TABLE " + TABLE_SOLITAIRE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_SCORE + " INTEGER,"
                + COLUMN_TIME + " INTEGER" + ")";
        db.execSQL(CREATE_SOLITAIRE);

        // Table Hangman
        String CREATE_HANGMAN = "CREATE TABLE " + TABLE_HANGMAN + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_SCORE + " INTEGER" + ")";
        db.execSQL(CREATE_HANGMAN);

        // Table Sudoku (Nouveau)
        String CREATE_SUDOKU = "CREATE TABLE " + TABLE_SUDOKU + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_SCORE + " INTEGER" + ")";
        db.execSQL(CREATE_SUDOKU);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOLITAIRE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HANGMAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUDOKU);
        onCreate(db);
    }

    // --- SOLITAIRE ---
    public void saveSolitaireScore(String name, int score, long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SOLITAIRE, null, null);
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, name);
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_TIME, time);
        db.insert(TABLE_SOLITAIRE, null, values);
        // Ajoute cette ligne :
        uploadScore(TABLE_SOLITAIRE, name, score);
    }
    public int getBestSolitaireScore() { return getIntData(TABLE_SOLITAIRE, COLUMN_SCORE); }
    public String getBestSolitairePlayer(){ return getStringData(TABLE_SOLITAIRE, COLUMN_USERNAME); }

    // --- HANGMAN ---
    public void saveHangmanScore(String name, int score) {
        saveHighScore(TABLE_HANGMAN, name, score);
    }
    public int getBestHangmanScore() { return getIntData(TABLE_HANGMAN, COLUMN_SCORE); }
    public String getBestHangmanPlayer() { return getStringData(TABLE_HANGMAN, COLUMN_USERNAME); }

    // --- SUDOKU (Nouveau) ---
    public void saveSudokuScore(String name, int score) {
        saveHighScore(TABLE_SUDOKU, name, score);
    }
    public int getBestSudokuScore() { return getIntData(TABLE_SUDOKU, COLUMN_SCORE); }
    public String getBestSudokuPlayer() { return getStringData(TABLE_SUDOKU, COLUMN_USERNAME); }

    // --- Helpers ---

    // Méthode générique pour sauvegarder si le score est meilleur
    private void saveHighScore(String tableName, String name, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        int currentBest = getIntData(tableName, COLUMN_SCORE);

        if (score > currentBest) {
            db.delete(tableName, null, null);
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, name);
            values.put(COLUMN_SCORE, score);
            db.insert(tableName, null, values);
            uploadScore(tableName, name, score);
        }
    }

    private int getIntData(String tableName, String column) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(" + column + ") FROM " + tableName, null);
        int result = 0;
        if (cursor.moveToFirst()) result = cursor.getInt(0);
        cursor.close();
        return result;
    }

    private String getStringData(String tableName, String column) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + column + " FROM " + tableName + " LIMIT 1", null);
        String result = "---";
        if (cursor.moveToFirst()) result = cursor.getString(0);
        cursor.close();
        return result;
    }
    private void uploadScore(String table, String username, int score) {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        okhttp3.RequestBody formBody = new okhttp3.FormBody.Builder()
                .add("table", table)
                .add("username", username)
                .add("score", String.valueOf(score))
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://mallygame.atwebpages.com/upload_score.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) { e.printStackTrace(); }
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                response.close();
            }
        });
    }
}