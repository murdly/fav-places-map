package com.example.arkadiuszkarbowy.maps.search;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public interface SearchPresenter {
    void setContext(Context context);
    void setGoogleApiClient(GoogleApiClient client);
    void setSearchBounds();
    void onSearchQuery(String query);
    void onResultItemClicked(AutocompletePrediction position);
}
