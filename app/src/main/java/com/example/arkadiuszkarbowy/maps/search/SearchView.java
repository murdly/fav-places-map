package com.example.arkadiuszkarbowy.maps.search;

import com.google.android.gms.location.places.AutocompletePrediction;

import java.util.List;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public interface SearchView {
    void showToast(String msg);
    void initAdapterIfNecessary();
    void updateResults(List<AutocompletePrediction> results);
    void hideResultsContainer();
    void showResultsContainer();
    void finishAndGoToChosenPlace(long id);
}
