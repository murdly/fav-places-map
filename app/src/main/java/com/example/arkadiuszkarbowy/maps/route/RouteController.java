package com.example.arkadiuszkarbowy.maps.route;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.DatabaseManager;
import com.example.arkadiuszkarbowy.maps.db.MyPlace;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkadiuszkarbowy on 29/09/15.
 */
public class RouteController {
    private Activity mContext;
    private DatabaseManager mDataSource;
    private RecyclerView mView;
    private RouteAdapter mAdapter;
    private List<MyPlace> mPlaces;
    private RouteList mRouteLegs;

    public RouteController(Activity context, DatabaseManager mDataSource, View mView) {
        mContext = context;
        this.mDataSource = mDataSource;
        this.mView = (RecyclerView) mView;
    }

    public void setUpView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mView.setLayoutManager(mLayoutManager);
        mView.addItemDecoration(new SpaceItemDecoration(24));


        mRouteLegs = new RouteList(mContext);
        mAdapter = new RouteAdapter(mContext, mRouteLegs, new RouteLegListener());
        mView.setAdapter(mAdapter);


        mDataSource.open();
        mPlaces = mDataSource.getAllMyPlaces();
        mDataSource.close();

    }

    public void msg() {
        Toast.makeText(mContext, mContext.getResources().getString(R.string.validation), Toast
                .LENGTH_SHORT).show();
    }

    public boolean validate() {
        return mRouteLegs.isSet();

    }

    public void addLeg() {
        if (mRouteLegs.getLegsCount() <= mPlaces.size())
            mRouteLegs.addNextLeg();
        else
            Toast.makeText(mContext, mContext.getResources().getString(R.string.lack_of_places), Toast
                    .LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
    }

    public ArrayList<RouteLeg> getRoute() {
        return mRouteLegs.getLegs();
    }


    private class RouteLegListener implements RouteAdapter.OnLegListener {
        @Override
        public void onLegChoose(int pos) {
            AvailablePlacesDialog.newInstance(mPlaces, new LegListener(pos)).show(mContext.getFragmentManager(),
                    mContext.getString(R.string.add_leg));
        }
    }

    public class LegListener implements AvailablePlacesDialog.LegListener {

        private int mLegNumber;

        public LegListener(int mLegNumber) {
            this.mLegNumber = mLegNumber;
        }

        @Override
        public void onLegChosen(int p) {
            mRouteLegs.getLegs().get(mLegNumber).setPlace(mPlaces.get(p));
            mAdapter.notifyDataSetChanged();
        }
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            if (parent.getChildAdapterPosition(view) == 0)
                outRect.top = space;
        }
    }
}