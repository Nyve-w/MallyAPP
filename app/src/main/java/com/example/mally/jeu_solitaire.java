package com.example.mally;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ImageButton;

public class jeu_solitaire extends Activity {
    private GameView gameView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu_solitaire);
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnHelp = findViewById(R.id.btnHelp);

        gameView = findViewById( R.id.gameView );
        gameView.game.initNewGame();
        gameView.invalidate();

        btnBack.setOnClickListener(v -> finish());

        btnHelp.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Aide")
                    .setMessage(
                           "Objectif du jeu :\n" +
                                    "Déplacer toutes les cartes dans les quatre piles de couleur, de l’As au Roi.\n\n" +
                                    "Comment jouer :\n" +
                                    "1. Préparer le tableau :\n" +
                                    "- Le tableau comporte 7 colonnes.\n" +
                                    "- La première colonne contient 1 carte, la deuxième 2 cartes, etc., jusqu’à la 7ᵉ colonne.\n" +
                                    "- Seule la carte du dessus de chaque colonne est visible.\n\n" +
                                    "2. Déplacer les cartes :\n" +
                                    "- Déplacer une carte visible sur une autre carte visible si elle est d’une couleur différente et d’une valeur immédiatement inférieure (ex : un 6 rouge sur un 7 noir).\n" +
                                    "- Si une colonne est vide, seule un Roi (et ses cartes empilées) peut y être placé.\n\n" +
                                    "3. Les piles de couleur :\n" +
                                    "- Les As vont sur les piles de couleur (cœur, carreau, trèfle, pique).\n" +
                                    "- Chaque pile est construite du plus petit au plus grand (As → 2 → 3 … → Roi).\n\n" +
                                    "4. Pioche :\n" +
                                    "- Prendre des cartes de la pioche et les placer sur le tableau ou sur les piles de couleur selon les règles.\n\n" +
                                    "5. Gagner :\n" +
                                    "- Le jeu est gagné quand toutes les cartes sont correctement placées sur les piles de couleur.\n\n" +
                                    "Conseil :\n" +
                                    "- Planifie tes déplacements et découvre les cartes cachées le plus vite possible.\n" +
                                    "- Utilise la pioche avec stratégie pour ne pas bloquer le jeu."
                    )
                    .setPositiveButton("OK", null)
                    .show();
        });

    }
}