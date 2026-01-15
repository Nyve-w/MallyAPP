package com.example.mally;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "news")
public class NewsItemEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String content;
    public String imageUrl;
    public String sourceName;
    public String publishedAt;
    public String url;

    public NewsItemEntity(String title, String content, String imageUrl, String sourceName, String publishedAt, String url) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.sourceName = sourceName;
        this.publishedAt = publishedAt;
        this.url = url;
    }
}
