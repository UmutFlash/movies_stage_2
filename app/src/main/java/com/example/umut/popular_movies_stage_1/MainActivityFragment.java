package com.example.umut.popular_movies_stage_1;

import android.arch.lifecycle.LiveData;
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

import com.example.umut.popular_movies_stage_1.database.AppDatabase;
import com.example.umut.popular_movies_stage_1.database.FavoritesEntry;

import java.util.List;

public class MainActivityFragment extends Fragment implements FetchMovies.CallbackPostExecute {


    public static final String MOVIE = "MOVIE";
    private static final String APIKEY = "b4f4470bb291ef6088ecd080afe68221";
    private static final String TOP_RATED = "top_rated";
    private static final String POPULARITY = "popular";

    private AppDatabase mDb;
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


        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<FavoritesEntry> favoritesMovies = mDb.favoritesDao().loadAllFavoritesMovies();
                // We will be able to simplify this once we learn more
                // about Android Architecture Components
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mAdapter.setTasks(tasks);

                        mGridView.setAdapter(new MoviesAdapter(getActivity(), parseMovieArray(favoritesMovies)));
                    }
                });
            }
        });

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

            //mGridView.setAdapter(new MoviesAdapter(getActivity(), movies));
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle(getString(R.string.favorites));
            alertDialog.setMessage(getString(R.string.no_favorites));
            alertDialog.show();
        }
    }

    private Movie[] parseMovieArray(List<FavoritesEntry> favoritesMovies ){

        int resultsLength = favoritesMovies.size();
        Movie[] movies = new Movie[resultsLength];
        for (int i = 0; i < resultsLength; i++) {
            movies[i] = new Movie();
            String title = favoritesMovies.get(i).getOriginalTitle();
            movies[i].setmOriginalTitle(title);

            String id = favoritesMovies.get(i).getMovieID();
            movies[i].setmId(id);


            movies[i].setmOverview(favoritesMovies.get(i).getOverview());

            movies[i].setmPosterPath(favoritesMovies.get(i).getPosterPath());

            movies[i].setmVoteAverage(favoritesMovies.get(i).getVoteAverage());

            movies[i].setmReleaseDate(favoritesMovies.get(i).getReleaseDate());
        }
       return movies;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}