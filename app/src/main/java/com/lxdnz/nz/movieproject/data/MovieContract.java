package com.lxdnz.nz.movieproject.data;

import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by alex on 14/05/16.
 */
public class MovieContract {

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /**
     * Inner class that defines the favorite movie entries
     */
    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        // this is the foreign key to the other tables
        public static final String MOVIE_ID = "id";

        // general information for each id(movie)
        public static final String MOVIE_TITLE = "title";
        public static final String MOVIE_ORIGINAL_TITLE = "original_title";
        public static final String MOVIE_OVERVIEW = "overview";
        public static final String MOVIE_RELEASE_DATE = "release_date";

        // url's for thumbnails and images
        public static final String MOVIE_POSTER_PATH = "poster_path";
        public static final String MOVIE_BACKDROP_PATH = "backdrop_path";

        // the rating of the id(movie)
        public static final String MOVIE_VOTE_AVERAGE = "vote_average";
        public static final String MOVIE_VOTE_COUNT = "vote_count";

    }

    /**
     * Inner class that stores the favorite trailers
     */
    public static final class TrailerEntry implements BaseColumns {

        public static final String TABLE_NAME = "trailers";

        public static final String TRAILER_ID = "id";
        public static final String TRAILER_KEY = "key";
        public static final String TRAILER_NAME = "name";
        public static final String TRAILER_SIZE = "size";

    }

    /**
     * Inner class that stores reviews for offline calls
     */
    public static final class ReviewEntry implements BaseColumns {

        public static final String TABLE_NAME = "reviews";

        public static final String REVIEW_ID = "id";
        public static final String REVIEW_AUTHOR = "author";
        public static final String REVIEW_CONTENT = "content";
        public static final String REVIEW_URL = "url";
    }
}
