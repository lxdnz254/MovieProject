package com.lxdnz.nz.movieproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 22/04/16.
 */
public class MovieParser {

    private static final String KEY_RESULTS = "results";

    public static List<Movie> getMovieDataFromJson(String raw)
            throws JSONException {

        List<Movie> result = new ArrayList<>();

        JSONObject movieJson = new JSONObject(raw);
        JSONArray movieArray = movieJson.getJSONArray(KEY_RESULTS);

        String KEY_ID = "id";
        String KEY_TITLE = "title";
        String KEY_POSTER = "poster_path";
        String KEY_OVERVIEW = "overview";
        String KEY_RATING = "vote_average";
        String KEY_RELEASE_DATE = "release_date";



        for (int i=0; i<movieArray.length(); i++){

            JSONObject movieObj = movieArray.getJSONObject(i);
            Movie movie = new Movie();

            if (movieObj.has(KEY_ID))
                movie.setId(movieObj.getString(KEY_ID));
            if (movieObj.has(KEY_TITLE))
                movie.setTitle(movieObj.getString(KEY_TITLE));
            if (movieObj.has(KEY_POSTER))
                movie.setPoster_path(movieObj.getString(KEY_POSTER));
            if (movieObj.has(KEY_OVERVIEW))
                movie.setOverview(movieObj.getString(KEY_OVERVIEW));
            if (movieObj.has(KEY_RATING))
                movie.setRating(movieObj.getString(KEY_RATING));
            if (movieObj.has(KEY_RELEASE_DATE))
                movie.setRelease_date(movieObj.getString(KEY_RELEASE_DATE));

            if(!movie.isValid())
                continue;

            result.add(movie);

        }

        return result;
    }
}
