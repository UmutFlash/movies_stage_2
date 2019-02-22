package com.example.umut.popular_movies_stage_1;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ReviewAdapter extends ArrayAdapter<Review> {

    ReviewAdapter(Activity context, Review[] reviews) {
        super(context, 0, reviews);

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Review review = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.reviews_item, parent, false);
        }

        TextView author = (TextView) convertView.findViewById(R.id.tv_author);
        author.setText(review.getAuthor());

        TextView content = (TextView) convertView.findViewById(R.id.tv_content);
        content.setText(review.getContent());
        return convertView;
    }


}

