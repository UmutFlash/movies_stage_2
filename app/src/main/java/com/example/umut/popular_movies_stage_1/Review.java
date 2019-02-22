package com.example.umut.popular_movies_stage_1;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {

    Review(){
    }
    protected Review(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    private String author;
    private String content;
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.content);
        parcel.writeString(this.author);
    }

    public void setAuthor(String key) {
        this.author = key;
    }

    public void setContent(String content) {
        this.content = content;
    }
    String getContent() {
        return content;
    }

    String getAuthor() {
        return author;
    }
}
