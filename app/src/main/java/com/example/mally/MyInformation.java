package com.example.mally;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import androidx.core.view.GravityCompat;



public class MyInformation extends AppCompatActivity {

    private static final String NEWS_API_URL =
            "https://api.thenewsapi.com/v1/news/all?api_token=3o8WFmp53Ic3VmYkiUBQXxhEJgJBKbWDaXJMR35J&q=Congo OR RDC OR Kinshasa&language=fr&limit=10";

    private RecyclerView recyclerView;
    private InfoAdapter adapter;
    private ArrayList<InfoItem> list = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private LottieAnimationView lottieLoading;
    private SwipeRefreshLayout swipeRefresh;
    private MaterialToolbar toolbarInfo;
    private DrawerLayout drawerLayout;

    private boolean isLoading = false;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);

        // ===== Drawer & Toolbar =====
        drawerLayout = findViewById(R.id.drawer);
        toolbarInfo = findViewById(R.id.toolbar_info);
        setSupportActionBar(toolbarInfo);

        // Hamburger ouvre Drawer
        toolbarInfo.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        toolbarInfo.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // ===== Navigation Drawer =====
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            Intent intent = null;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    intent = new Intent(this, MainActivity.class);
                    break;
                case R.id.nav_help:
                    intent = new Intent(this, Help_Games.class);
                    break;
                case R.id.nav_user:
                    intent = new Intent(this, UserGameParties.class);
                    break;
                case R.id.nav_game:
                    intent = new Intent(this, MyGame.class);
                    break;
            }

            if (intent != null) {
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(Gravity.START);
            return true;
        });

        // ===== RecyclerView =====
        recyclerView = findViewById(R.id.infoRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InfoAdapter(this, list);
        recyclerView.setAdapter(adapter);

        // ===== Loader et Swipe =====
        lottieLoading = findViewById(R.id.lottieLoading);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        swipeRefresh.setOnRefreshListener(() -> {
            list.clear();
            adapter.notifyDataSetChanged();
            page = 1;
            showLoading();
            fetchFeed();
        });

        // ===== Scroll infini =====
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    page++;
                    fetchFeed();
                }
            }
        });

        // ===== Chargement initial =====
        showLoading();
        fetchFeed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharge les news fraîches à chaque retour
        list.clear();
        adapter.notifyDataSetChanged();
        page = 1;
        showLoading();
        fetchFeed();
    }

    // ================= UI STATES =================
    private void showLoading() {
        isLoading = true;
        lottieLoading.setVisibility(LottieAnimationView.VISIBLE);
        toolbarInfo.setVisibility(MaterialToolbar.GONE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void showContent() {
        isLoading = false;
        lottieLoading.setVisibility(LottieAnimationView.GONE);
        toolbarInfo.setVisibility(MaterialToolbar.VISIBLE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    // ================= API =================
    private void fetchFeed() {
        String urlWithPage = NEWS_API_URL + "&page=" + page;
        new FetchNewsTask().execute(urlWithPage);
    }

    private class FetchNewsTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                JSONObject root = new JSONObject(sb.toString());
                return root.getJSONArray("data");

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray articles) {
            swipeRefresh.setRefreshing(false);
            showContent();

            if (articles == null) {
                Toast.makeText(MyInformation.this, "Impossible de charger les actualités", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int start = list.size();
                for (int i = 0; i < articles.length(); i++) {
                    JSONObject obj = articles.getJSONObject(i);

                    String title = obj.optString("title", "Actualité");
                    String content = obj.optString("description", "Aucune description disponible");
                    String imageUrl = obj.optString("image_url", "");
                    String url = obj.optString("url", "");
                    JSONObject sourceObj = obj.optJSONObject("source");
                    String source = sourceObj != null ? sourceObj.optString("name", "") : "";
                    String publishedAt = obj.optString("published_at", "");

                    list.add(new InfoItem(title, content, imageUrl, source, publishedAt, url));
                }

                adapter.notifyItemRangeInserted(start, articles.length());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
