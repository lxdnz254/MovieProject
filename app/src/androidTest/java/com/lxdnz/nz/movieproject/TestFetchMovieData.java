package com.lxdnz.nz.movieproject;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.lxdnz.nz.movieproject.async.FetchMovieData;
import com.lxdnz.nz.movieproject.data.MovieContract;
import com.lxdnz.nz.movieproject.objects.Movie;

/**
 * Created by alex on 18/05/16.
 */
public class TestFetchMovieData extends AndroidTestCase {
    static final Movie testMovie = new Movie(
                    "Jurassic World",
                    "Jurassic World",
                    "2015-06-12",
                    "Twenty-two years after the events...",
                    "/uXZYawqUsChGSj54wcuBtEdUJbh.jpg",
                    "/dkMD5qlogeRMiEixC4YNPUvax2T.jpg",
                    135397,
                    435,
                    7.1);

    static final String TEST_ID = Integer.toString(testMovie.getId());

    private static final String LOG_TAG = TestFetchMovieData.class.getSimpleName();

    static final MovieGridFragment movieGridFragment = new MovieGridFragment();


    @TargetApi(11)
    public void testMarkFavorite() {
        // start from clean slate.
        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_ID + " = ?",
                new String[]{TEST_ID});

        FetchMovieData fmd = new FetchMovieData(movieGridFragment, mContext);
        long favoriteId = fmd.markAsFavorite(testMovie);
        // does markAsFavorite return a valid record ID?
        assertFalse("Error: markAsFavorite returned an invalid ID on insert",
                favoriteId == -1);
        // test all this twice
        for (int i=0; i <2; i++) {

            // does the ID point to our movie?
            Cursor favoriteCursor = getContext().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{
                            MovieContract.MovieEntry.MOVIE_ID,
                            MovieContract.MovieEntry.MOVIE_TITLE,
                            MovieContract.MovieEntry.MOVIE_ORIGINAL_TITLE,
                            MovieContract.MovieEntry.MOVIE_OVERVIEW,
                            MovieContract.MovieEntry.MOVIE_RELEASE_DATE,
                            MovieContract.MovieEntry.MOVIE_POSTER_PATH,
                            MovieContract.MovieEntry.MOVIE_BACKDROP_PATH,
                            MovieContract.MovieEntry.MOVIE_VOTE_AVERAGE,
                            MovieContract.MovieEntry.MOVIE_VOTE_COUNT
                    },
                    MovieContract.MovieEntry.MOVIE_ID + " = ?",
                    new String[] {TEST_ID},
                    null
            );

            // these match the indices of the projection
            if (favoriteCursor.moveToFirst()) {
                assertEquals("Error: the queried value of favoriteId does not match the returned" +
                " value from markAsFavorite", favoriteCursor.getLong(0), favoriteId);
                assertEquals("Error: the queried value of Title is incorrect",
                        favoriteCursor.getString(1), testMovie.getTitle());
                assertEquals("Error: the queried value of Original_Title is incorrect",
                        favoriteCursor.getString(2), testMovie.getOriginal_title());
                assertEquals("Error: the queried value of Overview is incorrect",
                        favoriteCursor.getString(3), testMovie.getOverview());
                assertEquals("Error: the queried value of Release_Date is incorrect",
                        favoriteCursor.getString(4), testMovie.getRelease_date());
                assertEquals("Error: the queried value of Poster_Path is incorrect",
                        favoriteCursor.getString(5), testMovie.getPoster_path());
                assertEquals("Error: the queried value of Backdrop_path is incorrect",
                        favoriteCursor.getString(6), testMovie.getBackdrop_path());
                assertEquals("Error: the queried value of Vote_Average is incorrect",
                        favoriteCursor.getDouble(7), testMovie.getVote_average());
                assertEquals("Error: the queried value of Vote_Count is incorrect",
                        favoriteCursor.getInt(8), testMovie.getVote_count());
            } else {
                fail("Error: the ID you used to query returned an empty cursor");
            }
            // there should be no more records
            assertFalse("Error: there should only be one record returned from a favorite query",
                    favoriteCursor.moveToNext());
            // add the favorite again
            long newFavoriteId = fmd.markAsFavorite(testMovie);
            assertEquals("Error: inserting a favorite again should return the same ID",
                    favoriteId, newFavoriteId);
        }
        // reset our state back to normal
        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_ID + " = ?",
                new String[]{TEST_ID});
        // clean up the test so other tests can use the ContentProvider
        getContext().getContentResolver().
                acquireContentProviderClient(MovieContract.MovieEntry.CONTENT_URI).
                getLocalContentProvider().shutdown();
    }
}
