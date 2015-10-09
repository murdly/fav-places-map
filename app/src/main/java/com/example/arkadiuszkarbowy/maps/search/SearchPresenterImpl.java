package com.example.arkadiuszkarbowy.maps.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.DatabaseManager;
import com.example.arkadiuszkarbowy.maps.map.MapsActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public class SearchPresenterImpl implements SearchPresenter, OnFinishedListener, GoogleApiClient
        .OnConnectionFailedListener {
    private Context mContext;
    private SearchView mView;
    private QueryFinder mQueryFinder;
    private LatLngBounds mBounds;

    private GoogleApiClient mGoogleApiClient;

    public SearchPresenterImpl(SearchView view) {
        mView = view;
    }

    @Override
    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public void setGoogleApiClient(GoogleApiClient client) {
        mGoogleApiClient = client;
    }

    @Override
    public void setSearchBounds() {
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer placeLikelihoods) {
                LatLng position = placeLikelihoods.get(0).getPlace().getLatLng();
                placeLikelihoods.release();

                mBounds = LatLngBounds.builder().include(position).build();
                mView.enableSearch(true);
            }
        });
    }

    @Override
    public void onSearchQuery(String query) {
        if (mQueryFinder == null)
            mQueryFinder = new QueryFinderImpl(mGoogleApiClient, mBounds);

        mView.hideResultsContainer();
        if (!isNetworkConnected() && !mGoogleApiClient.isConnected())
            mView.showToast(mContext.getResources().getString(R.string.no_connection));
        else
            mQueryFinder.find(query, this);
    }

    @Override
    public void onResultItemClicked(AutocompletePrediction item) {
        final String placeId = item.getPlaceId();

        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                .getPlaceById(mGoogleApiClient, placeId);
        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer apiPlaces) {
            if (!apiPlaces.getStatus().isSuccess()) {
                mView.showToast(mContext.getResources().getString(R.string.wrong_query));
                apiPlaces.release();
                return;
            }

            DatabaseManager db = DatabaseManager.getInstance();
            db.open();
            long id = db.createMyPlaceFrom(apiPlaces.get(0));
            db.close();
            apiPlaces.release();

            mView.finishAndGoToChosenPlace(id);
        }
    };

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni == null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mView.showToast(mContext.getResources().getString(R.string.no_connection));
    }


    @Override
    public void onFinished(List<AutocompletePrediction> results) {
        mView.showResultsContainer();
        mView.initAdapterIfNecessary();
        mView.updateResults(results);
    }
}