package com.example.mally;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("top-headlines")
    Call<NewsResponse> getActualites(@Query("country") String pays, @Query("apiKey") String apiKey);

    @GET("everything")
    Call<NewsResponse> rechercherActualites(@Query("q") String motCle, @Query("language") String langue, @Query("apiKey") String apiKey);

    @GET("top-headlines")
    Call<NewsResponse> getActualitesParCategorie(@Query("country") String pays, @Query("category") String categorie, @Query("apiKey") String apiKey);
}
