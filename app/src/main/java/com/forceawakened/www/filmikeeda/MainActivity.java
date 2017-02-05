package com.forceawakened.www.filmikeeda;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends BaseActivity implements ShowMovieByGenreFragment.CallBack, ShowWatchListFragment.CallBack {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        try {
            String mostPopularUrl = MovieFactory.getPopularSearchURL();
            String nowPlayingUrl = MovieFactory.getNowPlayingSearchURL();
            String upcomingUrl = MovieFactory.getUpcomingSearchURL();
            new MovieDiscoverTask().execute(new URL(mostPopularUrl), new URL(nowPlayingUrl), new URL(upcomingUrl));
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(searchManager.QUERY);
            URL url;
            try {
                url = new URL(MovieFactory.getMovieSearchURL(query));
                new MovieSearchTask().execute(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void genreSelected(Integer id) {
        String query = MovieFactory.getMovieDiscoverURL(new Pair<>("sort_by","popularity.desc"),new Pair<>("with_genres",id.toString()));
        URL url;
        try {
            url = new URL(query);
            new MovieSearchTask().execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void movieSelected(Integer id) {
        Intent intent = new Intent(this, ShowMovieInfo.class);
        intent.putExtra(ShowMovieInfo.MOVIE_ID, id);
        startActivity(intent);
    }

    protected class MovieDiscoverTask extends AsyncTask<URL, Void, Integer[]> {
        final Integer NO_OF_REQUEST = 3;
        StringBuilder[] movieDiscoverResult = new StringBuilder[NO_OF_REQUEST];
        @Override
        protected Integer[] doInBackground(URL... url) {
            Integer[] result = new Integer[NO_OF_REQUEST];
            try {
                for(int i = 0; i<NO_OF_REQUEST; ++i) {
                    HttpURLConnection urlconn = (HttpURLConnection) url[i].openConnection();
                    urlconn.setConnectTimeout(2500);
                    int statuscode = urlconn.getResponseCode();

                    if (statuscode == HttpURLConnection.HTTP_OK) {
                        try {
                            movieDiscoverResult[i] = new StringBuilder();
                            InputStream inputStream = urlconn.getInputStream();
                            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                            String line = null;
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
            catch (IOException e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result[]){
            for(int i = 0; i < NO_OF_REQUEST; ++i){
                if(result[i] == 1){
                    //Log.d("main act", "request complete = " + i + movieDiscoverResult[i]);
                    Fragment fragment = new ShowMovieListHorizontal();
                    Bundle bundle = new Bundle();
                    bundle.putString(ShowMovieListHorizontal.MOVIE_LIST, movieDiscoverResult[i].toString());
                    fragment.setArguments(bundle);
                    switch (i){
                        case 0:
                           getSupportFragmentManager().beginTransaction()
                                   .add(R.id.content_most_popular, fragment, "MOST_POPULAR")
                                   .commit();
                            break;
                        case 1:
                            getSupportFragmentManager().beginTransaction()
                                    .add(R.id.content_now_playing, fragment, "NOW_PLAYING")
                                    .commit();
                            break;
                        case 2:
                            getSupportFragmentManager().beginTransaction()
                                    .add(R.id.content_upcoming, fragment, "UPCOMING")
                                    .commit();
                            break;
                    }
                }
                else{
                    Log.d("main act", "request not complete = " + i);
                    //// TODO: 3/2/17  implement functionality when internet connection is broken
                    //// TODO: 3/2/17  show refresh button
                }
            }
        }
    }

    //Async Thread to execute movie search requests
    protected class MovieSearchTask extends AsyncTask<URL, Void, Integer> {
        StringBuilder movieSearchResult;
        @Override
        protected Integer doInBackground(URL... params) {
            URL url=params[0];
            int result = 0;
            try {
                HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
                urlconn.setConnectTimeout(2500);
                int statuscode = urlconn.getResponseCode();

                if(statuscode == HttpURLConnection.HTTP_OK){
                    try {
                        movieSearchResult = new StringBuilder();
                        InputStream inputStream = urlconn.getInputStream();
                        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = r.readLine()) != null) {
                            movieSearchResult.append(line).append('\n');
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
                Fragment fragment = new ShowMovieListFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ShowMovieListFragment.MOVIE_LIST, movieSearchResult.toString());
                fragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
            else{
                //// TODO: 3/2/17  implement functionality when internet connection is broken
                //// TODO: 3/2/17  show refresh button
            }
        }
    }
    
    //// TODO: 4/2/17 add watchlist and reminder features in navigation display
}
