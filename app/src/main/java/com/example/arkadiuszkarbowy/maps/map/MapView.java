package com.example.arkadiuszkarbowy.maps.map;

import android.content.DialogInterface;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public interface MapView {
    void setUpMapIfNeeded();
    void invalidateMarkers();
    void addSingleMarker(MarkerOptions options);
    Polyline addPath(PolylineOptions options);
    void show();
    void hide();
    void showFilterDialog(DialogInterface.OnClickListener mFilterListener);
}
