package com.lxdnz.nz.movieproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by alex on 15/05/16.
 */
public class TestUtilities extends AndroidTestCase{

    static final int TEST_ID = 135397;
    static final long TEST_DATE = 1419033600L; // December 20th, 2014

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {

        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
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

    static int insertMovieValues(Context context) {
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


}
