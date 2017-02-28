package com.forceawakened.www.filmikeeda;

import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by forceawakened on 2/2/17.
 */
public abstract class MovieUtils {
    //for uri builder
    private static final String SCHEME = "https";
    private static final String AUTH = "api.themoviedb.org";
    private static final String AUTH_IMG = "image.tmdb.org";
    //for api queries
    private static final String SITE = "https://api.themoviedb.org/3/";
    private static final String MY_API = "496891e3b20e6e8f9b2a9817ca00c709";   //secret api key :p
    //some constant strings
    public static final String MOVIE_NAME = "movie name";
    public static final String MOVIE_ID = "movie id";
    public static final String MOVIE_LIST = "movie list";
    public static final String LAST_REMINDER_ID = "last reminder id";
    //some constants integers
    public static final int TIMEOUT_INTERVAL = 2000;
    //arbitrary numbers assigned as constants
    public static final int MOVIE_RESULTS = 1763;
    public static final int MOVIE_CAST = 9564;
    public static final int WATCHLIST = 132;
    public static final int REMINDER = 217;

    //take a json object representing a movie and returns movie object
    public static mMovie parseMovie(JSONObject movieObject) throws JSONException {
        mMovie movie = new mMovie();
        movie.setId(movieObject.getInt("id"))
                .setTitle(movieObject.getString("title"))
                .setReleaseDate(movieObject.getString("release_date"))
                .setPosterPath(movieObject.getString("poster_path"))
                .setAdult(movieObject.getBoolean("adult"));
        if(movieObject.has("overview")) {
            movie.setOverview(movieObject.getString("overview"));
        }
        if(movieObject.has("genres")){
            movie.setGenres(getGenreList(movieObject));
        }
        if(movieObject.has("runtime")){
            movie.setRuntime(movieObject.getInt("runtime"));
        }
        return movie;
    }

    //take a json object representing an actor and returns actor object
    public static Actor parseActor(JSONObject actorObject) throws JSONException {
        Actor actor = new Actor();
        actor.setId(actorObject.getInt("id"))
                .setName(actorObject.getString("name"))
                .setPosterPath(actorObject.getString("profile_path"))
                .setGender(actorObject.getInt("gender"))
                .setBirthdate(actorObject.getString("birthday"))
                .setDeathdate(actorObject.getString("deathday"))
                .setBiography(actorObject.getString("biography"))
                .setNicknames(getNicknames(actorObject.getJSONArray("also_known_as")));
        return actor;
    }

    //helper function takes nicknames as json array object and returns them as string
    private static String getNicknames(JSONArray nicknameArray) throws JSONException {
        String nicknames = "";
        for(int i = 0; i < nicknameArray.length(); ++i){
            if(i > 0){
                nicknames += ", ";
            }
            nicknames += nicknameArray.getString(i);
        }
        return nicknames;
    }

    //converts json object to list of movies
    public static ArrayList<mMovie> getMovieList(JSONObject movieJsonObject, int flag) throws JSONException {
        if(movieJsonObject != null){
            ArrayList<mMovie> movieList = new ArrayList<>();
            JSONArray movieObjectArray = null;
            if (flag == MOVIE_CAST) {
                movieObjectArray = movieJsonObject.getJSONArray("cast");

            } else if (flag == MOVIE_RESULTS) {
                movieObjectArray = movieJsonObject.getJSONArray("results");

            } else {
                //do something
            }
            if(movieObjectArray != null) {
                for(int i = 0; i < movieObjectArray.length(); ++i){
                    mMovie m = parseMovie(movieObjectArray.getJSONObject(i));
                    movieList.add(m);
                }
            }
            return movieList;
        }
        else{
            return null;
        }
    }

