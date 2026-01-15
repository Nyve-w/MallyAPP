package com.example.mally;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class NewsApiService {
    @GET("top-headlines")
    Call<NewsResponse> getTopHeadlines(@Query("country") String country, @Query("category") String category, @Query("apiKey") String apiKey);
}
