package com.example.mally;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyGame extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_game);
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            Intent intent = null;

            switch (item.getItemId()) {

                case R.id.nav_help:
                    intent = new Intent(this, Help_Games.class);
                    break;

                case R.id.nav_profile:
                    intent = new Intent(this, UserGameParties.class);
                    break;

                /*case R.id.nav_game:
                    intent = new Intent(this, MyGame.class);
                    break;*/
            }

            if (intent != null) {
                startActivity(intent);
            }

            return true;
        });


    }
    public void methtry(MenuItem item) {
        // Exemple de code à exécuter selon l'item cliqué
        switch (item.getItemId()) {
            case R.id.nav_help:
                Toast.makeText(this, "Item 1 cliqué", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_home:
                finish();
        }
    }

    private void moveTooFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container_view,fragment).commit();
    }

    public void ouvrirJeu2(View v){
        Intent openGame1 = new Intent(this,jeu_solitaire.class);
        startActivity(openGame1);;
    }
    public void ouvrirJeu1(View v){
        AlertDialog.Builder messageAlert=new AlertDialog.Builder(this);
        messageAlert.setTitle("Mally Information");
        messageAlert.setMessage("Ce jeu n'est pas disponible dans votre region");
        messageAlert.setIcon(android.R.drawable.ic_dialog_info);
        messageAlert.show();
        Intent openGame2 = new Intent(this,hangmanGame.class);
        startActivity(openGame2);;

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed(); // retour normal
    }

}