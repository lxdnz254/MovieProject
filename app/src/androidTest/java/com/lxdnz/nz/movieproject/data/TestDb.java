package com.lxdnz.nz.movieproject.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by alex on 15/05/16.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // since we want to start with a clean slate each test
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);

    }

    /**
     * this function gets called before each test. it executes the delete the database call
     *
     */

    public void setUp() {
        deleteTheDatabase();
    }

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

        // now do our tables contain the correct columns? Test Movie Table first
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")", null);
        assertTrue("Error: unable to query the database for Trailer information.",
                c.moveToFirst());

        // build HashSets of the columns we wish to look for.
        final HashSet<String> movieColumnHashSet = new HashSet<>();

        movieColumnHashSet.add(MovieContract.MovieEntry.MOVIE_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.MOVIE_ORIGINAL_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.MOVIE_OVERVIEW);
        movieColumnHashSet.add(MovieContract.MovieEntry.MOVIE_RELEASE_DATE);
        movieColumnHashSet.add(MovieContract.MovieEntry.MOVIE_POSTER_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.MOVIE_BACKDROP_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.MOVIE_VOTE_AVERAGE);
        movieColumnHashSet.add(MovieContract.MovieEntry.MOVIE_VOTE_COUNT);

        int movieColumnIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(movieColumnIndex);
            movieColumnHashSet.remove(columnName);
        } while ( c.moveToNext() );

        // if this fails, the database doesn't contain all the movie entry columns required
        assertTrue("Error: the database doesn't contain all the required Movie entry columns",
                movieColumnHashSet.isEmpty());
        c.close();

        // Now test Trailer Table
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
        c.close();

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
        c.close();
        // now close the database
        db.close();
    }

    public void testMovieTable() {
        insertMovie();
    }

    public void testTrailerTable() {
        // insert movie first and get back the movie_id to insert in to Trailer table
        int movieId = insertMovie();
        // make sure we have a valid movie_id
        assertFalse("Error: Movie not inserted correctly", movieId == -1);

        long trailerInsert = TestUtilities.insertTrailerValues(mContext, movieId);

        assertFalse("Error: Trailer not inserted correctly", trailerInsert == -1L);
    }

    public void testReviewTable() {
        // insert movie first and get back movie_id to insert in to Review table
        int movieId = insertMovie();
        // make sure we have a valid movie_id
        assertFalse("Error: Movie not inserted correctly", movieId == -1);

        long reviewInsert = TestUtilities.insertReviewValues(mContext, movieId);

        assertFalse("Error: Review not inserted correctly", reviewInsert == -1L);
    }

    public int insertMovie(){
        return TestUtilities.insertMovieValues(mContext);
    }
}
