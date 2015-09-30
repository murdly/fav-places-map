package com.example.arkadiuszkarbowy.maps.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkadiuszkarbowy on 30/09/15.
 */
public class PlacesDataSource {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_TITLE,
            SQLiteHelper.COLUMN_ADDRESS, SQLiteHelper.COLUMN_LATITUDE, SQLiteHelper.COLUMN_LONGITUDE};

    public PlacesDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Place createPlace(Place place) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_TITLE, place.getTitle());
        values.put(SQLiteHelper.COLUMN_ADDRESS, place.getAddress());
        values.put(SQLiteHelper.COLUMN_LATITUDE, place.getLatitude());
        values.put(SQLiteHelper.COLUMN_LONGITUDE, place.getLongitude());
        long insertId = database.insert(SQLiteHelper.TABLE_PLACES, null,
                values);
        Cursor cursor = database.query(SQLiteHelper.TABLE_PLACES,
                allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Place newPlace = cursorToPlace(cursor);
        cursor.close();
        return newPlace;
    }

    public void deletePlace(Place place) {
        long id = place.getId();
        System.out.println("Place deleted with id: " + id);
        database.delete(SQLiteHelper.TABLE_PLACES, SQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Place> getAllPlaces() {
        List<Place> places = new ArrayList<>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_PLACES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Place place = cursorToPlace(cursor);
            places.add(place);
            cursor.moveToNext();
        }
        cursor.close();
        return places;
    }

    private Place cursorToPlace(Cursor cursor) {
        Place place = new Place();
        place.setId(cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_ID)));
        place.setTitle(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_TITLE)));
        place.setAddress(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_ADDRESS)));
        place.setLatitude(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.COLUMN_LATITUDE)));
        place.setLongitude(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.COLUMN_LONGITUDE)));
        return place;
    }
}
