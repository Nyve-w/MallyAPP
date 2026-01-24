package com.example.mally;

public class InfoItem {
    public String title;
    public String content;
    public String imageUrl;
    public String sourceName;
    public String publishedAt;
    public String url;

    public InfoItem(String title, String content, String imageUrl, String sourceName, String publishedAt, String url) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.sourceName = sourceName;
        this.publishedAt = publishedAt;
        this.url = url;
    }
}
