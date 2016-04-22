package com.lxdnz.nz.movieproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by alex on 17/04/16.
 */
public class ImageAdapter extends ArrayAdapter<Movie> {

    private Context mContext;
    private String imageUrl;
    int layoutResourceId;
    Movie data[] = null;


    public ImageAdapter(Context context, int layoutResourceId, Movie[] data){
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = context;
        this.data = data;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        // check to see if we have a view
        if (convertView == null) {
            //no view so create one

            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            imageView = (ImageView) inflater.inflate(layoutResourceId, parent, false);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);

        } else {
            // use the recycled image view
            imageView = (ImageView) convertView;
        }

        if (data[position] == null){
            Picasso.with(mContext).load(R.mipmap.ic_launcher).into(imageView);
        }else{

        imageUrl = "http://image.tmdb.org/t/p/w185/"+data[position].getPoster_path();

        Picasso.with(mContext)
                .load(imageUrl)
                .into(imageView);
        }
        return imageView;
    }




}
