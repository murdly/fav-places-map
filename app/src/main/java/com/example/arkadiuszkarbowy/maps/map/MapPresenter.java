package com.example.arkadiuszkarbowy.maps.map;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public interface MapPresenter {
    void setFilterRadius(int radius);
    int getFilterRadius();
    void onResume();
}
