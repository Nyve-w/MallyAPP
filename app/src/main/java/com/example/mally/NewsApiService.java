package com.example.mally;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {
    @GET("v2/top-headlines")
    Call<NewsResponse> getTopHeadlines(@Query("country") String country, @Query("category") String category,@Query("q")String motCle, @Query("apiKey") String apiKey);
}
