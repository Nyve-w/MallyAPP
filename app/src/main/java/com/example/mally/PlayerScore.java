package com.example.mally;

public class PlayerScore {
    private String username;
    private int score;

    public PlayerScore(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }
}