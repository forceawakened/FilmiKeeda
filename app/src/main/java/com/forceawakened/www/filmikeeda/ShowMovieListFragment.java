package com.forceawakened.www.filmikeeda;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShowMovieListFragment extends Fragment implements CustomAdapter.ItemClickListener{
    private JSONObject movieListObject;
    private android.support.v7.widget.RecyclerView recyclerView;
    private android.support.v7.widget.RecyclerView.LayoutManager layoutManager;
    private ArrayList<MovieInfo> movieList;
    private CustomAdapter customAdapter;
    static final String MOVIE_LIST = "MOVIE_LIST";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.show_movie_list, container, false);
        recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        //recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        try {
            movieListObject = new JSONObject(getArguments().getString(MOVIE_LIST));
            movieList = MovieFactory.getMovieList(movieListObject);
            customAdapter = new CustomAdapter(getActivity(), this, movieList);
            recyclerView.setAdapter(customAdapter);
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
