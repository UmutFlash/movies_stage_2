package com.example.umut.popular_movies_stage_1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsViewActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);

        TextView tvOriginalTitle = (TextView) findViewById(R.id.tv_original_title);
        ImageView ivPoster = (ImageView) findViewById(R.id.iv_poster);
        TextView tvOverView = (TextView) findViewById(R.id.tv_overview);
        TextView tvVoteAverage = (TextView) findViewById(R.id.tv_vote_average);
        TextView tvReleaseDate = (TextView) findViewById(R.id.tv_release_date);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(MainActivityFragment.MOVIE);
        tvOriginalTitle.setText(movie.getmOriginalTitle());
        Picasso.get()
                .load(movie.getmPosterPath())
                .resize(185,
                        278)
                .placeholder(R.drawable.ic_picture)
                .into(ivPoster);

        tvOverView.setText(movie.getmOverview());
        String averageString = movie.getmVoteAverage().toString();
        tvVoteAverage.setText(getString(R.string.average, averageString));

        String releaseDate = movie.getmReleaseDate();
        tvReleaseDate.setText(releaseDate);
    }
}
