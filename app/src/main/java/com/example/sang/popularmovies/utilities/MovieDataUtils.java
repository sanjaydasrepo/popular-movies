package com.example.sang.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.sang.popularmovies.data.MoviesContract;
import com.example.sang.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieDataUtils {

    public static final String[] MAIN_FAVOURITE_MOVIES_PROJECTION = {
            MoviesContract.COLUMN_MOVIE_ID,
            MoviesContract.COLUMN_MOVIE_TITLE,
            MoviesContract.COLUMN_MOVIE_POSTER_PATH,
            MoviesContract.COLUMN_MOVIE_OVERVIEW,
            MoviesContract.COLUMN_MOVIE_RELEASE_DATE,
            MoviesContract.COLUMN_MOVIE_AVG_VOTE
    };
    private static Movie mMovie;
    private static AsyncTask mAsyncTask;
    private static Cursor mCursor;
    private static Context mContext;

    public static boolean markMovieAsFav(Context context, Movie movies ) {


        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.COLUMN_MOVIE_ID , movies.getmId());
        contentValues.put(MoviesContract.COLUMN_MOVIE_TITLE , movies.getmTitle());
        contentValues.put(MoviesContract.COLUMN_MOVIE_OVERVIEW , movies.getmOverview());
        contentValues.put(MoviesContract.COLUMN_MOVIE_POSTER_PATH , movies.getmPosterPath());
        contentValues.put(MoviesContract.COLUMN_MOVIE_AVG_VOTE , movies.getmAvgVote());
        contentValues.put(MoviesContract.COLUMN_MOVIE_RELEASE_DATE , movies.getmReleaseDate());

        Uri uri = context.getContentResolver().insert( MoviesContract.CONTENT_URI,
                contentValues);

        if( uri != null ){
            return true;
        }
        return false ;
    }
    public static boolean unMarkMovieAsFav(Context context, Movie movies ) {

        String[] selectionArgs = new String[]{ String.valueOf(movies.getmId())};

        int res = context.getContentResolver().delete(
                MoviesContract.buildMovieUriWithId(movies.getmId()) ,
                        MoviesContract.COLUMN_MOVIE_ID,
                        selectionArgs);


        if( res > 0 ) return true ;

        return false ;
    }


    public static List<Movie> getListFromCursor(Cursor cursor) {
        List<Movie> lMovie = new ArrayList<>();

        if( cursor !=null ){
            if (cursor.moveToFirst()){
                do{
                    String mId = cursor.getString(cursor.getColumnIndex(MoviesContract.COLUMN_MOVIE_ID));
                    String posterPath = cursor.getString(cursor.getColumnIndex(MoviesContract.COLUMN_MOVIE_POSTER_PATH));
                    String overview = cursor.getString(cursor.getColumnIndex(MoviesContract.COLUMN_MOVIE_OVERVIEW));
                    String releaseDate = cursor.getString(cursor.getColumnIndex(MoviesContract.COLUMN_MOVIE_RELEASE_DATE));
                    String title = cursor.getString(cursor.getColumnIndex(MoviesContract.COLUMN_MOVIE_TITLE));
                    String voteAverage = cursor.getString(cursor.getColumnIndex(MoviesContract.COLUMN_MOVIE_AVG_VOTE));


                    mMovie = new Movie();
                    mMovie.setmId(Long.parseLong(mId));
                    mMovie.setmTitle(title);
                    mMovie.setmPosterPath(posterPath);
                    mMovie.setmAvgVote(Double.parseDouble(voteAverage));
                    mMovie.setmOverview(overview);
                    mMovie.setmReleaseDate(releaseDate);

                    lMovie.add(mMovie);
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
        return lMovie;
    }

}
