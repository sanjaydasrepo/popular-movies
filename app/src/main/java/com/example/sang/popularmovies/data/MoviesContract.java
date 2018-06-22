package com.example.sang.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract implements BaseColumns {
    public static final String CONTENT_AUTHORITY = "com.example.sang.popularmovies";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_MOVIE = "movie";

     public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_MOVIE_TITLE = "title";

        public static final String COLUMN_MOVIE_POSTER_PATH = "poster_path";
        public static final String COLUMN_MOVIE_AVG_VOTE = "avg_vote";


        public static final String COLUMN_MOVIE_OVERVIEW = "overview";

        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";

    public static Uri buildMovieUriWithId(long id) {
        return CONTENT_URI.buildUpon()
                .appendPath(Long.toString(id))
                .build();
    }

}
