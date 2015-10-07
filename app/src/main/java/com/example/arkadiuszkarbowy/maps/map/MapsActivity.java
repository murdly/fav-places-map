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
import android.widget.Toast;

import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.DatabaseManager;
import com.example.arkadiuszkarbowy.maps.db.SQLiteHelper;
import com.example.arkadiuszkarbowy.maps.places.PlacesActivity;
import com.example.arkadiuszkarbowy.maps.search.SearchActivity;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class MapsActivity extends FragmentActivity {
    private static final String TAG = "MapsActivity";
    public static final String LATITUDE = "com.ak.lat";
    public static final String LONGITUDE = "com.ak.lon";

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Activity mActivity;

    private MapController mMapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_maps);
        DatabaseManager.initializeInstance(new SQLiteHelper(this));
        mMapController = new MapController(this, DatabaseManager.getInstance());
        buildGoogleApiClient();
        mMapController.initViews();
        mMapController.setFabListeners(mOnSearchListener, mOnListListener, mOnPathListener);

        if(savedInstanceState != null)
             mMapController.setFilterRadius(savedInstanceState.getInt(MapController.FILTER_RADIUS));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MapController.FILTER_RADIUS, mMapController.getFilterRadius());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapController.invalidateMarkers();
        mMapController.setUpMapIfNeeded();
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
                mMapController.moveCamera(new LatLng(mLastLocation.getLatitude(),
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
            mMapController.closeMenu();
            Intent intent = new Intent(MapsActivity.this, SearchActivity.class);
            startActivityForResult(intent, SearchActivity.REQUEST_SEARCH);
        }
    };

    private FloatingActionButton.OnClickListener mOnListListener = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMapController.closeMenu();
            startActivityForResult(new Intent(MapsActivity.this, PlacesActivity.class), PlacesActivity.REQUEST_PLACES);
        }
    };

    private FloatingActionButton.OnClickListener mOnPathListener = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SearchActivity.REQUEST_SEARCH) {
            if (resultCode == Activity.RESULT_OK) {
                long id = data.getLongExtra(SearchActivity.NEWLY_ADDED_ID, -1);
                mMapController.addMarker(id);
            }
        } else if (requestCode == PlacesActivity.REQUEST_PLACES) {
            if (resultCode == PlacesActivity.RESULT_GOTO_MARKER)
                if (data != null) {
                    double lat = data.getDoubleExtra(MapsActivity.LATITUDE, 0d);
                    double lng = data.getDoubleExtra(MapsActivity.LONGITUDE, 0d);
                    mMapController.moveCamera(new LatLng(lat,lng));
                }

        }
    }
}