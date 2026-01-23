package com.example.mally;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserGameParties extends AppCompatActivity {

    // 1. Déclaration des variables liées aux IDs du XML
    private RecyclerView recyclerScores;
    private TextView textCurrentGameTitle;
    private Button btnSolitaire, btnHangman, btnSudoku;

    // Outil pour faire les requêtes internet
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_game_parties);

        // 2. Liaison (Binding)
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerScores = findViewById(R.id.recyclerScores);
        textCurrentGameTitle = findViewById(R.id.textCurrentGameTitle);
        btnSolitaire = findViewById(R.id.btnSolitaire);
        btnHangman = findViewById(R.id.btnHangman);
        btnSudoku = findViewById(R.id.btnSudoku);

        // Configuration de la liste (Indispensable pour un RecyclerView)
        recyclerScores.setLayoutManager(new LinearLayoutManager(this));

        // 3. Gestion des Clics
        btnSolitaire.setOnClickListener(v -> loadGameData("table_solitaire", "Solitaire"));
        btnHangman.setOnClickListener(v -> loadGameData("table_hangman", "Pendu"));
        btnSudoku.setOnClickListener(v -> loadGameData("table_sudoku", "Sudoku"));

        // Charger le Solitaire par défaut au lancement
        loadGameData("table_solitaire", "Solitaire");
    }

    // Fonction principale qui télécharge et affiche
    private void loadGameData(String tableName, String gameTitle) {
        // Mise à jour du titre visuel
        textCurrentGameTitle.setText("Chargement " + gameTitle + "...");

        // ATTENTION : Choisis ton URL selon ton cas (voir explications infra partie 1)
        // Cas A (Émulateur) : "http://10.0.2.2/mally_game/get_leaderboard.php?table=" + tableName
        // Cas B (Téléphone USB + adb reverse) : "http://localhost/mally_game/get_leaderboard.php?table=" + tableName

        String url = "http://localhost/mally_game/get_leaderboard.php?table=" + tableName;

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // En cas d'erreur (pas d'internet, serveur éteint)
                runOnUiThread(() -> {
                    textCurrentGameTitle.setText("Erreur de connexion");
                    Toast.makeText(UserGameParties.this, "Serveur inaccessible", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();

                    // On prépare une liste vide
                    List<PlayerScore> scoreList = new ArrayList<>();

                    try {
                        // On transforme le texte JSON reçu en tableau utilisable
                        JSONArray jsonArray = new JSONArray(jsonResponse);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            // On crée l'objet PlayerScore
                            scoreList.add(new PlayerScore(
                                    obj.getString("username"),
                                    obj.getInt("score")
                            ));
                        }

                        // IMPORTANT : On revient sur le fil principal pour toucher à l'écran
                        runOnUiThread(() -> {
                            textCurrentGameTitle.setText("Meilleurs scores : " + gameTitle);
                            // On donne la liste à l'Adapter (le moteur d'affichage)
                            LeaderboardAdapter adapter = new LeaderboardAdapter(scoreList);
                            recyclerScores.setAdapter(adapter);
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}