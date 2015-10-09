package com.example.arkadiuszkarbowy.maps.places;

import android.content.Intent;

import com.example.arkadiuszkarbowy.maps.db.MyPlace;

import java.util.List;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public interface PlacesView {

    void setItems(List<MyPlace> places);
}
