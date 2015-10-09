package com.example.arkadiuszkarbowy.maps.search;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public interface QueryFinder {
    void find(String query, OnFinishedListener listener, LatLng position);
}
