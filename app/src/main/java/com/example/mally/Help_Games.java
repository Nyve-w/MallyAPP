package com.example.mally;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.material.appbar.MaterialToolbar;

public class Help_Games extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_games);
        MaterialToolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent open= new Intent(Help_Games.this,MyGame.class);
                startActivity(open);
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search:
                        messageAlert("Tu veux rechercher quoi ? Toi lis seulement","Information");

                }
                return false;
            }
        });
    }


    private void messageAlert(String message,String title){
        AlertDialog.Builder messageAlert=new AlertDialog.Builder(this);
        messageAlert.setTitle(title);
        messageAlert.setMessage(message);
        messageAlert.setIcon(android.R.drawable.ic_dialog_info);
        messageAlert.show();
    }
    public void showHelpRules(View v) {
        String rules = "Objectif du jeu :\n" +
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
                "- Utilise la pioche avec stratégie pour ne pas bloquer le jeu.";

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Règles du Solitaire");
        alert.setMessage(rules);
        alert.setIcon(android.R.drawable.ic_dialog_info);
        alert.setPositiveButton("OK", null);
        alert.show();
    }
    public void showHangmanRules(View v) {
        String rules = "JEU 2. PENDU (HANGMAN)\n\n" +
                "Objectif du jeu :\n" +
                "- Deviner le mot caché avant que le pendu ne soit complet.\n\n" +
                "Comment jouer :\n" +
                "1. Un mot ou une phrase est choisi aléatoirement.\n" +
                "2. Le mot est affiché avec des tirets à la place des lettres.\n" +
                "3. Tu dois proposer une lettre à chaque tour.\n" +
                "4. Si la lettre est dans le mot, elle apparaît à toutes ses positions.\n" +
                "5. Si la lettre n’est pas dans le mot, une partie du pendu est dessinée.\n" +
                "6. Tu as un nombre limité d’erreurs possibles avant que le pendu soit complet.\n\n" +
                "Gagner :\n" +
                "- Deviner toutes les lettres du mot avant que le pendu ne soit complet.\n\n" +
                "Conseils :\n" +
                "- Commence par les voyelles, elles apparaissent souvent dans les mots.\n" +
                "- Essaie des lettres fréquentes comme 'S', 'T', 'R', 'N'.\n" +
                "- Évite de répéter les lettres déjà proposées.";

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Règles du Pendu");
        alert.setMessage(rules);
        alert.setIcon(android.R.drawable.ic_dialog_info);
        alert.setPositiveButton("OK", null);
        alert.show();
    }
    public void showSudokuRules(View v) {
        String rules = "JEU 3. SUDOKU\n\n" +
                "Objectif du jeu :\n" +
                "- Remplir toutes les cases vides de la grille avec des chiffres de 1 à 9.\n\n" +
                "Règles de base :\n" +
                "1. Chaque ligne doit contenir tous les chiffres de 1 à 9 sans répétition.\n" +
                "2. Chaque colonne doit contenir tous les chiffres de 1 à 9 sans répétition.\n" +
                "3. Chaque carré 3x3 doit contenir tous les chiffres de 1 à 9 sans répétition.\n\n" +
                "Comment jouer :\n" +
                "- Commence par remplir les chiffres évidents, ceux qui ne peuvent aller qu’à un seul endroit.\n" +
                "- Utilise la logique pour éliminer les possibilités et trouver les chiffres cachés.\n" +
                "- Évite de deviner : le Sudoku est basé sur la déduction.\n\n" +
                "Gagner :\n" +
                "- La partie est terminée quand toutes les cases sont correctement remplies selon les règles.\n\n" +
                "Conseils :\n" +
                "- Note les possibilités dans chaque case pour t’aider.\n" +
                "- Commence par les lignes, colonnes ou carrés 3x3 les plus remplis.\n" +
                "- Vérifie souvent pour éviter les erreurs.";

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Règles du Sudoku");
        alert.setMessage(rules);
        alert.setIcon(android.R.drawable.ic_dialog_info);
        alert.setPositiveButton("OK", null);
        alert.show();
    }



}