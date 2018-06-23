package com.example.sang.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sang.popularmovies.data.MoviesContract;
import com.example.sang.popularmovies.model.Movie;
import com.example.sang.popularmovies.utilities.MovieDataUtils;
import com.example.sang.popularmovies.utilities.NetworkUtils;
import com.example.sang.popularmovies.utilities.PopularMoviesUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.OnMovieClickHandler
        ,LoaderManager.LoaderCallbacks<Cursor>{

    private MovieAdapter movieAdapter;
    @BindView(R.id.rv_movie_poster) RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator)ProgressBar mLoadingIndicator;
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;

    private int numberOfColumns ;

    private static final int FAV_LOADER_ID = 123;
    private List<Movie> movieList;
    private int mPosition = RecyclerView.NO_POSITION;

    //Distinguish between loader data and asynctask data
    private int LOADER_DATA = 0;
    //Check if favourite menu item is clicked
    private boolean IS_FAV_MOVIES_CLICKED = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        numberOfColumns = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 4 : 2;

        GridLayoutManager layoutManager = new GridLayoutManager(this , numberOfColumns);
        mRecyclerView.setLayoutManager(layoutManager);

        movieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(movieAdapter);

        if(savedInstanceState == null) {
            loadDefaultMovieList();
        }
        else if(savedInstanceState!=null && savedInstanceState.containsKey(getString(R.string.KEY_FOR_MOVIE_LIST))) {
            movieList = savedInstanceState.getParcelableArrayList(getString(R.string.KEY_FOR_MOVIE_LIST));

            if(movieList !=null) {
                movieAdapter.setMovies(movieList);
                if (!isOnline()) {
                    Toast.makeText(this,
                            R.string.error_showing_old_list,
                            Toast.LENGTH_LONG).show();
                }

                LOADER_DATA = (int) savedInstanceState.getInt(getString(R.string.LOADER_DATA_KEY));
                IS_FAV_MOVIES_CLICKED = savedInstanceState.getBoolean(getString(R.string.MENU_ITEM_CLICKED_KEY));
            }

        }else{
           showErrorMessage(R.string.network_error);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(movieList !=null){
            outState.putParcelableArrayList(getString(R.string.KEY_FOR_MOVIE_LIST) , (ArrayList<? extends Parcelable>) movieList);
            outState.putInt(getString(R.string.LOADER_DATA_KEY),LOADER_DATA);
            outState.putBoolean(getString(R.string.MENU_ITEM_CLICKED_KEY) ,IS_FAV_MOVIES_CLICKED);
        }

        super.onSaveInstanceState(outState);
    }


    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(this , MovieDetailsActivity.class);
        intent.putExtra(getString(R.string.KEY_FOR_MOVIE_LIST_INTENT) , movie);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu ,menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( IS_FAV_MOVIES_CLICKED ) loadFavourites();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.menu_item_popular:
                loadMostPopularMovies();
                break;
            case R.id.menu_item_toprated:
                loadTopRatedMovies();
                break;
            case R.id.menu_item_favourites:
                loadFavourites();
                break;

            default:
                loadDefaultMovieList();
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFavourites() {
         IS_FAV_MOVIES_CLICKED = true;
         movieList = null ;

        changeActionBarText( R.string.title_activity_fav_movies );

        if( LOADER_DATA == 0 ){
            getSupportLoaderManager().initLoader(FAV_LOADER_ID, null, this);
            LOADER_DATA = 1;
        }
        else {
            getSupportLoaderManager().restartLoader(FAV_LOADER_ID, null, this);
        }

    }

    private void loadDefaultMovieList() {

        mLoadingIndicator.setVisibility(View.VISIBLE);
        loadMostPopularMovies();
    }

    private void loadTopRatedMovies(){

        String mUrl = NetworkUtils.getTopRatedUrl();
        loadMovies(mUrl);
    }

    private void loadMostPopularMovies(){
        String mUrl = NetworkUtils.getPopularUrl();
        loadMovies(mUrl);
    }

    private void loadMovies(String mUrl){
        IS_FAV_MOVIES_CLICKED = false;
        changeActionBarText( R.string.app_name );

        if(isOnline()) {
            new LoadMovieTask().execute(mUrl);
        }
        else {
            showErrorMessage(R.string.network_error);
        }

    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(int stringIndex) {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setText(getString(stringIndex));
    }
    private void changeActionBarText(int id){
        getSupportActionBar().setTitle(id);
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

        if (data.getCount() > 0) showMovieDataView();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }


    //Stage 1 implementation

    class LoadMovieTask extends AsyncTask<String,Void,List<Movie>>{



        @Override
        protected List<Movie> doInBackground(String... strings) {
            String movieUrl = strings[0];

            URL url = NetworkUtils.buildURL(movieUrl);


                String response = NetworkUtils.getResponseFromHttpUrl(url);

                try {
                    movieList = PopularMoviesUtils.getMovieListFromJSONResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return movieList;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movies !=null){
                showMovieDataView();
                movieAdapter.setMovies(movies);
            }else{
               showErrorMessage(R.string.error_message);
            }
        }

    }



}
