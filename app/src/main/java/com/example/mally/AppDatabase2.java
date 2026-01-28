package com.example.mally;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ActualiteEntity.class}, version = 1)
public abstract class AppDatabase2 extends RoomDatabase {

    private static AppDatabase2 instance;

    public abstract  ActualiteDao actualiteDao();

    public static synchronized AppDatabase2 getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase2.class, "actualites_db").allowMainThreadQueries().build();
        }
        return instance;
    }
}
