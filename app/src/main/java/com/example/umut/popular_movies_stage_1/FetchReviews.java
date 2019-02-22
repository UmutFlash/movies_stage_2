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

public class FetchReviews extends AsyncTask<String, Void, Review[]> {

    private final static String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private final static String API_KEY_PARAM = "api_key";
    private final String LOG_TAG = FetchReviews.class.getSimpleName();
    private final String mApiKey;
    private static final String REVIEWS = "reviews";


    private CallbackPostExecute mCallbackPostExecute;

    FetchReviews(String apiKey, CallbackPostExecute callbackPostExecute) {
        super();
        mApiKey = apiKey;
        this.mCallbackPostExecute = callbackPostExecute;
    }

    @Override
    protected Review[] doInBackground(String... params) {

        String reviewJsonString;
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

            reviewJsonString = builder.toString();
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
            return getReviewFromJson(reviewJsonString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private Review[] getReviewFromJson(String reviewJsonString) throws JSONException {

        JSONObject reviewJson = new JSONObject(reviewJsonString);
        JSONArray resultsArray = reviewJson.getJSONArray("results");

        Review[] reviews = new Review[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {

            reviews[i] = new Review();
            JSONObject movieData = resultsArray.getJSONObject(i);

            reviews[i].setAuthor(movieData.getString("author"));
            reviews[i].setContent(movieData.getString("content"));
        }
        return reviews;
    }

    private URL getApiUrl(String[] parameters) throws MalformedURLException {

        Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(parameters[0])
                .appendPath(REVIEWS)
                .appendQueryParameter(API_KEY_PARAM, mApiKey)
                .build();

        return new URL(builtUri.toString());
    }

    @Override
    protected void onPostExecute(Review[] reviews) {
        super.onPostExecute(reviews);
        mCallbackPostExecute.onFetchReviewsTask(reviews);
    }

    interface CallbackPostExecute {
        void onFetchReviewsTask(Review[] reviews);
    }

}
