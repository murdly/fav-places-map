package com.example.arkadiuszkarbowy.maps.places;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arkadiuszkarbowy.maps.db.DatabaseManager;
import com.example.arkadiuszkarbowy.maps.map.MapsActivity;
import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.MyPlace;
import com.example.arkadiuszkarbowy.maps.search.SearchActivity;

public class PlacesActivityFragment extends Fragment implements PlacesAdapter.OnRecyclerInteractionListener {

    private PlacesController mController;

    public PlacesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        mController = new PlacesController(getActivity(), DatabaseManager.getInstance(),
                view.findViewById(R.id.places));
        mController.setOnViewClickListener(this);
        mController.setUpView();
        return view;
    }


    @Override
    public void onItemClicked(int pos) {
        Log.d("PlacesActivityFragment", pos + " clicked");
        Intent markerData = new Intent();
        MyPlace place = mController.getPlace(pos);
        markerData.putExtra(MapsActivity.LATITUDE, place.getLatitude());
        markerData.putExtra(MapsActivity.LONGITUDE, place.getLongitude());
        getActivity().setResult(PlacesActivity.RESULT_GOTO_MARKER, markerData);
        getActivity().finish();
    }

    @Override
    public void onItemDeleteClicked(long id) {
        mController.deleteItem(id);
    }

    @Override
    public void onEmptyStateClicked() {
        startActivityForResult(new Intent(getActivity(), SearchActivity.class), SearchActivity.REQUEST_SEARCH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SearchActivity.REQUEST_SEARCH)
            if (resultCode == Activity.RESULT_OK)
                mController.setUpView();
    }
}