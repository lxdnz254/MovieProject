package com.lxdnz.nz.movieproject.data;

import android.content.ContentUris;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.lxdnz.nz.movieproject.data.MovieContract;

/**
 * Created by alex on 18/05/16.
 */
public class TestMovieContract extends AndroidTestCase{

    // intentionally include a slash to make sure Uri is getting quoted correctly
    private static final long TEST_FAVORITE_MOVIE_ID = 135397;

    public void testBuildMovieEntry() {
        Uri testUri = MovieContract.MovieEntry.buildMovieUri(TEST_FAVORITE_MOVIE_ID);
        long testId = ContentUris.parseId(testUri);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieUri in " +
                        "MovieContract.",
                testUri);
        assertEquals("Error: Movie ID not properly appended to the end of the Uri: " ,
                TEST_FAVORITE_MOVIE_ID, testId);
        assertEquals("Error: Movie Uri doesn't match our expected result",
                testUri.toString(),
                "content://com.lxdnz.nz.movieproject/movies/135397");

    }
}
