package com.example.mally;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;

public class UserGameParties extends AppCompatActivity {
    private TextView tvBestScore;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_game_parties);
        MaterialToolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open= new Intent(UserGameParties.this,MyGame.class);
                startActivity(open);
            }
        });
        tvBestScore = findViewById(R.id.textView3);
        gameView = findViewById(R.id.gameView); // ID de ton GameView

        GameDatabaseHelper dbHelper = new GameDatabaseHelper(this);
        int bestScore = dbHelper.getBestScore();
        tvBestScore.setText("Meilleur Score : " + bestScore);
    }
}