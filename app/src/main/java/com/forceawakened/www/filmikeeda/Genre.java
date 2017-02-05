package com.forceawakened.www.filmikeeda;

/**
 * Created by forceawakened on 3/2/17.
 */
public class Genre {
    int id;
    String name;

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
