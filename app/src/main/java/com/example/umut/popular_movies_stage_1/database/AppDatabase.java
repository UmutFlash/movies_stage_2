package com.example.umut.popular_movies_stage_1.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;


@Database(entities = {FavoritesEntry.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {


    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final String DATABASE_NAME ="favorites";
    private static final Object LOG = new Object();

    private static AppDatabase INSTANCE;

    public abstract FavoritesDao favoritesDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, AppDatabase.DATABASE_NAME)
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .build();
        }
        Log.d(LOG_TAG,"Getting the database instance");
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
