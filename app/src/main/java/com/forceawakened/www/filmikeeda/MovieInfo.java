package com.forceawakened.www.filmikeeda;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by forceawakened on 2/2/17.
 */
public class MovieInfo {

    private Integer id;
    private String title, releaseDate, overview, posterPath;
    private ArrayList<Genre> genres;
    private int runtime;
    private boolean adult;

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setAdult(boolean adult) { this.adult = adult; }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setOverview(String overview) {this.overview = overview;}

    public void setGenres(ArrayList<Genre> genres) { this.genres = genres; }

    public void setGenres(String genreStringName, String genreStringId){
        String name[] = genreStringName.split(" ,");
        String id[] = genreStringId.split(", ");
        ArrayList<Genre> arrayListGenre = new ArrayList<>();
        for(int i = 0; i < name.length; ++i){
            arrayListGenre.add(new Genre(Integer.parseInt(id[i]), name[i]));
        }
        setGenres(arrayListGenre);
    }

    public void setRuntime(int runtime) { this.runtime = runtime; }

    public String getPosterPath() {
        return posterPath;
    }

    public Integer getId() { return id; }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Integer getRuntime() { return runtime; }

    public ArrayList<Genre> getGenres() { return genres; }

    public String getAllGenresName() {
        String allGenres = "";
        for(Genre g : genres){
            allGenres += g.name + ", ";
        }
        allGenres = allGenres.substring(0, allGenres.length() - 2); //to remove last extra ", " portion
        return allGenres;
    }

    public String getAllGenresId() {
        String allGenres = "";
        for(Genre g : genres){
            allGenres += g.id + ", ";
        }
        allGenres = allGenres.substring(0, allGenres.length() - 2); //to remove last extra ", " portion
        return allGenres;
    }

    public boolean getAdult() { return adult; }

    public String getAdultString() {
        return (adult ? "Yes" : "No");
    }

    public String getOverview() { return overview; }

    @Override
    public String toString() {
        return title;
    }
}
