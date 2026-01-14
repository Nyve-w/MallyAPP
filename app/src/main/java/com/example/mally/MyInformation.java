package com.example.mally;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
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

public class MyInformation extends AppCompatActivity {

    private static final String UNSPLASH_API_KEY = "8M-5017UjWpHBzKYDPFsclfRWdZOtUqizysHz26lAXE";
    private static final String UNSPLASH_URL = "https://api.unsplash.com/photos/random?client_id=" + UNSPLASH_API_KEY + "&count=10";

    // Drawer
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    // RecyclerView
    private RecyclerView recyclerView;
    private InfoAdapter adapter;
    private ArrayList<InfoItem> list = new ArrayList<>();
    private LinearLayoutManager layoutManager;

    // Loader
    private LottieAnimationView lottieLoading;
    private SwipeRefreshLayout swipeRefresh;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);

        // ===== INIT DRAWER =====
        toolbar = findViewById(R.id.toolbar_info);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            Intent intent = null;
            switch(item.getItemId()) {
                case R.id.nav_home:
                    intent = new Intent(MyInformation.this, MainActivity.class);
                    break;
                case R.id.nav_help:
                    intent = new Intent(MyInformation.this, Help_Games.class);
                    break;
                case R.id.nav_user:
                    intent = new Intent(MyInformation.this, UserGameParties.class);
                    break;
                case R.id.nav_game:
                    intent = new Intent(MyInformation.this, MyGame.class);
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            if(intent != null) startActivity(intent);
            return true;
        });

        // ===== INIT FEED =====
        recyclerView = findViewById(R.id.infoRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InfoAdapter(this, list);
        recyclerView.setAdapter(adapter);

        lottieLoading = findViewById(R.id.lottieLoading);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        fetchFeed();

        swipeRefresh.setOnRefreshListener(this::fetchFeed);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                if(dy > 0 && !isLoading) {
                    int visible = layoutManager.getChildCount();
                    int total = layoutManager.getItemCount();
                    int past = layoutManager.findFirstVisibleItemPosition();
                    if((visible + past) >= total) fetchFeed();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    private void fetchFeed() {
        isLoading = true;
        lottieLoading.setVisibility(android.view.View.VISIBLE);
        new FetchFeedTask().execute(UNSPLASH_URL);
    }

    private class FetchFeedTask extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                return new JSONArray(sb.toString());
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray array) {
            lottieLoading.setVisibility(android.view.View.GONE);
            swipeRefresh.setRefreshing(false);
            isLoading = false;

            if(array != null){
                try{
                    for(int i=0;i<array.length();i++){
                        JSONObject obj = array.getJSONObject(i);
                        String description = obj.optString("description");
                        String altDescription = obj.optString("alt_description");
                        String author = obj.getJSONObject("user").optString("name","Anonyme");
                        String image = obj.getJSONObject("urls").getString("regular");

                        if(description == null || description.equals("null") || description.trim().length()<2){
                            if(altDescription!=null && !altDescription.equals("null") && altDescription.trim().length()>=2) description=altDescription;
                            else description = "Toute l’équipe Mally vous salue";
                        }

                        list.add(new InfoItem(description, author, image));
                        adapter.notifyItemInserted(list.size()-1);
                    }
                } catch(Exception e){ e.printStackTrace(); }
            } else {
                Toast.makeText(MyInformation.this,"Impossible de récupérer le feed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
