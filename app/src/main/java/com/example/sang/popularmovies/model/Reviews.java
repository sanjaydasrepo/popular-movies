package com.example.sang.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Reviews implements Parcelable {

    private String reviewId;
    private String authorName;
    private String reviewContent;
    private String reviewUrl;

    static final Parcelable.Creator<Reviews> CREATOR
            = new Parcelable.Creator<Reviews>(){
        @Override
        public Reviews createFromParcel(Parcel source) {
            return new Reviews(source);
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };

    public Reviews(Parcel in) {
        this.reviewId = in.readString();
        this.authorName = in.readString();
        this.reviewContent = in.readString();
        this.reviewUrl = in.readString();
    }

    public Reviews() {
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    public void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(reviewId);
        dest.writeString(authorName);
        dest.writeString(reviewContent);
        dest.writeString(reviewUrl);
    }
}
