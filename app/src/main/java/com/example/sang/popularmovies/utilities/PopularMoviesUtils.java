package com.example.sang.popularmovies.utilities;

import com.example.sang.popularmovies.model.Movie;
import com.example.sang.popularmovies.model.Reviews;
import com.example.sang.popularmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PopularMoviesUtils {

    private static String RESULTS="results";

    public static List<Movie> getMovieListFromJSONResponse(String response) throws JSONException {
        JSONObject moviesJson = new JSONObject(response);
        JSONArray jsonArray = moviesJson.getJSONArray(RESULTS);

        Movie movie;
        List<Movie> lMovie = new ArrayList<>();

        for(int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            long mId = jsonObject.getLong("id");
            String posterPath = jsonObject.getString("poster_path");
            String overview = jsonObject.getString("overview");
            String releaseDate = jsonObject.getString("release_date");
            String title = jsonObject.getString("title");
            double voteAverage = jsonObject.getDouble("vote_average");


            movie = new Movie();
            movie.setmId(mId);
            movie.setmTitle(title);
            movie.setmPosterPath(posterPath);
            movie.setmAvgVote(voteAverage);
            movie.setmOverview(overview);
            movie.setmReleaseDate(releaseDate);

            lMovie.add(movie);
        }


        return lMovie;
    }
    public static List<Trailer> getTrailerListFromJSONResponse(String response) throws JSONException {
        JSONObject tJson = new JSONObject(response);
        JSONArray jsonArray = tJson.getJSONArray(RESULTS);
        JSONObject jsonObject =null;
        String trailerId  ;
        String trailerKey ;
        String trailerTitle ;
        String videoType;

        Trailer trailer;
        List<Trailer> lTrailers = new ArrayList<>();

        for(int i=0;i<jsonArray.length();i++){
            trailer = new Trailer();
            jsonObject = jsonArray.getJSONObject(i);
            trailerId = jsonObject.getString("id");
            trailerKey = jsonObject.getString("key");
            trailerTitle = jsonObject.getString("name");
            videoType = jsonObject.getString("type");

            trailer.setTrailerId( trailerId );
            trailer.setTrailerKey( trailerKey );
            trailer.setTrailerTitle( trailerTitle );
            trailer.setVideoType( videoType );

            lTrailers.add( trailer);
        }

        return lTrailers;
    }
    public static List<Reviews> getReviewListFromJSONResponse(String response) throws JSONException {

        JSONObject tJson = new JSONObject(response);
        JSONArray jsonArray = tJson.getJSONArray(RESULTS);
        JSONObject jsonObject =null;

        String reviewId;
        String authorName;
        String reviewContent;
        String reviewUrl;

       Reviews reviews;
        List<Reviews> lReviews = new ArrayList<>();

        for(int i=0;i<jsonArray.length();i++){
            jsonObject = jsonArray.getJSONObject(i);
            reviews = new Reviews();
            reviewId = jsonObject.getString("id");
            authorName = jsonObject.getString("author");
            reviewContent = jsonObject.getString("content");
            reviewUrl = jsonObject.getString("url");

            reviews.setReviewId( reviewId );
            reviews.setAuthorName( authorName );
            reviews.setReviewContent( reviewContent );
            reviews.setReviewUrl( reviewUrl );
            lReviews.add( reviews );
        }

        return lReviews;
    }


    public static String getFormattedDate(String dateStr){
        String formattedDate = dateStr;


        try {
            SimpleDateFormat origFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = origFormat.parse(dateStr) ;

            Format formatter = new SimpleDateFormat("MMM dd,yyyy");
            formattedDate = formatter.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }
}
