package com.example.mally;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {

    ImageView formation, information, jeu, musique;

    LinearLayout rootContainer;
    FrameLayout splashOverlay;
    LottieAnimationView lottieView;

    private boolean isSplashPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        method_for_match_id();

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v ->
                Toast.makeText(this, "Mally 4In1", Toast.LENGTH_SHORT).show()
        );

        // Clics
        setupClick(formation, MyFormation.class);
        setupClick(information, ActualiteActivity.class);
        setupClick(jeu, MyGame.class);
        setupClick(musique, MyMusique.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        playSplash();
    }

    // ================= SPLASH =================
    private void playSplash() {
        if (isSplashPlaying) return;
        isSplashPlaying = true;

        splashOverlay.setAlpha(1f);
        splashOverlay.setVisibility(View.VISIBLE);
        rootContainer.setAlpha(0f);

        long startTime = System.currentTimeMillis();

        lottieView.cancelAnimation();
        lottieView.playAnimation();

        lottieView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                long elapsed = System.currentTimeMillis() - startTime;
                long delay = Math.max(0, 3000 - elapsed);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {

                    splashOverlay.animate()
                            .alpha(0f)
                            .setDuration(400)
                            .withEndAction(() -> {
                                splashOverlay.setVisibility(View.GONE);
                                rootContainer.animate()
                                        .alpha(1f)
                                        .setDuration(600)
                                        .start();
                                isSplashPlaying = false;
                            })
                            .start();

                }, delay);
            }
        });
    }

    // ================= IDS =================
    private void method_for_match_id() {
        formation = findViewById(R.id.formation);
        information = findViewById(R.id.information);
        jeu = findViewById(R.id.jeu);
        musique = findViewById(R.id.musique);

        rootContainer = findViewById(R.id.rootContainer);
        splashOverlay = findViewById(R.id.splashOverlay);
        lottieView = findViewById(R.id.lottieView);
    }

    // ================= CLICS =================
    private void setupClick(ImageView img, Class<?> target) {
        if (img == null) return;
        img.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, target));
            } catch (Exception e) {
                Toast.makeText(this,
                        "Cette fonctionnalité n'est pas disponible",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= BACK =================
    @Override
    public void onBackPressed() {
        if (splashOverlay.getVisibility() == View.VISIBLE) return;
        super.onBackPressed();
    }
}
