package com.example.umut.popular_movies_stage_1.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.net.LinkAddress;

import com.example.umut.popular_movies_stage_1.database.FavoritesEntry;

import java.util.List;


@Dao
public interface FavoritesDao{

    @Query("SELECT * FROM favorites ORDER BY id")
    List<FavoritesEntry>loadAllFavoritesMovies();


    @Query("SELECT * FROM favorites WHERE movieID = :id LIMIT 1")
    FavoritesEntry getFavoritesEntry(String id);

    @Insert
    void insert(FavoritesEntry favorites);

    @Delete
    void delete(FavoritesEntry favorites);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(FavoritesEntry favorites);

}
