package com.example.arkadiuszkarbowy.maps.search;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public interface QueryFinder {
    void find(String query, OnFinishedListener listener);
}
