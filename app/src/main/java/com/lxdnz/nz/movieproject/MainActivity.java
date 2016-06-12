package com.lxdnz.nz.movieproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.lxdnz.nz.movieproject.adapters.ImageAdapter;
import com.lxdnz.nz.movieproject.fragments.MovieDetailFragment;
import com.lxdnz.nz.movieproject.fragments.MovieGridFragment;
import com.lxdnz.nz.movieproject.objects.Movie;
import com.lxdnz.nz.movieproject.preferenceactivity.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    public boolean mTwoPane;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp-land & res/layout-sw720dp). If this view is present,
            // then the activity should be in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_container, new MovieDetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }else {
            mTwoPane = false;
        }
        setupActionBar();
        MovieGridFragment movieGridFragment = ((MovieGridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_movie_grid));


    }

    private void setupActionBar() {

        String sortBy;
        // get SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String filter = sharedPreferences.getString(getString(R.string.preference_sorting_key),
                getString(R.string.preference_sorting_default));

        if (filter.contentEquals(getString(R.string.preference_sorting_default))){
            sortBy = getString(R.string.popular);
        }else if (filter.contentEquals(getString(R.string.get_top_rated))){
            sortBy = getString(R.string.top_rated);
        }else{
            sortBy = getString(R.string.favorites);
        }

        //implement actionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Set the Title in the action bar.

            actionBar.setTitle(getString(R.string.app_name) + ": "
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
}
