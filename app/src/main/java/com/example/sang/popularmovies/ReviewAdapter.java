package com.example.sang.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sang.popularmovies.model.Reviews;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    Context context ;
    List<Reviews> reviews = new ArrayList<Reviews>();
    OnReviewClickHandler onReviewClickHandler;

    public ReviewAdapter(OnReviewClickHandler onReviewClickHandler) {
        this.onReviewClickHandler = onReviewClickHandler;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        this.context = context;

        int layoutIdForTrailer = R.layout.item_reviews;

        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForTrailer , parent , shouldAttachToParentImmediately);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        String author = reviews.get(position).getAuthorName();
        String content  = reviews.get(position).getReviewContent();

        holder.tvReviewAuthor.setText( author );
        holder.tvReviewContent.setText( content );
    }

    @Override
    public int getItemCount() {
        if(reviews==null) return 0;
        return reviews.size();
    }


    public void setReview(List<Reviews> reviews){
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public interface OnReviewClickHandler {
        public void onReviewClick(Reviews Reviews);
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvReviewAuthor;
        private TextView tvReviewContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            tvReviewAuthor = itemView.findViewById( R.id.tv_review_author );
            tvReviewContent = itemView.findViewById( R.id.tv_review_content );

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int i = getAdapterPosition();

            Reviews r = reviews.get(i);

            onReviewClickHandler.onReviewClick( r );
        }
    }
}
