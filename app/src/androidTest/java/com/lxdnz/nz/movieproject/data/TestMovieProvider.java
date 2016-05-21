package com.lxdnz.nz.movieproject.data;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.lxdnz.nz.movieproject.data.MovieContract.MovieEntry;
import com.lxdnz.nz.movieproject.data.MovieContract.TrailerEntry;
import com.lxdnz.nz.movieproject.data.MovieContract.ReviewEntry;

/**
 * Created by alex on 18/05/16.
 */
public class TestMovieProvider extends AndroidTestCase{

    public static final String LOG_TAG = TestMovieProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.
    */
    public void deleteAllRecordsFromProvider(){
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(
                TrailerEntry.TRAILER_URI, null, null);
        mContext.getContentResolver().delete(
                ReviewEntry.REVIEW_URI, null, null);
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();
        cursor = mContext.getContentResolver().query(
                TrailerEntry.TRAILER_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Trailer table during delete", 0, cursor.getCount());
        cursor.close();
        cursor = mContext.getContentResolver().query(
                ReviewEntry.REVIEW_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Review table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
    This test checks to make sure the ContentProvider is registered correctly
     */
    public void testProviderRegistry(){
        PackageManager pm = mContext.getPackageManager();
        // We define the component name based on the package name from the context and the
        // MovieProvider class
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
           This test doesn't touch the database.  It verifies that the ContentProvider returns
           the correct type for each type of URI that it can handle.
        */
    public void testGetType() {
        ContentResolver resolver = mContext.getContentResolver();

        // content://com.lxdnz.nz.movieproject/movies/
        String type = resolver.getType(MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.lxdnz.nz.movieproject/movies
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        long testId = 135397;
        // content://com.lxdnz.nz.movieproject/movies/135397
        type = resolver.getType(MovieEntry.buildMovieUri(testId));
        // vnd.android.cursor.dir/com.lxdnz.nz.movieproject/movies
        assertEquals("Error: the MovieEntry CONTENT_URI with ID should return MovieEntry.CONTENT_ITEM_TYPE",
                 MovieEntry.CONTENT_ITEM_TYPE, type);

        // content://com.lxdnz.nz.movieproject/movies/trailers/
        type = resolver.getType(TrailerEntry.TRAILER_URI);
        Log.v(LOG_TAG, "Trailer Uri is:"+ TrailerEntry.TRAILER_URI.toString());
        Log.v(LOG_TAG, "Trailer Content type is:"+ TrailerEntry.CONTENT_TYPE);
        Log.v(LOG_TAG, "Trailer Uri Type is:"+type.toString());
        // vnd.android.cursor.dir/com.lxdnz.nz.movieproject/movies/trailers
        assertEquals("Error: the TrailerEntry CONTENT_URI should return TrailerEntry.CONTENT_TYPE",
                TrailerEntry.CONTENT_TYPE, type);
        long testTrailerId = 0;
        // content://com.lxdnz.nz.movieproject/movies/trailers/0
        type = resolver.getType(TrailerEntry.buildTrailersUri(testTrailerId));
        // vnd.android.cursor.dir/com.lxdnz.nz.movieproject/movies/trailers
        assertEquals("Error: the TrailerEntry CONTENT_URI with ID should return TrailerEntry.CONTENT_ITEM_TYPE",
                TrailerEntry.CONTENT_ITEM_TYPE, type);

        // content://com.lxdnz.nz.movieproject/movies/reviews/
        type = resolver.getType(ReviewEntry.REVIEW_URI);
        assertEquals("Error: the ReviewEntry CONTENT_URI should return ReviewEntry.CONTENT_TYPE",
                ReviewEntry.CONTENT_TYPE, type);
        long testReviewId = 0;
        // content://com.lxdnz.nz.movieproject/movies/reviews/0
        type = resolver.getType(ReviewEntry.buildReviewUri(testReviewId));
        // vnd.android.cursor.dir/com.lxdnz.nz.movieproject/reviews
        assertEquals("Error: the ReviewEntry CONTENT_URI with ID should return ReviewEntry.CONTENT_ITEM_TYPE",
                ReviewEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testBasicMovieQuery(){
        movieQuery();
    }

    public void testBasicTrailerQuery() {
        int movieId = movieQuery();
        MovieDBHelper dbHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // we have movie now add trailer and test
        ContentValues testTrailerValues = TestUtilities.createTrailerValues(movieId);
        long trailerRowId = db.insert(TrailerEntry.TABLE_NAME, null, testTrailerValues);
        assertTrue("Unable to insert TrailerEntry into the database", trailerRowId != -1);
        db.close();

        // now test the basic content provider query
        Cursor trailerCursor = mContext.getContentResolver().query(
                TrailerEntry.TRAILER_URI, null, null, null, null);
        // make sure we get correct cursor out of the database
        TestUtilities.validateCursor("testBasicTrailerQuery", trailerCursor, testTrailerValues);
    }

    public void testBasicReviewQuery() {
        int movieId = movieQuery();
        MovieDBHelper dbHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // add review and test
        ContentValues testReviewValues = TestUtilities.createReviewValues(movieId);
        long reviewRowId = db.insert(ReviewEntry.TABLE_NAME, null, testReviewValues);
        assertTrue("Unable to insert ReviewEntry into the database", reviewRowId != -1);
        db.close();

        // now test the basic content provider query
        Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.REVIEW_URI, null, null, null, null);
        // make sure we get correct cursor out of the database
        TestUtilities.validateCursor("testBasicReviewQuery", reviewCursor, testReviewValues);
    }

    public int movieQuery(){
        // insert our test records into the database
        MovieDBHelper dbHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();
        int movieRowId = TestUtilities.insertMovieValues(mContext);

        // Now we have a movie, test the basic content query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,null,null,null,null);
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQuery, movie query", movieCursor, testValues);
        // Has the NotificationUri been set correctly? -- we can only test this easily against
        // API level 19 or greater because getNotificationUri was added in API 19
        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movie query did not properly set NotificationUri",
                    movieCursor.getNotificationUri(), MovieEntry.CONTENT_URI);
        }
        db.close();
        return movieRowId;
    }

    public void testUpdateMovie(){
        // create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(
                MovieEntry.CONTENT_URI, values);
        long movieRowId = ContentUris.parseId(movieUri);
        // verify we got a row back
        assertTrue(movieRowId != -1);
        Log.d(LOG_TAG, "new row id: "+ movieRowId);

        ContentValues updateValues = new ContentValues(values);
        updateValues.put(MovieEntry.MOVIE_ID, movieRowId);
        updateValues.put(MovieEntry.MOVIE_TITLE, "Test Movie");

        // create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        movieCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieEntry.CONTENT_URI, updateValues, MovieEntry.MOVIE_ID + " = ?",
                new String[] {Long.toString(movieRowId)});
        assertEquals(count,1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        movieCursor.unregisterContentObserver(tco);
        movieCursor.close();

        // A cursor is your primary interface to the query results
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, //projection
                MovieEntry.MOVIE_ID + " = " + movieRowId, //selection
                null, // values for the "WHERE" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testUpdateMovie. Error validating Movie entry update",
                cursor, updateValues);

        cursor.close();
    }

    // make sure we can insert into the other tables on a Movie update.
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createMovieValues();

        // Register a content observer for our insert. This time directly with the Content Resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);

        // Did our content observer get called? If this fails, your insert Movie isn't calling
        // mContext.getContentResolver().notifyChange(uri, null)
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieRowId = ContentUris.parseId(movieUri);
        // Verify we get a row back
        assertTrue(movieRowId != -1);
        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry.",
                cursor, testValues);
        // Great, now we have a movie Entry add a trailer, test, then add a review and test
        // parse movieRowId to int
        int movieId = (int)movieRowId;
        ContentValues trailerValues = TestUtilities.createTrailerValues(movieId);
        // the TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(TrailerEntry.TRAILER_URI, true, tco);

        Uri trailerInsertUri = mContext.getContentResolver()
                .insert(TrailerEntry.TRAILER_URI, trailerValues);
        assertTrue(trailerInsertUri != null);
        // Did our content observer get called? If this fails, your insert Trailer in
        // your content provider isn't calling
        // mContext.getContentResolver().notifyChange(uri, null)
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);
        // get trailerCursor and validate
        Cursor trailerCursor = mContext.getContentResolver().query(
                TrailerEntry.TRAILER_URI, null, null, null, null);
        TestUtilities.validateCursor("testInsertReadProvider. Error validating TrailerEntry.",
                trailerCursor, trailerValues);

