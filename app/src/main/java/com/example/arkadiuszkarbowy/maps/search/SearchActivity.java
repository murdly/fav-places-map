package com.example.arkadiuszkarbowy.maps.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.map.MapsActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkadiuszkarbowy on 01/10/15.
 */
public class SearchActivity extends AppCompatActivity implements SearchView, ListView.OnItemClickListener {
    public static final int REQUEST_SEARCH = 1;
    public static final String NEWLY_ADDED_ID = "newly_added_id";

    private ListView mResultsContainer;
    private List<AutocompletePrediction> mResultsList;
    private SearchPresenterImpl mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setUpSearchBox();

        mResultsContainer = (ListView) findViewById(R.id.results);
        mResultsContainer.setOnItemClickListener(this);

        mPresenter = new SearchPresenterImpl(this);
        mPresenter.setContext(this);
        mPresenter.setGoogleApiClient(buildClient());

    }

    private synchronized GoogleApiClient buildClient() {
        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, mPresenter)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        client.connect();
        return client;
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
            if (!query.toString().isEmpty())
                mPresenter.onSearchQuery(query.toString(), getLastObtainedLocation());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private LatLng getLastObtainedLocation() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        float lat = sharedPref.getFloat(MapsActivity.LATITUDE, (float) MapsActivity.EARTH_CENTER_LATITUDE);
        float lon = sharedPref.getFloat(MapsActivity.LONGITUDE, (float) MapsActivity.EARTH_CENTER_LONGITUDE);
        return new LatLng(lat, lon);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void initAdapterIfNecessary() {
        if (mResultsList == null) {
            mResultsList = new ArrayList<>();
            mResultsContainer.setAdapter(new AutocompleteAdapter(this, mResultsList));
        }
    }

    @Override
    public void updateResults(List<AutocompletePrediction> results) {
        mResultsList.clear();
        mResultsList.addAll(results);
        ((AutocompleteAdapter) mResultsContainer.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void hideResultsContainer() {
        mResultsContainer.setVisibility(View.GONE);
    }

    @Override
    public void showResultsContainer() {
        mResultsContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishAndGoToChosenPlace(long id) {
        Intent markerData = new Intent();
        markerData.putExtra(NEWLY_ADDED_ID, id);
        setResult(RESULT_OK, markerData);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AutocompletePrediction item = (AutocompletePrediction) parent.getItemAtPosition(position);
        mPresenter.onResultItemClicked(item);
    }
}