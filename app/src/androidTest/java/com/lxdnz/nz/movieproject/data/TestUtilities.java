package com.lxdnz.nz.movieproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.lxdnz.nz.movieproject.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by alex on 15/05/16.
 */
public class TestUtilities extends AndroidTestCase{

    static final int TEST_ID = 135397;
    static final long TEST_DATE = 1419033600L; // December 20th, 2014

    public static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {

        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry: valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() + "' did not match the expected value '"
            + expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieContract.MovieEntry.MOVIE_ID, TEST_ID);
        movieValues.put(MovieContract.MovieEntry.MOVIE_TITLE, "Jurassic World");
        movieValues.put(MovieContract.MovieEntry.MOVIE_ORIGINAL_TITLE, "Jurassic World");
        movieValues.put(MovieContract.MovieEntry.MOVIE_OVERVIEW, "Twenty-two years after the events...");
        movieValues.put(MovieContract.MovieEntry.MOVIE_RELEASE_DATE, TEST_DATE);
        movieValues.put(MovieContract.MovieEntry.MOVIE_POSTER_PATH, "/uXZYawqUsChGSj54wcuBtEdUJbh.jpg");
        movieValues.put(MovieContract.MovieEntry.MOVIE_BACKDROP_PATH, "/dkMD5qlogeRMiEixC4YNPUvax2T.jpg");
        movieValues.put(MovieContract.MovieEntry.MOVIE_VOTE_AVERAGE, 7.1);
        movieValues.put(MovieContract.MovieEntry.MOVIE_VOTE_COUNT, 435);

