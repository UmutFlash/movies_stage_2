package com.example.umut.popular_movies_stage_1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
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

public class MainActivityFragment extends Fragment implements FetchMovies.CallbackPostExecute {


    public static final String MOVIE = "MOVIE";
    private static final String APIKEY = "b4f4470bb291ef6088ecd080afe68221";
    private static final String TOP_RATED = "top_rated";
    private static final String POPULARITY = "popular";

    private GridView mGridView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridview);

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

        if(isNetworkAvailable()) {
            FetchMovies movieTask = new FetchMovies(APIKEY, this);
            movieTask.execute(sortMethod);
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle(getString(R.string.network_error)); alertDialog.setMessage(getString(R.string.no_network));
            alertDialog.show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Movie[] movies = new Movie[mGridView.getCount()];
        for(int i = 0; i < mGridView.getCount(); i++){
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

        return super.onOptionsItemSelected(item);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}