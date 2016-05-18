package com.lxdnz.nz.movieproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;


public class MovieGridFragment extends Fragment {

    private static final String LOG_CAT = MovieGridFragment.class.getSimpleName();
    public GridView gridView;
    public String dimension;
    public ImageAdapter imageAdapter;
    public Context mContext;


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

                Movie movieClicked = imageAdapter.getMovie(position);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra("clickedMovie", movieClicked);
                startActivity(intent);
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

}
