package com.lxdnz.nz.movieproject.data;

import android.content.ContentUris;
import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by alex on 19/05/16.
 */
public class TestUriMatcher extends AndroidTestCase {

    private static final long TEST_MOVIE_ID = 135397;
    private static final String LOG_TAG = TestUriMatcher.class.getSimpleName();

    // content://com.lxdnz.nz.movieproject/movies
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_MOVIE_URI = MovieContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);
    private static final Uri TEST_MOVIE_WITH_MOVIE_ID = MovieContract.MovieEntry.buildMovieWithId(TEST_MOVIE_ID);
    // content://com.lxdnz.nz.movieproject/movies/#/trailers
    private static final Uri TEST_TRAILER_DIR = MovieContract.TrailerEntry.TRAILER_URI;
    // content://com.lxdnz.nz.movieproject/#/reviews
    private static final Uri TEST_REVIEW_DIR = MovieContract.ReviewEntry.REVIEW_URI;

    /*
    This function tests that your UriMatcher returns the correct integer value
    for each of the Uri types that our ContentProvider can handle.
     */
     public void testUriMatcher() {
         UriMatcher testMatcher = MovieProvider.buildUriMatcher();

         assertEquals("Error: the MOVIE URI was matched incorrectly.",
                 testMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIE);
         assertEquals("Error: the MOVIE buildUri was matched incorrectly.",
                 testMatcher.match(TEST_MOVIE_WITH_MOVIE_URI), MovieProvider.MOVIE_WITH_ID);
         assertEquals("Error: the MOVIE buildWithId was matched incorrectly.",
                 testMatcher.match(TEST_MOVIE_WITH_MOVIE_ID), MovieProvider.MOVIE_WITH_ID);
         Log.v(LOG_TAG, "Trailer URI is: " + TEST_TRAILER_DIR.toString());
         assertEquals("Error: the TRAILER URI was matched incorrectly.",
                 testMatcher.match(TEST_TRAILER_DIR), MovieProvider.TRAILER);
         assertEquals("Error: the REVIEW URI was matched incorrectly.",
                 testMatcher.match(TEST_REVIEW_DIR), MovieProvider.REVIEW);
     }
}
