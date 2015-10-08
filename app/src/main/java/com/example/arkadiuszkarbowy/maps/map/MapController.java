package com.example.arkadiuszkarbowy.maps.map;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.DatabaseManager;
import com.example.arkadiuszkarbowy.maps.db.MyPlace;
import com.example.arkadiuszkarbowy.maps.route.RouteLeg;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by arkadiuszkarbowy on 02/10/15.
 */
public class MapController {
    private static final String TAG = "MapController";

    public static final String FILTER_RADIUS = "filter_radius";
    private static final double EARTH_CENTER_LATITUDE = 34.513299;
    private static final double EARTH_CENTER_LONGITUDE = -94.1628807;

    private DatabaseManager mDataSource;
    private GoogleMap mMap;
    private LinearLayout mFab;
    private FloatingActionButton mSearch, mList, mPath;
    private FloatingActionMenu mMenu;
    private ImageButton mFilter;
    private int mFilterRadius = FilterDialog.FILTER_RADIUS_DISABLED;
    private Polyline mCurrentRoute;
    private PolylineOptions mCurrentRouteOptions;
    private FragmentActivity mActivity;
    private ArrayList<RouteLeg> mRoute;

    public MapController(FragmentActivity activity, DatabaseManager dataSource) {
        mActivity = activity;
        mDataSource = dataSource;
    }

    public void initViews() {
        mFab = (LinearLayout) mActivity.findViewById(R.id.fabs);
        mMenu = (FloatingActionMenu) mActivity.findViewById(R.id.fabMenu);
        mSearch = (FloatingActionButton) mActivity.findViewById(R.id.search);
        mList = (FloatingActionButton) mActivity.findViewById(R.id.list);
        mPath = (FloatingActionButton) mActivity.findViewById(R.id.path);
        mFilter = (ImageButton) mActivity.findViewById(R.id.filter);
        mFilter.setOnClickListener(new FilterListener());

        setUpMapIfNeeded();
    }

    public void showViews() {
        mFab.setVisibility(View.VISIBLE);
        mFilter.setVisibility(View.VISIBLE);
        mMap.setMyLocationEnabled(true);
    }

    public void hideViews() {
        mFab.setVisibility(View.INVISIBLE);
        mFilter.setVisibility(View.INVISIBLE);
        mMap.setMyLocationEnabled(false);
    }

    public void setUpMapIfNeeded() {
        Log.d(TAG, "setUpMapIfNeeded");
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
        mMap.setOnMapLongClickListener(new MarkerTitleListener());
    }

    public void invalidateMarkers() {
        Log.d(TAG, "invalidateMarkers");
        boolean filter = mFilterRadius != FilterDialog.FILTER_RADIUS_DISABLED;
        if (filter)
            mFilter.setImageResource(R.mipmap.ic_filter_on);
        else
            mFilter.setImageResource(R.mipmap.ic_filter_outline);


        mMap.clear();
        if(!addRouteIfAny())
            addMarkersIfAny(filter);
    }

    private boolean addRouteIfAny() {
        if(mCurrentRouteOptions != null) {

            mCurrentRoute = mMap.addPolyline(mCurrentRouteOptions);
            for(RouteLeg l : mRoute)
                mMap.addMarker(l.getPlace().getMarkerOptions());
            return true;
        }

        return false;
    }

    private void addMarkersIfAny(boolean filter) {
        Log.d(TAG, "addMarkersIfAny");
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

    public void drawRoute(ArrayList<RouteLeg> route) {
        mRoute = route;
        ArrayList<LatLng> latlngs = getLatLngs(route);
        mCurrentRouteOptions = new PolylineOptions()
                .geodesic(true)
                .addAll(latlngs)
                .color(Color.BLUE);

        moveCamera(latlngs.get(0));
    }

    public void cancelRoute(){
        if(mCurrentRoute != null) {
            mCurrentRoute.remove();
            mCurrentRouteOptions = null;
        }
    }

    private ArrayList<LatLng> getLatLngs(ArrayList<RouteLeg> route) {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (RouteLeg leg : route)
            latLngs.add(leg.getPlace().getLatLng());
        return latLngs;
    }




    public class MarkerTitleListener implements GoogleMap.OnMapLongClickListener, MarkerTitleDialog.TitleListener {
        private LatLng latLng;

        @Override
        public void onMapLongClick(LatLng latLng) {
            this.latLng = latLng;
            MarkerTitleDialog.newInstance(this).show(mActivity.getFragmentManager(),
                    mActivity.getResources().getString(R.string.set_name));
        }

        @Override
        public void onResult(String title) {
            try {
                List<Address> result = new GeocoderTask(mActivity).execute(latLng).get();
                mDataSource.open();
                long id = mDataSource.createMyPlaceFrom(result.get(0), title);
                mDataSource.close();
                addMarker(id);
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG, e.toString());
                Toast.makeText(mActivity, mActivity.getResources().getString(R.string.smh_wrong), Toast
                        .LENGTH_SHORT).show();
            }
        }
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
                    mFilterRadius = FilterDialog.FILTER_RADIUS_DISABLED;

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