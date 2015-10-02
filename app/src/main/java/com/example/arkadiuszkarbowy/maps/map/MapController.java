package com.example.arkadiuszkarbowy.maps.map;

import android.support.v4.app.FragmentActivity;

import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.DatabaseManager;
import com.example.arkadiuszkarbowy.maps.db.MyPlace;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;

/**
 * Created by arkadiuszkarbowy on 02/10/15.
 */
public class MapController {

    private DatabaseManager mDataSource;
    private GoogleMap mMap;
    private FloatingActionButton mSearch, mList, mPath;
    private FloatingActionMenu mMenu;
    private FragmentActivity mActivity;

    public MapController(FragmentActivity activity, DatabaseManager dataSource) {
        mActivity = activity;
        mDataSource = dataSource;
    }

    public void initViews() {
        mMenu = (FloatingActionMenu) mActivity.findViewById(R.id.fabMenu);
        mSearch = (FloatingActionButton) mActivity.findViewById(R.id.search);
        mList = (FloatingActionButton) mActivity.findViewById(R.id.list);
        mPath = (FloatingActionButton) mActivity.findViewById(R.id.path);

        setUpMapIfNeeded();
    }

    public void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) mActivity.getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) setUpMap();
        }
    }

    private void setUpMap() {
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMyLocationEnabled(true);
    }

    public void addMarkersIfAny(){
        mMap.clear();
        mDataSource.open();
        List<MyPlace> places = mDataSource.getAllMyPlaces();
        mDataSource.close();
        for(MyPlace p : places)
            mMap.addMarker(p.getMarkerOptions());
    }

    public void closeMenu() {
        mMenu.close(true);
    }

    public void setListeners(FloatingActionButton.OnClickListener mOnSearchListener, FloatingActionButton
            .OnClickListener mOnListListener, FloatingActionButton.OnClickListener mOnPathListener) {

        mSearch.setOnClickListener(mOnSearchListener);
        mList.setOnClickListener(mOnListListener);
        mPath.setOnClickListener(mOnPathListener);
    }

    public void moveCamera(LatLng latlng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f));
    }

    public void addMarker(long id) {
        mDataSource.open();
        MyPlace place = mDataSource.getMyPlaceById(id);
        mDataSource.close();
        mMap.addMarker(place.getMarkerOptions());
        moveCamera(place.getLatLng());

    }
}