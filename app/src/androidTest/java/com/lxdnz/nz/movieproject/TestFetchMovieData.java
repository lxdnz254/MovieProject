package com.lxdnz.nz.movieproject;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.test.AndroidTestCase;
import android.util.Log;

import com.lxdnz.nz.movieproject.async.FetchMovieData;
import com.lxdnz.nz.movieproject.async.FetchReviewData;
import com.lxdnz.nz.movieproject.async.FetchTrailerData;
import com.lxdnz.nz.movieproject.data.MovieContract;
import com.lxdnz.nz.movieproject.data.TestMovieProvider;
import com.lxdnz.nz.movieproject.data.TestUtilities;
import com.lxdnz.nz.movieproject.objects.Movie;
import com.lxdnz.nz.movieproject.objects.Review;
import com.lxdnz.nz.movieproject.objects.Trailer;
import com.lxdnz.nz.movieproject.utils.Utilities;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    CountDownLatch trailerSignal = null;
    CountDownLatch reviewSignal = null;

    Trailer[] mTrailers;
    Trailer[] testTrailers;

    Review[] mReviews;
    Review[] testReviews;

    private boolean trailersExist = false;
    private boolean reviewsExist = false;

    @Override
    protected void setUp() throws Exception {
        trailerSignal = new CountDownLatch(1);
        reviewSignal = new CountDownLatch(1);
    }


    @TargetApi(11)
    public void testMarkFavorite() {
        // start from clean slate.
        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_ID + " = ?",
                new String[]{TEST_ID});
        getContext().getContentResolver().delete(MovieContract.TrailerEntry.TRAILER_URI,
                MovieContract.TrailerEntry.TRAILER_MOVIE_ID + " = ?",
                new String[]{TEST_ID});
        getContext().getContentResolver().delete(MovieContract.ReviewEntry.REVIEW_URI,
                MovieContract.ReviewEntry.REVIEW_MOVIE_ID + " = ?",
                new String[]{TEST_ID});



        long favoriteId = new Utilities(mContext).markAsFavorite(testMovie);

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
            long newFavoriteId = new Utilities(mContext).markAsFavorite(testMovie);
            assertEquals("Error: inserting a favorite again should return the same ID",
                    favoriteId, newFavoriteId);
        }
        // create the fake test reviews & trailers
        ContentValues[] bulkTrailers = TestUtilities.createBulkTrailerValuesInsert(testMovie.getId());

        assertTrue("Error: failed to insert bulkTrailers", bulkTrailers != null);

        // Register a content Observer for the bulk insert
        TestUtilities.TestContentObserver trailerObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.TrailerEntry.TRAILER_URI, true, trailerObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.TRAILER_URI, bulkTrailers);

        // If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        trailerObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(trailerObserver);
        assertTrue("Error: inserted wrong amount of Trailers", insertCount == TestMovieProvider.BULK_RECORDS_TO_INSERT);

        ContentValues[] bulkReviews = TestUtilities.createBulkReviewValuesInsert(testMovie.getId());

        assertTrue("Error: failed to insert bulkReviews", bulkReviews != null);

        // Register the content observer
        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.ReviewEntry.REVIEW_URI, true, reviewObserver);

        insertCount = mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.REVIEW_URI, bulkReviews);
        // check for failure.
        reviewObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(reviewObserver);
        assertTrue("Error: inserted wrong amount of Reviews", insertCount == TestMovieProvider.BULK_RECORDS_TO_INSERT);


        try {
            testTrailers = testFetchTrailerData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            testReviews = testFetchReviewData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i =0; i < TestMovieProvider.BULK_RECORDS_TO_INSERT; i++) {
            Set<Map.Entry<String, Object>> trailerValueSet = bulkTrailers[i].valueSet();
            Set<Map.Entry<String, Object>> reviewValueSet = bulkReviews[i].valueSet();

            for (Map.Entry<String, Object> entry: trailerValueSet) {
                String columnName = entry.getKey();

                switch (columnName){
                    case "name": {
                        assertEquals(testTrailers[i].getTrailerName(), entry.getValue().toString());
                        break;
                    }
                    case "t_id": {
                        assertEquals(Integer.toString(testTrailers[i].getMovieId()), entry.getValue().toString());
                        break;
                    }
                    case "size": {
                        assertEquals(Integer.toString(testTrailers[i].getTrailerSize()),
                                entry.getValue().toString());
                        break;
                    }
                    case "key": {
                        assertEquals(testTrailers[i].getTrailerKey(), entry.getValue().toString());
                        break;
                    }
                    case "id": {
                        assertEquals(testTrailers[i].getTrailerId(), entry.getValue().toString());
                    }
                }
            }

            for (Map.Entry<String, Object> entry: reviewValueSet) {
                String columnName = entry.getKey();

                switch (columnName){
                    case "content": {
                        assertEquals(testReviews[i].getReviewContent(), entry.getValue().toString());
                        break;
                    }
                    case "id": {
                        assertEquals(testReviews[i].getReviewId(), entry.getValue().toString());
                        break;
                    }
                    case  "r_id": {
                        assertEquals(Integer.toString(testReviews[i].getMovieId()),
                                entry.getValue().toString());
                        break;
                    }
                    case "author": {
                        assertEquals(testReviews[i].getAuthor(), entry.getValue().toString());
                    }
                }
            }


        }
        // reset our state back to normal
        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_ID + " = ?",
                new String[]{TEST_ID});
        getContext().getContentResolver().delete(MovieContract.TrailerEntry.TRAILER_URI,
                MovieContract.TrailerEntry.TRAILER_MOVIE_ID + " = ?",
                new String[]{TEST_ID});
        getContext().getContentResolver().delete(MovieContract.ReviewEntry.REVIEW_URI,
                MovieContract.ReviewEntry.REVIEW_MOVIE_ID + " = ?",
                new String[]{TEST_ID});
        // clean up the test so other tests can use the ContentProvider

        getContext().getContentResolver().
                acquireContentProviderClient(MovieContract.MovieEntry.CONTENT_URI).
                getLocalContentProvider().shutdown();

    }

    public Trailer[] testFetchTrailerData() throws InterruptedException{

        // create countdown listeners
        FetchTrailerData trailerTask = new FetchTrailerData(mContext, mTrailers);

        trailerTask.setListener(new FetchTrailerData.Listener() {
            @Override
            public void onFetchTrailersFinished(Trailer[] trailers) {
                if (trailers != null) {
                    Log.v(LOG_TAG, "trailers are not null");
                    mTrailers = trailers;
                    trailersExist = true;
                }
                Log.v(LOG_TAG, "Trailers Finished");
                trailerSignal.countDown();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (long)testMovie.getId());


        // wait for the asyncTasks to finish before continuing the testing
        trailerSignal.await();

        assertTrue("Error: No trailers returned", trailersExist);
        assertTrue("Error: Wrong amount of trailers returned", mTrailers.length == TestMovieProvider.BULK_RECORDS_TO_INSERT);

        return mTrailers;
    }

    public Review[] testFetchReviewData() throws InterruptedException{

        FetchReviewData reviewTask = new FetchReviewData(mContext, mReviews);

        reviewTask.setListener(new FetchReviewData.Listener() {
            @Override
            public void onFetchReviewsFinished(Review[] reviews) {
                if (reviews !=null) {
                    Log.v(LOG_TAG, "reviews are not null");
                    mReviews = reviews;
                    reviewsExist = true;
                }
                Log.v(LOG_TAG, "reviews finished");
                reviewSignal.countDown();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (long) testMovie.getId());

        reviewSignal.await();

        assertTrue("Error: No reviews returned", reviewsExist);
        assertTrue("Error: Wrong amount of reviews returned", mReviews.length == TestMovieProvider.BULK_RECORDS_TO_INSERT);

        return mReviews;
    }
}
