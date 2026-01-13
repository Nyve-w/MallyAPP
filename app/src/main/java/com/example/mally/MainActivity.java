package com.example.mally;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {
    ImageView formation;
    ImageView information;
    ImageView jeu;
    ImageView musique;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        method_for_match_id();

        // Toolbar simple
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view ->
                Toast.makeText(MainActivity.this, "Fini de coder ça Monsieur", Toast.LENGTH_SHORT).show()
        );

        // Sécurisation des clics pour éviter crash si activité manquante
        setupClick(formation, MyFormation.class);
        setupClick(information, MyInformation.class);
        setupClick(jeu, MyGame.class);
        setupClick(musique, MyMusique.class);
    }

    // Associe les ImageView aux IDs du layout
    private void method_for_match_id(){
        formation = findViewById(R.id.formation);
        information = findViewById(R.id.information);
        jeu = findViewById(R.id.jeu);
        musique = findViewById(R.id.musique);
    }

    // Méthode sécurisée pour gérer les clics sur les ImageView
    private void setupClick(ImageView img, Class<?> targetActivity){
        if(img == null) return; // sécurité
        img.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity.this, targetActivity);
                startActivity(intent);
            } catch (Exception e) {
                // Si l'activité n'existe pas ou problème → message toast au lieu de crash
                Toast.makeText(MainActivity.this,
                        "Cette fonctionnalité n'est pas disponible pour le moment",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
