package com.example.sang.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.sang.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MoviePosterViewHolder> {



    List<Movie> movies= new ArrayList<>();
    OnMovieClickHandler onMovieClickHandler;
    Context context;

    public MovieAdapter(OnMovieClickHandler onMovieClickHandler) {
        this.onMovieClickHandler = onMovieClickHandler;
    }

    @NonNull
    @Override
    public MoviePosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        this.context = context;

        int gridIdForPoster = R.layout.movieposter;

        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(gridIdForPoster , parent , shouldAttachToParentImmediately);
        return new MoviePosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviePosterViewHolder holder, int position) {
        String posterPath = movies.get(position).getmPosterPath();

        Picasso.with(context).load(NetworkUtils.getImageUrl(posterPath)).into(holder.IVMoviePoster);

    }

    @Override
    public int getItemCount() {
        if(movies==null) return 0;
        return movies.size();
    }

    public void setMovies(List<Movie> movies){
        this.movies = movies;
        notifyDataSetChanged();
    }

    public interface OnMovieClickHandler {
        public void onMovieClick(Movie movie);
    }

    class MoviePosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ImageView IVMoviePoster;
        public MoviePosterViewHolder(View itemView) {
            super(itemView);
            IVMoviePoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int i = getAdapterPosition();
            Movie movie = movies.get(i);
            onMovieClickHandler.onMovieClick(movie);
        }
    }
}
