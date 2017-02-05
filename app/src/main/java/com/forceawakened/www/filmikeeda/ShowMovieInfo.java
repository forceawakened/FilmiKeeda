package com.forceawakened.www.filmikeeda;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

public class ShowMovieInfo extends BaseActivity {

    private MovieInfo movie;
    private StringBuilder response;
    private ImageView moviePoster;
    private TextView movieTitle, movieReleaseDate, movieOverview, movieGenres, movieAdult, movieRuntime;
    private Context mContext;
    private Button addToWatchlistBtn;
    private Button addReminderBtn;
    static final String MOVIE_ID = "MOVIE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_movie_info);

        moviePoster = (ImageView)findViewById(R.id.movie_poster);
        movieTitle = (TextView)findViewById(R.id.movie_title);
        movieReleaseDate = (TextView)findViewById(R.id.movie_release_date);
        movieGenres = (TextView)findViewById(R.id.movie_genres);
        movieAdult = (TextView)findViewById(R.id.movie_adult);
        movieOverview = (TextView)findViewById(R.id.movie_overview);
        movieRuntime = (TextView)findViewById(R.id.movie_runtime);

        mContext = this;

        addToWatchlistBtn = (Button)findViewById(R.id.add_to_watchlist_btn);
        addToWatchlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "added to watchlist", Toast.LENGTH_SHORT).show();
                Log.d("SMI", "add to watchlist button click listener");
                new AddToDBTask().execute();
            }
        });

        addReminderBtn = (Button)findViewById(R.id.set_reminder_btn);
        addReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        URL url;
        try {
            url = new URL(MovieFactory.getMovieURL(getIntent().getIntExtra(MOVIE_ID, 1)));
            new MovieQueryTask().execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public class MovieQueryTask extends AsyncTask<URL, Void, Integer> {

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
                        response = new StringBuilder();
                        InputStream inputStream = urlconn.getInputStream();
                        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = r.readLine()) != null) {
                            response.append(line).append('\n');
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
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(String.valueOf(response));
                    movie = MovieFactory.parseMovie(jsonObject);

                    Picasso.with(getBaseContext())
                            .load(MovieFactory.getPosterURL(movie.getPosterPath(), "w342"))
                            .error(R.drawable.icon_image_placeholder)
                            .placeholder(R.drawable.icon_image_placeholder)
                            .into(moviePoster);

                    movieTitle.setText(movie.getTitle());
                    movieReleaseDate.setText("Released: " + movie.getReleaseDate());
                    movieRuntime.setText("Runtime: " + movie.getRuntime() + " minutes");
                    movieGenres.setText("Genres: " + movie.getAllGenresName());
                    movieAdult.setText("Adult Content: " + movie.getAdultString());
                    movieOverview.setText(movie.getOverview());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                //// TODO: 3/2/17  implement functionality when internet connection is broken
                //// TODO: 3/2/17  show refresh button
                //// TODO: 4/2/17 add add reminder and add to watchlist buttons
            }
        }
    }

    public class AddToDBTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Log.d("SMI", "addToDb");
            //Toast.makeText(mContext, "added to watchlist", Toast.LENGTH_SHORT).show();
            DBHelper dbHelper = new DBHelper(mContext);
            dbHelper.open();
            dbHelper.addMovie(movie);
            dbHelper.close();
            Log.d("SMI", "addToDb end");
            return null;
        }
    }

}
