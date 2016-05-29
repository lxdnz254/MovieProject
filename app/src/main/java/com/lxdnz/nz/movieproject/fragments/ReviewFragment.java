package com.lxdnz.nz.movieproject.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lxdnz.nz.movieproject.R;
import com.lxdnz.nz.movieproject.objects.Movie;

/**
 * Created by alex on 29/05/16.
 */
public class ReviewFragment extends Fragment {
    Movie mMovie;

    public ReviewFragment(){

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review, container, false);
    }
}
