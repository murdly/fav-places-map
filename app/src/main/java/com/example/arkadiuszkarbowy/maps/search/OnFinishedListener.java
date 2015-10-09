package com.example.arkadiuszkarbowy.maps.search;

import com.google.android.gms.location.places.AutocompletePrediction;

import java.util.List;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public interface OnFinishedListener {
    void onFinished(List<AutocompletePrediction> autocompletePredictions);
}
