package com.lxdnz.nz.movieproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_container, new MovieDetailFragment())
                    .commit();
        }
    }

    public static class MovieDetailFragment extends Fragment{

        private Movie mMovie;

        public MovieDetailFragment(){
            /*
            placeholder for fragment
             */
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

            // call the Intent
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("clickedMovie")){
                mMovie = intent.getParcelableExtra("clickedMovie");
                ((TextView)rootView.findViewById(R.id.original_title)).setText(mMovie.getOriginal_title());
                ((TextView)rootView.findViewById(R.id.title)).setText(mMovie.getTitle());
                ((TextView)rootView.findViewById(R.id.original_language)).setText(mMovie.getOriginal_language());
                ((TextView)rootView.findViewById(R.id.overview)).setText(mMovie.getOverview());
                Picasso.with(getContext()).load(mMovie.getPoster_path()).into((ImageView)
                        rootView.findViewById(R.id.image_view_detail));
                ((RatingBar)rootView.findViewById(R.id.rating_bar)).setRating((float) (mMovie.getVote_average() * 0.5));
            }

            return rootView;
        }
    }
}
