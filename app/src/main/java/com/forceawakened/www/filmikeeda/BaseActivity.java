package com.forceawakened.www.filmikeeda;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ShowGenreList.CallBack, ShowWatchList.CallBack{
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    protected SearchView mSearchView;
    protected SearchManager mSearchManager;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base);
        //take hold of navigaton view and set on item click listener
        NavigationView mDrawer = (NavigationView) findViewById(R.id.navbar);
        mDrawer.setNavigationItemSelectedListener(this);
        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //save title string
        title = getResources().getString(R.string.app_name);
        //add toggle navigation bar button
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if(title != null){
                    mToolbar.setTitle(title);
                }
            }
        };
        //set toggle listeners
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuinflater = getMenuInflater();
        menuinflater.inflate(R.menu.appbar,menu);
        mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        ComponentName c = new ComponentName(this, MainActivity.class);
        mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(c));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_search) {
            //do stuff
        }
        else if (i == R.id.action_settings) {
            //do stuff
        }
        else {
            super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int i = item.getItemId();
        if(i == R.id.homepage){
            title = getString(R.string.homepage);
            fragment = new HomepageFragment();
        }
        if(i == R.id.by_genres){
            title = getString(R.string.by_genres);
            fragment = new ShowGenreList();
        }
        else if(i == R.id.my_watchlist){
            title = getString(R.string.watchlist);
            fragment = new ShowWatchList();
        }
        else if(i == R.id.my_reminders){
            title = getString(R.string.reminders);
            //do stuff
            return true;
        }
        else{
            //do stuff
            return true;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.loaded_content, fragment)
                .addToBackStack(null)
                .commit();
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }

    //callback function to show watchlist
    public void movieSelected(Integer id) {
        Fragment fragment = new ShowMovieInfoFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        bundle.putInt(ShowMovieInfoFragment.MOVIE_ID, id);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.loaded_content, fragment)
                .addToBackStack(null)
                .commit();
    }

    //callback function to genre list
    public void genreSelected(Integer id) {
        String query = MovieUtils.getMovieDiscoverURL(new Pair<>("sort_by","popularity.desc"),new Pair<>("with_genres",id.toString()));
        URL url;
        try {
            url = new URL(query);
            new MovieSearchTask().execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
                //Log.d("BA", "movie search: " + movieSearchResult.toString());
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
                //Log.d("BA", "movie search failed");
                //// TODO: 3/2/17  implement functionality when internet connection is broken
                //// TODO: 3/2/17  show refresh button
            }
        }
    }
}
