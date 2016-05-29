package com.lxdnz.nz.movieproject.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lxdnz.nz.movieproject.R;
import com.lxdnz.nz.movieproject.objects.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 29/05/16.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private final static String LOG_TAG = TrailerAdapter.class.getSimpleName();
    private final ArrayList<Trailer> mTrailers;
    private final Callbacks mCallbacks;

    public interface Callbacks {
        void watch(Trailer trailer, int position);
    }

    public TrailerAdapter(ArrayList<Trailer> trailers, Callbacks callbacks) {
        mTrailers = trailers;
        mCallbacks = callbacks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Trailer trailer = mTrailers.get(position);
        final Context context = holder.mView.getContext();
        /*
        float paddingLeft = 0;
        if (position == 0) {
            paddingLeft = context.getResources().getDimension(R.dimen.detail_horizontal_padding);
        }

        float paddingRight = 0;
        if (position + 1 != getItemCount()) {
            paddingRight = context.getResources().getDimension(R.dimen.detail_horizontal_padding) / 2;
        }

        holder.mView.setPadding((int) paddingLeft, 0, (int) paddingRight, 0);
        */
        holder.mTrailer = trailer;

        String thumbnailUrl = "http://img.youtube.com/vi/" + trailer.getTrailerKey() + "/0.jpg";
        Log.i(LOG_TAG, "thumbnailUrl -> " + thumbnailUrl);

        Picasso.with(context)
                .load(thumbnailUrl)
                .config(Bitmap.Config.RGB_565)
                .into(holder.mThumbnailView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.watch(trailer, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        ImageView mThumbnailView;
        public Trailer mTrailer;

        public ViewHolder(View itemView) {
            super(itemView);
            mThumbnailView = (ImageView) itemView.findViewById(R.id.trailer_thumbnail);
            mView = itemView.findViewById(R.id.trailer_list_layout);
        }
    }

    public void add(List<Trailer> trailers) {
        mTrailers.clear();
        mTrailers.addAll(trailers);
        notifyDataSetChanged();
    }

    public ArrayList<Trailer> getTrailers() {
        return mTrailers;
    }
}
