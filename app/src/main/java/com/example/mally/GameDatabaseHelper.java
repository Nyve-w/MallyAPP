package com.example.mally;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mally_game.db";
    private static final int DATABASE_VERSION = 2; // ⚠️ J'ai passé la version à 2 pour forcer la mise à jour

    // Table Solitaire
    private static final String TABLE_SOLITAIRE = "table_solitaire";
    // Table Hangman (Pendu)
    private static final String TABLE_HANGMAN = "table_hangman";

    // Colonnes communes
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username"; // Le petit nouveau
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_TIME = "time"; // Uniquement pour solitaire

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Création table Solitaire (Score + Temps + Nom)
        String CREATE_SOLITAIRE = "CREATE TABLE " + TABLE_SOLITAIRE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_SCORE + " INTEGER,"
                + COLUMN_TIME + " INTEGER" + ")";
        db.execSQL(CREATE_SOLITAIRE);

        // Création table Hangman (Score + Nom uniquement, le temps importe peu au pendu)
        String CREATE_HANGMAN = "CREATE TABLE " + TABLE_HANGMAN + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_SCORE + " INTEGER" + ")";
        db.execSQL(CREATE_HANGMAN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOLITAIRE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HANGMAN);
        // Si tu avais l'ancienne table unique, on la vire aussi
        db.execSQL("DROP TABLE IF EXISTS best_scores");
        onCreate(db);
    }

    // --- SOLITAIRE : Sauvegarde ---
    public void saveSolitaireScore(String name, int score, long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        // On supprime l'ancien record pour ne garder que le meilleur absolu
        db.delete(TABLE_SOLITAIRE, null, null);

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, name);
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_TIME, time);
        db.insert(TABLE_SOLITAIRE, null, values);
        //db.close();
    }

    // --- SOLITAIRE : Récupération ---
    public int getBestSolitaireScore() {
        return getIntData(TABLE_SOLITAIRE, COLUMN_SCORE);
    }
    public String getBestSolitairePlayer(){
        return  getStringData(TABLE_SOLITAIRE,COLUMN_USERNAME);
    }

    // --- HANGMAN : Sauvegarde ---
    public void saveHangmanScore(String name, int score) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Logique : On vérifie si le nouveau score est meilleur que l'ancien
        int currentBest = getBestHangmanScore();
        if (score > currentBest) {
            db.delete(TABLE_HANGMAN, null, null); // On garde seulement le champion
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, name);
            values.put(COLUMN_SCORE, score);
            db.insert(TABLE_HANGMAN, null, values);
        }
        //db.close();
    }

    // --- HANGMAN : Récupération ---
    public int getBestHangmanScore() {
        return getIntData(TABLE_HANGMAN, COLUMN_SCORE);
    }

    public String getBestHangmanPlayer() {
        return getStringData(TABLE_HANGMAN, COLUMN_USERNAME);
    }

    // --- Utilitaires génériques ---
    private int getIntData(String tableName, String column) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(" + column + ") FROM " + tableName, null);
        int result = 0;
        if (cursor.moveToFirst()) result = cursor.getInt(0);
        cursor.close();
        //db.close();
        return result;
    }

    private String getStringData(String tableName, String column) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + column + " FROM " + tableName + " LIMIT 1", null);
        String result = "---";
        if (cursor.moveToFirst()) result = cursor.getString(0);
        cursor.close();
        //db.close();
        return result;
    }
}