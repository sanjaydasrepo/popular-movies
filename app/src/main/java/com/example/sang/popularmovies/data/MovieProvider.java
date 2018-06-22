package com.example.sang.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ProxyInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.sang.popularmovies.data.MoviesContract.TABLE_NAME;

public class MovieProvider extends ContentProvider{

    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;

    private static MoviesDbHelper moviesDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();



    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIE, CODE_MOVIE);
        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/#", CODE_MOVIE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext() ;
        moviesDbHelper = new MoviesDbHelper( context ) ;
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor = null;


        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                cursor = moviesDbHelper.getReadableDatabase().query(
                        TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case CODE_MOVIE_WITH_ID:

                cursor = moviesDbHelper.getReadableDatabase().query(
                        TABLE_NAME,
                        projection,
                        MoviesContract.COLUMN_MOVIE_ID + " = ? ",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {


        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        Uri iUri;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE :
                db.beginTransaction();

                try {
                   long l = db.insert(TABLE_NAME ,null,values);

                   iUri = MoviesContract.buildMovieUriWithId( l );


                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                return iUri;


        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        int tasksDeleted;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE_WITH_ID :
                db.beginTransaction();

                try {
                     tasksDeleted = db.delete(TABLE_NAME,
                            MoviesContract.COLUMN_MOVIE_ID + " = ? ",
                            selectionArgs);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }


        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
