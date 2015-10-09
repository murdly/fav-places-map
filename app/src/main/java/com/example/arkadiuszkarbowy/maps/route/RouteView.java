package com.example.arkadiuszkarbowy.maps.route;

import com.example.arkadiuszkarbowy.maps.db.MyPlace;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public interface RouteView {
    void setRouteList(ArrayList<Leg> route, RouteAdapter.OnLegListener legListener);
    void showToast(String msg);
    void finishWithRoute(ArrayList<Leg> route);
    void onLegsStateChanged();
    void setAvailableDialogPlaces(List<MyPlace> places, RoutePresenterImpl.LegListener legListener);
}
