package com.example.umut.popular_movies_stage_1;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FetchMovies extends AsyncTask<String, Void, Movie[]> {

    private final static String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private final static String API_KEY_PARAM = "api_key";
    private static final String HTTPS_IMAGE_TMDB = "https://image.tmdb.org/t/p/w185";
    private final String LOG_TAG = FetchMovies.class.getSimpleName();
    private final String mApiKey;
    private CallbackPostExecute mCallbackPostExecute;

    FetchMovies(String apiKey, CallbackPostExecute callbackPostExecute) {
        super();
        mApiKey = apiKey;
        this.mCallbackPostExecute = callbackPostExecute;
    }

    @Override
    protected Movie[] doInBackground(String... params) {

        String moviesJsonString;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = getApiUrl(params);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();

            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            if (builder.length() == 0) {
                return null;
            }

            moviesJsonString = builder.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "closing reader", e);
                }
            }
        }

        try {
            return getMoviesFromJson(moviesJsonString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private Movie[] getMoviesFromJson(String moviesJsonString) throws JSONException {

        JSONObject moviesJson = new JSONObject(moviesJsonString);
        JSONArray resultsArray = moviesJson.getJSONArray("results");

        Movie[] movies = new Movie[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {

            movies[i] = new Movie();
            JSONObject movieData = resultsArray.getJSONObject(i);

            movies[i].setmOriginalTitle(movieData.getString("title"));
            movies[i].setmPosterPath(HTTPS_IMAGE_TMDB + movieData.getString("poster_path"));
            movies[i].setmOverview(movieData.getString("overview"));
            movies[i].setmVoteAverage(movieData.getDouble("vote_average"));
            movies[i].setmReleaseDate(movieData.getString("release_date"));
            movies[i].setmId(movieData.getString("id"));

        }
        return movies;
    }

    private URL getApiUrl(String[] parameters) throws MalformedURLException {

        Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(parameters[0])
                .appendQueryParameter(API_KEY_PARAM, mApiKey)
                .build();

        return new URL(builtUri.toString());
    }

    @Override
    protected void onPostExecute(Movie[] movies) {

        super.onPostExecute(movies);
        mCallbackPostExecute.onFetchMoviesTask(movies);
    }

    interface CallbackPostExecute {
        void onFetchMoviesTask(Movie[] movies);
    }

}
