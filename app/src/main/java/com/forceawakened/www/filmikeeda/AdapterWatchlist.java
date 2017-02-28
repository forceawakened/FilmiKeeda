package com.forceawakened.www.filmikeeda;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by forceawakened on 27/2/17.
 */

public class AdapterWatchlist extends RecyclerView.Adapter<AdapterWatchlist.ViewHolder> {
    private ArrayList<mMovie> movieList;
    private Callback mCallback;
    private Context mContext;

    public AdapterWatchlist(Context context, Callback callback,ArrayList<mMovie> movieList) {
        this.movieList = movieList;
        mCallback = callback;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.watchlist_row,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mMovie movie = movieList.get(position);
        //set movie title
        holder.movieTitle.setText(movie.getTitle());
        //set movie poster
        String moviePosterPath = MovieUtils.getImgPath(mContext.getFilesDir() + File.separator, String.valueOf(movie.getId()), MovieUtils.WATCHLIST);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(moviePosterPath, options);
        if(bitmap != null) {
            holder.moviePoster.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return (movieList == null ? 0 : movieList.size());
    }

    public interface Callback{
        void deleteItem(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView moviePoster;
        TextView movieTitle;
        Button removeBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
            movieTitle = (TextView) itemView.findViewById(R.id.movie_title);
            removeBtn = (Button) itemView.findViewById(R.id.remove_btn);
            removeBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCallback.deleteItem(getAdapterPosition());
        }
    }
}
