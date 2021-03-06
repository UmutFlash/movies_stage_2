package com.example.umut.popular_movies_stage_1;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.umut.popular_movies_stage_1.database.FavoritesEntry;

import java.util.List;

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

        getFavorites();
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
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getFavorites().observe(this, new Observer<List<FavoritesEntry>>() {
            @Override
            public void onChanged(@Nullable List<FavoritesEntry> favoritesEntries) {
                Movie[] favorites = parseMovieArray(favoritesEntries);
                if(favorites.length != 0){
                    mGridView.setAdapter(new MoviesAdapter(getActivity(), favorites));
                }else{
                    mGridView.setAdapter(null);
                }
            }
        });
    }

    private Movie[] parseMovieArray(List<FavoritesEntry> favoritesMovies ){
        int resultsLength = favoritesMovies.size();
        Movie[] movies = new Movie[resultsLength];
        for (int i = 0; i < resultsLength; i++) {
            movies[i] = new Movie();
            String title = favoritesMovies.get(i).getOriginalTitle();
            movies[i].setmOriginalTitle(title);
            int id = favoritesMovies.get(i).getMovieID();
            movies[i].setmId(Integer.toString(id));
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