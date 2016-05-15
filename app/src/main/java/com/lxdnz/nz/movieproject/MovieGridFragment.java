package com.lxdnz.nz.movieproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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


public class MovieGridFragment extends Fragment {

    private static final String LOG_CAT = MovieGridFragment.class.getSimpleName();
    private GridView gridView;
    private String dimension;
    private ImageAdapter imageAdapter;


    public MovieGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridview);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        dimension = sharedPreferences.getString(getString(R.string.preference_preview_width),
                getString(R.string.preference_preview_width_default));
        gridView.setColumnWidth(convertDimension());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Movie movieClicked = imageAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra("clickedMovie", movieClicked);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    private void updateMovies() {
        FetchMovieData movieTask = new FetchMovieData();
        // get SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getActivity());
        String filter = sharedPreferences.getString(getString(R.string.preference_sorting_key),
                getString(R.string.preference_sorting_default));
        movieTask.execute(filter);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private int convertDimension() {
        return Integer.parseInt(dimension.substring(1));
    }

    private class FetchMovieData extends AsyncTask<String, Void, Movie []> {

        private final String LOG_TAG = FetchMovieData.class.getSimpleName();
        private static final String SCHEME = "http";
        private static final String AUTHORITY = "api.themoviedb.org";
        private static final String AUTHORITY_IMAGE = "image.tmdb.org";
        private static final String VERSION = "3";
        private static final String MOVIE = "movie";
        private static final String API_KEY = "api_key";
        private static final String T = "t";
        private static final String P = "p";

        @Override
        protected Movie [] doInBackground(String... param) {


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

                Log.v(LOG_TAG, "uri = "+uri.toString());

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
                        .appendPath(getString(R.string.preference_preview_width_default))
                        .appendEncodedPath(movieToParse.getString(BACKDROP_PATH))
                        .build();
                String backdropPath = uriBackdrop.toString();

                Uri uriPoster = new Uri.Builder().scheme(SCHEME)
                        .authority(AUTHORITY_IMAGE)
                        .appendPath(T)
                        .appendPath(P)
                        .appendPath(dimension)
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
        protected void onPostExecute(Movie [] result) {
            super.onPostExecute(result);

            if (result != null) {
               imageAdapter = new ImageAdapter(getActivity(), Arrays.asList(result));
                gridView.setAdapter(imageAdapter);
            }
        }
    }
}
