package com.example.arkadiuszkarbowy.maps.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkadiuszkarbowy on 02/10/15.
 */
public class DatabaseManager {

    private int mOpenCounter;

    private static DatabaseManager instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    private String[] allColumns = {SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_API_ID, SQLiteHelper
            .COLUMN_TITLE,
            SQLiteHelper.COLUMN_ADDRESS, SQLiteHelper.COLUMN_LATITUDE, SQLiteHelper.COLUMN_LONGITUDE};


    public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
        if (instance == null) {
            instance = new DatabaseManager();
            mDatabaseHelper = helper;
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    public synchronized SQLiteDatabase open() {
        mOpenCounter++;
        if (mOpenCounter == 1) {
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void close() {
        mOpenCounter--;
        if (mOpenCounter == 0) {
            mDatabase.close();
        }
    }

    public long createMyPlaceFrom(Place place) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_API_ID, place.getId());
        values.put(SQLiteHelper.COLUMN_TITLE, place.getName().toString());
        values.put(SQLiteHelper.COLUMN_ADDRESS, place.getAddress().toString());
        values.put(SQLiteHelper.COLUMN_LATITUDE, place.getLatLng().latitude);
        values.put(SQLiteHelper.COLUMN_LONGITUDE, place.getLatLng().longitude);
        long insertId = mDatabase.insert(SQLiteHelper.TABLE_PLACES, null,
                values);
        mDatabase.query(SQLiteHelper.TABLE_PLACES,
                allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        return insertId;
    }

    public List<MyPlace> getAllMyPlaces() {
        List<MyPlace> places = new ArrayList<>();

        Cursor cursor = mDatabase.query(SQLiteHelper.TABLE_PLACES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MyPlace place = cursorToPlace(cursor);
            places.add(place);
            cursor.moveToNext();
        }
        cursor.close();
        return places;
    }

    public MyPlace getMyPlaceById(long id) {
        System.out.println("Got MyPlace with id: " + id);
        Cursor cursor = mDatabase.query(SQLiteHelper.TABLE_PLACES, allColumns, SQLiteHelper.COLUMN_ID
                + " = " + id, null, null, null, null, null);
        cursor.moveToFirst();
        MyPlace place = cursorToPlace(cursor);
        cursor.close();
        return place;
    }

    private MyPlace cursorToPlace(Cursor cursor) {
        MyPlace place = new MyPlace();
        place.setId(cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_ID)));
        place.setApiId(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_API_ID)));
        place.setTitle(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_TITLE)));
        place.setAddress(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_ADDRESS)));
        place.setLatitude(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.COLUMN_LATITUDE)));
        place.setLongitude(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.COLUMN_LONGITUDE)));
        return place;
    }

    public void deleteMyPlaceById(long id) {
        System.out.println("MyPlace deleted with id: " + id);
        mDatabase.delete(SQLiteHelper.TABLE_PLACES, SQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }
}