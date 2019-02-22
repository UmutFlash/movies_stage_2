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

public class FetchTrailers extends AsyncTask<String, Void, Trailer[]> {

    private final static String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private final static String API_KEY_PARAM = "api_key";
    private final String LOG_TAG = FetchTrailers.class.getSimpleName();
    private final String mApiKey;
    private static final String VIDEOS = "videos";


    private CallbackPostExecute mCallbackPostExecute;

    FetchTrailers(String apiKey, CallbackPostExecute callbackPostExecute) {
        super();
        mApiKey = apiKey;
        this.mCallbackPostExecute = callbackPostExecute;
    }

    @Override
    protected Trailer[] doInBackground(String... params) {

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
            return getTrailerFromJson(moviesJsonString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private Trailer[] getTrailerFromJson(String moviesJsonString) throws JSONException {

        JSONObject trailerJson = new JSONObject(moviesJsonString);
        JSONArray resultsArray = trailerJson.getJSONArray("results");

        Trailer[] trailers = new Trailer[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {

            trailers[i] = new Trailer();
            JSONObject movieData = resultsArray.getJSONObject(i);

            trailers[i].setKey(movieData.getString("key"));
            trailers[i].setName(movieData.getString("name"));
        }
        return trailers;
    }

    private URL getApiUrl(String[] parameters) throws MalformedURLException {

        Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(parameters[0])
                .appendPath(VIDEOS)
                .appendQueryParameter(API_KEY_PARAM, mApiKey)
                .build();

        return new URL(builtUri.toString());
    }

    @Override
    protected void onPostExecute(Trailer[] trailers) {
        super.onPostExecute(trailers);
        mCallbackPostExecute.onFetchTrailerTask(trailers);
    }

    interface CallbackPostExecute {
        void onFetchTrailerTask(Trailer[] trailers);
    }

}
