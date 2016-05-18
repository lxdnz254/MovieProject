package com.lxdnz.nz.movieproject.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lxdnz.nz.movieproject.data.MovieContract.MovieEntry;
import com.lxdnz.nz.movieproject.data.MovieContract.TrailerEntry;
import com.lxdnz.nz.movieproject.data.MovieContract.ReviewEntry;

/**
 * Created by alex on 14/05/16.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "favorite_movies.db";

    public MovieDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // create the movie table which will link through to trailer and review tables
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.MOVIE_ID + " INTEGER NOT NULL, " +
                MovieEntry.MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.MOVIE_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.MOVIE_BACKDROP_PATH + " TEXT NOT NULL, " +
                MovieEntry.MOVIE_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.MOVIE_VOTE_COUNT + " INTEGER NOT NULL); "; //+



                // to assure the app has only one entry per movie selected, its created
                // a PRIMARY KEY constraint with REPLACE strategy.
                // "PRIMARY KEY ON CONFLICT REPLACE);";

        // create trailer table
        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrailerEntry.TRAILER_MOVIE_ID + " INTEGER NOT NULL, " +
                TrailerEntry.TRAILER_ID + " TEXT NOT NULL, " +
                TrailerEntry.TRAILER_KEY + " TEXT NOT NULL, " +
                TrailerEntry.TRAILER_NAME + " TEXT NOT NULL, " +
                TrailerEntry.TRAILER_SIZE + " INTEGER NOT NULL, " +

                // set up the movie_id column as a foreign key to trailer & review tables
                " FOREIGN KEY (" + TrailerEntry.TRAILER_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.MOVIE_ID + "), " +

                // assign unique id to each trailer
                " UNIQUE (" + TrailerEntry.TRAILER_ID + ") ON CONFLICT REPLACE);";

        // create review table
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewEntry.REVIEW_MOVIE_ID + " INTEGER NOT NULL, " +
                ReviewEntry.REVIEW_ID + " TEXT NOT NULL, " +
                ReviewEntry.REVIEW_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.REVIEW_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.REVIEW_URL + " TEXT NOT NULL, " +

                // set up the movie_id column as a foreign key to trailer & review tables
                " FOREIGN KEY (" + ReviewEntry.REVIEW_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.MOVIE_ID + "), " +

                // assign unique id to each review
                " UNIQUE (" + ReviewEntry.REVIEW_ID + ") ON CONFLICT REPLACE);";


        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
        db.execSQL("PRAGMA recursive_triggers = 'ON';");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.

        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(db);

    }
}
