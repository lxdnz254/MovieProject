package com.lxdnz.nz.movieproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lxdnz.nz.movieproject.objects.Movie;
import com.lxdnz.nz.movieproject.preferenceactivity.SettingsActivity;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        setupActionBar();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_container, new MovieDetailFragment())
                    .commit();
        }
    }

    private void setupActionBar() {

        String sortBy;
        // get SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String filter = sharedPreferences.getString(getString(R.string.preference_sorting_key),
                getString(R.string.preference_sorting_default));

        if (filter.contentEquals(getString(R.string.preference_sorting_default))){
            sortBy = getString(R.string.popular);
        }else{
            sortBy = getString(R.string.top_rated);
        }

        //implement actionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Set the Title in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(getString(R.string.title_activity_movie_detail) + ": "
                    + sortBy);
            actionBar.setLogo(R.mipmap.ic_launcher);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                ((TextView)rootView.findViewById(R.id.release_date)).setText(mMovie.getRelease_date());
                ((TextView)rootView.findViewById(R.id.overview)).setText(mMovie.getOverview());
                Picasso.with(getContext()).load(mMovie.getPoster_path()).into((ImageView)
                        rootView.findViewById(R.id.image_view_detail));
                ((RatingBar)rootView.findViewById(R.id.rating_bar)).setRating((float) (mMovie.getVote_average() * 0.5));
            }

            return rootView;
        }
    }
}
