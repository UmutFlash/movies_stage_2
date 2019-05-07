package com.example.umut.popular_movies_stage_1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


@Entity(tableName = "favorites")
public interface FavoritesEntry{

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "")
    private String movieID;

    @ColumnInfo(name = "")
    private String OriginalTitlr;

    @ColumnInfo(name = "")
    private String overview;

    @ColumnInfo(name = "")
    private boolean posterPath;

    @ColumnInfo(name = "")
    private boolean voteAverage;

    @ColumnInfo(name = "")
    private boolean releaseDate;
}