    //converts json object to list of genres
    public static ArrayList<Genre> getGenreList(JSONObject jsonObject) {
        if(jsonObject.has("genres")){
            ArrayList<Genre> genres = new ArrayList<>();
            JSONArray genreArrayJSON;
            JSONObject genreObjectJSON;
            try {
                genreArrayJSON = jsonObject.getJSONArray("genres");
                for(int i = 0; i < genreArrayJSON.length(); ++i){
                    genreObjectJSON = genreArrayJSON.getJSONObject(i);
                    genres.add(new Genre(genreObjectJSON.getInt("id"),genreObjectJSON.getString("name")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                return genres;
            }
        }
        else{
            return null;
        }
    }

    //converts json object to list of actors
    public static ArrayList<Actor> getActorList(JSONObject actorJsonObject) throws JSONException {
        if(actorJsonObject != null){
            ArrayList<Actor> actorList = new ArrayList<>();
            Actor actor;
            JSONArray actorObjectArray = actorJsonObject.getJSONArray("cast");
            JSONObject actorObject;
            if(actorObjectArray != null ) {
                for (int i = 0; i < actorObjectArray.length(); ++i) {
                    actor = new Actor();
                    actorObject = actorObjectArray.getJSONObject(i);
                    actor.setId(actorObject.getInt("id"))
                            .setName(actorObject.getString("name"))
                            .setPosterPath(actorObject.getString("profile_path"));
                    actorList.add(actor);
                }
            }
            return actorList;
        }
        else{
            return null;
        }
    }

    //returns posterurl as uri for picasso to process it
    public static Uri getPosterURL(String posterPath, String size) {
        if (posterPath !=null && !"".equals(posterPath)) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(SCHEME)
                    .authority(AUTH_IMG)
                    .appendPath("t")
                    .appendPath("p")
                    .appendPath(size)
                    .appendPath(posterPath.substring(1));   //poster path starts with a redundant '/' character
            return builder.build();
        }
        else {
            return null;
        }
    }

    //returns discover movie url (such as search by genre, popularity, region etc)
    public static String getMovieDiscoverURL(Pair<String, String>... Parameters) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTH)
                .appendPath("3")
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("api_key", MY_API);
        for(Pair<String,String> p : Parameters){
            builder.appendQueryParameter(p.first, p.second);
        }
        return builder.build().toString();
    }

    //url to search a movie
    public static String getMovieSearchURL(String movieName) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTH)
                .appendPath("3")
                .appendPath("search")
                .appendPath("movie")
                .appendQueryParameter("api_key", MY_API)
                .appendQueryParameter("language","en-US")
                .appendQueryParameter("query", movieName)
                .appendQueryParameter("page",String.valueOf(1));
        return builder.build().toString();
    }

    //url to get list of genres
    public static String getGenreSearchURL() {
        return SITE + "genre/movie/list?api_key=" + MY_API;
    }
    //url to search for upcoming, trending, upcoming movies
    public static String getMovieOfTypeURL(String param){
        if("/upcoming".equals(param)){
            return getCustomUpcomingSearchURL();
        }
        return SITE + "movie" + param + "?api_key=" + MY_API + "&region=US&page=1";

    }

    //helper function to optimised to generate best query url for upcoming movies
    public static String getCustomUpcomingSearchURL(){
        Date d = new Date();
        CharSequence currentDate  = DateFormat.format("yyyy-MM-dd", d.getTime());
        return SITE + "discover/movie?api_key=" + MY_API +
                "&region=IN&sort_by=release_date.asc&include_adult=false" +
                "&include_video=false&page=1&primary_release_date.gte=" + currentDate +
                "&with_original_language=hi%7Cen";
    }

    //get urls about movie such as overview, runtime etc
    public static String getMovieURL(Integer movieId, String param) {
        return SITE + "movie/" + movieId + param + "?api_key=" + MY_API + "&page=1";
    }

    //get urls about people such as info, credited movies
    public static String getActorURL(Integer actorID, String param) {
        return SITE + "person/" + actorID + param + "?api_key=" + MY_API + "&language=en-US";
    }

    //returns image path in file directory
    public static String getImgPath(String filePath, String movieID, int who){
        return filePath + "MOVIE_" + movieID + "_" + who + ".png";
    }
}
