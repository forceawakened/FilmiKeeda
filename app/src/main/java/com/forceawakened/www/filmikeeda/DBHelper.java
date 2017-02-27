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

    private static final String TABLE_MOVIES = "movies";
    private static final String COLUMN_ID = "_id";
    private static final String MOVIE_ID = "movieId";
    private static final String MOVIE_TITLE = "movieName";
    private static final String[] ALL_COLUMNS = {MOVIE_ID, MOVIE_TITLE};
    private SQLiteDatabase database;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Log.d("DBH", "inside oncreate");
        String dbCreate = "CREATE TABLE " + TABLE_MOVIES + '(' +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MOVIE_ID + " INTEGER UNIQUE, " +
                MOVIE_TITLE + " TEXT NOT NULL)";
        db.execSQL(dbCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dbDelete = "DROP TABLE IF EXISTS " + TABLE_MOVIES +" ;";
        database.execSQL(dbDelete);
        onCreate(database);
    }

    public void close(){
        database.close();
    }

    public void addMovie(mMovie movie) throws SQLiteConstraintException{
        ContentValues values = new ContentValues();
        values.put(MOVIE_ID, movie.getId());
        values.put(MOVIE_TITLE, movie.getTitle());
        database = getWritableDatabase();
        database.insertOrThrow(TABLE_MOVIES, null, values);
        database.close();
    }
    
    public void deleteMovie(mMovie movie){
        database = getWritableDatabase();
        database.delete(TABLE_MOVIES, MOVIE_ID + "=?", new String[]{String.valueOf(movie.getId())});
        database.close();
    }

    public boolean checkMovie(mMovie movie){
        boolean result;
        database = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_MOVIES + " WHERE " + MOVIE_ID + " = " + movie.getId();
        Cursor cursor = database.rawQuery(query, null);
        if(!cursor.isAfterLast()){
            result = true;
        }
        else{
            result = false;
        }
        cursor.close();
        database.close();
        return result;
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
        database.close();
        return arrayList;
    }

    private mMovie cursorToMovie(Cursor cursor){
        mMovie movie = new mMovie();
        movie.setId(cursor.getInt(0));
        movie.setTitle(cursor.getString(1));
        return movie;
    }
}
