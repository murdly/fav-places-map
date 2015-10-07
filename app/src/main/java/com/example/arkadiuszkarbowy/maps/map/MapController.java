package com.example.arkadiuszkarbowy.maps.map;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
    public static final String FILTER_RADIUS = "filter_radius";
    private static final double EARTH_CENTER_LATITUDE = 34.513299;
    private static final double EARTH_CENTER_LONGITUDE = -94.1628807;

    private DatabaseManager mDataSource;
    private GoogleMap mMap;
    private FloatingActionButton mSearch, mList, mPath;
    private FloatingActionMenu mMenu;
    private ImageButton mFilter;
    private int mFilterRadius = FilterDialog.FILTER_RADIUS_DEFAULT;
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
        mFilter.setOnClickListener(new FilterListener());

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
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setMyLocationEnabled(true);
    }

    public void invalidateMarkers() {
        if (mFilterRadius != -1) {
            mFilter.setImageResource(R.mipmap.ic_filter_on);
            addMarkersIfAny(true);
        } else {
            mFilter.setImageResource(R.mipmap.ic_filter_outline);
            addMarkersIfAny(false);
        }
    }

    private void addMarkersIfAny(boolean filter) {
        mMap.clear();
        mDataSource.open();
        List<MyPlace> places = mDataSource.getAllMyPlaces();
        mDataSource.close();
        for (MyPlace p : places) {
            if (filter && outOfBounds(p.getLatLng())) continue;
            mMap.addMarker(p.getMarkerOptions());
        }
    }

    private boolean outOfBounds(LatLng point) {
        LatLng center = getLastObtainedLocation();
        LatLngBounds circle = new LatLngBounds.Builder().
                include(SphericalUtil.computeOffset(center, mFilterRadius, 0)).
                include(SphericalUtil.computeOffset(center, mFilterRadius, 90)).
                include(SphericalUtil.computeOffset(center, mFilterRadius, 180)).
                include(SphericalUtil.computeOffset(center, mFilterRadius, 270)).build();

        return !circle.contains(point);
    }

    private LatLng getLastObtainedLocation() {
        SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
        float lat = sharedPref.getFloat(MapsActivity.LATITUDE, (float) EARTH_CENTER_LATITUDE);
        float lon = sharedPref.getFloat(MapsActivity.LONGITUDE, (float) EARTH_CENTER_LONGITUDE);
        return new LatLng(lat, lon);
    }

    public void closeMenu() {
        mMenu.close(true);
    }

    public void setFabListeners(FloatingActionButton.OnClickListener mOnSearchListener, FloatingActionButton
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

    public int getFilterRadius() {
        return mFilterRadius;
    }

    public void setFilterRadius(int filterRadius) {
        mFilterRadius = filterRadius;
    }

    public class FilterListener implements View.OnClickListener {
        private DialogInterface.OnClickListener mFilterListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] values = mActivity.getResources().getStringArray(R.array.filter_values);
                String filter = values[which];
                if (filter.equals(mActivity.getString(R.string.m500)))
                    mFilterRadius = FilterDialog.FILTER_RADIUS_500m;
                else if (filter.equals(mActivity.getString(R.string.m1000)))
                    mFilterRadius = FilterDialog.FILTER_RADIUS_1000m;
                else if (filter.equals(mActivity.getString(R.string.m2000)))
                    mFilterRadius = FilterDialog.FILTER_RADIUS_2000m;
                else if (filter.equals(mActivity.getString(R.string.m5000)))
                    mFilterRadius = FilterDialog.FILTER_RADIUS_5000m;
                else if (filter.equals(mActivity.getString(R.string.world)))
                    mFilterRadius = FilterDialog.FILTER_RADIUS_DEFAULT;

                invalidateMarkers();
            }
        };

        @Override
        public void onClick(View v) {
            FilterDialog.newInstance(mFilterListener).show(mActivity.getFragmentManager(), mActivity
                    .getString(R.string.filter_value));
        }
    }
}