package com.example.mally;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;

public class UserGameParties extends AppCompatActivity {

    // Déclaration des vues pour le Solitaire
    private TextView userSolitaire, scoreSolitaire, timeSolitaire;

    // Déclaration des vues pour le Hangman
    private TextView userHangman, scoreHangman, timeHangman;
    // Déclaration des vues pour le sudoku
    private TextView userSudoku,scoreSudoku;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_game_parties);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // --- Liaison des composants (Binding) ---

        // Solitaire
        userSolitaire = findViewById(R.id.userSolitaire);
        scoreSolitaire = findViewById(R.id.scoreSolitaire);
        timeSolitaire = findViewById(R.id.textView5); // ID du temps Solitaire dans ton XML

        // Hangman
        userHangman = findViewById(R.id.userHangman);
        scoreHangman = findViewById(R.id.scoreHangman);
        // Note: Dans ton XML, le 2ème "Meilleur Temps" a aussi l'ID textView5, ce qui est un bug.
        // Change l'ID dans le XML en "@+id/timeHangman" pour que ce soit propre.
        // timeHangman = findViewById(R.id.timeHangman);
        //SUDOKU
        userSudoku = findViewById(R.id.userSudoku);
        scoreSudoku = findViewById(R.id.scoreSudoku);

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // On recharge les données si on revient sur cette page après avoir joué
        loadData();
    }

    private void loadData() {
        GameDatabaseHelper db = new GameDatabaseHelper(this);

        // --- Récupération Solitaire ---
        String solUser = db.getBestSolitairePlayer();
        int solScore = db.getBestSolitaireScore();
        //long solTimeMs = db.getBestSolitaireTime();

        /*Formatage du temps
        int seconds = (int) (solTimeMs / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        String solTimeStr = String.format("%02d:%02d", minutes, seconds);*/

        // Affichage Solitaire
        userSolitaire.setText("Joueur : " + solUser);
        scoreSolitaire.setText("Score : " + solScore);
        //timeSolitaire.setText("Temps : " + solTimeStr);


        // --- Récupération Hangman ---
        String hangUser = db.getBestHangmanPlayer();
        int hangScore = db.getBestHangmanScore();

        // Affichage Hangman
        userHangman.setText("Joueur : " + hangUser);
        scoreHangman.setText("Score : " + hangScore);

        // Pour le Hangman, on n'a pas mis de temps dans la DB, on peut masquer ou mettre N/A
        // timeHangman.setText("Temps : N/A");
        //---Récupération Sudoku
        String sudUser = db.getBestSudokuPlayer();
        int sudScore =db.getBestSudokuScore();
        //Affichage
        userSudoku.setText("joeur"+sudUser);
        scoreHangman.setText("Score"+sudScore);
    }
}