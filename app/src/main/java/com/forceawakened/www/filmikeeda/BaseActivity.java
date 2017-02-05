package com.forceawakened.www.filmikeeda;

import android.app.SearchManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FrameLayout activityContainer;
    private ListView drawerList;
    private String[] drawerMenuOptions;
    protected SearchView searchView;
    protected SearchManager searchManager;

    @Override
    public void setContentView(int layoutResID) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.basic_layout, null);
        activityContainer = (FrameLayout) drawerLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);

        drawerList = (ListView) drawerLayout.findViewById(R.id.drawer);
        drawerMenuOptions = getResources().getStringArray(R.array.drawer_options);
        drawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, drawerMenuOptions));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        super.setContentView(drawerLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuinflater = getMenuInflater();
        menuinflater.inflate(R.menu.my_appbar,menu);
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_toggle_navigation:
                drawerLayout.openDrawer(drawerList);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void selectItem(int position) {
        Toast.makeText(this,"Clicked pos="+position,Toast.LENGTH_SHORT).show();
        Fragment fragment = null;
        switch(position){
            case 0:
                fragment = new ShowMovieByGenreFragment();
                break;
            case 1:
                fragment = new ShowWatchListFragment();
                break;
            case 2:
                Log.d("BA", String.valueOf(getFragmentManager().getBackStackEntryCount()));
                return;
            default:
                break;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_content, fragment)
                .addToBackStack(null)
                .commit();
        drawerLayout.closeDrawer(drawerList);
    }
}
