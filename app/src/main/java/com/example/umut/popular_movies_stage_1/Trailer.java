package com.example.umut.popular_movies_stage_1;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {

    Trailer(){
    }
    protected Trailer(Parcel in) {
        key = in.readString();
        name = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    private String key;
    private String name;
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.key);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }
    String getName() {
        return name;
    }

    String getKey() {
        return key;
    }
}
