package com.example.sang.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable{
    static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private long mId;
    private String mTitle;
    private String mPosterPath;
    private double mAvgVote;
    private String mOverview;
    private String mReleaseDate;

    public Movie(Parcel in) {
        this.mId = in.readLong();
        this.mTitle = in.readString();
        this.mPosterPath = in.readString();
        this.mAvgVote = in.readDouble();
        this.mOverview = in.readString();
        this.mReleaseDate = in.readString();
    }

    public Movie() {
    }

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmPosterPath() {
        return mPosterPath;
    }

    public void setmPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
    }

    public double getmAvgVote() {
        return mAvgVote;
    }

    public void setmAvgVote(double mAvgVote) {
        this.mAvgVote = mAvgVote;
    }

    public String getmOverview() {
        return mOverview;
    }

    public void setmOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeDouble(mAvgVote);
        dest.writeString(mOverview);
        dest.writeString(mReleaseDate);
        
    }
}
