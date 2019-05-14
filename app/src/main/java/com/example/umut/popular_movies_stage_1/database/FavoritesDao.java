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

    @Query("SELECT * FROM favorites")
    LiveData<List<FavoritesEntry>>loadAllFavoritesMovies();

    @Query("SELECT * FROM favorites")
    List<FavoritesEntry> loadAllFavorites();


    @Query("DELETE FROM favorites WHERE movieID = :id")
    abstract void deleteFavoritesEntry(String id);

    @Query("SELECT * FROM favorites WHERE movieId = :id")
    FavoritesEntry getFavorite(int id);

    @Query("SELECT isFavorite FROM favorites WHERE movieID = :movieId")
    boolean isFavorite(int movieId);

    @Query("SELECT COUNT(isFavorite) FROM favorites WHERE movieID = :movieId")
    int getCount(int movieId);

    @Insert
    void insert(FavoritesEntry favorites);

    @Delete
    void delete(FavoritesEntry favorites);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(FavoritesEntry favorites);

}
