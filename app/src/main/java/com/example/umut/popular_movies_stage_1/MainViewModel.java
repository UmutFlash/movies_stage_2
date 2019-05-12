package com.example.umut.popular_movies_stage_1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.umut.popular_movies_stage_1.database.AppDatabase;
import com.example.umut.popular_movies_stage_1.database.FavoritesEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();
    private LiveData<List<FavoritesEntry>> favoritesMovies;

    public MainViewModel(@NonNull Application application) {
        super(application);

        AppDatabase appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        favoritesMovies = appDatabase.favoritesDao().loadAllFavoritesMovies();

    }
    public LiveData<List<FavoritesEntry>> getFavorites() {
        return favoritesMovies;
    }
}
