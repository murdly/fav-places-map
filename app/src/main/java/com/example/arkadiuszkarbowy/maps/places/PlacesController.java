package com.example.arkadiuszkarbowy.maps.places;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.arkadiuszkarbowy.maps.db.DatabaseManager;
import com.example.arkadiuszkarbowy.maps.db.MyPlace;

import java.util.List;

/**
 * Created by arkadiuszkarbowy on 29/09/15.
 */
public class PlacesController {
    private Context mContext;
    private DatabaseManager mDataSource;
    private RecyclerView mView;
    private PlacesActivityFragment mListener;
    private PlacesAdapter mAdapter;
    private List<MyPlace> mPlaces;

    public PlacesController(Context context, DatabaseManager mDataSource, View mView) {
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

        mDataSource.open();
        mPlaces = mDataSource.getAllMyPlaces();
        mDataSource.close();
        mAdapter = new PlacesAdapter(mContext, mPlaces, mListener);
        mView.setAdapter(mAdapter);
    }

    public void deleteItem(long id) {
        mDataSource.open();
        mDataSource.deleteMyPlaceById(id);
        mDataSource.close();
    }

    public MyPlace getPlace(int pos) {
        return mPlaces.get(pos);
    }
}