        return movieValues;
    }

    static ContentValues createTrailerValues(int movieId) {
        ContentValues trailerValues = new ContentValues();
        trailerValues.put(MovieContract.TrailerEntry._ID, 0);
        trailerValues.put(MovieContract.TrailerEntry.TRAILER_MOVIE_ID, movieId);
        trailerValues.put(MovieContract.TrailerEntry.TRAILER_ID, "5474d2339251416e58002ae1");
        trailerValues.put(MovieContract.TrailerEntry.TRAILER_KEY, "RFinNxS5KN4");
        trailerValues.put(MovieContract.TrailerEntry.TRAILER_NAME, "Official Trailer");
        trailerValues.put(MovieContract.TrailerEntry.TRAILER_SIZE, 1080);

        return trailerValues;
    }

    static ContentValues createReviewValues(int movieId) {
        ContentValues reviewValues = new ContentValues();
        reviewValues.put(MovieContract.ReviewEntry._ID, 0);
        reviewValues.put(MovieContract.ReviewEntry.REVIEW_MOVIE_ID, movieId);
        reviewValues.put(MovieContract.ReviewEntry.REVIEW_ID, "55910381c3a36807f900065d");
        reviewValues.put(MovieContract.ReviewEntry.REVIEW_AUTHOR, "jonlikesmoviesthatdontsuck");
        reviewValues.put(MovieContract.ReviewEntry.REVIEW_CONTENT, "I was a huge fan of the original 3 movies...");
        reviewValues.put(MovieContract.ReviewEntry.REVIEW_URL, "https://www.themoviedb.org/review/55910381c3a36807f900065d");

        return reviewValues;
    }

    public static int insertMovieValues(Context context) {
        // insert movie records into the database
        MovieDBHelper dbHelper = new MovieDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieValues();
        long movieRowId;
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // verify if we get a row back
        assertTrue("Error: Failure to insert Movie Values", movieRowId != -1);

        // Data is inserted IN THEORY, pull some out to verify its made the round trip.
        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, // table name
                null, // all columns
                null, // Columns for the "WHERE" clause
                null, // Values for the "WHERE" clause
                null, // Columns to group by
                null, // Columns to filter by row groups
                null  // Sort order
        );

        // Move the cursor to valid database row and see if we get any records back
        // from the query
        assertTrue("Error: No records returned from Movie query.", cursor.moveToFirst() );
        // Validate the data
        TestUtilities.validateCurrentRecord("Error: Movie query validation failed", cursor, testValues);

        // get the Movie_ID entry
        int movieId;
        movieId = cursor.getInt(0);

        // move the cursor to demonstrate there's only one record in the database
        assertFalse("Error: More than one record returned from Movie query", cursor.moveToNext() );

        // close the the cursor and database
        cursor.close();
        db.close();

        return movieId;
    }

    static long insertTrailerValues(Context context, int movieId) {
        // insert trailer record into the database
        MovieDBHelper dbHelper = new MovieDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createTrailerValues(movieId);
        long trailerRowId;
        trailerRowId = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, testValues);

        //verify if we get a row back
        assertTrue("Error: Failure to insert Trailer Values", trailerRowId != -1);

        // Data is inserted IN THEORY, pull some out to verify its made the round trip.
        Cursor cursor = db.query(MovieContract.TrailerEntry.TABLE_NAME,
                null, null, null, null, null, null);
        // Move the cursor to valid database row and see if we get any records back
        // from the query
        assertTrue("Error: No records returned from Trailer query.", cursor.moveToFirst() );
        // Validate the data
        TestUtilities.validateCurrentRecord("Error: Trailer query validation failed", cursor, testValues);
        // move the cursor to demonstrate there's only one record in the database
        assertFalse("Error: More than one record returned from Trailer query", cursor.moveToNext() );
        // close the cursor and database
        cursor.close();
        db.close();

        return trailerRowId;
    }

    static long insertReviewValues(Context context, int movieId) {
        //insert review record into the database
        MovieDBHelper dbHelper = new MovieDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues =TestUtilities.createReviewValues(movieId);
        long reviewRowId;
        reviewRowId = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, testValues);

        //verify if we get row back
        assertTrue("Error: Failure to insert Review Values", reviewRowId != -1);

        // Data is inserted IN THEORY, pull some out to verify its made the round trip.
        Cursor cursor = db.query(MovieContract.ReviewEntry.TABLE_NAME,
                null, null, null, null, null, null);
        // Move the cursor to valid databse row and see if we get any records back
        // from the query.
        assertTrue("Error: No records returned from Review query.", cursor.moveToFirst() );
        // Validate the data
        TestUtilities.validateCurrentRecord("Error: Review query validation failed", cursor, testValues);
        // move the cursor to demonstrate there's only one record in the database
        assertFalse("Error: More than one record returned from Review query", cursor.moveToNext() );
        // close the cursor and database
        cursor.close();
        db.close();

        return reviewRowId;
    }

    public static ContentValues[] createBulkTrailerValuesInsert(long movieId) {
        String TEST_ID = "54749bea9251414f41001b58";
        String TEST_KEY = "bvu-zlR5A8Q";
        String TEST_NAME = "Teaser";
        int[] TEST_SIZE = {320, 540, 720, 1080, 2160};

        ContentValues[] returnContentValues = new ContentValues[TestMovieProvider.BULK_RECORDS_TO_INSERT];

        for (int i = 0; i < TestMovieProvider.BULK_RECORDS_TO_INSERT; i++){
            ContentValues trailerValues = new ContentValues();
            trailerValues.put(MovieContract.TrailerEntry.TRAILER_MOVIE_ID, movieId);
            trailerValues.put(MovieContract.TrailerEntry.TRAILER_ID, TEST_ID+"//"+i);
            trailerValues.put(MovieContract.TrailerEntry.TRAILER_KEY, TEST_KEY);
            trailerValues.put(MovieContract.TrailerEntry.TRAILER_NAME, TEST_NAME);
            trailerValues.put(MovieContract.TrailerEntry.TRAILER_SIZE, TEST_SIZE[i]);
            returnContentValues[i] = trailerValues;
        }
        return returnContentValues;
    }

    public static ContentValues[] createBulkReviewValuesInsert(long movieId) {
        String TEST_ID = "55910381c3a36807f900065d";
        String TEST_CONTENT = "I was a huge fan of the original ";
        String TEST_AUTHOR = "jonlikesmoviesthatdontsuck";
        String TEST_URL = "https://www.themoviedb.org/review/55910381c3a36807f900065d";

        ContentValues[] returnContentValues = new ContentValues[TestMovieProvider.BULK_RECORDS_TO_INSERT];

        for (int i = 0; i < TestMovieProvider.BULK_RECORDS_TO_INSERT; i++) {
            ContentValues reviewValues = new ContentValues();
            reviewValues.put(MovieContract.ReviewEntry.REVIEW_MOVIE_ID, movieId);
            reviewValues.put(MovieContract.ReviewEntry.REVIEW_ID,TEST_ID+"//"+i);
            reviewValues.put(MovieContract.ReviewEntry.REVIEW_CONTENT,TEST_CONTENT + i + " movies.");
            reviewValues.put(MovieContract.ReviewEntry.REVIEW_AUTHOR,TEST_AUTHOR);
            reviewValues.put(MovieContract.ReviewEntry.REVIEW_URL,TEST_URL);
            returnContentValues[i] = reviewValues;
        }

        return returnContentValues;
    }

    /*
        Use this utility class to test the ContentObserver callbacks using the PollingCheck class
        that we grabbed from the Android CTS tests.
        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */

    public static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    public static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

}
