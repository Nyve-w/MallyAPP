package com.example.mymusique;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
;
class Playlist extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Button btnPlay1, btnPlay2, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        btnPlay1 = findViewById(R.id.btnPlay1);
        btnPlay2 = findViewById(R.id.btnPlay2);
        btnBack = findViewById(R.id.btnBackToStyles);

        // Jouer la première musique
        btnPlay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic(R.raw.gospel_un); //
            }
        });

        // Jouer la deuxième musique
        btnPlay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic(R.raw.gospel_deux);
            }
        });

        // Bouton retour
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Ferme cette page et revient à la liste des styles
            }
        });
    }

    private void playMusic(int resId) {
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Arrête la musique précédente
        }
        mediaPlayer = MediaPlayer.create(this, resId);
        mediaPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}