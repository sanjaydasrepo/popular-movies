package com.example.sang.popularmovies;

import android.content.Context;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.OnMovieClickHandler  {

    private MovieAdapter movieAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private int numberOfColumns = 2;



    private List<Movie> movieList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        * The ProgressBar that will indicate to the user that we are loading data. It will be
//        * hidden when no data is loading.
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

//       * Textview to display errors
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

//       * Recyclerview to display movie posters
        mRecyclerView = findViewById(R.id.rv_movie_poster);

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
            }

        }else{
           showErrorMessage(R.string.network_error);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(movieList !=null)
            outState.putParcelableArrayList(getString(R.string.KEY_FOR_MOVIE_LIST) , (ArrayList<? extends Parcelable>) movieList);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMovieClick(Movie movie) {
        Context context = this;
        Class destClass = MovieDetailsActivity.class;
        Intent intent = new Intent(context , destClass);
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
        Context context = this;
        Class destClass = FavMoviesActivity.class;
        Intent intent = new Intent(context , destClass);
        startActivity(intent);
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

    private boolean isOnline(){
        NetworkInfo netInfo=null;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm != null)
            netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
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
