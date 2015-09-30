package com.example.arkadiuszkarbowy.maps.places;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.arkadiuszkarbowy.maps.db.Place;
import com.example.arkadiuszkarbowy.maps.db.PlacesDataSource;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by arkadiuszkarbowy on 29/09/15.
 */
public class PlacesController {
    private Context mContext;
    private PlacesDataSource mDataSource;
    private RecyclerView mView;
    private PlacesActivityFragment mListener;

    public PlacesController(Context context, PlacesDataSource mDataSource, View mView) {
        mContext = context;
        this.mDataSource = mDataSource;
        this.mView = (RecyclerView) mView;


    }

    public void setOnViewClickListener(PlacesActivityFragment listener) {
        mListener = listener;
    }

    public void setUpView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mView.setLayoutManager(mLayoutManager);

        try {
            mDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Place> places = mDataSource.getAllPlaces();
        mDataSource.close();
        places.add(new Place(2, "a", "b" , 3, 3));
        mView.setAdapter(new PlacesAdapter(mContext, places, mListener, mListener));
        Log.d("PlacesController", "adapter is set");

    }
}
