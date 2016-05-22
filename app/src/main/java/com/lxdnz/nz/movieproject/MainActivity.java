package com.lxdnz.nz.movieproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.lxdnz.nz.movieproject.preferenceactivity.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        setupActionBar();


        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieGridFragment())
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
