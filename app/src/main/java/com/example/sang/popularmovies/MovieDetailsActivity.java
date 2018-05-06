package com.example.sang.popularmovies;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sang.popularmovies.utilities.NetworkUtils;
import com.example.sang.popularmovies.utilities.PopularMoviesUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {
    ImageView ivMoviePoster;
    TextView tvMovieTitle;
    TextView tvMovieReleasedDate;
    TextView tvMovieVote;
    TextView tvMovieOverview;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ivMoviePoster = findViewById(R.id.iv_movie_detail_poster);
        tvMovieTitle = findViewById(R.id.tv_movie_title);
        tvMovieReleasedDate = findViewById(R.id.tv_release_date);
        tvMovieVote = findViewById(R.id.tv_movie_vote);
        tvMovieOverview = findViewById(R.id.tv_movie_overview);


        context = getApplicationContext();


        Bundle bundle = getIntent().getExtras();
        Movie selectedMovie = bundle.getParcelable("movie");

        fillPlaceholdersWithData(selectedMovie);

       // Toast.makeText(this , selectedMovie.getmPopularity()+"" ,Toast.LENGTH_LONG).show();
    }

    private void fillPlaceholdersWithData(Movie selectedMovie) {

        String posterPath = selectedMovie.getmPosterPath();
        Picasso.with(context).load(NetworkUtils.getImageUrl(posterPath)).into(ivMoviePoster);

        tvMovieTitle.setText(selectedMovie.getmTitle());
        tvMovieReleasedDate.setText(PopularMoviesUtils.getFormattedDate(selectedMovie.getmReleaseDate()));
        tvMovieVote.setText(String.valueOf(selectedMovie.getmAvgVote()) +"/10");
        tvMovieOverview.setText(selectedMovie.getmOverview());

    }


}
