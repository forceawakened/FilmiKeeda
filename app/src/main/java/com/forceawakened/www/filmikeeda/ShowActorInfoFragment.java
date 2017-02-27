package com.forceawakened.www.filmikeeda;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ShowActorInfoFragment extends Fragment{
    private View v;
    private ImageView actorPoster;
    private TextView actorName, actorInfo, actorBiography;
    private Context mContext;
    private ProgressDialog dialog;
    private int status;
    public static final String ACTOR_ID = "actor id";

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = inflater.inflate(R.layout.show_actor_info_fragment, container, false);
        //take hold of all views
        actorPoster = (ImageView)v.findViewById(R.id.actor_poster);
        actorName = (TextView)v.findViewById(R.id.actor_name);
        actorInfo = (TextView)v.findViewById(R.id.actor_info);
        actorBiography = (TextView)v.findViewById(R.id.actor_biography);
        //take hold of this activity as context
        mContext = getActivity();
        Integer actorID = getArguments().getInt(ACTOR_ID, 1);
        dialog = ProgressDialog.show(getActivity(), null, "Loading... Please Wait", true);
        status = 0;
        try {
            //display information about actor
            URL url = new URL(MovieUtils.getActorURL(actorID, ""));
            new ActorQueryTask().execute(url);
            url = new URL(MovieUtils.getActorURL(actorID, "/credits")); //params are send with a '/'
            new CreditedMoviesTask().execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        finally {
            return v;
        }
    }

    public class ActorQueryTask extends AsyncTask<URL, Void, Integer> {
        private StringBuilder response;
        @Override
        protected Integer doInBackground(URL... params) {
            URL url=params[0];
            int result = 0;
            try {
                HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
                urlconn.setConnectTimeout(MovieUtils.TIMEOUT_INTERVAL);
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
                    Actor actor = MovieUtils.parseActor(jsonObject);
                    //set actor poster
                    if(mContext == null)
                        Log.d("SAIF", "problem1");
                    if(actorPoster == null)
                        Log.d("SAIF", "problem2");
                    Picasso.with(mContext)
                            .load(MovieUtils.getPosterURL(actor.getPosterPath(), "w342"))
                            //.error(R.drawable.icon_image_placeholder)
                            //.placeholder(R.drawable.icon_image_placeholder)
                            .into(actorPoster);
                    //// TODO: 26/2/17 find a decent placeholder for unloaded images
                    //set actor Name
                    actorName.setText(actor.getName());
                    //set actor info
                    StringBuilder actorInfoString = new StringBuilder();
                    actorInfoString.append("Also Known As: ")
                            .append(actor.getNickNames())
                            .append("\nGender: ")
                            .append(actor.getGender())
                            .append("\nBorn: ")
                            .append(actor.getBirthDate());
                    if(!"N/A".equals(actor.getDeathDate()))
                        actorInfoString.append("\nDied: ").append(actor.getDeathDate());
                    actorInfo.setText(actorInfoString);
                    //set actor biography
                    actorInfoString = new StringBuilder();
                    if("N/A".equals(actor.getBiography()))
                        actorInfoString.append("Biography N/A");
                    else
                        actorInfoString.append(actor.getBiography());
                    actorBiography.setText(actorInfoString);
                    ++status;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(status == 2){
                dialog.dismiss();
            }
        }
    }

    public class CreditedMoviesTask extends AsyncTask<URL, Void, Integer> {
        private StringBuilder response;
        @Override
        protected Integer doInBackground(URL... params) {
            URL url=params[0];
            int result = 0;
            try {
                HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
                urlconn.setConnectTimeout(MovieUtils.TIMEOUT_INTERVAL);
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
                bundle.putInt(ShowMovieListHorizontal.FLAG, MovieUtils.MOVIE_CAST);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.credited_movies, fragment)
                        .commit();
                (v.findViewById(R.id.credited_movies_title)).setVisibility(View.VISIBLE);
                ++status;
            }
            if(status == 2){
                dialog.dismiss();
            }
        }
    }

}
