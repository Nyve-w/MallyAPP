package com.example.mally;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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
import java.util.Random;
import java.util.Set;

public class MyMusique extends AppCompatActivity {

    // ===== PLAYER =====
    public static MediaPlayer mediaPlayer;
    public static int currentPos = -1;

    // ===== DATA =====
    List<Music> all = new ArrayList<>();
    List<Music> displayed = new ArrayList<>();

    String activeCategory = "Tous";
    String activeArtist = "Tous";

    MusicAdapter adapter;

    // ===== UI =====
    LinearLayout miniPlayer, categoryContainer, artistContainer;
    TextView miniTitle, miniArtist;
    Button miniPrev, miniPlay, miniNext;
    SeekBar miniSeekBar;

    FrameLayout loaderContainer;
    LottieAnimationView lottieLoading;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    Handler handler = new Handler();

    // ===== OPTIONS =====
    boolean isShuffle = false;
    boolean isRepeatOne = false;
    Random random = new Random();

    private static final int LOADER_DURATION = 1500;

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
        artistContainer = findViewById(R.id.artistContainer);

        RecyclerView recyclerView = findViewById(R.id.recyclerMusic);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loaderContainer = findViewById(R.id.loaderContainer);
        lottieLoading = findViewById(R.id.lottieLoading);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        // ===== TOOLBAR =====
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else
                drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // ===== LOAD DATA =====
        showLoader();
        loadJson();

        displayed.addAll(all);
        adapter = new MusicAdapter(this, displayed);
        recyclerView.setAdapter(adapter);

        buildCategoryButtons();
        buildArtistButtons();

        // ===== MINI PLAYER =====
        miniNext.setOnClickListener(v -> playNext());
        miniPrev.setOnClickListener(v -> playPrevious());

        initSeekBar();
    }

    // ================= LOADER =================
    private void showLoader() {
        loaderContainer.setVisibility(View.VISIBLE);
        lottieLoading.playAnimation();

        handler.postDelayed(() -> {
            loaderContainer.setVisibility(View.GONE);

            findViewById(R.id.recyclerMusic).setVisibility(View.VISIBLE);
            categoryContainer.setVisibility(View.VISIBLE);
            artistContainer.setVisibility(View.VISIBLE);

            if (currentPos != -1) miniPlayer.setVisibility(View.VISIBLE);
        }, LOADER_DURATION);
    }

    // ================= FILTERS =================
    private void buildCategoryButtons() {
        addRoundedButton(categoryContainer, "Tous", true, t -> {
            activeCategory = t;
            applyFilters();
        });

        Set<String> set = new HashSet<>();
        for (Music m : all) set.add(m.category);

        for (String c : set)
            addRoundedButton(categoryContainer, c, false, t -> {
                activeCategory = t;
                applyFilters();
            });
    }

    private void buildArtistButtons() {
        addRoundedButton(artistContainer, "Tous", true, t -> {
            activeArtist = t;
            applyFilters();
        });

        Set<String> set = new HashSet<>();
        for (Music m : all) set.add(m.artist);

        for (String a : set)
            addRoundedButton(artistContainer, a, false, t -> {
                activeArtist = t;
                applyFilters();
            });
    }

    private void applyFilters() {
        List<Music> filtered = new ArrayList<>();

        for (Music m : all) {
            boolean okCat = activeCategory.equals("Tous") || m.category.equals(activeCategory);
            boolean okArt = activeArtist.equals("Tous") || m.artist.equals(activeArtist);
            if (okCat && okArt) filtered.add(m);
        }

        // reset player
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        currentPos = -1;
        miniPlayer.setVisibility(View.GONE);

        adapter.updateList(filtered);
    }

    // ================= ROUNDED BUTTON =================
    private void addRoundedButton(LinearLayout container, String text, boolean active, OnTextClick cb) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setAllCaps(false);
        btn.setPadding(40, 16, 40, 16);

        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(100f);
        bg.setColor(getColor(active ? R.color.activeBtnColor : R.color.defaultBtnColor));
        btn.setBackground(bg);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(16, 0, 16, 0);
        btn.setLayoutParams(lp);

        btn.setOnClickListener(v -> {
            for (int i = 0; i < container.getChildCount(); i++) {
                Button b = (Button) container.getChildAt(i);
                ((GradientDrawable) b.getBackground())
                        .setColor(getColor(R.color.defaultBtnColor));
            }
            bg.setColor(getColor(R.color.activeBtnColor));
            cb.onClick(text);
        });

        container.addView(btn);
    }

    interface OnTextClick {
        void onClick(String text);
    }

    // ================= DATA =================
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

                if (resId != 0)
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

    // ================= PLAYER =================
    public void playMusicFromAdapter(Music music, int pos) {
        int oldPos = currentPos;
        currentPos = pos;

        if (mediaPlayer != null) mediaPlayer.release();

        mediaPlayer = MediaPlayer.create(this, music.audioResId);
        mediaPlayer.start();

        showMiniPlayer(music);

        if (oldPos != -1) adapter.notifyItemChanged(oldPos);
        adapter.notifyItemChanged(currentPos);

        mediaPlayer.setOnCompletionListener(mp -> {
            if (isRepeatOne) {
                playMusicFromAdapter(displayed.get(currentPos), currentPos);
            } else {
                playNext();
            }
        });
    }

    private void showMiniPlayer(Music music) {
        miniPlayer.setVisibility(View.VISIBLE);
        miniTitle.setText(music.title);
        miniArtist.setText(music.artist);
        miniPlay.setText("⏸");

        miniPlay.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                miniPlay.setText("▶");
            } else {
                mediaPlayer.start();
                miniPlay.setText("⏸");
            }
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
        if (adapter.getItemCount() == 0 || currentPos == -1) return;

        int next;
        if (isShuffle) {
            do {
                next = random.nextInt(adapter.getItemCount());
            } while (next == currentPos && adapter.getItemCount() > 1);
        } else {
            next = (currentPos + 1) % adapter.getItemCount();
        }

        playMusicFromAdapter(adapter.getItem(next), next);
    }

    private void playPrevious() {
        if (adapter.getItemCount() == 0 || currentPos == -1) return;

        int prev = currentPos - 1 < 0
                ? adapter.getItemCount() - 1
                : currentPos - 1;

        playMusicFromAdapter(adapter.getItem(prev), prev);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            currentPos = -1;
        }
    }
}
