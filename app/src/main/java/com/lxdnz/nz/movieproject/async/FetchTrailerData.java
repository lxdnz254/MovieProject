package com.lxdnz.nz.movieproject.async;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.lxdnz.nz.movieproject.BuildConfig;
import com.lxdnz.nz.movieproject.data.MovieContract;
import com.lxdnz.nz.movieproject.objects.Trailer;
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
public class FetchTrailerData extends AsyncTask<Long, Void, Trailer[]> {

    private Context mContext;
    private Trailer[] mTrailers;
    private static final String SCHEME = "http";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String VERSION = "3";
    private static final String MOVIE = "movie";
    private static final String TRAILER = "videos";
    private static final String API_KEY = "api_key";

    private final String LOG_TAG = FetchTrailerData.class.getSimpleName();

    public FetchTrailerData(Context context, Trailer[] trailers) {
        this.mContext = context;
        this.mTrailers = trailers;
    }

    @Override
    protected Trailer[] doInBackground(Long... params) {

        Trailer[] arrayofTrailers = new Trailer[0];
        if (params == null) {
            return null;
        } else {
            String movieId = params+"";
            if (new Utilities(mContext).isFavorite(movieId)){
                // fetch existing data from local database
                String [] columnString = new String[]{
                        MovieContract.TrailerEntry.TRAILER_MOVIE_ID,
                        MovieContract.TrailerEntry.TRAILER_ID,
                        MovieContract.TrailerEntry.TRAILER_KEY,
                        MovieContract.TrailerEntry.TRAILER_NAME,
                        MovieContract.TrailerEntry.TRAILER_SIZE
                };

                Cursor trailerCursor = mContext.getContentResolver().query(
                        MovieContract.TrailerEntry.TRAILER_URI,
                        columnString,
                        MovieContract.TrailerEntry.TRAILER_MOVIE_ID + " = ?",
                        new String[]{movieId},
                        null
                );
                if (trailerCursor != null) {
                    arrayofTrailers = new Trailer[trailerCursor.getCount()];
                    trailerCursor.moveToFirst();
                    int i=0;
                    do{
                        int trailerMovieId = trailerCursor.getInt(0);
                        String trailerId = trailerCursor.getString(1);
                        String trailerKey = trailerCursor.getString(2);
                        String trailerName = trailerCursor.getString(3);
                        int trailerSize = trailerCursor.getInt(4);
                        arrayofTrailers[i]= new Trailer(trailerMovieId,
                                trailerId, trailerKey, trailerName, trailerSize);
                        i++;

                    }while (trailerCursor.moveToNext());
                    trailerCursor.close();
                }else{
                    trailerCursor.close();
                    arrayofTrailers = null;
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
                            .appendPath(TRAILER)
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
        return arrayofTrailers; // the trailers should be returned here
    }

    private Trailer[] parseResponse(String resultFromFetch, Long movieId) throws JSONException{
        final String RESULTS = "results";
        final String TRAILER_ID = "id";
        final String TRAILER_KEY = "key";
        final String TRAILER_NAME = "name";
        final String TRAILER_SIZE = "size";

        String mainId = Long.toString(movieId);
        int mainInt = Integer.parseInt(mainId);

        JSONObject fetchResult = new JSONObject(resultFromFetch);
        JSONArray results = fetchResult.getJSONArray(RESULTS);

        Trailer[] trailerArray = new Trailer[results.length()];

        for (int i=0; i < trailerArray.length; i++){
            JSONObject trailerToParse = results.getJSONObject(i);

            String trailerid = trailerToParse.getString(TRAILER_ID);
            String trailerKey = trailerToParse.getString(TRAILER_KEY);
            String trailerName = trailerToParse.getString(TRAILER_NAME);
            int trailerSize = trailerToParse.getInt(TRAILER_SIZE);

            trailerArray[i] = new Trailer(mainInt, trailerid, trailerKey, trailerName, trailerSize);

        }

        return trailerArray;

    }

    @Override
    protected void onPostExecute(Trailer[] trailers) {
        if (trailers != null){
            for (int i=0; i < trailers.length; i++){
                mTrailers[i] = trailers[i];
            }
        }

    }
}
