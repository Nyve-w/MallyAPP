package com.example.mally;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.material.appbar.MaterialToolbar;

public class UserGameParties extends AppCompatActivity {
    private TextView tvBestScore, tvBestTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_game_parties);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent open = new Intent(UserGameParties.this, MyGame.class);
            startActivity(open);
        });

        tvBestScore = findViewById(R.id.tvBestScoreSolitaire);
        tvBestTime = findViewById(R.id.tvBestTimeSolitaire);

        // DB
        GameDatabaseHelper dbHelper = new GameDatabaseHelper(this);
        int bestScore = dbHelper.getBestSolitaireScore();
        long bestTime = dbHelper.getBestTime();

        tvBestScore.setText("Meilleur Score : " + bestScore);
        tvBestTime.setText("Meilleur Temps : " + formatTime(bestTime));
    }

    private String formatTime(long timeMs){
        int totalSeconds = (int)(timeMs / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
