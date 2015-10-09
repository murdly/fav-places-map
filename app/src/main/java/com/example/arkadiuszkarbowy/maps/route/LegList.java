package com.example.arkadiuszkarbowy.maps.route;

import com.example.arkadiuszkarbowy.maps.db.MyPlace;

import java.util.ArrayList;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public interface LegList {
    void insertLeg();

    boolean isSet();

    void assign(MyPlace myPlace, int mLegNumber);

    ArrayList<Leg> getLegs();
}
