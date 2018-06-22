package com.example.sang.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sang.popularmovies.model.Trailer;

import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    Context context ;
    List<Trailer> trailers = new ArrayList<Trailer>();
    OnTrailerClickHandler onTrailerClickHandler;

    public TrailerAdapter(OnTrailerClickHandler onTrailerClickHandler) {
        this.onTrailerClickHandler = onTrailerClickHandler;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        this.context = context;

        int layoutIdForTrailer = R.layout.item_trailer;

        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForTrailer , parent , shouldAttachToParentImmediately);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        String trailerTitle = trailers.get(position).getTrailerTitle();
        holder.tvTrailerTitle.setText(trailerTitle);
    }

    @Override
    public int getItemCount() {
        if(trailers==null) return 0;
        return trailers.size();
    }


    public void setTrailers(List<Trailer> trailers){
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    public interface OnTrailerClickHandler {
        public void onTrailerClick(Trailer trailer);
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvTrailerTitle = null;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            tvTrailerTitle = (TextView) itemView.findViewById(R.id.tv_trailer_label);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int i = getAdapterPosition();

            Trailer trailer = trailers.get(i);

            onTrailerClickHandler.onTrailerClick( trailer ) ;
        }
    }
}
