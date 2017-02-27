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

public class ShowMovieListHorizontal extends Fragment implements AdapterMovieHorizontal.ItemClickListener{
    public static final String FLAG = "flag";
    private ArrayList<mMovie> movieList;
    static final String MOVIE_LIST = "movie list";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recycler_view_horizontal, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        try {
            JSONObject movieListObject = new JSONObject(getArguments().getString(MOVIE_LIST));
            Integer flag = getArguments().getInt(FLAG);
            movieList = MovieUtils.getMovieList(movieListObject, flag);
            AdapterMovieHorizontal mAdapter = new AdapterMovieHorizontal(getActivity(), this, movieList);
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
        bundle.putInt(ShowMovieInfoFragment.MOVIE_ID, movie.getId());
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.loaded_content, fragment)
                .addToBackStack(null)
                .commit();
    }
}
