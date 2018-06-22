package com.example.sang.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 12;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE =

                "CREATE TABLE " + MoviesContract.TABLE_NAME + " (" +

                        MoviesContract._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        MoviesContract.COLUMN_MOVIE_ID       + " TEXT NOT NULL, "                 +

                        MoviesContract.COLUMN_MOVIE_TITLE + " TEXT NOT NULL,"                  +

                        MoviesContract.COLUMN_MOVIE_OVERVIEW   + " TEXT NOT NULL, "                    +
                        MoviesContract.COLUMN_MOVIE_AVG_VOTE   + " TEXT NOT NULL, "                    +

                        MoviesContract.COLUMN_MOVIE_POSTER_PATH   + " TEXT NOT NULL, "                    +
                        MoviesContract.COLUMN_MOVIE_RELEASE_DATE   + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.TABLE_NAME);
        onCreate(db);
    }
}
