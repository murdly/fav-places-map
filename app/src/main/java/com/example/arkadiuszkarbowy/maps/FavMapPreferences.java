package com.example.arkadiuszkarbowy.maps;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by akarbowy on 03.01.2016.
 */
public class FavMapPreferences {
    public static final String LATITUDE = "com.ak.lat";
    public static final String LONGITUDE = "com.ak.lon";
    private static final double EARTH_CENTER_LATITUDE = 34.513299;
    private static final double EARTH_CENTER_LONGITUDE = -94.1628807;

    private SharedPreferences prefs;

    public FavMapPreferences(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public float getLastLat() {
        return prefs.getFloat(LATITUDE, (float) EARTH_CENTER_LATITUDE);
    }

    public float getLastLon() {
        return prefs.getFloat(LONGITUDE, (float) EARTH_CENTER_LONGITUDE);

    }

    public void putLat(double latitude) {
        prefs.edit().putFloat(LATITUDE, (float) latitude).apply();
    }

    public void putLon(double longitude) {
        prefs.edit().putFloat(LONGITUDE, (float) longitude).apply();
    }
}
