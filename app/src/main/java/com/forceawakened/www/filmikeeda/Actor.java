package com.forceawakened.www.filmikeeda;

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

    public String getPosterPath() {return ("".equals(posterPath) || posterPath == null ? "N/A" : posterPath);}

    public String getNickNames() {return ("".equals(nicknames) || nicknames == null ? "N/A" : nicknames);}

    public String getBirthDate() {return ("".equals(birthdate) || birthdate == null ? "N/A" : birthdate);}

    public String getDeathDate() {return ("".equals(deathdate) || deathdate == null ? "N/A" : deathdate);}

    public String getBiography() {return ("".equals(biography) || biography == null ? "N/A" : biography);}

    public String getGender() {
        return (gender == 1 ? "Female" : (gender == 2 ? "Male" : "N/A"));
    }
}
