package com.example.arkadiuszkarbowy.maps.search;

import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public class QueryFinderImpl implements QueryFinder {
    private GoogleApiClient mGoogleApiClient;

    public QueryFinderImpl(GoogleApiClient client) {
        mGoogleApiClient = client;
    }

    @Override
    public void find(String query, OnFinishedListener listener, LatLng position) {
        try {
            SearchTask task = new SearchTask(mGoogleApiClient);
            task.setBounds(buildBounds(position));
            AsyncTask<String, Void, List<AutocompletePrediction>> result = task.execute(query);

            if (result.get() != null)
                listener.onFinished(result.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private LatLngBounds buildBounds(LatLng position){
        return LatLngBounds.builder().include(position).build();
    }
}