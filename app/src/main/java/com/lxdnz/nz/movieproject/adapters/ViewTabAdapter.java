package com.lxdnz.nz.movieproject.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lxdnz.nz.movieproject.fragments.ReviewFragment;
import com.lxdnz.nz.movieproject.fragments.TrailerFragment;
import com.lxdnz.nz.movieproject.objects.Movie;

/**
 * Created by alex on 29/05/16.
 */
public class ViewTabAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        Movie mMovie;
        Bundle args;
        Context mContext;
    public static final String ARG_MOVIE = "ARG_MOVIE";
    public static final String ARG_CONTEXT = "ARG_CONTEXT";

    public ViewTabAdapter(FragmentManager fm, int numOfTabs, Movie movie) {
        super(fm);
        this.mNumOfTabs = numOfTabs;
        this.mMovie = movie;
        args = new Bundle();
        args.putParcelable(ARG_MOVIE, mMovie);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TrailerFragment tab1 = new TrailerFragment();
                tab1.setArguments(args);
                return tab1;
            case 1:
                ReviewFragment tab2 = new ReviewFragment();
                tab2.setArguments(args);
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
