package com.example.arkadiuszkarbowy.maps;

import android.app.Application;

/**
 * Created by akarbowy on 03.01.2016.
 */
public class FavMapApplication extends Application {

    private FavMapPreferences mPreferences;
    private static FavMapApplication mInstance = null;

    public static FavMapApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mPreferences = new FavMapPreferences(this);
    }

    public FavMapPreferences getPreferences() {
        return mPreferences;
    }
}
