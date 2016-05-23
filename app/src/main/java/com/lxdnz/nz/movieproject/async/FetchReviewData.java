package com.lxdnz.nz.movieproject.async;

import android.content.Context;
import android.os.AsyncTask;

import com.lxdnz.nz.movieproject.utils.Utilities;

/**
 * Created by alex on 23/05/16.
 */
public class FetchReviewData extends AsyncTask<Long, Void, Void> {

    private Context mContext;

    public FetchReviewData(Context context){
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(Long... params) {
        if (params == null) {
            return null;
        } else {
            String movieId = params+"";
            if (new Utilities(mContext).isFavorite(movieId)){
                // fetch existing data from local database

            }else{
                // fetch data from themoviedb.org
            }
        }
        return null; // the reviews should be returned here
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
