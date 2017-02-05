package com.forceawakened.www.filmikeeda;

import android.graphics.Movie;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by forceawakened on 2/2/17.
 */
public class MovieFactory {

    static final private String MAIN_SCHEME="https";
    static final private String MAIN_AUTH = "api.themoviedb.org";
    static final private String MAIN_AUTH_IMG = "image.tmdb.org";
    static final private String MY_API="496891e3b20e6e8f9b2a9817ca00c709";

    private MovieFactory(){}    //cannot instantiate this class

    public static ArrayList<MovieInfo> getMovieList(JSONObject jsonObject) throws JSONException {
        ArrayList<MovieInfo> movieList = null;
        if(jsonObject != null){
            movieList = new ArrayList<>();

            JSONArray movieObjectArray = jsonObject.getJSONArray("results");
            JSONObject movieObject;

            for(int i = 0; i < movieObjectArray.length(); ++i){
                movieObject = movieObjectArray.getJSONObject(i);
                MovieInfo movie = parseMovie(movieObject);
                movieList.add(movie);
            }
        }
        return movieList;
    }

    public static Uri getPosterURL(String posterPath, String size) {
        Uri.Builder builder = new Uri.Builder();

        if(!"".equals(posterPath)) {
            builder.scheme(MAIN_SCHEME);
            builder.authority(MAIN_AUTH_IMG);
            builder.appendPath("t")
                    .appendPath("p")
                    .appendPath(size)
                    .appendPath(posterPath.substring(1));       //poster path starts with a redundant '/' character
        }

        return builder.build();
    }

    public static String getMovieSearchURL(String movieName) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(MAIN_SCHEME);
        builder.authority(MAIN_AUTH);

        builder.appendPath("3")
                .appendPath("search")
                .appendPath("movie")
                .appendQueryParameter("api_key", MY_API)
                .appendQueryParameter("query", movieName);
        return builder.build().toString();
    }

    public static String getMovieDiscoverURL(Pair<String, String>... Parameters) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(MAIN_SCHEME);
        builder.authority(MAIN_AUTH);

        builder.appendPath("3")
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("api_key", MY_API);
        for(Pair<String,String> p : Parameters){
            builder.appendQueryParameter(p.first, p.second);
        }
        return builder.build().toString();
    }

    public static String getMovieURL(Integer movieId) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(MAIN_SCHEME);
        builder.authority(MAIN_AUTH);

        builder.appendPath("3")
                .appendPath("movie")
                .appendPath(movieId.toString())
                .appendQueryParameter("api_key",MY_API);

        return builder.build().toString();
    }

    public static MovieInfo parseMovie(JSONObject movieObject) throws JSONException {
        MovieInfo movie = new MovieInfo();
        movie.setId(movieObject.getInt("id"));
        movie.setTitle(movieObject.getString("title"));
        movie.setReleaseDate(movieObject.getString("release_date"));
        movie.setPosterPath(movieObject.getString("poster_path"));
        movie.setAdult(movieObject.getBoolean("adult"));
        movie.setOverview(movieObject.getString("overview"));

        if(movieObject.has("genres")){
            movie.setGenres(getGenreList(movieObject));
        }
        if(movieObject.has("runtime")){
            movie.setRuntime(movieObject.getInt("runtime"));
        }
        return movie;
    }

    public static String getGenreSearchURL() {
        String genreQueryURL = "https://api.themoviedb.org/3/genre/movie/list?api_key=";
        genreQueryURL += MY_API;
        return genreQueryURL;
    }

    public static String getNowPlayingSearchURL(){
        String nowPlayingSearchURL = "https://api.themoviedb.org/3/movie/now_playing?api_key=";
        nowPlayingSearchURL += MY_API + "&language=en-US&page=1";
        return nowPlayingSearchURL;

    }

    public static String getPopularSearchURL(){
        String popularSearchURL = "https://api.themoviedb.org/3/movie/popular?api_key=";
        popularSearchURL += MY_API + "&language=en-US&page=1";
        return popularSearchURL;

    }

    public static String getUpcomingSearchURL(){
        String upcomingSearchURL = "https://api.themoviedb.org/3/discover/movie?api_key=";
        Date d = new Date();
        CharSequence s  = DateFormat.format("yyyy-MM-dd", d.getTime());
        upcomingSearchURL += MY_API + "&region=IN&sort_by=release_date.asc&include_adult=false" +
                "&include_video=false&page=1&primary_release_date.gte=" + s +
                "&with_original_language=hi%7Cen";
        return upcomingSearchURL;

    }

    public static ArrayList<Genre> getGenreList(JSONObject jsonObject) {
        ArrayList<Genre> genres = null;
        if(jsonObject.has("genres")){
            genres = new ArrayList<>();
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
        }
        return genres;
    }
}
