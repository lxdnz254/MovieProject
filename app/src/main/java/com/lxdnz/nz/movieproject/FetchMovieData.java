package com.lxdnz.nz.movieproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by alex on 18/04/16.
 */
public class FetchMovieData extends AsyncTask<String, Void, String []> {

    private final String LOG_TAG = FetchMovieData.class.getSimpleName();



    private String [] getMovieDataFromJson(String movieJson)
        throws JSONException {

        return null;
    }


    @Override
    protected String[] doInBackground(String... param) {



        // if there's no string then there's nothing to look up. Verify size of params
        if (param.length == 0) {
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;



        try

        {
            // Construct the URL for the themoviedb.or query
            // Possible parameters are avaiable at tmdb's API page, at
            // http://www.themoviedb.org/documentation/API

            final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String QUERY_PARAM = param[0];
            final String API_PARAM = "?api_key=";

            Uri builtUri = Uri.parse(TMDB_BASE_URL + QUERY_PARAM +
                    API_PARAM + BuildConfig.TMDB_API_KEY).buildUpon()
                    .build();

            Log.v(LOG_TAG, "uri = "+builtUri.toString());

            URL url = new URL(builtUri.toString());

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
            movieJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attemping
            // to parse it.
            return null;
        }

        finally

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

        try {
            return getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.

        return null;
    }

    @Override
    protected void onPostExecute(String [] result) {
        super.onPostExecute(result);
    }
}
