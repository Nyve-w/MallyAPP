package com.example.mally;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mally.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActualiteActivity extends AppCompatActivity {
    // Déclaration des éléments
    RecyclerView recyclerActualites;
    ActualiteAdapter adapter;
    List<Actualite> actualites;
    EditText edtRecherche;
    ProgressBar progressBar;
    TextView txtErreur;
     SwipeRefreshLayout swipeRefresh;
     Spinner spinnerCategories;
     private enum ModeChargement {
         ACCUEIL, RECHERCHE, CATEGORIE
     }
     private ModeChargement modeActuel = ModeChargement.ACCUEIL;
     private String motRecherche = "";
     private String categorieActuelle = "general";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualite);

        //Initialisation des éléments
        recyclerActualites = findViewById(R.id.recyclerActualites);
        progressBar = findViewById(R.id.progressBar);
        txtErreur = findViewById(R.id.txtErreur);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        edtRecherche = findViewById(R.id.edtRecherche);
        spinnerCategories = findViewById(R.id.spinnerCategories);

        //Liste
        actualites = new ArrayList<>();

        //Creation de l'Adapter
        adapter = new ActualiteAdapter(actualites, new ActualiteAdapter.OnItemClickListener() {
            // Ajout du clic
            @Override
            public void onItemClick(Actualite actualite) {
                Intent intent = new Intent(ActualiteActivity.this, DetailActualiteActivity.class);
                intent.putExtra("titre", actualite.getTitle());
                intent.putExtra("description", actualite.getDescription());
                intent.putExtra("image", actualite.getUrlToImage());
                intent.putExtra("url", actualite.getUrl());
                startActivity(intent);
            }
        });

        //Appel de recyclerActualites
        recyclerActualites.setLayoutManager(new LinearLayoutManager(this));
        recyclerActualites.setAdapter(adapter);

        //Ajout de l'option de recherche local
        edtRecherche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                rechercherLocalement(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable charSquence) {

            }
        });

        //Ajout de la recherche au niveau distant
        edtRecherche.setOnEditorActionListener((v, actionId, event) -> {
            modeActuel = ModeChargement.RECHERCHE;
            motRecherche = edtRecherche.getText().toString();

            chargerActualites();
            return true;
        });

        //Ajout du spinner pour les catégories
        String[] categories = {
                "general", "business", "sports", "technology", "health", "science"
        };

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                modeActuel = ModeChargement.CATEGORIE;
                categorieActuelle = categories[position];

                chargerActualites();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // Ajout du Refresh
        swipeRefresh.setOnRefreshListener(() -> {
            chargerActualites();
        });

        //Chargement initial
        modeActuel = ModeChargement.ACCUEIL;
        chargerActualites();
       }

    //Chargement des actualités
    private void chargerActualites() {

        recyclerActualites.setVisibility(View.GONE);
        txtErreur.setVisibility(View.GONE);

        if (!swipeRefresh.isRefreshing()) {
            progressBar.setVisibility(View.VISIBLE);
        }

        // Appel de l'API
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://newsapi.org/v2/").addConverterFactory(GsonConverterFactory.create()).build();
        NewsApiService apiService = retrofit.create(NewsApiService.class);

        Call<NewsResponse> call;

        if (modeActuel == ModeChargement.RECHERCHE) {
            call = apiService.rechercherActualites(motRecherche, "fr", "273d48ce29bd4323985dd8c41a61bce0");
        } else if (modeActuel == ModeChargement.CATEGORIE) {
            call = apiService.getActualitesParCategorie("fr", categorieActuelle, "273d48ce29bd4323985dd8c41a61bce0");
        } else {
            call = apiService.getActualites("fr", "273d48ce29bd4323985dd8c41a61bce0");
        }

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {

                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null && response.body().getArticles() != null) {
                    actualites.clear();
                    actualites.addAll(response.body().getArticles());
                    adapter.notifyDataSetChanged();
                    recyclerActualites.setVisibility(View.VISIBLE);

                    mettreAJourCache(actualites);

                }else {
                    afficherErreurOuCache();
                    }
                }

                @Override
                public void onFailure(Call<NewsResponse> call, Throwable t) {

                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);

                    afficherErreurOuCache();
                }
            });
        }

        private void rechercherLocalement(String mot) {
            AppDatabase2 db = AppDatabase2.getInstance(ActualiteActivity.this);

            List<ActualiteEntity> resultats = new ArrayList<>();

            if (mot.isEmpty()) {
                resultats = db.actualiteDao().getAll();
            } else {
                actualites.clear();

                for (ActualiteEntity e : resultats) {
                    Actualite a = new Actualite();
                    a.setTitle(e.title);
                    a.setDescription(e.description);
                    a.setUrl(e.url);
                    a.setUrlToImage(e.imageUrl);
                    actualites.add(a);
                }
                adapter.notifyDataSetChanged();
            }
        }

        private void  mettreAJourCache(List<Actualite> actualites) {

        AppDatabase2 db = AppDatabase2.getInstance(this);
        db.actualiteDao().deleteAll();

        List<ActualiteEntity> cache = new ArrayList<>();

        for (Actualite act: actualites) {
            ActualiteEntity entite = new ActualiteEntity();
            entite.title = act.getTitle();
            entite.description = act.getDescription();
            entite.url = act.getUrl();
            entite.imageUrl = act.getUrlToImage();
            cache.add(entite);
        }
        db.actualiteDao().InsertAll(cache);
        }

        private void afficherErreurOuCache() {

            AppDatabase2 db = AppDatabase2.getInstance(this);
            List<ActualiteEntity> cache = db.actualiteDao().getAll();

            if (!cache.isEmpty()) {
                actualites.clear();

                for (ActualiteEntity entity : cache) {
                    Actualite actu = new Actualite();
                    actu.setTitle(entity.title);
                    actu.setDescription(entity.description);
                    actu.setUrl(entity.url);
                    actu.setUrlToImage(entity.imageUrl);
                    actualites.add(actu);
                }

                adapter.notifyDataSetChanged();
                recyclerActualites.setVisibility(View.VISIBLE);
            }else {
                txtErreur.setText("Aucune donnée disponible.");
                txtErreur.setVisibility(View.VISIBLE);
            }
        }

}

