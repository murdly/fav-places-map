package com.example.arkadiuszkarbowy.maps.map;

import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.view.View;

import com.example.arkadiuszkarbowy.maps.FavMapApplication;
import com.example.arkadiuszkarbowy.maps.db.DatabaseManager;
import com.example.arkadiuszkarbowy.maps.db.MyPlace;
import com.example.arkadiuszkarbowy.maps.route.Leg;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkadiuszkarbowy on 02/10/15.
 */
public class MapPresenterImpl implements MapPresenter {
    private static final String TAG = "MapController";
    public static final String FILTER_RADIUS = "filter_radius";

    private DatabaseManager mDataSource;
    private FilterEnum mFilterRadius;
    private Polyline mCurrentRoute;
    private PolylineOptions mCurrentRouteOptions;
    private MapView mView;
    private ArrayList<Leg> mRoute;

    public MapPresenterImpl(MapView view, DatabaseManager dataSource) {
        mView = view;
        mDataSource = dataSource;
    }

    @Override
    public void onResume() {
        mView.setUpMapIfNeeded();
        mView.invalidateMarkers();
    }

    @Override
    public boolean isRouteSet() {
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
        for (Leg l : mRoute)
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
                include(SphericalUtil.computeOffset(center, mFilterRadius.value, 0)).
                include(SphericalUtil.computeOffset(center, mFilterRadius.value, 90)).
                include(SphericalUtil.computeOffset(center, mFilterRadius.value, 180)).
                include(SphericalUtil.computeOffset(center, mFilterRadius.value, 270)).build();

        return !circle.contains(point);
    }

    private LatLng getLastObtainedLocation() {
        float lat = FavMapApplication.getInstance().getPreferences().getLastLat();
        float lon = FavMapApplication.getInstance().getPreferences().getLastLon();
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
    public void closeRoutePresentation() {
        mView.show();
        cancelRoute();
        mView.invalidateMarkers();
    }

    private void cancelRoute() {
        if (mCurrentRoute != null) {
            mCurrentRoute.remove();
            mCurrentRouteOptions = null;
        }
    }

    @Override
    public FilterEnum getFilterRadius() {
        return mFilterRadius;
    }

    @Override
    public void setFilterRadius(int filterRadius) {
        mFilterRadius = FilterEnum.get(filterRadius);
    }

    @Override
    public FilterListener createFilterListener() {
        return new FilterListener();
    }

    @Override
    public void createPlace(Address address, String title) {
        mDataSource.open();
        long id = mDataSource.createMyPlaceFrom(address, title);
        mDataSource.close();
        saveMarker(id);
    }

    public class FilterListener implements View.OnClickListener {
        private DialogInterface.OnClickListener mFilterListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mFilterRadius = FilterEnum.get(which);
                mView.invalidateMarkers();
            }
        };

        @Override
        public void onClick(View v) {
            mView.showFilterDialog(mFilterListener);
        }
    }
}