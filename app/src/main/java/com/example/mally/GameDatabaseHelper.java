package com.example.mally;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class GameDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mally_game.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "best_scores";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_TIME = "time";

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SCORE + " INTEGER,"
                + COLUMN_TIME + " INTEGER"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // --- Méthode pour ajouter ou mettre à jour le meilleur score ---
   /* public void saveScore(int score, long time) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Vérifier s'il y a déjà un score
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " LIMIT 1", null);
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_TIME, time);

        if(cursor.moveToFirst()) {
            // Mettre à jour
            db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{ String.valueOf(cursor.getInt(0)) });
        } else {
            // Insérer
            db.insert(TABLE_NAME, null, values);
        }
        cursor.close();
        db.close();
    }*/
    public void saveScore(int score, long time) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 🔍 récupérer le meilleur score existant
        Cursor cursor = db.rawQuery(
                "SELECT MAX(" + COLUMN_SCORE + ") FROM " + TABLE_NAME,
                null
        );

        int bestScore = 0;
        if (cursor.moveToFirst()) {
            bestScore = cursor.getInt(0);
        }
        cursor.close();

        // 🧠 si le nouveau score est meilleur → on sauvegarde
        if (score > bestScore) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SCORE, score);
            values.put(COLUMN_TIME, time);

            db.delete(TABLE_NAME, null, null); // 🔥 on garde UNE SEULE ligne
            db.insert(TABLE_NAME, null, values);
        }

        db.close();
    }


    // --- Méthode pour récupérer le meilleur score ---
    public int getBestScore() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT MAX(" + COLUMN_SCORE + ") FROM " + TABLE_NAME, null);
        int score = 0;
        if (cursor.moveToFirst()) {
            score = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return score;
    }


    // --- Méthode pour récupérer le meilleur temps ---
    public long getBestTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_TIME + " FROM " + TABLE_NAME + " LIMIT 1", null);
        long time = Long.MAX_VALUE;
        if(cursor.moveToFirst()) {
            time = cursor.getLong(0);
        }
        cursor.close();
        db.close();
        return time;
    }
}
