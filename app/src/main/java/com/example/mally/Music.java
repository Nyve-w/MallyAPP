package com.example.mally;

public class Music {
    public String title;
    public String artist;
    public String category;
    public int audioResId;
    public boolean isFavorite = false; // ← nouveau

    public Music(String title, String artist, String category, int audioResId) {
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.audioResId = audioResId;
    }
}
