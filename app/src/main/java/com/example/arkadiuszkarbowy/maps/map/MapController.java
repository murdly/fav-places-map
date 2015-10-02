package com.example.arkadiuszkarbowy.maps.map;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;

import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.DatabaseManager;
import com.example.arkadiuszkarbowy.maps.db.MyPlace;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.util.List;

/**
 * Created by arkadiuszkarbowy on 02/10/15.
 */
public class MapController {
    private static final int FILTER_RADIUS = 1000;
    private DatabaseManager mDataSource;
    private GoogleMap mMap;
    private FloatingActionButton mSearch, mList, mPath;
    private FloatingActionMenu mMenu;
    private ImageButton mFilter;
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
        mFilter = (ImageButton) mActivity.findViewById(R.id.filter);
        mFilter.setOnClickListener(mOnFilterListener);

        setUpMapIfNeeded();
    }

    private View.OnClickListener mOnFilterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mFilter.getTag().equals(mActivity.getResources().getString(R.string.off))) {
                mFilter.setTag(mActivity.getResources().getString(R.string.on));
                mFilter.setImageResource(R.mipmap.ic_filter_off);
                addMarkersIfAny(true);
            } else {
                mFilter.setTag(mActivity.getResources().getString(R.string.off));
                mFilter.setImageResource(R.mipmap.ic_filter_on);
                addMarkersIfAny(false);
            }
        }
    };

    public void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) mActivity.getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) setUpMap();
        }
    }

    private void setUpMap() {
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setMyLocationEnabled(true);
    }

    public void addMarkersIfAny(boolean filter) {
        mMap.clear();
        mDataSource.open();
        List<MyPlace> places = mDataSource.getAllMyPlaces();
        mDataSource.close();
        for (MyPlace p : places) {
            if (filter && outOfBounds(p.getLatLng())) continue;
            mMap.addMarker(p.getMarkerOptions());
        }
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

    private boolean outOfBounds(LatLng point) {
        LatLng center = new LatLng(51.107885, 17.038538);
        LatLngBounds circle = new LatLngBounds.Builder().
                include(SphericalUtil.computeOffset(center, FILTER_RADIUS, 0)).
                include(SphericalUtil.computeOffset(center, FILTER_RADIUS, 90)).
                include(SphericalUtil.computeOffset(center, FILTER_RADIUS, 180)).
                include(SphericalUtil.computeOffset(center, FILTER_RADIUS, 270)).build();

        return !circle.contains(point);
    }
}