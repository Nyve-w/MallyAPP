package com.example.mally;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ActualiteDao {
    @Query("SELECT * FROM actualites")
    List<ActualiteEntity> getAll();

    @Insert
    void InsertAll(List<ActualiteEntity> actualites);

    @Query("DELETE FROM actualites")
    void deleteAll();

    @Query("SELECT * FROM actualites WHERE title LIKE '%' || :mot || '%' OR description LIKE '%' || :mot || '%'")
    List<ActualiteEntity> search(String mot);
}
