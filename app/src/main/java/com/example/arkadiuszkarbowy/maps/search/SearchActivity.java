package com.example.arkadiuszkarbowy.maps.search;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.arkadiuszkarbowy.maps.db.DatabaseManager;
import com.example.arkadiuszkarbowy.maps.map.MapsActivity;
import com.example.arkadiuszkarbowy.maps.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by arkadiuszkarbowy on 01/10/15.
 */
public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    public static final int REQUEST_SEARCH = 1;
    public static final String NEWLY_ADDED_ID = "newly_added_id";
    private Activity mActivity;
    private List<AutocompletePrediction> mResultsList;
    private GoogleApiClient mGoogleApiClient;
    private AutocompleteAdapter mAdapter;
    private LatLngBounds mBounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mActivity = this;

        buildGoogleApiClient();
        getCurrentLocationBounds();
        setUpSearchBox();
        setUpSearchResults();
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, mConnectionFailedCallback)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
    }

    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedCallback = new GoogleApiClient
            .OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.d(TAG, "API connection failed");
            Toast.makeText(mActivity, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT)
                    .show();
        }
    };

    private void getCurrentLocationBounds() {
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                mBounds = buildBounds(likelyPlaces.get(0).getPlace().getLatLng());
                likelyPlaces.release();
            }

            private LatLngBounds buildBounds(LatLng latLng) {
                return LatLngBounds.builder().include(latLng).build();
            }
        });
    }

    private void setUpSearchBox() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        EditText mSearchBox = (EditText) findViewById(R.id.searchBox);
        mSearchBox.addTextChangedListener(mTextWatcher);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence query, int start, int before, int count) {
            mAdapter.clear();
            mResultsList.clear();
            if (!query.toString().isEmpty()) {
                search(query.toString());
            }
        }

        private void search(String query) {
            try {
                if (!mGoogleApiClient.isConnected()) {
                    Log.d(TAG, "API is not connected");
                } else {
                    SearchTask search = new SearchTask(mGoogleApiClient);
                    search.setBounds(mBounds);
                    AsyncTask<String, Void, List<AutocompletePrediction>> result = search.execute(query);

                    if (result.get() != null)
                        mResultsList.addAll(result.get());

                    mAdapter.notifyDataSetChanged();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Toast.makeText(mActivity, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT)
                        .show();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void setUpSearchResults() {
        ListView mResults = (ListView) findViewById(R.id.results);
        mResults.setOnItemClickListener(mResultClickListener);

        mResultsList = new ArrayList<>();
        mAdapter = new AutocompleteAdapter(mActivity, mResultsList);
        mResults.setAdapter(mAdapter);
    }

    private ListView.OnItemClickListener mResultClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place apiPlace = places.get(0);

            DatabaseManager db = DatabaseManager.getInstance();
            db.open();
            long id = db.createMyPlaceFrom(apiPlace);
            db.close();

            Intent markerData = new Intent();
            markerData.putExtra(NEWLY_ADDED_ID, id);
            places.release();


            setResult(RESULT_OK, markerData);
            finish();
        }
    };
}