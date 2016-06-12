package com.lxdnz.nz.movieproject.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lxdnz.nz.movieproject.R;
import com.lxdnz.nz.movieproject.adapters.ViewTabAdapter;
import com.lxdnz.nz.movieproject.objects.Movie;
import com.lxdnz.nz.movieproject.utils.Utilities;
import com.squareup.picasso.Picasso;

/**
 * Created by alex on 29/05/16.
 */
public class MovieDetailFragment extends Fragment {

    private Movie mMovie;
    private Context mContext;

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    public MovieDetailFragment() {
        /*
        placeholder for fragment
         */
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mContext = getContext();

        // call the Intent
        Intent intent = getActivity().getIntent();
        Bundle arguments = getArguments();
        if (intent != null && intent.hasExtra("clickedMovie")) {
            mMovie = intent.getParcelableExtra("clickedMovie");
        }else if (arguments != null){
            mMovie = arguments.getParcelable("clickedMovie");
        }
        if (mMovie != null){
            ((TextView) rootView.findViewById(R.id.original_title)).setText(mMovie.getOriginal_title());
            ((TextView) rootView.findViewById(R.id.title)).setText(mMovie.getTitle());
            ((TextView) rootView.findViewById(R.id.release_date)).setText(mMovie.getRelease_date());
            ((TextView) rootView.findViewById(R.id.overview)).setText(mMovie.getOverview());
            Picasso.with(getContext()).load(mMovie.getPoster_path()).into((ImageView)
                    rootView.findViewById(R.id.image_view_detail));
            ((RatingBar) rootView.findViewById(R.id.rating_bar)).setRating((float) (mMovie.getVote_average() * 0.5));

            final CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.favorite_checkBox);
            checkBox.setChecked(new Utilities(mContext).isFavorite(Integer.toString(mMovie.getId())));

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()){
                        long favId = new Utilities(mContext).markAsFavorite(mMovie);
                        Log.v(LOG_TAG, "favorite set:"+favId);
                        Toast.makeText(mContext, "Adding " + mMovie.getTitle()
                                + " to favorite database", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(mContext, "Removing from favorites", Toast.LENGTH_SHORT).show();
                        new Utilities(mContext).removeFavorite(mMovie);
                    }
                }
            });

            TabLayout tab_layout = (TabLayout) rootView.findViewById(R.id.tab_layout);
            tab_layout.addTab(tab_layout.newTab().setText("Trailers"));
            tab_layout.addTab(tab_layout.newTab().setText("Reviews"));

            final ViewPager view_pager = (ViewPager) rootView.findViewById(R.id.pager);

            final ViewTabAdapter adapter = new ViewTabAdapter
                    (getChildFragmentManager(), tab_layout.getTabCount(), mMovie);

            view_pager.setAdapter(adapter);

            view_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_layout));

            tab_layout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    view_pager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        }

        return rootView;
    }
}
