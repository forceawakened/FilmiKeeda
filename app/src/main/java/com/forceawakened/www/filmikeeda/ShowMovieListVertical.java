package com.forceawakened.www.filmikeeda;

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

public class ShowMovieListVertical extends Fragment implements AdapterMovieVertical.ItemClickListener{
    private ArrayList<mMovie> movieList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recycler_view_vertical, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        try {
            JSONObject movieListObject = new JSONObject(getArguments().getString(MovieUtils.MOVIE_LIST));
            movieList = MovieUtils.getMovieList(movieListObject, MovieUtils.MOVIE_RESULTS);
            AdapterMovieVertical mAdapter = new AdapterMovieVertical(getActivity(), this, movieList);
            recyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            return v;
        }
    }

    @Override
    public void onItemClick(int position) {
        mMovie movie = movieList.get(position);
        Fragment fragment = new ShowMovieInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MovieUtils.MOVIE_ID, movie.getId());
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.loaded_content, fragment)
                .addToBackStack(null)
                .commit();
    }
}
