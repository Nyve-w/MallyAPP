package com.example.mally;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyMusique extends AppCompatActivity {

    // ===== MEDIA =====
    public static MediaPlayer mediaPlayer;
    public static int currentPos = -1;

    // ===== DATA =====
    List<Music> all = new ArrayList<>();
    List<Music> filtered = new ArrayList<>();
    MusicAdapter adapter;

    // ===== UI =====
    LinearLayout miniPlayer, categoryContainer, artistContainer; // <-- artistContainer ajouté
    TextView miniTitle, miniArtist;
    Button miniPrev, miniPlay, miniNext;
    SeekBar miniSeekBar;

    FrameLayout loaderContainer;
    LottieAnimationView lottieLoading;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    Handler handler = new Handler();
    Button activeCategoryButton = null;
    Button activeArtistButton = null; // <-- pour artiste

    private static final int LOADER_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_musique);

        // ===== FIND VIEWS =====
        miniPlayer = findViewById(R.id.miniPlayer);
        miniTitle = findViewById(R.id.miniTitle);
        miniArtist = findViewById(R.id.miniArtist);
        miniPrev = findViewById(R.id.miniPrev);
        miniPlay = findViewById(R.id.miniPlay);
        miniNext = findViewById(R.id.miniNext);
        miniSeekBar = findViewById(R.id.miniSeek);

        categoryContainer = findViewById(R.id.categoryContainer);
        artistContainer = findViewById(R.id.artistContainer); // <-- trouve le conteneur artiste

        RecyclerView recyclerView = findViewById(R.id.recyclerMusic);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loaderContainer = findViewById(R.id.loaderContainer);
        lottieLoading = findViewById(R.id.lottieLoading);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        // ===== TOOLBAR SETUP =====
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // ===== NAVIGATION DRAWER =====
        navigationView.setNavigationItemSelectedListener(item -> {
            Intent intent = null;
            if (item.getItemId() == R.id.nav_home) {
                intent = new Intent(MyMusique.this, MainActivity.class);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            if (intent != null) {
                startActivity(intent);
                finish();
            }
            return true;
        });

        // ===== LOAD DATA =====
        showLoader();
        loadJson();

        // ===== ADAPTER =====
        filtered.addAll(all);
        adapter = new MusicAdapter(this, filtered);
        recyclerView.setAdapter(adapter);

        // ===== CATÉGORIES (BOUTONS ARRONDIS) =====
        addCategoryButton("Tous");
        Set<String> categories = new HashSet<>();
        for (Music m : all) categories.add(m.category);
        for (String cat : categories) addCategoryButton(cat);

        // ===== ARTISTES (BOUTONS ARRONDIS) =====
        addArtistButton("Tous");
        Set<String> artists = new HashSet<>();
        for (Music m : all) artists.add(m.artist);
        for (String artist : artists) addArtistButton(artist);

        // ===== MINI PLAYER =====
        miniNext.setOnClickListener(v -> playNext());
        miniPrev.setOnClickListener(v -> playPrevious());
        initSeekBar();
    }

    // ================== UI ==================
    private void showLoader() {
        loaderContainer.setVisibility(FrameLayout.VISIBLE);
        lottieLoading.playAnimation();
        handler.postDelayed(() -> {
            lottieLoading.cancelAnimation();
            loaderContainer.setVisibility(FrameLayout.GONE);
        }, LOADER_DURATION);
    }

    // ===== CATEGORY BUTTONS =====
    private void addCategoryButton(String category) {
        Button btn = createRoundedButton(category);
        btn.setOnClickListener(v -> {
            if (activeCategoryButton != null)
                activeCategoryButton.setBackgroundColor(getColor(R.color.defaultBtnColor));

            btn.setBackgroundColor(getColor(R.color.activeBtnColor));
            activeCategoryButton = btn;

            filtered.clear();
            if (category.equals("Tous")) filtered.addAll(all);
            else for (Music m : all)
                if (m.category.equals(category)) filtered.add(m);

            currentPos = -1;
            adapter.notifyDataSetChanged();
        });
        categoryContainer.addView(btn);

        if (category.equals("Tous")) {
            activeCategoryButton = btn;
            btn.setBackgroundColor(getColor(R.color.activeBtnColor));
        }
    }

    // ===== ARTIST BUTTONS =====
    private void addArtistButton(String artist) {
        Button btn = createRoundedButton(artist);
        btn.setOnClickListener(v -> {
            if (activeArtistButton != null)
                activeArtistButton.setBackgroundColor(getColor(R.color.defaultBtnColor));

            btn.setBackgroundColor(getColor(R.color.activeBtnColor));
            activeArtistButton = btn;

            filtered.clear();
            if (artist.equals("Tous")) filtered.addAll(all);
            else for (Music m : all)
                if (m.artist.equals(artist)) filtered.add(m);

            currentPos = -1;
            adapter.notifyDataSetChanged();
        });
        artistContainer.addView(btn);

        if (artist.equals("Tous")) {
            activeArtistButton = btn;
            btn.setBackgroundColor(getColor(R.color.activeBtnColor));
        }
    }

    // ===== CREATE ROUNDED BUTTON =====
    private Button createRoundedButton(String text) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setAllCaps(false);
        btn.setPadding(32, 16, 32, 16);

        // Bordure arrondie
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(getColor(R.color.defaultBtnColor));
        gd.setCornerRadius(50); // <-- arrondi
        btn.setBackground(gd);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        params.setMargins(24, 0, 24, 0);
        btn.setLayoutParams(params);
        return btn;
    }

    // ================== DATA ==================
    private void loadJson() {
        try {
            InputStream is = getAssets().open("musics.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            JSONArray arr = new JSONArray(new String(buffer, StandardCharsets.UTF_8));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                int resId = getResources().getIdentifier(
                        o.getString("file"), "raw", getPackageName());
                if (resId == 0) continue;

                all.add(new Music(
                        o.getString("title"),
                        o.getString("artist"),
                        o.getString("category"),
                        resId
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================== PLAYER ==================
    public void playMusicFromAdapter(Music music, int pos) {
        currentPos = pos;

        if (mediaPlayer != null) mediaPlayer.release();

        mediaPlayer = MediaPlayer.create(this, music.audioResId);
        mediaPlayer.start();

        showMiniPlayer(music);
        adapter.notifyDataSetChanged();

        mediaPlayer.setOnCompletionListener(mp -> {
            currentPos = -1;
            adapter.notifyDataSetChanged();
            mp.release();
            mediaPlayer = null;
        });
    }

    private void showMiniPlayer(Music music) {
        miniPlayer.setVisibility(LinearLayout.VISIBLE);
        miniTitle.setText(music.title);
        miniArtist.setText(music.artist);

        miniPlay.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                miniPlay.setText("▶");
            } else {
                mediaPlayer.start();
                miniPlay.setText("⏸");
            }
            adapter.notifyDataSetChanged();
        });

        miniSeekBar.setMax(mediaPlayer.getDuration());
        startSeekUpdate();
    }

    private void initSeekBar() {
        miniSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean fromUser) {
                if (fromUser && mediaPlayer != null) mediaPlayer.seekTo(p);
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });
    }

    private void startSeekUpdate() {
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                if (mediaPlayer != null) {
                    miniSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 300);
                }
            }
        }, 300);
    }

    private void playNext() {
        if (filtered.isEmpty() || currentPos == -1) return;
        int next = (currentPos + 1) % filtered.size();
        playMusicFromAdapter(filtered.get(next), next);
    }

    private void playPrevious() {
        if (filtered.isEmpty() || currentPos == -1) return;
        int prev = currentPos - 1;
        if (prev < 0) prev = filtered.size() - 1;
        playMusicFromAdapter(filtered.get(prev), prev);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            currentPos = -1;
        }
    }
}
