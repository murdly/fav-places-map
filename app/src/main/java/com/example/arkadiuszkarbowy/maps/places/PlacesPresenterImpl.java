package com.example.arkadiuszkarbowy.maps.places;

import com.example.arkadiuszkarbowy.maps.db.DatabaseManager;
import com.example.arkadiuszkarbowy.maps.db.MyPlace;

import java.util.List;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public class PlacesPresenterImpl implements PlacesPresenter {

    private DatabaseManager mModel;
    private PlacesView mView;

    public PlacesPresenterImpl(PlacesView view) {
        mView = view;
        mModel = DatabaseManager.getInstance();
    }

    @Override
    public void deleteItem(long id) {
        mModel.open();
        mModel.deleteMyPlaceById(id);
        mModel.close();
    }

    @Override
    public void onResume() {
        mModel.open();
        List<MyPlace> places = mModel.getAllMyPlaces();
        mModel.close();
        mView.setItems(places);
    }
}