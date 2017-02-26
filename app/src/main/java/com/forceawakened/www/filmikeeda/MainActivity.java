package com.forceawakened.www.filmikeeda;

import android.app.SearchManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends BaseActivity {
    public void onCreate(Bundle savedInstanceState) {
        handleIntent(getIntent());
        super.onCreate(savedInstanceState);
        Fragment fragment;
        if(isNetworkAvailable()) {
            fragment = new HomepageFragment();
        }
        else{
            fragment = new NoConnection();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.loaded_content, fragment)
                .addToBackStack(null)
                .commit();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //launch mode ---> singleTop so newIntent is called
    protected void onNewIntent(Intent intent) {
        //Log.d("SRA", "on new intent");
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //Log.d("SRA", "handle search query intent");
            String query = intent.getStringExtra(SearchManager.QUERY);
            URL url;
            try {
                url = new URL(MovieUtils.getMovieSearchURL(query));
                new MovieSearchTask().execute(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    //Async Thread to execute movie search requests
    protected class MovieSearchTask extends AsyncTask<URL, Void, Integer> {
        StringBuilder movieSearchResult;
        @Override
        protected Integer doInBackground(URL... params) {
            Log.d("BA", "movie search task");
            URL url=params[0];
            int result = 0;
            try {
                HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
                urlconn.setConnectTimeout(MovieUtils.TIMEOUT_INTERVAL);
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
                        urlconn.disconnect();
                        result = 1;
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result){
            if(result == 1){
                //Log.d("SRA", "movie search: " + movieSearchResult.toString());
                Fragment fragment = new ShowMovieListVertical();
                Bundle bundle = new Bundle();
                bundle.putString(ShowMovieListVertical.MOVIE_LIST, movieSearchResult.toString());
                fragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.loaded_content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
            else{
                //Log.d("SRA", "movie search failed");
                //// TODO: 3/2/17  implement functionality when internet connection is broken
                //// TODO: 3/2/17  show refresh button
            }
        }
    }
}
