package com.example.arkadiuszkarbowy.maps.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by arkadiuszkarbowy on 30/09/15.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_PLACES = "places";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_API_ID = "apiId";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    private static final String DATABASE_NAME = "places.db";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_PLACES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_API_ID
            + " text not null, " + COLUMN_TITLE
            + " text not null, " + COLUMN_ADDRESS + " text not null, " + COLUMN_LATITUDE + " real, "
            + COLUMN_LONGITUDE + " real" + ");";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(SQLiteHelper.class.getName(), "Upgrading db from " + oldVersion + " to "
                + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        onCreate(db);
    }
}
