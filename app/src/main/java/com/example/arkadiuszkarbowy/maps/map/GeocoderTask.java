package com.example.arkadiuszkarbowy.maps.map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by arkadiuszkarbowy on 30/09/15.
 */
public class GeocoderTask extends AsyncTask<LatLng, Void, List<Address>> {
    private static final String TAG = "GeocoderTask";
    private Context mContext;


    public GeocoderTask(Context context) {
        mContext = context;
    }

    @Override
    protected List<Address> doInBackground(LatLng... params) {
        List<Address> results;
        try {
            Locale locale = mContext.getResources().getConfiguration().locale;
            Geocoder geocoder = new Geocoder(mContext, locale);
            results = geocoder.getFromLocation(params[0].latitude, params[0].longitude, 1);

            if (results.size() == 0) {
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
        return results;
    }
}