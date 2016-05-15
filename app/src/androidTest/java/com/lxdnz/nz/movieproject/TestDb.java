package com.lxdnz.nz.movieproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.lxdnz.nz.movieproject.data.MovieContract;
import com.lxdnz.nz.movieproject.data.MovieDBHelper;

import java.util.HashSet;

/**
 * Created by alex on 15/05/16.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Note that this only tests the creation of Trailer & Review databases
    // as the code for the Movie Db is supplied in Utilities.

    public void testCreateDb() throws Throwable {

        // build a hash set of all the tables we wish to look for
        final HashSet<String> tableNameHashSet = new HashSet<>();

        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.TrailerEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.ReviewEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDBHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we wanted?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'",null);
        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        }while( c.moveToNext() );

        // if this fails then the database doesn't contain all the tables.
        assertTrue("Error: the database is missing tables", tableNameHashSet.isEmpty());

        // now do our tables contain the correct columns? Test Trailer Table first
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.TrailerEntry.TABLE_NAME + ")", null);
        assertTrue("Error: unable to query the database for Trailer information.",
                c.moveToFirst());

        // build HashSets of the columns we wish to look for.
        final HashSet<String> trailerColumnHashSet = new HashSet<>();

        trailerColumnHashSet.add(MovieContract.TrailerEntry._ID);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.TRAILER_ID);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.TRAILER_MOVIE_ID);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.TRAILER_KEY);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.TRAILER_NAME);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.TRAILER_SIZE);

        int trailerColumnIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(trailerColumnIndex);
            trailerColumnHashSet.remove(columnName);
        }while ( c.moveToNext() );

        // if this fails, the database doesn't contain all trailer entry columns required
        assertTrue("Error: the database doesn't contain all the required Trailer entry columns",
                trailerColumnHashSet.isEmpty());

        // Now test Review Table
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.ReviewEntry.TABLE_NAME + ")", null);
        assertTrue("Error: unable to query the database for Review information.",
                c.moveToFirst());

        final HashSet<String> reviewColumnHashSet = new HashSet<>();

        reviewColumnHashSet.add(MovieContract.ReviewEntry._ID);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.REVIEW_ID);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.REVIEW_MOVIE_ID);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.REVIEW_AUTHOR);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.REVIEW_CONTENT);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.REVIEW_URL);

        int reviewColumnIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(reviewColumnIndex);
            reviewColumnHashSet.remove(columnName);
        }while ( c.moveToNext() );

        // if this fails, the database doesn't contain all review entry columns required.
        assertTrue("Error: the database doesn't contain all the required Review entry columns",
                reviewColumnHashSet.isEmpty());
        db.close();
    }
}
