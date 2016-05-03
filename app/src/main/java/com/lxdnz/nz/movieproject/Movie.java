package com.lxdnz.nz.movieproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alex on 22/04/16.
 */
public class Movie implements Parcelable{


    private int id;
    private String backdrop_path;
    private String release_date;
    private String original_title;
    private String overview;
    private String poster_path;
    private String title;
    private double vote_average;
    private int vote_count;


    public Movie(String title,
                 String original_title,
                 String release_date,
                 String overview,
                 String poster_path,
                 String backdrop_path,
                 int id,
                 int vote_count,
                 double vote_average) {
        this.title = title;
        this.release_date = release_date;
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.backdrop_path = backdrop_path;
        this.id =id;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeInt(vote_count);
        out.writeDouble(vote_average);
        out.writeString(backdrop_path);
        out.writeString(release_date);
        out.writeString(original_title);
        out.writeString(overview);
        out.writeString(poster_path);
        out.writeString(title);
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

    public int getVote_count() {
        return vote_count;
    }

    public double getVote_average() {
        return vote_average;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getOverview() {
        return overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    private Movie(Parcel in) {
        id = in.readInt();
        vote_count = in.readInt();

        vote_average = in.readDouble();
        backdrop_path = in.readString();
        release_date = in.readString();
        original_title = in.readString();
        overview = in.readString();
        poster_path = in.readString();
        title = in.readString();
    }

    public String toString() {
        return "Title: " + title + " Rating: " + vote_average;
    }


}
