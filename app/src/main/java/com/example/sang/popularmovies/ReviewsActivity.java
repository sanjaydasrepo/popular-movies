package com.example.sang.popularmovies;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sang.popularmovies.model.Reviews;
import com.example.sang.popularmovies.utilities.NetworkUtils;
import com.example.sang.popularmovies.utilities.PopularMoviesUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.List;

public class ReviewsActivity extends AppCompatActivity
        implements ReviewAdapter.OnReviewClickHandler ,
        LoaderManager.LoaderCallbacks<List<Reviews>>{

    Context context;
    ReviewAdapter adapter;
    private static final String MOVIE_ID_KEY = "movie-id";
    private static final int REVIEW_LOADER_ID = 13 ;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private static String movieId;

    RecyclerView mRecyclerView;
    List<Reviews> lReviews =null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mRecyclerView = findViewById( R.id.rv_review);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

//       * Textview to display errors
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        context = getApplicationContext();

        LinearLayoutManager layoutManager =
                new LinearLayoutManager( this ,LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager( layoutManager );

        adapter = new ReviewAdapter( this );
        mRecyclerView.setAdapter( adapter );

        Bundle bundle = getIntent().getExtras();
        movieId = bundle.getString( MOVIE_ID_KEY );


        int loaderId = REVIEW_LOADER_ID;
        LoaderManager.LoaderCallbacks<List<Reviews>> callback = ReviewsActivity.this;
        Bundle bundleForLoader = null;

        if(isOnline()) {
            getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);
        }else{
            showErrorMessage(R.string.network_error_reviews);
        }
    }



    @Override
    public void onReviewClick(Reviews Reviews) {

    }

    @Override
    public Loader<List<Reviews>> onCreateLoader(int id, final Bundle args) {


        return new AsyncTaskLoader<List<Reviews>>(this) {


            @Override
            protected void onStartLoading() {

                if ( lReviews != null  ){
                    deliverResult(lReviews);
                }
                else{
                    mLoadingIndicator.setVisibility(View.VISIBLE);

                    forceLoad();

                }
            }

            @Override
            public List<Reviews> loadInBackground() {


                String reviewUrl = NetworkUtils.getReviewUrl( movieId );


                URL url = NetworkUtils.buildURL(reviewUrl);

                String response = NetworkUtils.getResponseFromHttpUrl(url);


                try {
                    lReviews = PopularMoviesUtils.
                            getReviewListFromJSONResponse(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return lReviews;
            }

            @Override
            public void deliverResult(@Nullable List<Reviews> data) {
                lReviews = data ;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Reviews>> loader, List<Reviews> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if( null == data ){
            showErrorMessage(R.string.error_message);
        }else if (data.isEmpty()){
            showErrorMessage(R.string.no_reviews);
        }
        else{
             showReviewsDataView();
             adapter.setReview( data );
        }
    }

    private void showErrorMessage(int stringIndex) {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setText(getString(stringIndex));
    }
    private void showReviewsDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private boolean isOnline(){
        NetworkInfo netInfo=null;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm != null)
            netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onLoaderReset(Loader<List<Reviews>> loader) {

    }
}
