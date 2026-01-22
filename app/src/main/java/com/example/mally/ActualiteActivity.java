package com.example.mally;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActualiteActivity extends AppCompatActivity {
    TextView txtTitreActualites, txtMessage, tvEmpty, tvError;
    Spinner spinnerCategories;
    RecyclerView recyclerActualites;
    ProgressBar progressBar;
    SearchView searchView;
    private String categorieActuelle="general";
    private String rechercheActuelle = null;
    SwipeRefreshLayout swipeRefresh;
    Button btnRetry;
    LinearLayout layoutError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualite);

        txtTitreActualites=findViewById(R.id.txtTitreActualites);
        spinnerCategories=findViewById(R.id.spinnerCategories);
        recyclerActualites=findViewById(R.id.recyclerActualites);
        progressBar=findViewById(R.id.progressBar);
        txtMessage=findViewById(R.id.txtMessage);
        searchView=findViewById(R.id.searchView);
        swipeRefresh=findViewById(R.id.swipeRefresh);
        layoutError=findViewById(R.id.layoutError);
        tvEmpty=findViewById(R.id.tvEmpty);
        tvError=findViewById(R.id.tvError);
        btnRetry=findViewById(R.id.btnRetry);

        swipeRefresh.setOnRefreshListener(() ->{
            chargerActualites(categorieActuelle, rechercheActuelle);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query){
                rechercheActuelle=query;
                chargerActualites(categorieActuelle, rechercheActuelle);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(() -> {
            rechercheActuelle=null;
            chargerActualites(categorieActuelle, null);
            return false;
        });

        String[] categoriesVisibles={
                "Générale", "Technologie", "Sport", "Santé", "Business", "Divertissement"
        };

        ArrayAdapter<String> adapterSpinner= new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(adapterSpinner);

        recyclerActualites.setLayoutManager(new LinearLayoutManager(this));

        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choix=parent.getItemAtPosition(position).toString();
                categorieActuelle=mapCategorie(choix);

                rechercheActuelle=null;
                chargerActualites(categorieActuelle, rechercheActuelle);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){
                
            }
        });
    }
    private String mapCategorie(String choix){
        switch (choix){
            case "Technologie": return "technology";
            case "Sport": return "sports";
            case "Santé": return "health";
            case "Business": return "business";
            case "Divertissement": return "entertainment";
            default: return "general";
        }
    }
    private void afficherChargement() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerActualites.setVisibility(View.GONE);
        txtMessage.setVisibility(View.GONE);
    }
    private void afficherListe() {
        progressBar.setVisibility(View.GONE);
        recyclerActualites.setVisibility(View.VISIBLE);
        txtMessage.setVisibility(View.GONE);
    }
    private void afficherMessage(String message) {
        progressBar.setVisibility(View.GONE);
        recyclerActualites.setVisibility(View.GONE);
        txtMessage.setVisibility(View.VISIBLE);
        txtMessage.setText(message);
    }
    private void chargerActualites(String categorie, String recherche){
        progressBar.setVisibility(View.VISIBLE);
        txtMessage.setVisibility(View.GONE);
        recyclerActualites.setVisibility(View.GONE);
        afficherChargement();

        NewsApiService apiService=RetrofitClient.getRetrofit().create(NewsApiService.class);

        Call<NewsResponse> call=apiService.getTopHeadlines("fr","categories","recherche" ,"273d48ce29bd4323985dd8c41a61bce0");

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()&&response.body()!=null&&response.body().getArticles()!=null&&!response.body().getArticles().isEmpty()) {
                    afficherMessage("Aucun résultat trouvé");
                } else{
                    afficherListe();
                    recyclerActualites.setAdapter(new ActualiteAdapter(response.body().getArticles()));
                }
            }
            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
               afficherMessage("Choisissez une catégorie ou lancez une recherche");
            }
        });
    }
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerActualites.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
    }
    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        recyclerActualites.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }
    private void showEmpty() {
        progressBar.setVisibility(View.GONE);
        recyclerActualites.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
    }
    private void showContent() {
        progressBar.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        recyclerActualites.setVisibility(View.VISIBLE);
    }
}