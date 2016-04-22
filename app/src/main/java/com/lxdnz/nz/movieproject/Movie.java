package com.lxdnz.nz.movieproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alex on 22/04/16.
 */
public class Movie implements Parcelable{

    private String id = null;
    private String title = null;
    private String poster_path = null;
    private String overview = null;
    private String release_date = null;
    private String rating = null;

    public Movie(){

    }

    private Movie(Parcel in){
        id = in.readString();
        title = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        rating = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(title);
        out.writeString(poster_path);
        out.writeString(overview);
        out.writeString(release_date);
        out.writeString(rating);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    /*
     * A movie object is valid if it has title and poster_path,
     * this is enough for displaying nicely.
     */
    public boolean isValid(){
        return title != null &&
                poster_path != null;
    }



}
