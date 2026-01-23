package com.example.mymusique;

import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText searchBar;
    private Button btnGospel, btnPop;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des vues
        btnBack = findViewById(R.id.btnBack);
        searchBar = findViewById(R.id.search_bar);
        btnGospel = findViewById(R.id.btnGospel);
        btnPop = findViewById(R.id.btnPop);


        // Bouton GOSPEL
        btnGospel.setOnClickListener(v ->
                playSong(R.raw.gospel_un)
        );

        // Bouton POP
        btnPop.setOnClickListener(v ->
                playSong(R.raw.pop_un)
        );


        // Bouton retour
        btnBack.setOnClickListener(v -> finish());
    }

    // Méthode pour jouer une musique
    private void playSong(int songId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(this, songId);
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}