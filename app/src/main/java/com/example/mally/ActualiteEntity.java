package com.example.mally;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "actualites")
public class ActualiteEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;
    public String url;
    public String imageUrl;
}
