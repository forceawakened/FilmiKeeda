package com.forceawakened.www.filmikeeda;

import android.app.ProgressDialog;
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

public class ShowGenreList extends Fragment {
    private StringBuilder genreQueryResult;
    private ListView listView;
    private ArrayList<Genre> genreList;
    private ArrayAdapter<Genre> adapter;
    private ProgressDialog dialog;
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
        View v = inflater.inflate(R.layout.list_view, container, false);
        try {
            genreList = new ArrayList<>();
            String genreQueryURL = MovieUtils.getGenreSearchURL();
            dialog = ProgressDialog.show(getActivity(), null, "Loading... Please Wait", true);
            new GenreQueryTask().execute(new URL(genreQueryURL));
            listView = (ListView) v.findViewById(R.id.list_view);
            listView.setOnItemClickListener(new ListItemClickListener());
            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, genreList);
            listView.setAdapter(adapter);
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        finally {
            return v;
        }
    }

    public class GenreQueryTask extends AsyncTask<URL, Void, Integer> {

        @Override
        protected Integer doInBackground(URL... params) {
            URL url=params[0];
            int result = 0;
            try {
                HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
                urlconn.setConnectTimeout(5000);
                int statuscode = urlconn.getResponseCode();

                if(statuscode == HttpURLConnection.HTTP_OK){
                    try {
                        genreQueryResult = new StringBuilder();
                        InputStream inputStream = urlconn.getInputStream();
                        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = r.readLine()) != null) {
                            genreQueryResult.append(line).append('\n');
                        }
                    } finally {
                        urlconn.disconnect();
                        result = 1;
                    }
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result){
            if(result == 1){
                try {
                    genreList.addAll(MovieUtils.getGenreList(new JSONObject(genreQueryResult.toString())));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        }
    }

    private class ListItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        mCallBack.genreSelected(genreList.get(position).id);
    }

    public interface CallBack{
        void genreSelected(Integer id);
    }
}
