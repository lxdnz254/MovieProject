package com.lxdnz.nz.movieproject.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alex on 22/05/16.
 */
public class Trailer implements Parcelable {

    private int movie_id;
    private String trailer_id;
    private String trailer_key;
    private String trailer_name;
    private int trailer_size;

    public Trailer(int movieId,
                   String trailerId,
                   String key,
                   String name,
                   int size) {
        this.movie_id = movieId;
        this.trailer_id = trailerId;
        this.trailer_key = key;
        this.trailer_name = name;
        this.trailer_size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movie_id);
        dest.writeString(trailer_id);
        dest.writeString(trailer_key);
        dest.writeString(trailer_name);
        dest.writeInt(trailer_size);
    }

    public int getMovieId() {
        return movie_id;
    }

    public String getTrailerId() {
        return trailer_id;
    }

    public String getTrailerKey() {
        return trailer_key;
    }

    public String getTrailerName() {
        return trailer_name;
    }

    public int getTrailerSize() {
        return trailer_size;
    }

    public static final Parcelable.Creator<Trailer> CREATOR =
            new Creator<Trailer>() {
                @Override
                public Trailer createFromParcel(Parcel source) {
                    return new Trailer(source);
                }

                @Override
                public Trailer[] newArray(int size) {
                    return new Trailer[size];
                }
            };

    private Trailer(Parcel in) {
        movie_id = in.readInt();
        trailer_id = in.readString();
        trailer_key = in.readString();
        trailer_name = in.readString();
        trailer_size = in.readInt();
    }

    public String toString() {
        return "Title: " + trailer_name;
    }

}
