package com.lxdnz.nz.movieproject.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;
import android.util.Log;


/**
 * Created by alex on 14/05/16.
 */
public class MovieContract {

    public static final String LOG_TAG = MovieContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "com.lxdnz.nz.movieproject";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_REVIEWS = "reviews";

    /**
     * Inner class that defines the favorite movie entries
     */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

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

        // build movies Uri
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // build movie Uri with movieID
        public static Uri buildMovieWithId(long movieId){
            String stringId = Long.toString(movieId);
            Uri builtUri = CONTENT_URI.buildUpon().appendPath(stringId).build();
            return builtUri;
        }

        public static final String [] MOVIE_COLUMNS = {
                TABLE_NAME + "." + MOVIE_ID,
                MOVIE_TITLE,
                MOVIE_ORIGINAL_TITLE,
                MOVIE_OVERVIEW,
                MOVIE_RELEASE_DATE,
                MOVIE_POSTER_PATH,
                MOVIE_BACKDROP_PATH,
                MOVIE_VOTE_AVERAGE,
                MOVIE_VOTE_COUNT
        };

        public static final int COL_MOVIE_ID = 0;
        public static final int COL_MOVIE_TITLE = 1;
        public static final int COL_MOVIE_ORIGINAL_TITLE = 2;
        public static final int COL_MOVIE_OVERVIEW = 3;
        public static final int COL_MOVIE_RELEASE_DATE = 4;
        public static final int COL_MOVIE_POSTER_PATH = 5;
        public static final int COL_MOVIE_BACKDROP_PATH = 6;
        public static final int COL_MOVIE_VOTE_AVERAGE = 7;
        public static final int COL_MOVIE_VOTE_COUNT = 8;

    }

    /**
     * Inner class that stores the favorite trailers
     */
    public static final class TrailerEntry implements BaseColumns {

        public static final String LOG_TAG = TrailerEntry.class.getSimpleName();

        public static final Uri TRAILER_URI =
                MovieEntry.CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();


        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_MOVIES + "/" + PATH_TRAILERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_MOVIES + "/" + PATH_TRAILERS;

        public static final String TABLE_NAME = "trailers";

        public static final String TRAILER_MOVIE_ID = "t_id";
        public static final String TRAILER_ID = "id";
        public static final String TRAILER_KEY = "key";
        public static final String TRAILER_NAME = "name";
        public static final String TRAILER_SIZE = "size";

        // build Trailers Uri's
        public static Uri buildTrailersUri(long id) {
            return ContentUris.withAppendedId(TRAILER_URI, id);
        }

        public static final String [] TRAILER_COLUMNS = {
                _ID,
            TRAILER_MOVIE_ID,
            TRAILER_ID,
            TRAILER_KEY,
            TRAILER_NAME,
            TRAILER_SIZE
        };

        public static final int COL_ID_TRAILER = 0;
        public static final int COL_TRAILER_MOVIE_ID = 1;
        public static final int COL_TRAILER_ID = 2;
        public static final int COL_TRAILER_KEY = 3;
        public static final int COL_TRAILER_NAME = 4;
        public static final int COL_TRAILER_SIZE = 5;
    }

    /**
     * Inner class that stores reviews for offline calls
     */
    public static final class ReviewEntry implements BaseColumns {

        public static final Uri REVIEW_URI =
                MovieEntry.CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_MOVIES + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_MOVIES + "/" + PATH_REVIEWS;

        public static final String TABLE_NAME = "reviews";

        public static final String REVIEW_MOVIE_ID = "r_id";
        public static final String REVIEW_ID = "id";
        public static final String REVIEW_AUTHOR = "author";
        public static final String REVIEW_CONTENT = "content";
        public static final String REVIEW_URL = "url";

        // build Reviews Uri
        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(REVIEW_URI, id);
        }


        public static final String [] REVIEW_COLUMNS = {
                _ID,
                REVIEW_MOVIE_ID,
                REVIEW_ID,
                REVIEW_AUTHOR,
                REVIEW_CONTENT,
                REVIEW_URL
        };

        public static final int COL_ID_REVIEWS = 0;
        public static final int COL_REVIEW_MOVIE_ID = 1;
        public static final int COL_REVIEW_ID = 2;
        public static final int COL_REVIEW_AUTHOR = 3;
        public static final int COL_REVIEW_CONTENT = 4;
        public static final int COL_REVIEW_URL = 5;
    }
}
