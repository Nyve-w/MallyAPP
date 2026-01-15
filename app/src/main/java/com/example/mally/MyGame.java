package com.example.mally;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyGame extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private LottieAnimationView lottieLoading;
    private FrameLayout loaderContainer;

    private static final int LOADING_DURATION = 3000; // ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_game);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE); // cachée au départ

        initLoader();
        initBottomNavigation();
    }

    /* ===================== LOADER ===================== */

    private void initLoader() {
        loaderContainer = findViewById(R.id.loader_container);
        lottieLoading = findViewById(R.id.lottieLoading);

        loaderContainer.setVisibility(View.VISIBLE);
        lottieLoading.playAnimation();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            lottieLoading.cancelAnimation();
            loaderContainer.setVisibility(View.GONE);

            bottomNavigationView.setVisibility(View.VISIBLE); // affichée après
        }, LOADING_DURATION);
    }

    /* ===================== BOTTOM NAV ===================== */

    private void initBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {

            Intent intent = null;

            switch (item.getItemId()) {
                case  R.id.nav_home:
                        finish();
                case R.id.nav_help:
                    intent = new Intent(this, Help_Games.class);
                    break;

                case R.id.nav_user:
                    intent = new Intent(this, UserGameParties.class);
                    break;

                case R.id.nav_game:
                    return true; // déjà ici
            }

            if (intent != null) {
                startActivity(intent);
                finish();
            }

            return true;
        });
    }

    /* ===================== ACTIONS ===================== */

    public void ouvrirJeu2(View v) {
        startActivity(new Intent(this, jeu_solitaire.class));
    }
    public void ouvrirJeu3(View v) {
        startActivity(new Intent(this, hangmanGame.class));
    }

    public void ouvrirJeu1(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Mally Information")
                .setMessage("Ce jeu n'est pas disponible dans votre region")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("OK", null)
                .show();

    }
}
