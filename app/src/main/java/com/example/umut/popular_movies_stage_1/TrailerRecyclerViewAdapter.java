package com.example.umut.popular_movies_stage_1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.ViewHolder> {

    private Trailer[] mValues;
    private LayoutInflater mInflater;

    public class ViewHolder extends RecyclerView.ViewHolder {
        String mKeyString;

        final View mView;
        final TextView mTrailerTitle;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mTrailerTitle = view.findViewById(R.id.tv_trailer_title);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTrailerTitle.getText();
        }
    }

    TrailerRecyclerViewAdapter(Context context, Trailer[] trailers) {
        this.mInflater = LayoutInflater.from(context);
        mValues = trailers;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.from(parent.getContext())
                .inflate(R.layout.trailer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mKeyString = mValues[position].getKey();
        holder.mTrailerTitle.setText(mValues[position].getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                final String trailerUrl = "http://youtube.com/watch?v=" + holder.mKeyString;
                Uri youtubeLink = Uri.parse(trailerUrl);
                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, youtubeLink);
                context.startActivity(youtubeIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.length;
    }
}
