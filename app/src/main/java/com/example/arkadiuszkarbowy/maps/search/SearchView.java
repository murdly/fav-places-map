package com.example.arkadiuszkarbowy.maps.search;

import com.google.android.gms.location.places.AutocompletePrediction;

import java.util.List;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public interface SearchView {
    void initAdapterIfNecessary();
    void updateResults(List<AutocompletePrediction> results);
    void enableSearch(boolean enabled);
    void hideResultsContainer();
    void showResultsContainer();
    void finishAndGoToChosenPlace(long id);
    void showToast(String msg);
}
