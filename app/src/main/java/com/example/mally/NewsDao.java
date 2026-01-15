package com.example.mally;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<NewsItemEntity> news);

    @Query("SELECT * FROM news ORDER BY publishedAt DESC")
    List<NewsItemEntity> getAllNews();

    @Query("DELETE FROM news")
    void deleteAll();
}
