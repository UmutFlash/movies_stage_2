package com.example.umut.popular_movies_stage_1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.example.umut.popular_movies_stage_1.database.AppDatabase;
import com.example.umut.popular_movies_stage_1.database.FavoritesEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailsViewActivity extends AppCompatActivity implements FetchTrailers.CallbackPostExecute, FetchReviews.CallbackPostExecute {

    private RecyclerView mTrailerRecyclerView;
    private RecyclerView mReviewRecyclerView;
    private TextView mTrailerHeader;
    private TextView mReviewHeader;
    private Button mFavoriteButton;
    private String mId;
    private String mOriginalTitle;
    private String mPosterPath;
    private String mOverview;
    private String mVoteAverage;
    private String mReleaseDate;

    private AppDatabase mDb;
    private boolean isFavorite = false;

    private static final String APIKEY = "b4f4470bb291ef6088ecd080afe68221";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);

        mTrailerRecyclerView = findViewById(R.id.recyclerview_trailers);
        mReviewRecyclerView = findViewById(R.id.recyclerview_reviews);
        mFavoriteButton = findViewById(R.id.btn_favorite);
        mReviewHeader = findViewById(R.id.tv_review_header);
        mTrailerHeader = findViewById(R.id.tv_trailer_header);


        TextView tvOriginalTitle = (TextView) findViewById(R.id.tv_original_title);
        ImageView ivPoster = (ImageView) findViewById(R.id.iv_poster);
        TextView tvOverView = (TextView) findViewById(R.id.tv_overview);
        TextView tvVoteAverage = (TextView) findViewById(R.id.tv_vote_average);
        TextView tvReleaseDate = (TextView) findViewById(R.id.tv_release_date);

        Intent intent = getIntent();

        mDb = AppDatabase.getAppDatabase(getApplicationContext());


        Movie movie = intent.getParcelableExtra(MainActivityFragment.MOVIE);
        mOriginalTitle = movie.getmOriginalTitle();
        tvOriginalTitle.setText(mOriginalTitle);
        mPosterPath = movie.getmPosterPath();
        Picasso.get()
                .load(mPosterPath)
                .resize(185,
                        278)
                .placeholder(R.drawable.ic_picture)
                .into(ivPoster);

        mOverview = movie.getmOverview();
        tvOverView.setText(movie.getmOverview());
        mVoteAverage = movie.getmVoteAverage().toString();
        tvVoteAverage.setText(getString(R.string.average, mVoteAverage));
        mReleaseDate = movie.getmReleaseDate();
        tvReleaseDate.setText(mReleaseDate);
        mId = movie.getmId();

        getFavorites(mId);
        getTrailerANDReviewFromTMDb(mId);
        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {

                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.favoritesDao().deleteFavoritesEntry(mId);
                        }
                    });
                    isFavorite = false;
                    Toast.makeText(getBaseContext(),
                            "Removed from Favorites!", Toast.LENGTH_SHORT).show();
                    mFavoriteButton.setText(getString(R.string.make_as_favorite));
                } else {

                    final FavoritesEntry favoritesEntry = new FavoritesEntry(Integer.parseInt(mId), mOriginalTitle, mPosterPath, mOverview, Double.parseDouble(mVoteAverage), mReleaseDate, true);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.favoritesDao().insert(favoritesEntry);
                            finish();
                        }
                    });
                    isFavorite = true;
                    mFavoriteButton.setText(getString(R.string.remove_favorites));
                    Toast.makeText(getBaseContext(),
                            "Add to Favorites!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        handleFavoriteBtn();
    }

    private void handleFavoriteBtn() {
        if (isFavorite) {
            mFavoriteButton.setText(getString(R.string.remove_favorites));
        } else {
            mFavoriteButton.setText(getString(R.string.make_as_favorite));
        }
    }

    private void getTrailerANDReviewFromTMDb(String mId) {
        if (isNetworkAvailable()) {
            FetchTrailers trailersTask = new FetchTrailers(APIKEY, this);
            trailersTask.execute(mId);
            FetchReviews reviewsTask = new FetchReviews(APIKEY, this);
            reviewsTask.execute(mId);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.network_error));
            alertDialog.setMessage(getString(R.string.no_network));
            alertDialog.show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onFetchTrailerTask(Trailer[] trailers) {
        if (trailers != null || trailers.length != 0) {
            mTrailerHeader.setText(getString(R.string.trailer));
            mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mTrailerRecyclerView.setAdapter(new TrailerRecyclerViewAdapter(this, trailers));
        } else {
            mTrailerHeader.setText(getString(R.string.no_trailer_avaible));
        }
    }

    @Override
    public void onFetchReviewsTask(Review[] reviews) {
        if (reviews.length != 0) {
            mReviewHeader.setText(R.string.reviews);
            mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mReviewRecyclerView.setAdapter(new ReviewRecyclerViewAdapter(this, reviews));
        } else {
            mReviewHeader.setText(getString(R.string.no_reviews_avaible));
        }
    }

    private void getFavorites(final String s) {
        final int mId = Integer.parseInt(s);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<FavoritesEntry> favoritesMovies = mDb.favoritesDao().loadAllFavorites();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!favoritesMovies.isEmpty()) {
                            isFavorite = false;
                            //int resultsLength = favoritesMovies.size();
                           // for (int i = 0; i < resultsLength; i++) {
                            for(FavoritesEntry favorites : favoritesMovies){
                                    if (favorites.getMovieID()== mId) {
                                        isFavorite = true;
                                    }
                            }
                            handleFavoriteBtn();
                        }
                    }
                });
            }
        });
    }
}

