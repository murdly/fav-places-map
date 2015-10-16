package com.example.arkadiuszkarbowy.maps.map;

import com.example.arkadiuszkarbowy.maps.route.Leg;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public interface MapPresenter {
    void onResume();
    boolean isRouteSet();
    LatLng setUpRoute(ArrayList<Leg> route);
    void applyRoute();
    void applyMarkersIfAny(boolean filter);
    LatLng saveMarker(long id);
    void closeRoutePresentation();
    void setFilterRadius(int radius);
    int getFilterRadius();
    MapPresenterImpl.MarkerTitleListener createMarkerTitleListener();
    MapPresenterImpl.FilterListener createFilterListener();
}
