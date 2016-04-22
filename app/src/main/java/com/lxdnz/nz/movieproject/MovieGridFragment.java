package com.lxdnz.nz.movieproject;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieGridFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieGridFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context mContext;
    private ImageAdapter imageAdapter;

    private OnFragmentInteractionListener mListener;

    public MovieGridFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieGridFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieGridFragment newInstance(String param1, String param2) {
        MovieGridFragment fragment = new MovieGridFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        Movie[] movies = new Movie[4];
        movies[0] = new Movie();
        movies[1] = new Movie();
        movies[2] = new Movie();
        movies[3] = new Movie();

        GridView gridview = (GridView) rootview.findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(getActivity(), R.layout.grid_view_thumbnail, movies);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "Clicked on " + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Inflate the layout for this fragment
        return rootview;
    }

    private void updateMovies() {
        FetchMovieData movieTask = new FetchMovieData();
        movieTask.execute(getString(R.string.popular));
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }


    /**
     * Blank out to implement later


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class FetchMovieData extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = FetchMovieData.class.getSimpleName();

        @Override
        protected List<Movie> doInBackground(String... param) {


            // if there's no string then there's nothing to look up. Verify size of params
            if (param.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            List<Movie> movieJsonStr = new ArrayList<>();


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

                try {
                    movieJsonStr = new ArrayList<>(MovieParser.getMovieDataFromJson(buffer.toString()));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }


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


            return movieJsonStr;
        }

        @Override
        protected void onPostExecute(List<Movie> result) {
            super.onPostExecute(result);

            if (result != null) {
                if (imageAdapter != null ) {
                  //  imageAdapter.clear();
                    Log.v(LOG_TAG, "should clear imageAdapter");
                }
                imageAdapter.addAll(result);
            }
        }
    }


}
