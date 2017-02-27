package com.forceawakened.www.filmikeeda;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by forceawakened on 26/2/17.
 */

public class HomepageFragment extends Fragment {
    private View v;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.homepage, container, false);
        //send connection requests for movies
        try {
            String mostPopularUrl = MovieUtils.getMovieOfTypeURL("/now_playing");
            String nowPlayingUrl = MovieUtils.getMovieOfTypeURL("/popular");
            String upcomingUrl = MovieUtils.getMovieOfTypeURL("/upcoming");
            new MovieDiscoverTask().execute(new URL(mostPopularUrl), new URL(nowPlayingUrl), new URL(upcomingUrl));
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        return v;
    }

    protected class MovieDiscoverTask extends AsyncTask<URL, Void, Integer[]> {
        final Integer NO_OF_REQUEST = 3;
        StringBuilder[] movieDiscoverResult = new StringBuilder[NO_OF_REQUEST];
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(getActivity(), null, "Loading... Please Wait", true);
        }

        @Override
        protected Integer[] doInBackground(URL... url) {
            Integer[] result = {0, 0, 0};
            try {
                for(int i = 0; i<NO_OF_REQUEST; ++i) {
                    HttpURLConnection urlconn = (HttpURLConnection) url[i].openConnection();
                    if(urlconn != null) {
                        urlconn.setConnectTimeout(MovieUtils.TIMEOUT_INTERVAL);
                        int statuscode = urlconn.getResponseCode();
                        if (statuscode == HttpURLConnection.HTTP_OK) {
                            try {
                                movieDiscoverResult[i] = new StringBuilder();
                                InputStream inputStream = urlconn.getInputStream();
                                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                                String line;
                                while ((line = r.readLine()) != null) {
                                    movieDiscoverResult[i].append(line).append('\n');
                                }
                            } finally {
                                urlconn.disconnect();
                                result[i] = 1;
                            }
                        }
                    }
                }

            }
            catch (IOException e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result[]){
            int flag = 0;
            for(int i = 0; i < NO_OF_REQUEST; ++i){
                if(result[i] == 1){
                    Fragment fragment = new ShowMovieListHorizontal();
                    Bundle bundle = new Bundle();
                    bundle.putString(ShowMovieListHorizontal.MOVIE_LIST, movieDiscoverResult[i].toString());
                    bundle.putInt(ShowMovieListHorizontal.FLAG, MovieUtils.MOVIE_RESULTS);
                    fragment.setArguments(bundle);
                    if (i == 0) {
                        getFragmentManager().beginTransaction()
                                .add(R.id.content_most_popular, fragment, "MOST_POPULAR")
                                .commit();
                        (v.findViewById(R.id.text_upcoming)).setVisibility(View.VISIBLE);

                    } else if (i == 1) {
                        getFragmentManager().beginTransaction()
                                .add(R.id.content_now_playing, fragment, "NOW_PLAYING")
                                .commit();
                        (v.findViewById(R.id.text_now_playing)).setVisibility(View.VISIBLE);

                    } else if (i == 2) {
                        getFragmentManager().beginTransaction()
                                .add(R.id.content_upcoming, fragment, "UPCOMING")
                                .commit();
                        (v.findViewById(R.id.text_most_popular)).setVisibility(View.VISIBLE);
                    }
                    flag = 1;
                }
            }
            if(flag == 0){

            }
            else{
                //do stuff
            }
            dialog.dismiss();
        }
    }
}
