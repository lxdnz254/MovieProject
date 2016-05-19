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
        Log.v(LOG_TAG, "type is: "+type);
        // vnd.android.cursor.dir/com.lxdnz.nz.movieproject/movies
        assertEquals("Error: the MovieEntry CONTENT_URI with ID should return MovieEntry.CONTENT_TYPE",
                 MovieEntry.CONTENT_TYPE, type);

        // content://com.lxdnz.nz.movieproject/trailers/
        type = resolver.getType(TrailerEntry.TRAILER_URI);
        // vnd.android.cursor.dir/com.lxdnz.nz.movieproject/trailers
        assertEquals("Error: the TrailerEntry CONTENT_URI should return TrailerEnrty.CONTENT_TYPE",
                TrailerEntry.CONTENT_TYPE, type);
        // content://com.lxdnz.nz.movieproject/reviews/
        type = resolver.getType(ReviewEntry.REVIEW_URI);
        assertEquals("Error: the ReviewEntry CONTENT_URI should return ReviewEntry.CONTENT_TYPE",
                ReviewEntry.CONTENT_TYPE, type);
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



}
