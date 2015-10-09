package com.example.arkadiuszkarbowy.maps.places;

import com.example.arkadiuszkarbowy.maps.db.MyPlace;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public interface PlacesPresenter {
    void deleteItem(long id);
    void onResume();
}
