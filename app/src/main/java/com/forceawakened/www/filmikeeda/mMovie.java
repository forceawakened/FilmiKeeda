package com.forceawakened.www.filmikeeda;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by forceawakened on 2/2/17.
 */
public class mMovie {
    private Integer id;
    private String title, releaseDate, overview, posterPath;
    private ArrayList<Genre> genres;
    private Integer runtime;
    private boolean adult;

    public mMovie setId(Integer id) {
        this.id = id;
        return this;
    }

    public mMovie setTitle(String title) {
        this.title = title;
        return this;
    }

    public mMovie setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public mMovie setAdult(boolean adult) {
        this.adult = adult;
        return this;
    }

    public mMovie setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }

    public mMovie setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public mMovie setGenres(ArrayList<Genre> genres) {
        this.genres = genres;
        return this;
    }

    public mMovie setGenres(String genreStringName, String genreStringId){
        String name[] = genreStringName.split(", ");
        String id[] = genreStringId.split(", ");
        ArrayList<Genre> arrayListGenre = new ArrayList<>();
        for(int i = 0; i < name.length; ++i){
            arrayListGenre.add(new Genre(Integer.parseInt(id[i]), name[i]));
        }
        setGenres(arrayListGenre);
        return this;
    }

    public mMovie setRuntime(Integer runtime) {
        this.runtime = runtime;
        return this;
    }

    public String getPosterPath() {return ("".equals(posterPath) || posterPath == null ? "N/A" : posterPath);}

    public Integer getId() {return id;}

    public String getTitle() {return title;}

    public String getReleaseDate() {return ("".equals(releaseDate) || releaseDate == null ? "N/A" : releaseDate);}

    public Integer getRuntime() {return (runtime == null ? 0 : runtime);}

    public String getAllGenresName() {
        String allGenres = "";
        for(Genre g : genres) {
            allGenres += g.name + ", ";
        }
        allGenres = allGenres.substring(0, allGenres.length() - 2); //to remove last extra ", " portion
        Log.d("mMovie", allGenres);
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

    public String getAdultString() {
        return (adult ? "Yes" : "No");
    }

    public String getOverview() { return ("".equals(overview) || overview == null ? "N/A" : overview); }

    @Override
    public String toString() {
        return title;
    }
}
