package com.lxdnz.nz.movieproject.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alex on 22/05/16.
 */
public class Review implements Parcelable {

    private int movie_id;
    private String review_id;
    private String author;
    private String content;
    private String url;

    public Review(int movieId,
                  String reviewId,
                  String author,
                  String content,
                  String url) {
        this.movie_id = movieId;
        this.review_id = reviewId;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movie_id);
        dest.writeString(review_id);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }

    public static final Parcelable.Creator<Review> CREATOR =
            new Creator<Review>() {
                @Override
                public Review createFromParcel(Parcel source) {
                    return null;
                }

                @Override
                public Review[] newArray(int size) {
                    return new Review[size];
                }
            };

    public int getMovieId() {
        return movie_id;
    }

    public String getReviewId() {
        return review_id;
    }

    public String getAuthor() {
        return author;
    }

    public String getReviewContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    private Review(Parcel in) {
        movie_id = in.readInt();
        review_id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }
}
