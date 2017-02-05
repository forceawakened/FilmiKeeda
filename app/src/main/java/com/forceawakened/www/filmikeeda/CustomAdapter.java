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
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{

    private Context mContext;
    private ItemClickListener mItemClickListener;
    private ArrayList<MovieInfo> movieList;

    public CustomAdapter(Context context, ItemClickListener itemClickListener, ArrayList<MovieInfo> movieList) {
        mContext = context;
        this.movieList = movieList;
        mItemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_row,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MovieInfo movie = movieList.get(position);

        Picasso.with(mContext)
                .load(MovieFactory.getPosterURL(movie.getPosterPath(), "w154"))
                .error(R.drawable.icon_image_placeholder)
                .placeholder(R.drawable.icon_image_placeholder)
                .into(holder.moviePoster);

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
