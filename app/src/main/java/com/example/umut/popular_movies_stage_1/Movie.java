package com.example.umut.popular_movies_stage_1;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private static final String HTTPS_IMAGE_TMDB = "https://image.tmdb.org/t/p/w185";
    private String mOriginalTitle;
    private String mPosterPath;
    private String mOverview;
    private Double mVoteAverage;
    private String mReleaseDate;

    Movie() {
    }

    private Movie(Parcel in) {
        mOriginalTitle = in.readString();
        mPosterPath = in.readString();
        mOverview = in.readString();
        if (in.readByte() == 0) {
            mVoteAverage = null;
        } else {
            mVoteAverage = in.readDouble();
        }
        mReleaseDate = in.readString();
    }

    String getmReleaseDate() {
        return mReleaseDate;
    }

    void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    Double getmVoteAverage() {
        return mVoteAverage;
    }

    void setmVoteAverage(Double mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    String getmOverview() {
        return mOverview;
    }

    void setmOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    String getmPosterPath() {

        return HTTPS_IMAGE_TMDB + mPosterPath;
    }

    void setmPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
    }

    String getmOriginalTitle() {
        return mOriginalTitle;
    }

    void setmOriginalTitle(String mOriginalTitle) {
        this.mOriginalTitle = mOriginalTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mPosterPath);
        parcel.writeString(mOverview);
        if (mVoteAverage == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(mVoteAverage);
        }
        parcel.writeString(mReleaseDate);
    }

}

