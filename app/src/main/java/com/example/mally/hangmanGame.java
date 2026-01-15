package com.example.mally;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class hangmanGame extends AppCompatActivity {
    private TextInputEditText mng;
    private TextView wordDisplay;        // Affichage _ _ _ _
    private String word = "HANGMAN";       // Mot test
    private char[] displayedWord;        // Tableau des lettres affichées

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hangman_game);
        mng = findViewById(R.id.hangman_inputText);
        GridLayout grid = findViewById(R.id.gridLayout);
        wordDisplay = findViewById(R.id.wordDisplay);
        char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        // Initialisation du mot affiché avec _
        displayedWord = new char[word.length()];
        for (int i = 0; i < displayedWord.length; i++) {
            displayedWord[i] = '_';
        }
        updateWordDisplay();



        for (char letter : letters) {
            Button button = new Button(this);
            button.setText(String.valueOf(letter));
            button.setBackgroundResource(R.drawable.btn_color);
            // Optionnel : marges autour de chaque bouton
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0; // pour que chaque bouton prenne un poids égal
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // chaque colonne prend 1 poids
            params.setMargins(8, 8, 8, 8);
            button.setLayoutParams(params);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkLetter(letter);
                    button.setEnabled(false);
                    button.setAlpha(0.5f); // montre que le bouton est désactivé

                }
            });

            grid.addView(button);
        }

        // Clique sur le bouton de recherche du champ texte
        ImageButton btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            String input = mng.getText().toString().toUpperCase();
            if (input.length() == 1) {
                checkLetter(input.charAt(0));
            }
            mng.setText(""); // Vider le champ
        });


    }
    private void checkLetter(char letter) {
        mng.setText(String.valueOf(letter));
        boolean found = false;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                displayedWord[i] = letter;
                found = true;
            }
            else Toast.makeText(this,"(anime echec)",LENGTH_SHORT).show();
        }

        if (found) {
            Toast.makeText(this,"(anime succes)",LENGTH_SHORT).show();
            updateWordDisplay();
        }
    }
    // Affiche le mot dans le TextView
    private void updateWordDisplay() {
        StringBuilder sb = new StringBuilder();
        for (char c : displayedWord) {
            sb.append(c).append(" "); // ajoute un espace entre les lettres
        }
        wordDisplay.setText(sb.toString().trim());
    }

}