package com.example.arkadiuszkarbowy.maps.map;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.arkadiuszkarbowy.maps.FavMapApplication;
import com.example.arkadiuszkarbowy.maps.FavMapPreferences;
import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.DatabaseManager;
import com.example.arkadiuszkarbowy.maps.db.SQLiteHelper;
import com.example.arkadiuszkarbowy.maps.places.PlacesActivity;
import com.example.arkadiuszkarbowy.maps.route.Leg;
import com.example.arkadiuszkarbowy.maps.route.RouteActivity;
import com.example.arkadiuszkarbowy.maps.search.SearchActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements MapView {
    private static final String TAG = "MapsActivity";

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

        DatabaseManager.initializeInstance(new SQLiteHelper(this));
        mMapPresenter = new MapPresenterImpl(this, DatabaseManager.getInstance());

        buildGoogleApiClient();
        mFilter.setOnClickListener(mMapPresenter.createFilterListener());
        setFabListeners(mOnSearchListener, mOnListListener, mOnRouteListener);

        if (savedInstanceState != null)
            mMapPresenter.setFilterRadius(savedInstanceState.getInt(MapPresenterImpl.FILTER_RADIUS));


        mCloseRoute = (Button) findViewById(R.id.close_route);
        mCloseRoute.setOnClickListener(mOnCloseRouteListener);
    }

    private View.OnClickListener mOnCloseRouteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMapPresenter.closeRoutePresentation();
            v.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mMapPresenter.onResume();
    }

    @Override
    public void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
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

    @Override
    public void invalidateMarkers() {
        boolean filter = mMapPresenter.getFilterRadius() != FilterEnum.RADIUS_DISABLED;
        if (filter)
            mFilter.setImageResource(R.mipmap.ic_filter_on);
        else
            mFilter.setImageResource(R.mipmap.ic_filter_outline);


        mMap.clear();
        if (mMapPresenter.isRouteSet())
            mMapPresenter.applyRoute();
        else
            mMapPresenter.applyMarkersIfAny(filter);
    }

    @Override
    public void addSingleMarker(MarkerOptions options) {
        mMap.addMarker(options);
    }

    @Override
    public Polyline addPath(PolylineOptions options) {
        return mMap.addPolyline(options);
    }

    @Override
    public void show() {
        mFab.setVisibility(View.VISIBLE);
        mFilter.setVisibility(View.VISIBLE);
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void hide() {
        mFab.setVisibility(View.INVISIBLE);
        mFilter.setVisibility(View.INVISIBLE);
        mMap.setMyLocationEnabled(false);
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
            obtainLastLocation();
            if (mLastLocation != null) {
                moveCamera(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Toast.makeText(mActivity, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    };

    private void obtainLastLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            FavMapApplication.getInstance().getPreferences().putLat(mLastLocation.getLatitude());
            FavMapApplication.getInstance().getPreferences().putLon(mLastLocation.getLongitude());
        }
    }

    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedCallback = new GoogleApiClient
            .OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.d(TAG, "onConnected failed");
            Toast.makeText(mActivity, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    };

    private void setFabListeners(FloatingActionButton.OnClickListener mOnSearchListener, FloatingActionButton
            .OnClickListener mOnListListener, FloatingActionButton.OnClickListener mOnPathListener) {

        mSearch.setOnClickListener(mOnSearchListener);
        mList.setOnClickListener(mOnListListener);
        mPath.setOnClickListener(mOnPathListener);
    }

    private FloatingActionButton.OnClickListener mOnSearchListener = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            closeMenu();
            Intent intent = new Intent(MapsActivity.this, SearchActivity.class);
            startActivityForResult(intent, SearchActivity.REQUEST_SEARCH);
        }
    };

    private FloatingActionButton.OnClickListener mOnListListener = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            closeMenu();
            startActivityForResult(new Intent(MapsActivity.this, PlacesActivity.class), PlacesActivity.REQUEST_PLACES);
        }
    };

    private FloatingActionButton.OnClickListener mOnRouteListener = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(MapsActivity.this, RouteActivity.class), RouteActivity.REQUEST_ROUTE);
        }
    };

    private void closeMenu() {
        mMenu.close(true);
    }

    public void showMarkerDialog(MarkerTitleListener markerTitleListener) {
        MarkerTitleDialog.newInstance(markerTitleListener).show(getFragmentManager(),
                getResources().getString(R.string.set_name));
    }

    public class MarkerTitleListener implements GoogleMap.OnMapLongClickListener, MarkerTitleDialog.TitleListener {
        private LatLng latLng;

        @Override
        public void onMapLongClick(LatLng latLng) {
            this.latLng = latLng;
            showMarkerDialog(this);
        }

        @Override
        public void onResult(String title) {
            try {
                List<Address> result = new GeocoderTask(mActivity).execute(latLng).get();
                mMapPresenter.createPlace(result.get(0), title);
            } catch (InterruptedException | ExecutionException e) {
                Toast.makeText(mActivity, mActivity.getResources().getString(R.string.smh_wrong), Toast
                        .LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void showFilterDialog(DialogInterface.OnClickListener mFilterListener) {
        FilterDialog.newInstance(mFilterListener).show(getFragmentManager(), getString(R.string.filter_value));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SearchActivity.REQUEST_SEARCH) {
            if (resultCode == Activity.RESULT_OK) {
                saveMarker(data);
            }
        } else if (requestCode == PlacesActivity.REQUEST_PLACES) {
            if (resultCode == PlacesActivity.RESULT_GOTO_MARKER) {
                goToMarker(data);
            }
        } else if (requestCode == RouteActivity.REQUEST_ROUTE) {
            if (resultCode == RouteActivity.RESULT_ROUTE_LEGS) {
                showRoute(data);
            }
        }
    }

    private void saveMarker(Intent data) {
        if (data != null) {
            long id = data.getLongExtra(SearchActivity.NEWLY_ADDED_ID, -1);
            LatLng position = mMapPresenter.saveMarker(id);
            moveCamera(position);
        }
    }

    private void goToMarker(Intent data) {
        if (data != null) {
            double lat = data.getDoubleExtra(FavMapPreferences.LATITUDE, 0d);
            double lng = data.getDoubleExtra(FavMapPreferences.LONGITUDE, 0d);
            moveCamera(new LatLng(lat, lng));
        }
    }

    private void showRoute(Intent data) {
        if (data != null) {
            mCloseRoute.setVisibility(View.VISIBLE);
            ArrayList<Leg> route = data.getParcelableArrayListExtra(RouteActivity.ROUTE_LEGS);
            LatLng position = mMapPresenter.setUpRoute(route);
            moveCamera(position);
        }
    }

    private void moveCamera(LatLng latlng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MapPresenterImpl.FILTER_RADIUS, mMapPresenter.getFilterRadius().position);
        super.onSaveInstanceState(outState);
    }
}