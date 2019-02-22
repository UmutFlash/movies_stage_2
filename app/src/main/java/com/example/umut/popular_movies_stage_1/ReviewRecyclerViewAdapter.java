package com.example.umut.popular_movies_stage_1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {

    private Review[] mValues;
    private LayoutInflater mInflater;

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mAuthor;
        TextView mContent;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mAuthor = view.findViewById(R.id.tv_author);
            mContent = view.findViewById(R.id.tv_content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAuthor.getText();
        }
    }

    ReviewRecyclerViewAdapter(Context context, Review[] reviews) {
        this.mInflater = LayoutInflater.from(context);
        mValues = reviews;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.from(parent.getContext())
                .inflate(R.layout.reviews_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mContent.setText(mValues[position].getContent());
        holder.mAuthor.setText(mValues[position].getAuthor());
    }

    @Override
    public int getItemCount() {
        return mValues.length;
    }
}
