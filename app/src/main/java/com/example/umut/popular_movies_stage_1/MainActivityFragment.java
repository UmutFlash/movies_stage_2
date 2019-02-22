package com.example.umut.popular_movies_stage_1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivityFragment extends Fragment implements FetchMovies.CallbackPostExecute {


    public static final String MOVIE = "MOVIE";
    private static final String APIKEY = "";
    private static final String TOP_RATED = "top_rated";
    private static final String POPULARITY = "popular";

    private GridView mGridView;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = rootView.findViewById(R.id.gridview);



        if (savedInstanceState == null) {
            getMoviesFromTMDb(POPULARITY);
        } else {
            Parcelable[] parcelableMovies = savedInstanceState.getParcelableArray(MOVIE);
            if (parcelableMovies != null) {
                Movie[] movieCollection = new Movie[parcelableMovies.length];
                for (int i = 0; i < parcelableMovies.length; i++) {
                    movieCollection[i] = (Movie) parcelableMovies[i];
                }
                mGridView.setAdapter(new MoviesAdapter(getActivity(), movieCollection));
            }
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Movie movie = (Movie) parent.getItemAtPosition(position);

                Intent intent = new Intent(getContext(), DetailsViewActivity.class);
                intent.putExtra(MOVIE, movie);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void getMoviesFromTMDb(String sortMethod) {

        if (isNetworkAvailable()) {
            FetchMovies movieTask = new FetchMovies(APIKEY, this);
            movieTask.execute(sortMethod);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle(getString(R.string.network_error));
            alertDialog.setMessage(getString(R.string.no_network));
            alertDialog.show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Movie[] movies = new Movie[mGridView.getCount()];
        for (int i = 0; i < mGridView.getCount(); i++) {
            movies[i] = (Movie) mGridView.getItemAtPosition(i);
        }
        outState.putParcelableArray(MOVIE, movies);
    }

    @Override
    public void onFetchMoviesTask(Movie[] movies) {

        mGridView.setAdapter(new MoviesAdapter(getActivity(), movies));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_avarage) {
            getMoviesFromTMDb(TOP_RATED);
            return true;
        }
        if (id == R.id.action_pupolar) {
            getMoviesFromTMDb(POPULARITY);
            return true;
        }
        if (id == R.id.action_favorites) {
            getFavorites();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getFavorites() {
        Uri uri = FavoritesContentProvider.CONTENT_URI;
        Cursor cursor = getActivity().getContentResolver()
                .query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) { //If there are existing favorited items
            int resultsLength = cursor.getCount();
            Movie[] movies = new Movie[resultsLength];
            for (int i = 0; i < resultsLength; i++) {
                movies[i] = new Movie();
                String title = cursor
                        .getString(cursor.getColumnIndex(FavoritesContentProvider.COLUMN_ORIGINAL_TITLE));
                movies[i].setmOriginalTitle(title);

                String id = cursor
                        .getString(cursor.getColumnIndex(FavoritesContentProvider.COLUMN_MOVIE_ID));
                movies[i].setmId(id);


                movies[i].setmOverview(cursor
                        .getString(cursor.getColumnIndex(FavoritesContentProvider.COLUMN_OVERVIEW)));

                movies[i].setmPosterPath(cursor
                        .getString(cursor.getColumnIndex(FavoritesContentProvider.COLUMN_POSTER_PATH)));

                movies[i].setmVoteAverage(cursor
                        .getDouble(cursor.getColumnIndex(FavoritesContentProvider.COLUMN_VOTE_AVERAGE)));

                movies[i].setmReleaseDate(cursor
                        .getString(cursor.getColumnIndex(FavoritesContentProvider.COLUMN_RELEASE_DATE)));
                cursor.moveToNext();
            }

            mGridView.setAdapter(new MoviesAdapter(getActivity(), movies));
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle(getString(R.string.favorites));
            alertDialog.setMessage(getString(R.string.no_favorites));
            alertDialog.show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}