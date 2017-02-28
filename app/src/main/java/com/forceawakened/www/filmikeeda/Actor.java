package com.forceawakened.www.filmikeeda;

import android.util.Log;

/**
 * Created by forceawakened on 25/2/17.
 */
public class Actor {

    //variables
    private Integer id, gender;
    private String name, posterPath;
    private String nicknames, biography, deathdate, birthdate;

    //setters
    public Actor setId(Integer id){
        this.id = id;
        return this;
    }

    public Actor setName(String name){
        this.name = name;
        return this;
    }

    public Actor setPosterPath(String posterPath){
        this.posterPath = posterPath;
        return this;
    }

    public Actor setGender(Integer gender){
        this.gender = gender;
        return this;
    }

    public Actor setNicknames(String nicknames){
        this.nicknames = nicknames;
        return this;
    }

    public Actor setBirthdate(String birthdate){
        this.birthdate = birthdate;
        return this;
    }

    public Actor setDeathdate(String deathdate){
        this.deathdate = deathdate;
        return this;
    }

    public Actor setBiography(String biography) {
        this.biography = biography;
        return this;
    }

    //getters
    public Integer getId() {return id;}

    public String getName() {return name;}

    public String getPosterPath() {return (posterPath == null || "".equals(posterPath) ? "N/A" : posterPath);}

    public String getNickNames() {return (nicknames == null || "".equals(nicknames) ? "N/A" : nicknames);}

    public String getBirthDate() {return (("null".equals(birthdate) || "".equals(birthdate)) ? "N/A" : birthdate);}

    public String getDeathDate() {return ("null".equals(deathdate) || "".equals(deathdate) ? "N/A" : deathdate);}

    public String getBiography() {return ("null".equals(biography) || "".equals(biography) ? "N/A" : biography);}

    public String getGender() {
        return (gender == null ? "N/A" : (gender == 2 ? "Male" : "Female"));
    }
}
