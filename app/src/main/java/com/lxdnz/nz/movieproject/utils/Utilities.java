package com.lxdnz.nz.movieproject.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.lxdnz.nz.movieproject.async.FetchReviewData;
import com.lxdnz.nz.movieproject.async.FetchTrailerData;
import com.lxdnz.nz.movieproject.data.MovieContract;
import com.lxdnz.nz.movieproject.objects.Movie;
import com.lxdnz.nz.movieproject.objects.Review;
import com.lxdnz.nz.movieproject.objects.Trailer;

/**
 * Created by alex on 23/05/16.
 */
public class Utilities {

    public Context mContext;
    public Trailer[] mTrailers;
    public Review[] mReviews;

    private static int insertTrailers;
    private static int insertReviews;

    public Utilities(Context context){
        this.mContext = context;
    }

    /**
     * Helper method to insert favorite Movies into the database
     *
     * @param favMovie the Movie Object being inserted
     * @return the row ID of the favorite Movie
     */
    public long markAsFavorite(Movie favMovie){
        long favMovieId;
        String stringId = Integer.toString(favMovie.getId());

        // first check if it already exist in database

        Cursor favoriteCursor = getCursor(stringId);

        if (favoriteCursor.moveToFirst()){
            int favMovieIdIndex = favoriteCursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_ID);
            favMovieId = favoriteCursor.getLong(favMovieIdIndex);
        } else {
            // Fetch trailer and review data
            FetchTrailerData ftd = new FetchTrailerData(mContext, mTrailers);
            FetchReviewData frd = new FetchReviewData(mContext, mReviews);

            ftd.execute((long) favMovie.getId());
            frd.execute((long)favMovie.getId());

            // now ContentProvider is set up, inserting rows is pretty simple
            // First create ContentValues object to hold the data you want to insert
            ContentValues favoriteValues = new ContentValues();

            // Then add the data, with the corresponding data type,
            // so that the ContentProvider knows what kind of data is being inserted.
            favoriteValues.put(MovieContract.MovieEntry.MOVIE_ID, favMovie.getId());
            favoriteValues.put(MovieContract.MovieEntry.MOVIE_TITLE, favMovie.getTitle());
            favoriteValues.put(MovieContract.MovieEntry.MOVIE_ORIGINAL_TITLE, favMovie.getOriginal_title());
            favoriteValues.put(MovieContract.MovieEntry.MOVIE_OVERVIEW, favMovie.getOverview());
            favoriteValues.put(MovieContract.MovieEntry.MOVIE_RELEASE_DATE, favMovie.getRelease_date());
            favoriteValues.put(MovieContract.MovieEntry.MOVIE_POSTER_PATH, favMovie.getPoster_path());
            favoriteValues.put(MovieContract.MovieEntry.MOVIE_BACKDROP_PATH, favMovie.getBackdrop_path());
            favoriteValues.put(MovieContract.MovieEntry.MOVIE_VOTE_AVERAGE, favMovie.getVote_average());
            favoriteValues.put(MovieContract.MovieEntry.MOVIE_VOTE_COUNT, favMovie.getVote_count());
            // Finally, insert movie data into the database
            Uri insertedUri = mContext.getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI,
                    favoriteValues
            );
            // the resulting URI contains the ID for the row, extract the Id from the URI
            favMovieId = ContentUris.parseId(insertedUri);

            // Now add the trailer and review data to the database. iterate over the returned array.
            if (mTrailers != null){
                ContentValues [] bulkTrailers = new ContentValues[mTrailers.length];
                for (int i=0; i < mTrailers.length; i++){
                    ContentValues trailerValues = new ContentValues();

                    trailerValues.put(MovieContract.TrailerEntry.TRAILER_MOVIE_ID, mTrailers[i].getMovieId());
                    trailerValues.put(MovieContract.TrailerEntry.TRAILER_ID, mTrailers[i].getTrailerId());
                    trailerValues.put(MovieContract.TrailerEntry.TRAILER_KEY, mTrailers[i].getTrailerKey());
                    trailerValues.put(MovieContract.TrailerEntry.TRAILER_NAME, mTrailers[i].getTrailerName());
                    trailerValues.put(MovieContract.TrailerEntry.TRAILER_SIZE, mTrailers[i].getTrailerSize());
                    bulkTrailers[i] = trailerValues;
                }
                insertTrailers = mContext.getContentResolver().bulkInsert(
                        MovieContract.TrailerEntry.TRAILER_URI,
                        bulkTrailers
                );
            }

            if (mReviews != null){
                ContentValues [] bulkReviews = new ContentValues[mReviews.length];
                for (int i = 0; i < mReviews.length; i++) {
                    ContentValues reviewValues = new ContentValues();

                    reviewValues.put(MovieContract.ReviewEntry.REVIEW_MOVIE_ID, mReviews[i].getMovieId());
                    reviewValues.put(MovieContract.ReviewEntry.REVIEW_ID, mReviews[i].getReviewId());
                    reviewValues.put(MovieContract.ReviewEntry.REVIEW_AUTHOR, mReviews[i].getAuthor());
                    reviewValues.put(MovieContract.ReviewEntry.REVIEW_CONTENT, mReviews[i].getReviewContent());
                    reviewValues.put(MovieContract.ReviewEntry.REVIEW_URL, mReviews[i].getUrl());
                    bulkReviews[i] = reviewValues;
                }
                insertReviews = mContext.getContentResolver().bulkInsert(
                        MovieContract.ReviewEntry.REVIEW_URI,
                        bulkReviews
                );
            }
        }
        favoriteCursor.close();

        // Wait, that worked? Yes!
        return favMovieId;
    }

    public Cursor getCursor(String stringId) {
        return mContext.getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{MovieContract.MovieEntry.MOVIE_ID},
                    MovieContract.MovieEntry.MOVIE_ID + " = ?",
                    new String[]{stringId},
                    null
            );
    }

    public boolean isFavorite(String movieId) {
        Cursor favCursor = getCursor(movieId);
        if (favCursor.moveToFirst()){
            return true;
        }
        return false;
    }

    public static int getTrailerCount() {

        return insertTrailers;
    }

    public static int getReviewCount() {

        return insertReviews;
    }
}
