package com.example.sang.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sang.popularmovies.data.MoviesContract;
import com.example.sang.popularmovies.model.Movie;
import com.example.sang.popularmovies.utilities.MovieDataUtils;

import java.util.List;

public class FavMoviesActivity extends AppCompatActivity
        implements MovieAdapter.OnMovieClickHandler
,LoaderManager.LoaderCallbacks<Cursor>{

    private MovieAdapter movieAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;

    private int numberOfColumns = 2;
    private static final int FAV_LOADER_ID = 123;
    private int mPosition = RecyclerView.NO_POSITION;



    private List<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_movies);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

//       * Textview to display errors
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

//       * Recyclerview to display movie posters
        mRecyclerView = findViewById(R.id.rv_movie_poster);

        GridLayoutManager layoutManager = new GridLayoutManager(this , numberOfColumns);
        mRecyclerView.setLayoutManager(layoutManager);

        movieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(movieAdapter);

        showLoading();

        if(isOnline()) {
            getSupportLoaderManager().initLoader(FAV_LOADER_ID, null, this);
        }else{
            showErrorMessage(R.string.network_error);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        movieList = null ;
        getSupportLoaderManager().restartLoader(FAV_LOADER_ID, null, this);
    }


    @Override
    public void onMovieClick(Movie movie) {
        Context context = this;
        Class destClass = MovieDetailsActivity.class;
        Intent intent = new Intent(context , destClass);
        intent.putExtra(getString(R.string.KEY_FOR_MOVIE_LIST_INTENT) , movie);
        startActivity(intent);
    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        /* Then, hide the weather data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(int stringIndex) {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setText(getString(stringIndex));
    }

    private boolean isOnline(){
        NetworkInfo netInfo=null;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm != null)
            netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        switch (id) {
            case FAV_LOADER_ID:
                Uri movieQueryUri = MoviesContract.CONTENT_URI;

                return new CursorLoader(this,
                        movieQueryUri,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if( movieList == null )
          movieList = MovieDataUtils.getListFromCursor(data);

        if( movieList.size() > 0){
            movieAdapter.setMovies( movieList );
        }else {
            showErrorMessage(R.string.no_favourites);
        }

        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);

        if (data.getCount() != 0) showMovieDataView();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
