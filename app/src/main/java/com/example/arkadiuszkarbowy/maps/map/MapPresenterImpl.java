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
import com.example.arkadiuszkarbowy.maps.route.Leg;
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
public class MapPresenterImpl implements MapPresenter {
    private static final String TAG = "MapController";
    public static final String FILTER_RADIUS = "filter_radius";

    private DatabaseManager mDataSource;
    private int mFilterRadius = FilterDialog.FILTER_RADIUS_DISABLED;
    private Polyline mCurrentRoute;
    private PolylineOptions mCurrentRouteOptions;
    private FragmentActivity mActivity;
    private MapView mView;
    private ArrayList<Leg> mRoute;

    public MapPresenterImpl(MapView view, FragmentActivity activity, DatabaseManager dataSource) {
        mView = view;
        mActivity = activity;
        mDataSource = dataSource;
    }

    @Override
    public void onResume() {
        mView.setUpMapIfNeeded();
        mView.invalidateMarkers();
    }

    @Override
    public boolean isRouteSet(){
        return mCurrentRouteOptions != null;
    }

    @Override
    public LatLng setUpRoute(ArrayList<Leg> route) {
        mView.hide();
        mRoute = route;
        ArrayList<LatLng> latlngs = getLatLngs(route);
        mCurrentRouteOptions = new PolylineOptions()
                .geodesic(true)
                .addAll(latlngs)
                .color(Color.BLUE);

        return latlngs.get(0);
    }

    private ArrayList<LatLng> getLatLngs(ArrayList<Leg> route) {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (Leg leg : route)
            latLngs.add(leg.getPlace().getLatLng());
        return latLngs;
    }

    @Override
    public void applyRoute() {
            mCurrentRoute = mView.addPath(mCurrentRouteOptions);
            for(Leg l : mRoute)
                mView.addSingleMarker(l.getPlace().getMarkerOptions());
    }

    @Override
    public void applyMarkersIfAny(boolean filter) {
        mDataSource.open();
        List<MyPlace> places = mDataSource.getAllMyPlaces();
        mDataSource.close();
        for (MyPlace p : places) {
            if (filter && outOfBounds(p.getLatLng())) continue;
            mView.addSingleMarker(p.getMarkerOptions());
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
        float lat = sharedPref.getFloat(MapsActivity.LATITUDE, (float) MapsActivity.EARTH_CENTER_LATITUDE);
        float lon = sharedPref.getFloat(MapsActivity.LONGITUDE, (float) MapsActivity.EARTH_CENTER_LONGITUDE);
        return new LatLng(lat, lon);
    }

    @Override
    public LatLng saveMarker(long id) {
        mDataSource.open();
        MyPlace place = mDataSource.getMyPlaceById(id);
        mDataSource.close();
        mView.addSingleMarker(place.getMarkerOptions());
        return place.getLatLng();
    }

    @Override
    public void closeRoutePresentation(){
        mView.show();
        cancelRoute();
        mView.invalidateMarkers();
    }

    private void cancelRoute(){
        if(mCurrentRoute != null) {
            mCurrentRoute.remove();
            mCurrentRouteOptions = null;
        }
    }

    @Override
    public int getFilterRadius() {
        return mFilterRadius;
    }

    @Override
    public void setFilterRadius(int filterRadius) {
        mFilterRadius = filterRadius;
    }


    @Override
    public MarkerTitleListener createMarkerTitleListener(){
        return new MarkerTitleListener();
    }

    @Override
    public FilterListener createFilterListener() {
        return new FilterListener();
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
                saveMarker(id);
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

                mView.invalidateMarkers();
            }
        };

        @Override
        public void onClick(View v) {
            FilterDialog.newInstance(mFilterListener).show(mActivity.getFragmentManager(), mActivity
                    .getString(R.string.filter_value));
        }
    }
}