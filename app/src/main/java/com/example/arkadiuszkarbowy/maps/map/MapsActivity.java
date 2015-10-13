package com.example.arkadiuszkarbowy.maps.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.DatabaseManager;
import com.example.arkadiuszkarbowy.maps.db.SQLiteHelper;
import com.example.arkadiuszkarbowy.maps.places.PlacesActivity;
import com.example.arkadiuszkarbowy.maps.route.RouteActivity;
import com.example.arkadiuszkarbowy.maps.route.Leg;
import com.example.arkadiuszkarbowy.maps.search.SearchActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity {
    private static final String TAG = "MapsActivity";
    public static final String LATITUDE = "com.ak.lat";
    public static final String LONGITUDE = "com.ak.lon";
    public static final double EARTH_CENTER_LATITUDE = 34.513299;
    public static final double EARTH_CENTER_LONGITUDE = -94.1628807;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Activity mActivity;

    private MapPresenter mMapPresenter;
    private Button mCloseRoute;

    private GoogleMap mMap;
    private LinearLayout mFab;
    private FloatingActionButton mSearch, mList, mPath;
    private FloatingActionMenu mMenu;
    private ImageButton mFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_maps);

        mFab = (LinearLayout) findViewById(R.id.fabs);
        mMenu = (FloatingActionMenu) findViewById(R.id.fabMenu);
        mSearch = (FloatingActionButton) findViewById(R.id.search);
        mList = (FloatingActionButton) findViewById(R.id.list);
        mPath = (FloatingActionButton) findViewById(R.id.path);
        mFilter = (ImageButton) findViewById(R.id.filter);
        mFilter.setOnClickListener(new FilterListener());

        DatabaseManager.initializeInstance(new SQLiteHelper(this));
        mMapPresenter = new MapPresenterImpl(this, DatabaseManager.getInstance());
        buildGoogleApiClient();
        mMapPresenter.setFabListeners(mOnSearchListener, mOnListListener, mOnRouteListener);

        if (savedInstanceState != null)
            mMapPresenter.setFilterRadius(savedInstanceState.getInt(MapPresenterImpl.FILTER_RADIUS));


        mCloseRoute = (Button) findViewById(R.id.close_route);
        mCloseRoute.setOnClickListener(mOnCloseRouteListener);
    }

    private View.OnClickListener mOnCloseRouteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMapPresenter.showViews();
            v.setVisibility(View.GONE);
            mMapPresenter.cancelRoute();
            mMapPresenter.invalidateMarkers();
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MapPresenterImpl.FILTER_RADIUS, mMapPresenter.getFilterRadius());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mMapPresenter.invalidateMarkers();
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
        mMap.setOnMapLongClickListener(new MarkerTitleListener());
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mConnectionFailedCallback)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            Log.d(TAG, "onConnected");
            obtainLastLocation();
            if (mLastLocation != null) {
                mMapPresenter.moveCamera(new LatLng(mLastLocation.getLatitude(),
                        mLastLocation.getLongitude()));
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "onConnected suspended");
            Toast.makeText(mActivity, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    };

    private void obtainLastLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(LATITUDE, (float) mLastLocation.getLatitude());
        editor.putFloat(LONGITUDE, (float) mLastLocation.getLongitude());
        editor.commit();
    }

    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedCallback = new GoogleApiClient
            .OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.d(TAG, "onConnected failed");
            Toast.makeText(mActivity, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    };

    private FloatingActionButton.OnClickListener mOnSearchListener = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMapPresenter.closeMenu();
            Intent intent = new Intent(MapsActivity.this, SearchActivity.class);
            startActivityForResult(intent, SearchActivity.REQUEST_SEARCH);
        }
    };

    private FloatingActionButton.OnClickListener mOnListListener = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMapPresenter.closeMenu();
            startActivityForResult(new Intent(MapsActivity.this, PlacesActivity.class), PlacesActivity.REQUEST_PLACES);
        }
    };

    private FloatingActionButton.OnClickListener mOnRouteListener = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(MapsActivity.this, RouteActivity.class), RouteActivity.REQUEST_ROUTE);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == SearchActivity.REQUEST_SEARCH) {
            if (resultCode == Activity.RESULT_OK) {
                long id = data.getLongExtra(SearchActivity.NEWLY_ADDED_ID, -1);
                mMapPresenter.addMarker(id);
            }
        } else if (requestCode == PlacesActivity.REQUEST_PLACES) {
            if (resultCode == PlacesActivity.RESULT_GOTO_MARKER) {
                if (data != null) {
                    double lat = data.getDoubleExtra(MapsActivity.LATITUDE, 0d);
                    double lng = data.getDoubleExtra(MapsActivity.LONGITUDE, 0d);
                    mMapPresenter.moveCamera(new LatLng(lat, lng));
                }
            }
        } else if (requestCode == RouteActivity.REQUEST_ROUTE) {
            if (resultCode == RouteActivity.RESULT_ROUTE_LEGS) {
                if (data != null) {
                    Log.d(TAG, "result route legs");
                    mCloseRoute.setVisibility(View.VISIBLE);
                    mMapPresenter.hideViews();
                    ArrayList<Leg> route = data.getParcelableArrayListExtra(RouteActivity.ROUTE_LEGS);
                    mMapPresenter.drawRoute(route);
                }
            }
        }
    }
}