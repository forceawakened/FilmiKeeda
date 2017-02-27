package com.forceawakened.www.filmikeeda;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by forceawakened on 2/2/17.
 */
public class AdapterMovieVertical extends RecyclerView.Adapter<AdapterMovieVertical.ViewHolder>{
    private Context mContext;
    private ItemClickListener mItemClickListener;
    private ArrayList<mMovie> movieList;

    public AdapterMovieVertical(Context context, ItemClickListener itemClickListener, ArrayList<mMovie> movieList) {
        mContext = context;
        this.movieList = movieList;
        mItemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mMovie movie = movieList.get(position);
        Picasso.with(mContext)
                .load(MovieUtils.getPosterURL(movie.getPosterPath(), "w154"))
                //.error(R.drawable.icon_image_placeholder)
                //.placeholder(R.drawable.icon_image_placeholder)
                .into(holder.moviePoster);
        //// TODO: 26/2/17 get a decent placeholder 

        String movieTitle = movie.getTitle();
        if(!"".equals(movie.getReleaseDate())){
            movieTitle += "\n(" + movie.getReleaseDate() + ')';
        }
        holder.movieTitle.setText(movieTitle);
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
