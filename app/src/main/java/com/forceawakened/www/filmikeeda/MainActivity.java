package com.forceawakened.www.filmikeeda;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends BaseActivity{
    public void onCreate(Bundle savedInstanceState) {
        handleIntent(getIntent());
        super.onCreate(savedInstanceState);
        if(isNetworkAvailable()) {
            Fragment fragment = new HomepageFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.loaded_content, fragment)
                    .commit();

        }
        else{
            Fragment fragment = new NoConnection();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.loaded_content, fragment) //don't add error page to backstack
                    .commit();
        }
    }

    //launch mode is singleTop so newIntent is called
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            dialog = ProgressDialog.show(this, null, "Loading... Please Wait", true);
            try {
                new MovieSearchTask().execute(new URL(MovieUtils.getMovieSearchURL(query)));
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
            //Log.d("BA", "movie search task");
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
                Fragment fragment = new ShowMovieListVertical();
                Bundle bundle = new Bundle();
                bundle.putString(MovieUtils.MOVIE_LIST, movieSearchResult.toString());
                fragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.loaded_content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
            else{
                //movie search conn unsuccessful
                Fragment fragment = new NoConnection();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.loaded_content, fragment)
                        .commit();
            }
            dialog.dismiss();
        }
    }
}
