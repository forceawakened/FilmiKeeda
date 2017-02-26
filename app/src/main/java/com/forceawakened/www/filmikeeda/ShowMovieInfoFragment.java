package com.forceawakened.www.filmikeeda;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;

public class ShowMovieInfoFragment extends Fragment implements View.OnClickListener{
    private View v;
    private mMovie movie;
    private ImageView moviePoster;
    private TextView movieTitle, movieInfo, movieOverview;
    private Context mContext;
    private Button showTimePickerBtn;
    private Button showDatePickerBtn;
    private boolean toggle, dateSet, timeSet;
    private RelativeLayout setReminderContent;
    private Calendar c;
    public static final String MOVIE_ID = "movie id";
    public static final String MOVIE_NAME = "movie name";
    final Integer TIME_OUT_INTERVAL = 2000;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = inflater.inflate(R.layout.show_movie_info_fragment, container, false);
        //take hold of all views
        moviePoster = (ImageView)v.findViewById(R.id.movie_poster);
        movieTitle = (TextView)v.findViewById(R.id.movie_title);
        movieInfo = (TextView)v.findViewById(R.id.movie_info);
        movieOverview = (TextView)v.findViewById(R.id.movie_overview);
        //take hold of all buttons :p
        Button setReminderBtn = (Button) v.findViewById(R.id.set_reminder_btn);
        Button addToWatchlistBtn = (Button) v.findViewById(R.id.add_to_watchlist_btn);
        showTimePickerBtn = (Button)v.findViewById(R.id.show_time_picker_btn);
        showDatePickerBtn = (Button)v.findViewById(R.id.show_date_picker_btn);
        Button okBtn = (Button) v.findViewById(R.id.ok_btn);
        setReminderContent = (RelativeLayout)v.findViewById(R.id.set_reminder_content);
        //initialise flags
        toggle = false;
        dateSet = false;
        timeSet = false;
        mContext = getActivity();
        //set listeners
        setReminderBtn.setOnClickListener(this);
        addToWatchlistBtn.setOnClickListener(this);
        showTimePickerBtn.setOnClickListener(this);
        showDatePickerBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
        //get movie id from intent
        Integer movieID = getArguments().getInt(MOVIE_ID, 1);
        try {
            //display information about movie
            URL url = new URL(MovieUtils.getMovieURL(movieID, ""));
            new MovieQueryTask().execute(url);
            //display similar movies
            url = new URL(MovieUtils.getMovieURL(movieID, "/similar"));
            new SimilarMoviesTask().execute(url);
            //display actors in movie
            url = new URL(MovieUtils.getMovieURL(movieID, "/credits"));
            new MovieActorsTask().execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        finally {
            return v;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    //reset everything in set reminder toolbar
    private  void resetReminderToolbar(){
        toggle = false;
        timeSet = false;
        dateSet = false;
        showDatePickerBtn.setText(R.string.string_choose_date);
        showTimePickerBtn.setText(R.string.string_choose_time);
        setReminderContent.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.add_to_watchlist_btn:
                //add mContext movie to users watchlist
                new AddToDBTask().execute();
                break;
            case R.id.set_reminder_btn:
                //show or hide time & date picker buttons
                if(!toggle) {
                    setReminderContent.setVisibility(View.VISIBLE);
                    toggle = true;
                }
                else {
                    resetReminderToolbar();
                }
                break;
            case R.id.show_date_picker_btn:
                //get current date
                final Calendar c1 = Calendar.getInstance();
                int mYear = c1.get(Calendar.YEAR);
                int mMonth = c1.get(Calendar.MONTH);
                int mDay = c1.get(Calendar.DAY_OF_MONTH);
                //show date picker
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //show selected date on button
                        showDatePickerBtn.setText(dayOfMonth + " / " + month + " / " + year);
                        if(!dateSet){
                            dateSet = true;
                        }
                        //set values in calendar
                        if(c == null){
                            c = Calendar.getInstance();
                        }
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, month);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            case R.id.show_time_picker_btn:
                //get current time
                final Calendar c2 = Calendar.getInstance();
                int mHour = c2.get(Calendar.HOUR_OF_DAY);
                int mMinute = c2.get(Calendar.MINUTE);
                //show time picker
                TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //show selected time on button
                        showTimePickerBtn.setText(hourOfDay + " : " + minute);
                        if(!timeSet){
                            timeSet = true;
                        }
                        //set values in calendar
                        if(c == null){
                            c = Calendar.getInstance();
                        }
                        c.set(Calendar.HOUR, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                    }
                }, mHour, mMinute, true);
                timePickerDialog.show();
                break;
            case R.id.ok_btn:
                //check if date and time are selected then proceed
                if(timeSet && dateSet){
                    //sets alarm
                    AlarmManager alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(mContext, AlarmReceiver.class);
                    intent.putExtra(MOVIE_NAME, movie.getTitle());
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
                    //alarmMgr.set(AlarmManager.ELAPSED_REALTIME, 10000, alarmIntent);
                    alarmMgr.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), alarmIntent);
                    Toast.makeText(mContext, "reminder set for " + movie.getTitle(), Toast.LENGTH_SHORT).show();
                }
                resetReminderToolbar();
                break;
        }
    }

    public class MovieQueryTask extends AsyncTask<URL, Void, Integer> {
        private StringBuilder response;
        @Override
        protected Integer doInBackground(URL... params) {
            URL url=params[0];
            int result = 0;
            try {
                HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
                urlconn.setConnectTimeout(TIME_OUT_INTERVAL);
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
                    movie = MovieUtils.parseMovie(jsonObject);
                    //set movie poster
                    Picasso.with(mContext)
                            .load(MovieUtils.getPosterURL(movie.getPosterPath(), "w342"))
                            //.error(R.drawable.icon_image_placeholder)
                            //.placeholder(R.drawable.icon_image_placeholder)
                            .into(moviePoster);
                    (v.findViewById(R.id.set_reminder_btn)).setVisibility(View.VISIBLE);
                    (v.findViewById(R.id.add_to_watchlist_btn)).setVisibility(View.VISIBLE);
                    //set movie title
                    movieTitle.setText(movie.getTitle());
                    //set movie info
                    StringBuilder movieInfoString = new StringBuilder();
                    movieInfoString.append("Release date: ")
                            .append(movie.getReleaseDate())
                            .append("\nRuntime: ");
                    if(movie.getRuntime()>0)
                        movieInfoString.append(movie.getRuntime()).append(" minutes");
                    else
                        movieInfoString.append("N/A");
                    movieInfoString.append("\nGenres: ")
                            .append(movie.getAllGenresName());
                    movieInfo.setText(movieInfoString);
                    //set movie overview
                    String overview;
                    if("".equals(movie.getOverview()))
                        overview = "Overview N/A";
                    else
                        overview = movie.getOverview();
                    movieOverview.setText(overview);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                //// TODO: 3/2/17  implement functionality when internet connection is broken
                //// TODO: 3/2/17  show refresh button
            }
        }
    }

    public class SimilarMoviesTask extends AsyncTask<URL, Void, Integer> {
        private StringBuilder response;
        @Override
        protected Integer doInBackground(URL... params) {
            URL url=params[0];
            int result = 0;
            try {
                HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
                urlconn.setConnectTimeout(TIME_OUT_INTERVAL);
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
                        result = 1;
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        urlconn.disconnect();
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
                Fragment fragment = new ShowMovieListHorizontal();
                Bundle bundle = new Bundle();
                bundle.putString(ShowMovieListHorizontal.MOVIE_LIST, response.toString());
                bundle.putInt(ShowMovieListHorizontal.FLAG, MovieUtils.MOVIE_RESULTS);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.similar_movies, fragment)
                        .commit();
                (v.findViewById(R.id.similar_movies_title)).setVisibility(View.VISIBLE);
            }
            else{
                //do stuff
            }
        }
    }

    public class MovieActorsTask extends AsyncTask<URL, Void, Integer> {
        private StringBuilder response;
        @Override
        protected Integer doInBackground(URL... params) {
            URL url=params[0];
            int result = 0;
            try {
                HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
                urlconn.setConnectTimeout(TIME_OUT_INTERVAL);
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
                        result = 1;
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        urlconn.disconnect();
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
                Fragment fragment = new ShowActorListHorizontal();
                Bundle bundle = new Bundle();
                bundle.putString(ShowActorListHorizontal.ACTOR_LIST, response.toString());
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.movie_cast, fragment)
                        .commit();
                (v.findViewById(R.id.movie_cast_title)).setVisibility(View.VISIBLE);
            }
            else{
                //do stuff
            }
        }
    }

    public class AddToDBTask extends AsyncTask<Void, Void, Void> {
        int result = 0;
        @Override
        protected Void doInBackground(Void... params) {
            DBHelper dbHelper = new DBHelper(mContext);
            dbHelper.open();
            try {
                dbHelper.addMovie(movie);
                result = 1;
            }
            catch (SQLiteConstraintException e){
                e.printStackTrace();
            }
            finally{
                dbHelper.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //show toast based on result
            if(result == 1){
                Toast.makeText(mContext, "added to your watchlist", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(mContext, "already in your watchlist", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
