package com.example.umut.popular_movies_stage_1;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavoritesContentProvider extends ContentProvider {

    public static final String PREFIX = "content://";
    public static final String AUTHORITY = "com.example.umut.popular_movies_stage_1";
    public static final Uri URI_BASE = Uri.parse(PREFIX + AUTHORITY);
    public static final Uri CONTENT_URI = URI_BASE.buildUpon()
            .appendPath(FavoritesDbHelper.TABLE_NAME)
            .build();

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final String COLUMN_ORIGINAL_TITLE = "original_title";
    public static final String COLUMN_POSTER_PATH = "poster_path";
    public static final String COLUMN_OVERVIEW = "overview";
    public static final String COLUMN_VOTE_AVERAGE = "vote_average";
    public static final String COLUMN_RELEASE_DATE = "release_date";


    private FavoritesDbHelper mFavoritesDbHelper;

    @Override
    public boolean onCreate() {
        mFavoritesDbHelper = new FavoritesDbHelper(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor cursor;
        cursor = mFavoritesDbHelper.getReadableDatabase().query(
                mFavoritesDbHelper.TABLE_NAME,
                strings,
                s,
                strings1,
                null,
                null,
                s1
        );
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase database = mFavoritesDbHelper.getWritableDatabase();
        long id = database.insert(FavoritesDbHelper.TABLE_NAME, null, contentValues);
        if (id > 0) {
            Uri result = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(result, null);
            return result;
        } else {
            return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase database = mFavoritesDbHelper.getWritableDatabase();
        if (s == null) {
            s = "1";
        }
        int deleted = database.delete(mFavoritesDbHelper.TABLE_NAME, s, strings);
        getContext().getContentResolver().notifyChange(uri, null);
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
