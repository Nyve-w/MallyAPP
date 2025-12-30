package com.example.mally;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

public class jeu_solitaire extends Activity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu_solitaire);

        gameView = findViewById( R.id.gameView );
        gameView.game.initNewGame();
        gameView.invalidate();

    }
}