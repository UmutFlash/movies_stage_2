package com.example.umut.popular_movies_stage_1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favorites.db";
    public static final int VERSION = 1;
    public static final String TABLE_NAME = "favorites";


    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITES_TABLE =
                "CREATE TABLE " + TABLE_NAME
                        + " ("
                        + FavoritesContentProvider.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + FavoritesContentProvider.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                        + FavoritesContentProvider.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, "
                        + FavoritesContentProvider.COLUMN_OVERVIEW + " TEXT, "
                        + FavoritesContentProvider.COLUMN_POSTER_PATH + " TEXT, "
                        + FavoritesContentProvider.COLUMN_VOTE_AVERAGE + " REAL, "
                        + FavoritesContentProvider.COLUMN_RELEASE_DATE + " TEXT);";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
