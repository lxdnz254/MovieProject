package com.lxdnz.nz.movieproject.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lxdnz.nz.movieproject.R;
import com.lxdnz.nz.movieproject.adapters.TrailerAdapter;
import com.lxdnz.nz.movieproject.async.FetchTrailerData;
import com.lxdnz.nz.movieproject.objects.Movie;
import com.lxdnz.nz.movieproject.objects.Trailer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 29/05/16.
 */
public class TrailerFragment extends Fragment implements FetchTrailerData.Listener, TrailerAdapter.Callbacks{

    /**
     * The fragment argument representing the movie that this fragment
     * represents.
     */
    public static final String ARG_MOVIE = "ARG_MOVIE";
    public static final String EXTRA_TRAILERS = "EXTRA_TRAILERS";
    public static final String EXTRA_REVIEWS = "EXTRA_REVIEWS";

    private TrailerAdapter mTrailerAdapter;
    private Movie mMovie;
    private Context mContext;
    private Trailer[] mTrailers;
    private RecyclerView mRecyclerView;
    private ShareActionProvider mShareActionProvider;

    public TrailerFragment() {
    }


    //private Button mButtonWatchTrailer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MOVIE)) {
            mMovie = getArguments().getParcelable(ARG_MOVIE);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_trailer, container, false);

        mContext = getContext();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.trailer_list);

        mTrailerAdapter = new TrailerAdapter(new ArrayList<Trailer>(), this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(mTrailerAdapter);
        // Fetch trailers only if savedInstanceState == null
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TRAILERS)) {
            List<Trailer> trailers = savedInstanceState.getParcelableArrayList(EXTRA_TRAILERS);
            mTrailerAdapter.add(trailers);
            //mButtonWatchTrailer.setEnabled(true);
        } else {
            fetchTrailers();
        }

        return rootView;
    }

    private void fetchTrailers() {
        FetchTrailerData task = new FetchTrailerData(mContext, mTrailers);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (long)mMovie.getId());
    }

    @Override
    public void onFetchTrailersFinished(Trailer[] trailers) {
        List trailerList = Arrays.asList(trailers);
        mTrailerAdapter.add(trailerList);
        if (mTrailerAdapter.getItemCount() > 0) {
            Trailer trailer = mTrailerAdapter.getTrailers().get(0);
            updateShareActionProvider(trailer);
        }


    }

    @Override
    public void watch(Trailer trailer, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getTrailerUrl())));
    }

    private void updateShareActionProvider(Trailer trailer) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mMovie.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, trailer.getTrailerName() + ": "
                + trailer.getTrailerUrl());
        mShareActionProvider.setShareIntent(sharingIntent);
    }
}
