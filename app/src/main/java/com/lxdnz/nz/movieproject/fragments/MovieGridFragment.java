package com.lxdnz.nz.movieproject.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.lxdnz.nz.movieproject.MainActivity;
import com.lxdnz.nz.movieproject.MovieDetailActivity;
import com.lxdnz.nz.movieproject.R;
import com.lxdnz.nz.movieproject.adapters.ImageAdapter;
import com.lxdnz.nz.movieproject.async.FetchMovieData;
import com.lxdnz.nz.movieproject.objects.Movie;

import java.util.List;


public class MovieGridFragment extends Fragment implements ImageAdapter.Callbacks{

    private static final String LOG_CAT = MovieGridFragment.class.getSimpleName();
    public GridView gridView;
    public String dimension;
    public ImageAdapter imageAdapter;
    public Context mContext;
    private MainActivity activity;



    public MovieGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
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

                Movie movieClicked = imageAdapter.getMovie(position);
                Log.v(LOG_CAT, "movie clicked");
                open(movieClicked, position);
                /*
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra("clickedMovie", movieClicked);
                startActivity(intent);
                */
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    private void updateMovies() {
        FetchMovieData movieTask = new FetchMovieData(this, mContext);
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

    @Override
    public void open(Movie movie, int position) {
        if (activity.mTwoPane) {
            Log.v(LOG_CAT, "using two pane movie clicked");
            Bundle arguments = new Bundle();
            arguments.putParcelable("clickedMovie", movie);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.movie_container, fragment)
                    .commit();
        } else {
            Log.v(LOG_CAT, "Sending intent movie clicked");
            Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
            intent.putExtra("clickedMovie", movie);
            startActivity(intent);
        }
    }
}
