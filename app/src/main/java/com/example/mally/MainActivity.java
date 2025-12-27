package com.example.mally;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

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
    }
    public void method_for_match_id(){
        formation=findViewById(R.id.formation);
        information=findViewById(R.id.information);
        jeu=findViewById(R.id.jeu);
        musique=findViewById(R.id.musique);
    }
    public void methForOpenFiles(View v){
        Intent openChaussure = new Intent(this,myFormation.class);
        startActivity(openChaussure);;

    }

    public void methForOpenFiles2(View v){
        Intent openChaussure = new Intent(this,MyInformation.class);
        startActivity(openChaussure);;

    }

    public void methForOpenFiles3(View v){
        Intent openChaussure = new Intent(this,MyGame.class);
        startActivity(openChaussure);;

    }

    public void methForOpenFiles4(View v){
        Intent openChaussure = new Intent(this,MyMusique.class);
        startActivity(openChaussure);;

    }
}