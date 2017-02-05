package com.forceawakened.www.filmikeeda;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShowMovieListHorizontal extends Fragment implements CustomAdapterHorizontal.ItemClickListener{
    private JSONObject movieListObject;
    private ArrayList<MovieInfo> movieList;
    private android.support.v7.widget.RecyclerView recyclerView;
    private android.support.v7.widget.RecyclerView.LayoutManager layoutManager;
    private CustomAdapterHorizontal customAdapterHorizontal;
    static final String MOVIE_LIST = "MOVIE_LIST";


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.show_movie_list_horizontal, container, false);
        recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        //recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        try {
            movieListObject = new JSONObject(getArguments().getString(MOVIE_LIST));
            movieList = MovieFactory.getMovieList(movieListObject);
            //Log.d("SMLH", "on create view " + movieListObject);
            customAdapterHorizontal = new CustomAdapterHorizontal(getActivity(), this, movieList);
            recyclerView.setAdapter(customAdapterHorizontal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            return v;
        }
    }

    @Override
    public void onItemClick(int position) {
        MovieInfo movie = movieList.get(position);
        Intent intent = new Intent(getActivity(), ShowMovieInfo.class);
        intent.putExtra(ShowMovieInfo.MOVIE_ID, movie.getId());
        startActivity(intent);
    }
}
