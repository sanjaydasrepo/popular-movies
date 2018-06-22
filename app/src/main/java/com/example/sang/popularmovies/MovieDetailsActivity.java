package com.example.sang.popularmovies;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sang.popularmovies.data.MoviesContract;
import com.example.sang.popularmovies.model.Movie;
import com.example.sang.popularmovies.model.Trailer;
import com.example.sang.popularmovies.utilities.MovieDataUtils;
import com.example.sang.popularmovies.utilities.NetworkUtils;
import com.example.sang.popularmovies.utilities.PopularMoviesUtils;
import com.example.sang.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.squareup.picasso.Picasso;
import org.json.JSONException;

import java.net.URL;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity
        implements TrailerAdapter.OnTrailerClickHandler ,
        LoaderManager.LoaderCallbacks<List<Trailer>> , View.OnClickListener{



    Context context;
    TrailerAdapter adapter;
    private Movie selectedMovie;
    private final String PARCELABLE_KEY = "movie";
    private final String BASE_RATING = "10";
    private final int TRAILER_LOADER_ID = 12 ;

    RecyclerView mRecyclerView;
    List<Trailer> lTrailers =null;

    private ActivityMovieDetailsBinding mDetailBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDetailBinding= DataBindingUtil.setContentView(this ,R.layout.activity_movie_details);
        mRecyclerView = mDetailBinding.rvTrailers;

        Bundle bundle = getIntent().getExtras();
        if( bundle != null)
          selectedMovie = bundle.getParcelable(PARCELABLE_KEY);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        context = getApplicationContext();



        LinearLayoutManager layoutManager =
                new LinearLayoutManager( this ,LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager( layoutManager );

        adapter = new TrailerAdapter( this );
        mRecyclerView.setAdapter( adapter );



        fillPlaceholdersWithData(selectedMovie);


        int loaderId = TRAILER_LOADER_ID;
        LoaderManager.LoaderCallbacks<List<Trailer>> callback = MovieDetailsActivity.this;

        Bundle bundleForLoader = null;


        if(isOnline()) {
            getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);
        }else{
            showErrorMessage(R.string.network_error_trailers);
        }

        mDetailBinding.tvReviewsLink.setOnClickListener(this);
        mDetailBinding.ivFavIcon.setOnClickListener(this);

        isMovieFavourite();

    }


    private void fillPlaceholdersWithData(final Movie selectedMovie) {

        String posterPath = selectedMovie.getmPosterPath();
        Picasso.with(context).load(NetworkUtils.getImageUrl(posterPath)).into(mDetailBinding.ivMovieDetailPoster);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                mDetailBinding.tvMovieTitle.setText(selectedMovie.getmTitle());
                mDetailBinding.tvReleaseDate.setText(PopularMoviesUtils.getFormattedDate(selectedMovie.getmReleaseDate()));
                mDetailBinding.tvMovieVote.setText(String.valueOf(selectedMovie.getmAvgVote()) + "/" + BASE_RATING);
                mDetailBinding.tvMovieOverview.setText(selectedMovie.getmOverview());

            }
        });

        t.start();

    }

    private void loadReviews(){
        Context context = this;
        Class destClass = ReviewsActivity.class;
        Intent intent = new Intent(context , destClass);
        intent.putExtra(getString(R.string.KEY_FOR_MOVIE_ID_REVIEW) , String.valueOf(selectedMovie.getmId()));
        startActivity(intent);
    }


    @Override
    public void onTrailerClick(Trailer trailer) {
        Context context = this ;
        watchYoutubeVideo( context , trailer.getTrailerKey());
    }

    @Override
    public Loader<List<Trailer>> onCreateLoader(int id, final Bundle args) {


        return new AsyncTaskLoader<List<Trailer>>(this) {


            @Override
            protected void onStartLoading() {

                if ( lTrailers != null  ){
                    deliverResult( lTrailers );
                }
                else{
                    mDetailBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);

                    forceLoad();

                }
            }

            @Override
            public List<Trailer> loadInBackground() {

                String movieId = String.valueOf(selectedMovie.getmId()) ;

                String videoUrl = NetworkUtils.getVideoUrl( movieId );


                URL url = NetworkUtils.buildURL(videoUrl);

                String response = NetworkUtils.getResponseFromHttpUrl(url);


                try {
                    lTrailers = PopularMoviesUtils.getTrailerListFromJSONResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return lTrailers;
            }

            @Override
            public void deliverResult(@Nullable List<Trailer> data) {
                lTrailers = data ;
                super.deliverResult(data);
            }
        };
    }

     @Override
    public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> data) {
        mDetailBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);

        if( null == data ){
            showErrorMessage(R.string.error_message);
        }else{
             showMovieDataView();
             adapter.setTrailers( data );
        }
    }

    private void showErrorMessage(int stringIndex) {
        /* First, hide the currently visible data */
        mDetailBinding.rvTrailers.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mDetailBinding.tvErrorMessageDisplay.setVisibility(View.VISIBLE);
        mDetailBinding.tvErrorMessageDisplay.setText(getString(stringIndex));
    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mDetailBinding.tvErrorMessageDisplay.setVisibility(View.INVISIBLE);
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
    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(NetworkUtils.getYoutubeVendUrl(id)));

        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(NetworkUtils.getYoutubeUrl(id)));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Trailer>> loader) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch ( id ){
            case R.id.tv_reviews_link :
                loadReviews();
                break;
            case R.id.iv_fav_icon:
               String tag = (String)  mDetailBinding.ivFavIcon.getTag();

               if(tag.equals(context.getString(R.string.FAV_TAG)) ){
                   unMarkMovieAsFavourite();
               }else {

                   markMovieAsFavourite();
               }

            break;
        }
    }

    private  void isMovieFavourite(){
        new FetchTask().execute(context);

    }
    private  void markMovieAsFavourite() {
        if(MovieDataUtils.markMovieAsFav( context , selectedMovie))
            isMovieFavourite();
    }
    private  void unMarkMovieAsFavourite() {

        if(MovieDataUtils.unMarkMovieAsFav( context , selectedMovie))
            isMovieFavourite();
    }


    public class FetchTask extends AsyncTask<Context, Void, Cursor > {

        String mId = String.valueOf(selectedMovie.getmId());

        String[] selectionArgs = new String[]{mId};


        @Override
        protected Cursor doInBackground(Context... params) {

            return params[0].getContentResolver().query(
                    MoviesContract.buildMovieUriWithId(selectedMovie.getmId()),
                    MovieDataUtils.MAIN_FAVOURITE_MOVIES_PROJECTION,
                    MoviesContract.COLUMN_MOVIE_ID,
                    selectionArgs,
                    null);

        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if ( cursor != null ){
                if (cursor.moveToFirst()){
                        setFavIcon();
                    }

                else{
                    unsetFavIcon();
                }

            }
            else{
                unsetFavIcon();
            }
        }

        private void unsetFavIcon() {
            setIcon(R.drawable.favhearticon , R.string.mark_as_favourite,R.string.NOT_FAV);
        }

        private void setFavIcon() {
            setIcon(R.drawable.favmarkedicon , R.string.marked_as_favourite,R.string.FAV_TAG);
        }

        private void setIcon(final int imgResource , final int favText ,final int tag){

                    mDetailBinding.ivFavIcon.setImageResource(imgResource);
                    mDetailBinding.tvMarkFav.setText(favText);
                    mDetailBinding.ivFavIcon.setTag(context.getString(tag));

        }


    }

}
