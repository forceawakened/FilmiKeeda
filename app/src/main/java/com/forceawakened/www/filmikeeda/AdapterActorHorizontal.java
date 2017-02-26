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
public class AdapterActorHorizontal extends RecyclerView.Adapter<AdapterActorHorizontal.ViewHolder>{

    private Context mContext;
    private AdapterActorHorizontal.ItemClickListener mItemClickListener;
    private ArrayList<Actor> actorList;

    public AdapterActorHorizontal(Context context, AdapterActorHorizontal.ItemClickListener itemClickListener, ArrayList<Actor> actorList) {
        mContext = context;
        this.actorList = actorList;
        mItemClickListener = itemClickListener;
    }

    @Override
    public AdapterActorHorizontal.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.actor_col, parent, false);
        return new AdapterActorHorizontal.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterActorHorizontal.ViewHolder holder, int position) {
        Actor actor = actorList.get(position);
        Picasso.with(mContext)
                .load(MovieUtils.getPosterURL(actor.getPosterPath(), "w92"))
                //.error(R.drawable.icon_image_placeholder_small)
                //.placeholder(R.drawable.icon_image_placeholder_small)
                .into(holder.actorPoster);
        //// TODO: 26/2/17 decent placeholder
        holder.actorName.setText(actor.getName());
    }

    @Override
    public int getItemCount() {
        return (actorList == null) ? 0 : actorList.size();
    }

    public interface ItemClickListener{
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView actorPoster;
        TextView actorName;

        public ViewHolder(View itemView) {
            super(itemView);
            actorPoster = (ImageView) itemView.findViewById(R.id.actor_poster);
            actorName = (TextView) itemView.findViewById(R.id.actor_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
