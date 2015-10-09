package com.example.arkadiuszkarbowy.maps.route;

import android.content.Context;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public interface RoutePresenter {
    void onResume(Context context);
    void insertNextLeg();
    void onCreateRouteClicked();
}
