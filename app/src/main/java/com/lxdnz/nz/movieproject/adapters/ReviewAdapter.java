package com.lxdnz.nz.movieproject.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lxdnz.nz.movieproject.R;
import com.lxdnz.nz.movieproject.objects.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 29/05/16.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final static String LOG_TAG = ReviewAdapter.class.getSimpleName();

    private final ArrayList<Review> mReviews;
    private final Callbacks mCallbacks;

    public ReviewAdapter(ArrayList<Review> reviews, Callbacks callbacks) {
        mReviews = reviews;
        mCallbacks = callbacks;
    }

    public interface Callbacks {
        void read(Review review, int position);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Review review = mReviews.get(position);


        holder.mReview = review;
        holder.mContentView.setText(review.getReviewContent());
        holder.mAuthorView.setText(review.getAuthor());

        if (holder.mView != null) {
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallbacks.read(review, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_content, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        TextView mContentView;
        TextView mAuthorView;
        public Review mReview;

        public ViewHolder(View view){
            super(view);
            mContentView = (TextView) view.findViewById(R.id.review_content);
            mAuthorView = (TextView) view.findViewById(R.id.review_author);
            mView = view.findViewById(R.id.review_list_layout);
        }

    }

    public void add(List<Review> reviews) {
        mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    public ArrayList<Review> getReviews() {
        return mReviews;
    }
}
