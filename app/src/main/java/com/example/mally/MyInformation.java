package com.example.mally;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_info);
        toolbar.setNavigationOnClickListener(view ->
                Toast.makeText(this, "Menu cliqué", Toast.LENGTH_SHORT).show()
        );

        // RecyclerView + Adapter
        recyclerView = findViewById(R.id.infoRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InfoAdapter(this, list);
        recyclerView.setAdapter(adapter);

        // SwipeRefresh + Lottie Loader
        lottieLoading = findViewById(R.id.lottieLoading);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        // Premier fetch
        fetchFeed();

        // Pull-to-refresh
        swipeRefresh.setOnRefreshListener(this::fetchFeed);

        // Scroll infini
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                if (dy > 0 && !isLoading) {
                    int visible = layoutManager.getChildCount();
                    int total = layoutManager.getItemCount();
                    int past = layoutManager.findFirstVisibleItemPosition();

                    if ((visible + past) >= total) {
                        fetchFeed();
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

    // AsyncTask pour récupérer les images + infos
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
                        String description = obj.optString("description");
                        String altDescription = obj.optString("alt_description");
                        String author = obj.getJSONObject("user").optString("name", "Anonyme");
                        String image = obj.getJSONObject("urls").getString("regular");

                        // Fallback texte si null ou trop court
                        if (description == null || description.equals("null") || description.trim().length() < 2) {
                            if (altDescription != null && !altDescription.equals("null") && altDescription.trim().length() >= 2) {
                                description = altDescription;
                            } else {
                                description = "Toute l’équipe Mally vous salue";
                            }
                        }

                        final String textToTranslate = description;
                        final String finalAuthor = author;

                        // Traduction via LibreTranslate
                        translateWithLibre(textToTranslate, translatedText -> {
                            InfoItem item = new InfoItem(translatedText, finalAuthor, image);
                            list.add(item);
                            adapter.notifyItemInserted(list.size() - 1);
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MyInformation.this, "Impossible de récupérer le feed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Traduction en ligne via LibreTranslate
    private void translateWithLibre(String text, InfoItemCallback callback) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    URL url = new URL("https://libretranslate.com/translate");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setDoOutput(true);

                    String jsonInput = "{\"q\":\"" + escapeJson(text) + "\",\"source\":\"en\",\"target\":\"fr\"}";
                    conn.getOutputStream().write(jsonInput.getBytes("utf-8"));

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    reader.close();

                    JSONObject obj = new JSONObject(sb.toString());
                    return obj.getString("translatedText");
                } catch (Exception e) {
                    e.printStackTrace();
                    return text; // fallback
                }
            }

            @Override
            protected void onPostExecute(String translated) {
                callback.onTranslated(translated);
            }
        }.execute();
    }

    // Échapper le texte pour JSON (protection guillemets, accents, retour ligne)
    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    interface InfoItemCallback {
        void onTranslated(String translatedText);
    }
}
