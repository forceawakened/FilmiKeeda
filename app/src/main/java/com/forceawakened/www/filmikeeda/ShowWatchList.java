package com.forceawakened.www.filmikeeda;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by forceawakened on 3/2/17.
 */

public class ShowWatchList extends Fragment {
    private ArrayList<mMovie> watchList;
    private ListView listView;
    private ArrayAdapter<mMovie> adapter;
    private CallBack mCallBack;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallBack = (CallBack) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.show_list, container, false);
        listView = (ListView) v.findViewById(R.id.list_view);
        listView.setOnItemClickListener(new ListItemClickListener());
        new WatchListQueryTask().execute();
        return v;
    }

    public class WatchListQueryTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //Log.d("SWLF", "do in backg start");
            DBHelper dbHelper = new DBHelper(getActivity());
            watchList = dbHelper.getAllMovies();
            return null;
        }

        @Override
        protected void onPostExecute(Void v){

            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, watchList);
            listView.setAdapter(adapter);

            //// TODO: 3/2/17 store movie poster and display using ShowMovieListVertical
            //// TODO: 3/2/17 give users option to delete from watchlist
        }
    }

    private class ListItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        mCallBack.movieSelected(watchList.get(position).getId());
    }

    public interface CallBack{
        void movieSelected(Integer id);
    }
}
