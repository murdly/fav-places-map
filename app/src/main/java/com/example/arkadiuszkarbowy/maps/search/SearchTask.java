package com.example.arkadiuszkarbowy.maps.search;

import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by arkadiuszkarbowy on 30/09/15.
 */
public class SearchTask extends AsyncTask<String, Void, List<AutocompletePrediction>> {
    private LatLngBounds mBounds;
    private GoogleApiClient mGoogleApiClient;


    public SearchTask(GoogleApiClient googleApiClient, LatLngBounds bounds) {
        mGoogleApiClient = googleApiClient;
        mBounds = bounds;
    }

    @Override
    protected List<AutocompletePrediction> doInBackground(String... query) {
        return getAutocomplete(query[0]);
    }

    private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence query) {
        if (mGoogleApiClient.isConnected()) {
            PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi
                    .getAutocompletePredictions(mGoogleApiClient, query.toString(),
                            mBounds, null);

            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(60, TimeUnit.SECONDS);

            final com.google.android.gms.common.api.Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                autocompletePredictions.release();
                return null;
            }

            return DataBufferUtils.freezeAndClose(autocompletePredictions);
        }
        return null;
    }
}