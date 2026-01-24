package com.example.mally;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActualiteActivity extends AppCompatActivity {

    Spinner spinnerCategories;
    RecyclerView recyclerActualites;
    ProgressBar progressBar;
    TextView txtErreur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualite);

        spinnerCategories = findViewById(R.id.spinnerCategories);
        recyclerActualites = findViewById(R.id.recyclerActualites);
        progressBar = findViewById(R.id.progressBar);
        txtErreur = findViewById(R.id.txtErreur);

        recyclerActualites.setLayoutManager(new LinearLayoutManager(this));

        String[] categories = {
                "Générale", "Technologie", "Sport", "Santé", "Business", "Divertissement"
        };

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);

        spinnerCategories.setAdapter(spinnerAdapter);

        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categorieApi = mapCategorie(categories[position]);
                chargerActualites(categorieApi);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private String mapCategorie(String choix) {
        switch (choix) {
            case "Technologie": return "technology";
            case "Sport": return "sports";
            case "Santé": return "health";
            case "Business": return "business";
            case "Divertissement": return "entertainment";
            default: return "general";
        }
    }

    private void chargerActualites(String categorie) {

        progressBar.setVisibility(View.VISIBLE);
        txtErreur.setVisibility(View.GONE);

        NewsApiService api =
                RetrofitClient.getRetrofit().create(NewsApiService.class);

        Call<NewsResponse> call =
                api.getTopHeadlines("fr", categorie, "273d48ce29bd4323985dd8c41a61bce0");

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Actualite> actualites = response.body().getArticles();
                    recyclerActualites.setAdapter(new ActualiteAdapter(actualites));
                } else {
                    txtErreur.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                txtErreur.setVisibility(View.VISIBLE);
            }
        });
    }
}
