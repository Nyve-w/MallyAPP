package com.example.mally;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

public class MyFormation extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;

    // Loader
    LottieAnimationView lottieLoading;
    View loaderContainer;

    private static final int LOADER_DURATION = 3000; // 3 secondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_formation);

        // ===== INIT IDs =====
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        loaderContainer = findViewById(R.id.loader_container);
        lottieLoading = findViewById(R.id.lottieLoading);

        // ===== Toolbar + Drawer =====
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            Intent intent = null;
            switch(item.getItemId()){
                case R.id.nav_home: intent = new Intent(this, MainActivity.class); break;
                case R.id.nav_help: intent = new Intent(this, Help_Games.class); break;
                case R.id.nav_user: intent = new Intent(this, UserGameParties.class); break;
                case R.id.nav_game: intent = new Intent(this, MyGame.class); break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            if(intent != null) startActivity(intent);
            return true;
        });

        // ===== Loader =====
        loaderContainer.setVisibility(View.VISIBLE);
        lottieLoading.playAnimation();

        new Handler().postDelayed(() -> {
            lottieLoading.cancelAnimation();
            loaderContainer.setVisibility(View.GONE); // drawer + toolbar deviennent interactifs
        }, LOADER_DURATION);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Ouvrir un PDF
    public void pdf(){
        File file = new File(getFilesDir(), "cours1.pdf");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".provider",
                        file
                ),
                "application/pdf"
        );
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    // Exemple bouton vers nosformations
    public void moveToActivity(View v){
        startActivity(new Intent(MyFormation.this, nosformations.class));
    }
}
