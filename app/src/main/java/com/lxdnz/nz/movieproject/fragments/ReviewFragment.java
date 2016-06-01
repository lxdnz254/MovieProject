package com.lxdnz.nz.movieproject.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lxdnz.nz.movieproject.R;
import com.lxdnz.nz.movieproject.adapters.ReviewAdapter;
import com.lxdnz.nz.movieproject.async.FetchReviewData;
import com.lxdnz.nz.movieproject.objects.Movie;
import com.lxdnz.nz.movieproject.objects.Review;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 29/05/16.
 */
public class ReviewFragment extends Fragment implements ReviewAdapter.Callbacks{

    /**
     * The fragment argument representing the movie that this fragment
     * represents.
     */
    public static final String ARG_MOVIE = "ARG_MOVIE";
    public static final String EXTRA_TRAILERS = "EXTRA_TRAILERS";
    public static final String EXTRA_REVIEWS = "EXTRA_REVIEWS";

    private static final String LOG_TAG = ReviewFragment.class.getSimpleName();

    private Movie mMovie;
    private Context mContext;
    private ReviewAdapter mReviewAdapter;
    private RecyclerView mRecylerView;
    private Review[] mReviews;


    public ReviewFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MOVIE)) {
            mMovie = getArguments().getParcelable(ARG_MOVIE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);

        mContext = getContext();
        mRecylerView = (RecyclerView) rootView.findViewById(R.id.review_list);
        mReviewAdapter = new ReviewAdapter(new ArrayList<Review>(), this);
        mRecylerView.setAdapter(mReviewAdapter);

        // Fetch reviews only if savedInstanceState == null
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_REVIEWS)) {
            List<Review> reviews = savedInstanceState.getParcelableArrayList(EXTRA_REVIEWS);
            mReviewAdapter.add(reviews);
        } else {
            fetchReviews();
        }

        return rootView;
    }

    private void fetchReviews() {
        FetchReviewData task = new FetchReviewData(mContext, mReviews);
        task.setListener(new FetchReviewData.Listener() {
            @Override
            public void onFetchReviewsFinished(Review[] reviews) {
                if (reviews != null) {
                    List listReviews = Arrays.asList(reviews);
                    mReviewAdapter.add(listReviews);
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (long)mMovie.getId());

    }

    @Override
    public void read(Review review, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(review.getUrl())));
    }
    
    
}
