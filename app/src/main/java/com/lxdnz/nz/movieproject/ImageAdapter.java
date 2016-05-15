package com.lxdnz.nz.movieproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by alex on 17/04/16.
 */
public class ImageAdapter extends ArrayAdapter<Movie> {

    private Context mContext;
    private List<Movie> movies;

    public ImageAdapter(Context context, List<Movie> movies){
        super(context, 0, movies);

        this.mContext = context;
        this.movies = movies;
    }

    public Movie getMovie(int position) {
        return movies.get(position);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movieItem = getItem(position);


        // check to see if we have a view
        if (convertView == null) {
            //no view so create one

            convertView = LayoutInflater.from(mContext).inflate(R.layout.my_image_view, parent, false);
        }

        Picasso.with(mContext)
                .load(movieItem.getPoster_path())
                .into((ImageView) convertView.findViewById(R.id.image_view_thumbnail));

        return convertView;
    }




}
