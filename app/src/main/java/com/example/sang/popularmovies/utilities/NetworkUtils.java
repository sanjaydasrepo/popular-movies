package com.example.sang.popularmovies.utilities;

import android.net.Uri;

import com.example.sang.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String BASE_URL="https://api.themoviedb.org/3/movie/";
    private static final String POPULAR_URL=BASE_URL + "popular";
    private static final String TOP_RATED_URL=BASE_URL + "top_rated";
    private static final String VIDEO_URL_ENDPOINT="videos";
    private static final String MOVIE_REVIEW_ENDPOINT="reviews";

    private static final String YOUTUBE_URL="https://www.youtube.com/watch?v=";
    private static final String YOUTUBE_VEND_URL="vnd.youtube://";

    private static final String SIZE = "w185";
    private static final String IMAGE_URL="https://image.tmdb.org/t/p/"+SIZE;

    private static final String language="en-US";
    private static final String page="1";

    //API Key here
    private static final String api_key= BuildConfig.API_KEY;;

    private static final String default_sort ="popularity.desc";

    private static String API_KEY_PARAM="api_key";
    private static String LANGUAGE_PARAM="language";
    private static String PAGE_NO_PARAM="page";
    private static String SORT_BY_PARAM="sort_by";


    public static URL buildURL(String qryType){

        Uri uri = Uri.parse(qryType).buildUpon()
        .appendQueryParameter(API_KEY_PARAM,api_key)
        .appendQueryParameter(LANGUAGE_PARAM,language)
                .appendQueryParameter(SORT_BY_PARAM,default_sort)
                .appendQueryParameter(PAGE_NO_PARAM,page)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    public static String getImageUrl(String imagePath){
        return IMAGE_URL+imagePath;
    }

    public static String getVideoUrl(String movieId){
        String videoUrl = BASE_URL + movieId + "/"+ VIDEO_URL_ENDPOINT;
        return videoUrl ;
    }
    public static String getReviewUrl(String movieId){
        String videoUrl = BASE_URL + movieId + "/"+ MOVIE_REVIEW_ENDPOINT;
        return videoUrl ;
    }
    public static String getYoutubeUrl(String videoKey){
        String videoUrl = YOUTUBE_URL + videoKey;
        return videoUrl ;
    }
    public static String getYoutubeVendUrl(String videoKey){
        String videoUrl = YOUTUBE_VEND_URL + videoKey;
        return videoUrl ;
    }
    public static String getPopularUrl(){
        return POPULAR_URL;
    }
    public static String getTopRatedUrl(){
        return TOP_RATED_URL;
    }



    public static String getResponseFromHttpUrl(URL url){

        HttpURLConnection connection=null;

        try {

            connection = (HttpURLConnection) url.openConnection();
            InputStream in = connection.getInputStream();

            Scanner sc = new Scanner(in);
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(connection !=null)
             connection.disconnect();
        }
        return null;

    }



}
