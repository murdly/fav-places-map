package com.example.arkadiuszkarbowy.maps.route;

import android.content.Context;

import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.DatabaseManager;
import com.example.arkadiuszkarbowy.maps.db.MyPlace;

import java.util.List;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public class RoutePresenterImpl implements RoutePresenter {
    private Context mContext;
    private DatabaseManager mModel;
    private RouteView mView;
    private LegListImpl mLegList;
    private List<MyPlace> mPlaces;

    public RoutePresenterImpl(RouteView view) {
        mModel = DatabaseManager.getInstance();
        mView = view;

        mModel.open();
        mPlaces = mModel.getAllMyPlaces();
        mModel.close();
    }

    @Override
    public void onResume(Context context) {
        mContext = context;
        mLegList = new LegListImpl(mContext);
        mView.setRouteList(mLegList.getLegs(), mOnLegListener);
    }

    @Override
    public void insertNextLeg() {
        boolean possible = mLegList.getLegsCount() < mPlaces.size();
        if (!possible) mView.showToast(mContext.getResources().getString(R.string.lack_of_places));
        else {
            mLegList.insertLeg();
            mView.onLegsStateChanged();
        }
    }

    @Override
    public void onCreateRouteClicked() {
        if (mLegList.isSet())
            mView.finishWithRoute(mLegList.getLegs());
        else
            mView.showToast(mContext.getResources().getString(R.string.validation));
    }

    private RouteAdapter.OnLegListener mOnLegListener = new RouteAdapter.OnLegListener() {
        @Override
        public void onLegChoose(int pos) {
            mView.setAvailableDialogPlaces(mPlaces, new LegListener(pos));
        }
    };

    public class LegListener implements AvailablePlacesDialog.LegListener {

        private int mLegNumber;

        public LegListener(int mLegNumber) {
            this.mLegNumber = mLegNumber;
        }

        @Override
        public void onLegChosen(int p) {
            mLegList.assign(mPlaces.get(p), mLegNumber);
            mView.onLegsStateChanged();
        }
    }
}