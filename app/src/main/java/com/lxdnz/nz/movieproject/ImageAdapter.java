package com.lxdnz.nz.movieproject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by alex on 17/04/16.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    // Constructor
    public ImageAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        // check to see if we have a view
        if (convertView == null) {
            //no view so create one
            imageView = new ImageView(mContext);
        } else {
            // use the recycled image view
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext)
                .load("http://i.imgur.com/DvpvklR.png")
                .into(imageView);

        return imageView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
}
