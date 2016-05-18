package com.lxdnz.nz.movieproject;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;

import com.lxdnz.nz.movieproject.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 18/05/16.
 */
class FetchMovieData extends AsyncTask<String, Void, Movie[]> {

    private MovieGridFragment movieGridFragment;
    public Context mContext;

    private final String LOG_TAG = FetchMovieData.class.getSimpleName();

    private static final String SCHEME = "http";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String AUTHORITY_IMAGE = "image.tmdb.org";
    private static final String VERSION = "3";
    private static final String MOVIE = "movie";
    private static final String API_KEY = "api_key";
    private static final String T = "t";
    private static final String P = "p";

    public FetchMovieData(MovieGridFragment movieGridFragment, Context context) {
        this.movieGridFragment = movieGridFragment;
        this.mContext = context;

    }

    /**
     * Helper method to insert favorite Movies into the database
     *
     * @param favMovie the Movie Object being inserted
     * @return the row ID of the favorite Movie
     */
    long markAsFavorite(Movie favMovie){
        long favMovieId;
        String stringId = Integer.toString(favMovie.getId());

        // first check if it already exist in database

            Cursor favoriteCursor = mContext.getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{MovieContract.MovieEntry.MOVIE_ID + " as _id"},
                    MovieContract.MovieEntry.COL_MOVIE_ID + "= ?",
                    new String[]{stringId},
                    null
            );



        if (favoriteCursor.moveToFirst()){
            int favMovieIdIndex = favoriteCursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_ID);
            Log.v(LOG_TAG, "cursor exists, index is:"+favMovieIdIndex);
            favMovieId = favoriteCursor.getLong(favMovieIdIndex);
        } else {
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
        }
        favoriteCursor.close();


        // Wait, that worked? Yes!
        return favMovieId;
    }

    @Override
    protected Movie[] doInBackground(String... param) {


        // if there's no string then there's nothing to look up. Verify size of params
        if (param.length == 0) {
            return null;
        }

        // These three need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultFromFetch;

        try

        {

            // Construct the URL for the themoviedb.or query
            // Possible parameters are avaiable at tmdb's API page, at
            // http://www.themoviedb.org/documentation/API

            Uri uri = new Uri.Builder().scheme(SCHEME)
                    .authority(AUTHORITY)
                    .appendPath(VERSION)
                    .appendPath(MOVIE)
                    .appendPath(param[0])
                    .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
                    .build();

            Log.v(LOG_TAG, "uri = " + uri.toString());

            URL url = new URL(uri.toString());

            // Create the request to OpenWeatherMap, and open the connection
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
                return parseResponse(resultFromFetch);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


        } catch (IOException e) {
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


        return null;
    }

    private Movie[] parseResponse(String resultFromFetch) throws JSONException {
        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String ID = "id";
        final String ORIGINAL_TITLE = "original_title";
        final String RELEASE_DATE = "release_date";
        final String TITLE = "title";
        final String BACKDROP_PATH = "backdrop_path";
        final String VOTE_AVERAGE = "vote_average";
        final String VOTE_COUNT = "vote_count";

        JSONObject fetchResult = new JSONObject(resultFromFetch);
        JSONArray results = fetchResult.getJSONArray(RESULTS);

        Movie[] arrayOfMovies = new Movie[results.length()];

        for (int i = 0; i < arrayOfMovies.length; ++i) {
            JSONObject movieToParse = results.getJSONObject(i);


            String overview = movieToParse.getString(OVERVIEW);
            int id = movieToParse.getInt(ID);
            String originalTitle = movieToParse.getString(ORIGINAL_TITLE);
            String releaseDate = movieToParse.getString(RELEASE_DATE);
            String title = movieToParse.getString(TITLE);

            double voteAverage = movieToParse.getDouble(VOTE_AVERAGE);
            int voteCount = movieToParse.getInt(VOTE_COUNT);

            Uri uriBackdrop = new Uri.Builder().scheme(SCHEME)
                    .authority(AUTHORITY_IMAGE)
                    .appendPath(T)
                    .appendPath(P)
                    .appendPath(movieGridFragment.getString(R.string.preference_preview_width_default))
                    .appendEncodedPath(movieToParse.getString(BACKDROP_PATH))
                    .build();
            String backdropPath = uriBackdrop.toString();

            Uri uriPoster = new Uri.Builder().scheme(SCHEME)
                    .authority(AUTHORITY_IMAGE)
                    .appendPath(T)
                    .appendPath(P)
                    .appendPath(movieGridFragment.dimension)
                    .appendEncodedPath(movieToParse.getString(POSTER_PATH))
                    .build();
            String posterPath = uriPoster.toString();
            arrayOfMovies[i] = new Movie(title,
                    originalTitle,
                    releaseDate,
                    overview,
                    posterPath,
                    backdropPath,
                    id,
                    voteCount,
                    voteAverage);
        }

        return arrayOfMovies;
    }

    @Override
    protected void onPostExecute(Movie[] result) {
        super.onPostExecute(result);

        if (result != null) {
            movieGridFragment.imageAdapter = new ImageAdapter(movieGridFragment.getActivity(), Arrays.asList(result));
            movieGridFragment.gridView.setAdapter(movieGridFragment.imageAdapter);
        }
    }
}
