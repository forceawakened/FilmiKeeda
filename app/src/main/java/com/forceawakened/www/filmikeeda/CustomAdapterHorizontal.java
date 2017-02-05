package com.forceawakened.www.filmikeeda;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by forceawakened on 3/2/17.
 */
public class CustomAdapterHorizontal extends RecyclerView.Adapter<CustomAdapterHorizontal.ViewHolder>{
    Context mContext;
    CustomAdapterHorizontal.ItemClickListener mItemClickListener;
    ArrayList<MovieInfo> movieList;

    public CustomAdapterHorizontal(Context context, CustomAdapterHorizontal.ItemClickListener itemClickListener, ArrayList<MovieInfo> movieList) {
        mContext = context;
        //Log.d("CAH", movieList.get(0).getTitle());
        this.movieList = movieList;
        mItemClickListener = itemClickListener;
    }

    @Override
    public CustomAdapterHorizontal.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_col,parent,false);
        CustomAdapterHorizontal.ViewHolder viewHolder = new CustomAdapterHorizontal.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomAdapterHorizontal.ViewHolder holder, int position) {
        MovieInfo movie = movieList.get(position);

        Picasso.with(mContext)
                .load(MovieFactory.getPosterURL(movie.getPosterPath(), "w92"))
                .error(R.drawable.icon_image_placeholder_small)
                .placeholder(R.drawable.icon_image_placeholder_small)
                .into(holder.moviePoster);
        holder.movieTitle.setText(movie.getTitle());
    }

    @Override
    public int getItemCount() {
        return (movieList == null) ? 0 : movieList.size();
    }

    public interface ItemClickListener{
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView moviePoster;
        TextView movieTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
            movieTitle = (TextView) itemView.findViewById(R.id.movie_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
