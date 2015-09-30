package com.example.arkadiuszkarbowy.maps.places;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.PlacesDataSource;

public class PlacesActivityFragment extends Fragment implements PlacesAdapter.ViewHolder
        .OnRecyclerItemClickListener, PlacesAdapter.EmptyViewHolder.OnRecyclerEmptyStateClickListener {

    private PlacesController mController;

    public PlacesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        mController = new PlacesController(getActivity(), new PlacesDataSource(getActivity()),
                view.findViewById(R.id.places));
        mController.setOnViewClickListener(this);
        mController.setUpView();
        return view;
    }


    @Override
    public void onItemClicked(View view, int pos) {
        Log.d("PlacesActivityFragment", pos + " clicked");
    }

    @Override
    public void onAddPlaceClicked() {
        Log.d("PlacesActivityFragment",   "empty state clicked");
    }
}
