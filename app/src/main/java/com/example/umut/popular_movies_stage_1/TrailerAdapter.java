package com.example.umut.popular_movies_stage_1;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class TrailerAdapter extends ArrayAdapter<Trailer> {

    TrailerAdapter(Activity context, Trailer[] trailers) {
        super(context, 0, trailers);

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Trailer trailer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.trailer_item, parent, false);
        }

        TextView trailerTitle = (TextView) convertView.findViewById(R.id.tv_trailer_title);
        trailerTitle.setText(trailer.getName());
        return convertView;
    }


}

