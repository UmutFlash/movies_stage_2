package com.example.umut.popular_movies_stage_1;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MoviesAdapter extends ArrayAdapter<Movie> {


    MoviesAdapter(Activity context, Movie[] movieCollection) {
        super(context, 0, movieCollection);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.poster_item, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.poster_image);

        Picasso.get().
                load(movie != null ? movie.getmPosterPath() : null)
                .resize(185, 278).into(imageView);

        return convertView;
    }
}