        // Validate the ReviewEntry
        ContentValues reviewValues = TestUtilities.createReviewValues(movieId);
        // the TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(ReviewEntry.REVIEW_URI, true, tco);

        Uri reviewInsertUri = mContext.getContentResolver()
                .insert(ReviewEntry.REVIEW_URI, reviewValues);
        assertTrue(reviewInsertUri != null);
        // Did our content observer get called? If this fails, your insert Review in
        // your content provider isn't calling
        // mContext.getContentResolver().notifyChange(uri, null)
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);
        // get reviewCursor and validate
        Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.REVIEW_URI, null, null, null, null);
        TestUtilities.validateCursor("testInsertReadProvider. Error validating ReviewEntry.",
                reviewCursor, reviewValues);

        // do same cursor check with MovieEntry.buildUri and MovieEntry.buildWithId
        Cursor buildMovieCursor = mContext.getContentResolver().query(
                MovieEntry.buildMovieUri(TestUtilities.TEST_ID), null, null, null, null);
        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry.buildMovieUri.",
                buildMovieCursor, testValues);
        Cursor buildMovieIdCursor = mContext.getContentResolver().query(
                MovieEntry.buildMovieWithId(TestUtilities.TEST_ID), null, null, null, null);
        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry.buildMovieWithId.",
                buildMovieIdCursor, testValues);

        // do same cursor checks for TrailerEntry.buildTrailerUri and ReviewEntry.buildReviewUri
        Cursor buildTrailerCursor = mContext.getContentResolver().query(
                TrailerEntry.buildTrailersUri(0), null, null, null, null);
        TestUtilities.validateCursor("testInsertReadProvider. Error validating TrailerEntry.buildTrailersUri.",
                buildTrailerCursor, trailerValues);
        Cursor buildReviewCursor = mContext.getContentResolver().query(
                ReviewEntry.buildReviewUri(0), null, null, null, null);
        TestUtilities.validateCursor("tetsInsertReadProvider. Error validating ReviewEntry.buildReviewUri.",
                buildReviewCursor, reviewValues);
    }

    // Test we can still delete after doing inserts/updates.
    public void testDeleteRecords() {
        testInsertReadProvider();

        //register ContentObservers for our table deletes
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);
        TestUtilities.TestContentObserver trailerObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TrailerEntry.TRAILER_URI, true, trailerObserver);
        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.REVIEW_URI, true, reviewObserver);

        deleteAllRecordsFromProvider();

        // if any of these fail, you are most likely not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete. (only if the insertReadProvider is succeeding)
        reviewObserver.waitForNotificationOrFail();
        trailerObserver.waitForNotificationOrFail();
        movieObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(reviewObserver);
        mContext.getContentResolver().unregisterContentObserver(trailerObserver);
        mContext.getContentResolver().unregisterContentObserver(movieObserver);
    }

    // Create some bulk values to test.
    static private final int BULK_RECORDS_TO_INSERT = 5;

    static ContentValues[] createBulkTrailerValuesInsert(long movieId) {
        String TEST_ID = "54749bea9251414f41001b58";
        String TEST_KEY = "bvu-zlR5A8Q";
        String TEST_NAME = "Teaser";
        int[] TEST_SIZE = {320, 540, 720, 1080, 2160};

        ContentValues[] returnContentValues = new ContentValues[BULK_RECORDS_TO_INSERT];

        for (int i=0; i < BULK_RECORDS_TO_INSERT; i++){
            ContentValues trailerValues = new ContentValues();
            trailerValues.put(TrailerEntry.TRAILER_MOVIE_ID, movieId);
            trailerValues.put(TrailerEntry.TRAILER_ID, TEST_ID+"//"+i);
            trailerValues.put(TrailerEntry.TRAILER_KEY, TEST_KEY);
            trailerValues.put(TrailerEntry.TRAILER_NAME, TEST_NAME);
            trailerValues.put(TrailerEntry.TRAILER_SIZE, TEST_SIZE[i]);
            returnContentValues[i] = trailerValues;
        }
        return returnContentValues;
    }

    static ContentValues[] createBulkReviewValuesInsert(long movieId) {
        String TEST_ID = "55910381c3a36807f900065d";
        String TEST_CONTENT = "I was a huge fan of the original ";
        String TEST_AUTHOR = "jonlikesmoviesthatdontsuck";
        String TEST_URL = "https://www.themoviedb.org/review/55910381c3a36807f900065d";

        ContentValues[] returnContentValues = new ContentValues[BULK_RECORDS_TO_INSERT];

        for (int i=0; i < BULK_RECORDS_TO_INSERT; i++) {
            ContentValues reviewValues = new ContentValues();
            reviewValues.put(ReviewEntry.REVIEW_MOVIE_ID, movieId);
            reviewValues.put(ReviewEntry.REVIEW_ID,TEST_ID+"//"+i);
            reviewValues.put(ReviewEntry.REVIEW_CONTENT,TEST_CONTENT + i + " movies.");
            reviewValues.put(ReviewEntry.REVIEW_AUTHOR,TEST_AUTHOR);
            reviewValues.put(ReviewEntry.REVIEW_URL,TEST_URL);
            returnContentValues[i] = reviewValues;
        }

        return returnContentValues;
    }

    // Create the bulkInsert Test
    public void testBulkInsert() {
        // first create a movie value
        ContentValues testValues = TestUtilities.createMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);
        long movieId = ContentUris.parseId(movieUri);
        // Verify we got a row back
        assertTrue(movieId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating MovieEntry.",
                cursor, testValues);
        cursor.close();
        // now we can insert some bulk trailers .. with Content Providers you only need to
        // implement the features you use, so no need to bulk insert Movies as they'll be actioned
        // to the database one at a time.
        ContentValues[] bulkInsertTrailerValues = createBulkTrailerValuesInsert(movieId);

        // Register a content Observer for the bulk insert
        TestUtilities.TestContentObserver trailerObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TrailerEntry.TRAILER_URI, true, trailerObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(TrailerEntry.TRAILER_URI, bulkInsertTrailerValues);

        // If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        trailerObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(trailerObserver);

        assertEquals(insertCount, BULK_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor trailerCursor = mContext.getContentResolver().query(
                TrailerEntry.TRAILER_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                TrailerEntry.TRAILER_SIZE + " ASC"  // sort order == by SIZE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(trailerCursor.getCount(), BULK_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        trailerCursor.moveToFirst();
        for ( int i = 0; i < BULK_RECORDS_TO_INSERT; i++, trailerCursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating TrailerEntry " + i,
                    trailerCursor, bulkInsertTrailerValues[i]);
        }
        trailerCursor.close();

        // Now test the Review BulkInsert
        ContentValues[] bulkInsertReviewValues = createBulkReviewValuesInsert(movieId);

        // Register the content observer
        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.REVIEW_URI, true, reviewObserver);

        insertCount = mContext.getContentResolver().bulkInsert(ReviewEntry.REVIEW_URI, bulkInsertReviewValues);
        // check for failure.
        reviewObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(reviewObserver);
        assertEquals(insertCount, BULK_RECORDS_TO_INSERT);

        Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.REVIEW_URI,
                null,
                null,
                null,
                null
        );
        // we should have as many records in the databes as we've inserted.
        assertEquals(reviewCursor.getCount(), BULK_RECORDS_TO_INSERT);
        // and make sure they match the ones we created
        reviewCursor.moveToFirst();
        for (int i = 0; i < BULK_RECORDS_TO_INSERT; i++, reviewCursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert. Error validating ReviewEntry " + i,
                    reviewCursor, bulkInsertReviewValues[i]);
        }
        reviewCursor.close();

    }

    public void testOverwriteBulkInsert() {
        // First run the testBulkInsert() test.. if written correctly this will pass
        testBulkInsert();
        // Get the movie Id from the insert
        long movieId = 0;
        long trailerId = 0;
        long newTrailerId = 0;
        long reviewId = 0;
        long newReviewId = 0;
        int insertCount;
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                new String[] {MovieEntry.MOVIE_ID},
                null,
                null,
                null
        );
        if (cursor != null) {
            cursor.moveToFirst();
            movieId = cursor.getLong(0);
        }else{
            assertNull("testOverwriteBulkInsert: Error cursor returned null", cursor);
        }
        cursor.close();
        // get last  trailer ids
        Cursor trailerCursor = mContext.getContentResolver().query(
                TrailerEntry.TRAILER_URI,
                new String[] {TrailerEntry._ID},
                null,
                null,
                null
        );
        if (trailerCursor != null) {
            trailerCursor.moveToLast();
            trailerId = trailerCursor.getLong(0);
        }else{
            assertNull("testOverwriteBulkInsert: Error trailerCursor returned null", trailerCursor);
        }
        trailerCursor.close();

        // Now add the data again, it should overwrite the data and end with same number of records
        // but ids increased
        ContentValues[] bulkInsertTrailerValues = createBulkTrailerValuesInsert(movieId);

        // Register a content Observer for the bulk insert
        TestUtilities.TestContentObserver trailerObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TrailerEntry.TRAILER_URI, true, trailerObserver);

        insertCount = mContext.getContentResolver().bulkInsert(TrailerEntry.TRAILER_URI, bulkInsertTrailerValues);

        // If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        trailerObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(trailerObserver);

        assertEquals(insertCount, BULK_RECORDS_TO_INSERT);

        // get new last trailer inputs
        Cursor newTrailerCursor = mContext.getContentResolver().query(
                TrailerEntry.TRAILER_URI,
                new String[] {TrailerEntry._ID},
                null,
                null,
                null
        );
        if (newTrailerCursor != null) {
            // test only new records exist
            assertEquals(newTrailerCursor.getCount(), BULK_RECORDS_TO_INSERT);
            // get the new last _id
            newTrailerCursor.moveToLast();
            newTrailerId = newTrailerCursor.getLong(0);
        }else{
            assertNull("testOverwriteBulkInsert: Error newTrailerCursor returned null", newTrailerCursor);
        }
        newTrailerCursor.close();
        // check the autoincrement has worked.
        assertEquals(newTrailerId, trailerId + BULK_RECORDS_TO_INSERT);

        // Now test the Review BulkInsert

        // get the review id first
        Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.REVIEW_URI,
                new String[] {ReviewEntry._ID},
                null,
                null,
                null
        );
        if (reviewCursor != null) {
            reviewCursor.moveToLast();
            reviewId = reviewCursor.getLong(0);
        }else{
            assertNull("testOverwriteBulkInsert: Error reviewCursor returned null", reviewCursor);
        }
        reviewCursor.close();

        // insert review data again
        ContentValues[] bulkInsertReviewValues = createBulkReviewValuesInsert(movieId);

        // Register the content observer
        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.REVIEW_URI, true, reviewObserver);

        insertCount = mContext.getContentResolver().bulkInsert(ReviewEntry.REVIEW_URI, bulkInsertReviewValues);
        // check for failure.
        reviewObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(reviewObserver);
        assertEquals(insertCount, BULK_RECORDS_TO_INSERT);
        // get new review ids
        Cursor newReviewCursor = mContext.getContentResolver().query(
                ReviewEntry.REVIEW_URI,
                new String[] {ReviewEntry._ID},
                null,
                null,
                null
        );
        if (newReviewCursor != null) {
            // test only the new review records exist
            assertEquals(newReviewCursor.getCount(), BULK_RECORDS_TO_INSERT);
            // now get the last new _id
            newReviewCursor.moveToLast();
            newReviewId = newReviewCursor.getLong(0);
        }else{
            assertNull("testOverwriteBulkInsert: Error newReviewCursor returned null", newReviewCursor);
        }
        newReviewCursor.close();
        // check the autoincrement has worked
        assertEquals(newReviewId, reviewId+BULK_RECORDS_TO_INSERT);

    }

}
