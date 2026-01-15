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
import java.util.List;

public class MyInformation extends AppCompatActivity {

    private static final String NEWS_API_URL =
            "https://api.thenewsapi.com/v1/news/all?api_token=NUbnPpC9JNn2FhKQVsxGYasyqG4xYlb7ikVKrinn&q=Congo OR RDC OR Kinshasa&language=fr&limit=10";

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

        drawerLayout = findViewById(R.id.drawer);
        toolbarInfo = findViewById(R.id.toolbar_info);
        setSupportActionBar(toolbarInfo);
        toolbarInfo.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        toolbarInfo.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            Intent intent = null;
            switch (item.getItemId()) {
                case R.id.nav_home: intent = new Intent(this, MainActivity.class); break;
                case R.id.nav_help: intent = new Intent(this, Help_Games.class); break;
                case R.id.nav_user: intent = new Intent(this, UserGameParties.class); break;
                case R.id.nav_game: return true;
            }
            if (intent != null) { startActivity(intent); finish(); }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        recyclerView = findViewById(R.id.infoRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InfoAdapter(this, list);
        recyclerView.setAdapter(adapter);

        lottieLoading = findViewById(R.id.lottieLoading);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        swipeRefresh.setOnRefreshListener(() -> {
            list.clear();
            adapter.notifyDataSetChanged();
            page = 1;
            showLoading();
            fetchFeed();
        });

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

        // Affiche le cache puis fetch API
        loadCache();
        showLoading();
        fetchFeed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCache();
        page = 1;
        showLoading();
        fetchFeed();
    }

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

    private void loadCache() {
        list.clear();
        List<NewsItemEntity> cached = AppDatabase.getInstance(this).newsDao().getAllNews();
        for (NewsItemEntity n : cached) {
            list.add(new InfoItem(n.title, n.content, n.imageUrl, n.sourceName, n.publishedAt, n.url));
        }
        adapter.notifyDataSetChanged();
    }

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

            } catch (Exception e) { e.printStackTrace(); return null; }
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

                // Sauvegarder dans Room
                List<NewsItemEntity> entities = new ArrayList<>();
                for (InfoItem i : list) {
                    entities.add(new NewsItemEntity(i.title, i.content, i.imageUrl, i.sourceName, i.publishedAt, i.url));
                }
                AppDatabase db = AppDatabase.getInstance(MyInformation.this);
                db.newsDao().deleteAll();
                db.newsDao().insertAll(entities);

            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
