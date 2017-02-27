package com.forceawakened.www.filmikeeda;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by forceawakened on 3/2/17.
 */

public class ShowWatchList extends Fragment implements AdapterWatchlist.Callback{
    private ArrayList<mMovie> movieList;
    private RecyclerView recyclerView;
    private AdapterWatchlist adapter;
    private ProgressDialog dialog;
    private mMovie movie; //movie that will be deleted

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recycler_view_vertical, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        movieList = new ArrayList<>();
        adapter = new AdapterWatchlist(getActivity(), this, movieList);
        recyclerView.setAdapter(adapter);
        dialog = ProgressDialog.show(getActivity(), null, "Loading... Please Wait", true);
        new WatchListQueryTask().execute();
        return v;
    }

    @Override
    public void deleteItem(int position) {
        movie = movieList.get(position);
        new DeleteMovieTask().execute();
        movieList.remove(position);
        recyclerView.removeViewAt(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, movieList.size());
        File file = new File(MovieUtils.getImgPath(String.valueOf(getContext().getFilesDir()), String.valueOf(movie.getId())));
        boolean result = file.delete();
        Log.d("SWL", "result=" + result);
        Toast.makeText(getActivity(), movie.getTitle() + " is removed from your watchlist", Toast.LENGTH_SHORT).show();
    }

    public class WatchListQueryTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DBHelper dbHelper = new DBHelper(getActivity());
            movieList.addAll(dbHelper.getAllMovies());
            return null;
        }
        @Override
        protected void onPostExecute(Void v){
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        }
    }

    public class DeleteMovieTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DBHelper dbHelper = new DBHelper(getActivity());
            dbHelper.deleteMovie(movie);
            return null;
        }
    }
}
