package com.forceawakened.www.filmikeeda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by forceawakened on 4/2/17.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "watchlist.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_MOVIES = "movie";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_MOVIE_ID = "movie_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_RELEASED = "released";
    private static final String COLUMN_RUNTIME = "runtime";
    private static final String COLUMN_GENRES = "genres";
    private static final String COLUMN_GENRES_ID = "genres_id";
    private static final String COLUMN_ADULT_CONTENT = "adult";
    private static final String COLUMN_OVERVIEW = "overview";
    private static final String COLUMN_POSTER_PATH = "poster_path";
    private static final String[] ALL_COLUMNS = {COLUMN_MOVIE_ID, COLUMN_NAME, COLUMN_RELEASED, COLUMN_RUNTIME,
            COLUMN_GENRES, COLUMN_GENRES_ID, COLUMN_ADULT_CONTENT, COLUMN_OVERVIEW, COLUMN_POSTER_PATH};

    private SQLiteDatabase database;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DBH", "inside oncreate");
        String dbCreate = "create table " + TABLE_MOVIES + '(' +
                COLUMN_ID + " integer primary key autoincrement, " +
                COLUMN_MOVIE_ID + " integer unique, " +
                COLUMN_NAME + " text not null, " +
                COLUMN_RELEASED + " text, " +
                COLUMN_RUNTIME + " integer, " +
                COLUMN_GENRES + " text, " +
                COLUMN_GENRES_ID + " text, " +
                COLUMN_ADULT_CONTENT + " text, " +
                COLUMN_OVERVIEW + " text, " +
                COLUMN_POSTER_PATH + " text)";
        db.execSQL(dbCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dbDelete = "drop table if exists " + TABLE_MOVIES +" ;";
        database.execSQL(dbDelete);
        onCreate(database);
    }

    public void open(){
        database = getWritableDatabase();
    }

    public void close(){
        database.close();
    }

    public void addMovie(mMovie movie) throws SQLiteConstraintException{
        ContentValues values = new ContentValues();
        values.put(COLUMN_MOVIE_ID, movie.getId());
        values.put(COLUMN_NAME, movie.getTitle());
        values.put(COLUMN_RELEASED, movie.getReleaseDate());
        values.put(COLUMN_RUNTIME, movie.getRuntime());
        values.put(COLUMN_GENRES, movie.getAllGenresName());
        values.put(COLUMN_GENRES_ID, movie.getAllGenresId());
        values.put(COLUMN_ADULT_CONTENT, movie.getAdultString());
        values.put(COLUMN_OVERVIEW, movie.getOverview());
        database = getReadableDatabase();
        database.insertOrThrow(TABLE_MOVIES, null, values);
        database.close();
    }
    public ArrayList<mMovie> getAllMovies(){
        database = getReadableDatabase();
        ArrayList<mMovie> arrayList = new ArrayList<>();

        Cursor cursor = database.query(TABLE_MOVIES, ALL_COLUMNS, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            arrayList.add(cursorToMovie(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return  arrayList;
    }

    private mMovie cursorToMovie(Cursor cursor){
        mMovie movie = new mMovie();
        movie.setId(cursor.getInt(0));
        movie.setTitle(cursor.getString(1));
        movie.setReleaseDate(cursor.getString(2));
        movie.setRuntime(cursor.getInt(3));
        movie.setGenres(cursor.getString(4), cursor.getString(5));
        if("Yes".equals(cursor.getString(6)))
            movie.setAdult(true);
        else
            movie.setAdult(false);
        movie.setOverview(cursor.getString(7));
        movie.setPosterPath(cursor.getString(8));
        return movie;
    }
}
