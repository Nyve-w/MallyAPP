package com.example.mally;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MyGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_game);

    }
    public void ouvrirJeu2(View v){
        Intent openGame1 = new Intent(this,jeu_solitaire.class);
        startActivity(openGame1);;
    }
    public void ouvrirJeu1(View v){
        Intent openGame1 = new Intent(this,MyMusique.class);
        startActivity(openGame1);;
    }
}