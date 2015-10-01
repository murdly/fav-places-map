package com.example.arkadiuszkarbowy.maps.search;

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
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by arkadiuszkarbowy on 30/09/15.
 */
public class GeocoderTask extends AsyncTask<String, Void, List<Address>> {
    private static final String TAG = "GeocoderTask";
    private Context mContext;


    public GeocoderTask(Context context) {
        mContext = context;
    }

    @Override
    protected List<Address> doInBackground(String... params) {
        List<Address> results;
        try {
            Geocoder geocoder = new Geocoder(mContext, Locale.ENGLISH);
            results = geocoder.getFromLocationName(params[0], 1);

            if (results.size() == 0) {
                return null;
            }

//            address = results.get(0);
//            GeoPoint p = new GeoPoint((int) (address.getLatitude() * 1E6), (int) (address.getLongitude() * 1E6))

        } catch (Exception e) {
            Log.e("", "Something went wrong: ", e);
            return null;
        }
        return results;
    }
}
