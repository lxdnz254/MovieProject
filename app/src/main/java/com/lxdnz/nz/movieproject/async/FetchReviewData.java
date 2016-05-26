package com.lxdnz.nz.movieproject.async;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.lxdnz.nz.movieproject.BuildConfig;
import com.lxdnz.nz.movieproject.data.MovieContract;
import com.lxdnz.nz.movieproject.objects.Review;
import com.lxdnz.nz.movieproject.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by alex on 23/05/16.
 */
public class FetchReviewData extends AsyncTask<Long, Void, Review[]> {

    private Context mContext;
    private Review[] mReviews;
    private static final String SCHEME = "http";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String VERSION = "3";
    private static final String MOVIE = "movie";
    private static final String REVIEW = "reviews";
    private static final String API_KEY = "api_key";

    private final String LOG_TAG = FetchReviewData.class.getSimpleName();

    public FetchReviewData(Context context, Review[] reviews){
        this.mContext = context;
        this.mReviews = reviews;
    }

    @Override
    protected Review[] doInBackground(Long... params) {

        Review[] arrayOfReviews = new Review[0];
        if (params == null) {
            return null;
        } else {
            String movieId = params+"";
            if (new Utilities(mContext).isFavorite(movieId)){
                // fetch existing data from local database
                String[] columnString = new String[] {
                        MovieContract.ReviewEntry.REVIEW_MOVIE_ID,
                        MovieContract.ReviewEntry.REVIEW_ID,
                        MovieContract.ReviewEntry.REVIEW_AUTHOR,
                        MovieContract.ReviewEntry.REVIEW_CONTENT,
                        MovieContract.ReviewEntry.REVIEW_URL
                };

                Cursor reviewCursor = mContext.getContentResolver().query(
                        MovieContract.ReviewEntry.REVIEW_URI,
                        columnString,
                        MovieContract.ReviewEntry.REVIEW_MOVIE_ID + " = ?",
                        new String[] {movieId},
                        null
                );

                if (reviewCursor != null) {
                    arrayOfReviews = new Review[reviewCursor.getCount()];
                    reviewCursor.moveToFirst();
                    int i=0;
                    do {
                        int reviewMovieId = reviewCursor.getInt(0);
                        String reviewId = reviewCursor.getString(1);
                        String reviewAuthor = reviewCursor.getString(2);
                        String reviewContent = reviewCursor.getString(3);
                        String reviewUrl = reviewCursor.getString(4);
                        arrayOfReviews[i] = new Review(reviewMovieId,
                                reviewId, reviewAuthor, reviewContent, reviewUrl);
                        i++;

                    }while (reviewCursor.moveToNext());
                    reviewCursor.close();
                }else{
                    reviewCursor.close();
                    arrayOfReviews = null;
                }

            }else{
                // fetch data from themoviedb.org
                // These three need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String resultFromFetch;

                try{
                    // Construct the URL for the themoviedb.or query
                    // Possible parameters are avaiable at tmdb's API page, at
                    // http://www.themoviedb.org/documentation/API

                    Uri uri = new Uri.Builder().scheme(SCHEME)
                            .authority(AUTHORITY)
                            .appendPath(VERSION)
                            .appendPath(MOVIE)
                            .appendPath(movieId)
                            .appendPath(REVIEW)
                            .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
                            .build();

                    Log.v(LOG_TAG, "uri = " + uri.toString());

                    URL url = new URL(uri.toString());

                    // Create the request to TheMovieDb, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    resultFromFetch = buffer.toString();
                    try {
                        return parseResponse(resultFromFetch, params[0]);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                        e.printStackTrace();
                    }

                }catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the movie data, there's no point in attempting
                    // to parse it.
                    return null;
                } finally

                {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }

            }
        }
        return arrayOfReviews; // the reviews should be returned here
    }

    private Review[] parseResponse(String resultFromFetch, Long movieId) throws JSONException{
        final String RESULTS = "results";
        final String REVIEW_ID = "id";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        final String REVIEW_URL = "url";

        String mainId = Long.toString(movieId);
        int mainInt = Integer.parseInt(mainId);

        JSONObject fetchResult = new JSONObject(resultFromFetch);
        JSONArray results = fetchResult.getJSONArray(RESULTS);

        Review[] reviewArray = new Review[results.length()];

        for (int i=0; i < reviewArray.length; i++){
            JSONObject reviewToParse = results.getJSONObject(i);

            String reviewId = reviewToParse.getString(REVIEW_ID);
            String reviewAuthor = reviewToParse.getString(REVIEW_AUTHOR);
            String reviewContent = reviewToParse.getString(REVIEW_CONTENT);
            String reviewUrl = reviewToParse.getString(REVIEW_URL);

            reviewArray[i] = new Review(mainInt, reviewId, reviewAuthor, reviewContent, reviewUrl);

        }

        return reviewArray;
    }

}
