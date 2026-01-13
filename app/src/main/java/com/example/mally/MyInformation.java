package com.example.mally;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.MaterialToolbar;

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

    RecyclerView recyclerView;
    InfoAdapter adapter;
    ArrayList<InfoItem> list = new ArrayList<>();
    LottieAnimationView lottieLoading;
    SwipeRefreshLayout swipeRefresh;
    LinearLayoutManager layoutManager;

    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_info);
        toolbar.setNavigationOnClickListener(view ->
                Toast.makeText(this, "Menu cliqué", Toast.LENGTH_SHORT).show()
        );

        recyclerView = findViewById(R.id.infoRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InfoAdapter(this, list);
        recyclerView.setAdapter(adapter);

        lottieLoading = findViewById(R.id.lottieLoading);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        fetchFeed();

        swipeRefresh.setOnRefreshListener(() -> fetchFeed());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                if (dy > 0 && !isLoading) {
                    int visible = layoutManager.getChildCount();
                    int total = layoutManager.getItemCount();
                    int past = layoutManager.findFirstVisibleItemPosition();

                    if ((visible + past) >= total) {
                        fetchFeed(); // reload 10 nouvelles images
                    }
                }
            }
        });
    }

    private void fetchFeed() {
        isLoading = true;
        lottieLoading.setVisibility(View.VISIBLE);
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
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                return new JSONArray(sb.toString());

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray array) {
            lottieLoading.setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);
            isLoading = false;

            if (array != null) {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String title = obj.optString("description", "Sans description");
                        String content = obj.getJSONObject("user").optString("name", "Anonyme");
                        String image = obj.getJSONObject("urls").getString("regular");
                        list.add(new InfoItem(title, content, image));
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MyInformation.this, "Impossible de récupérer le feed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
