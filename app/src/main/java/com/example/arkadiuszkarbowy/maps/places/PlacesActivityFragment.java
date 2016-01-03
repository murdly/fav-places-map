package com.example.arkadiuszkarbowy.maps.places;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arkadiuszkarbowy.maps.FavMapPreferences;
import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.MyPlace;
import com.example.arkadiuszkarbowy.maps.search.SearchActivity;

import java.util.List;

public class PlacesActivityFragment extends Fragment implements PlacesView,
        PlacesAdapter.OnRecyclerInteraction {
    private RecyclerView mRecycler;
    private PlacesPresenter mPresenter;

    public PlacesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        mRecycler = (RecyclerView) view.findViewById(R.id.places);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mLayoutManager);

        mPresenter = new PlacesPresenterImpl(this);
        return view;
    }

    @Override
    public void setItems(List<MyPlace> places) {
        //todo set adapter only once
        mRecycler.setAdapter(new PlacesAdapter(places, this));
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void moveToPlaceMarker(int position) {
        MyPlace place = ((PlacesAdapter) mRecycler.getAdapter()).getItemAt(position);
        Intent data = new Intent();
        data.putExtra(FavMapPreferences.LATITUDE, place.getLatitude());
        data.putExtra(FavMapPreferences.LONGITUDE, place.getLongitude());
        getActivity().setResult(PlacesActivity.RESULT_GOTO_MARKER, data);
        getActivity().finish();
    }

    @Override
    public void onItemDeleteClicked(long id) {
        mPresenter.deleteItem(id);
    }

    @Override
    public void onEmptyStateClicked() {
        startActivity(new Intent(getActivity(), SearchActivity.class));
    }
}