package com.lxdnz.nz.movieproject.data;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
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

}
