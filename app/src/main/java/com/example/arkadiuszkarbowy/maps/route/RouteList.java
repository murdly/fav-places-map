package com.example.arkadiuszkarbowy.maps.route;

import android.content.Context;

import com.example.arkadiuszkarbowy.maps.R;

import java.util.ArrayList;

/**
 * Created by arkadiuszkarbowy on 07/10/15.
 */
public class RouteList {
    private ArrayList<RouteLeg> mLegs;
    private Context mContext;
    String mThrough;

    public RouteList(Context context) {
        mContext = context;
        this.mLegs = new ArrayList<>();
        init();
    }

    private void init() {
        String from = mContext.getResources().getString(R.string.from);
        mThrough = mContext.getResources().getString(R.string.through);
        String to = mContext.getResources().getString(R.string.to);
        mLegs.add(new RouteLeg(from));
        mLegs.add(new RouteLeg(to));
    }

    public ArrayList<RouteLeg> getLegs() {
        return mLegs;
    }

    public int getLegsCount() {
        return mLegs.size();
    }

    public void addNextLeg() {
        int beforeDestination = getLegsCount() - 1;
        mLegs.add(beforeDestination, new RouteLeg(mThrough));
    }

    public boolean isSet() {
        for (RouteLeg leg : mLegs)
            if (!leg.hasPlace()) return false;

        for (int i = 0; i < getLegsCount() - 1; i++) {
            RouteLeg leg = mLegs.get(i);
            if (!isUnique(i+1, leg)) return false;
        }
        return true;
    }

    private boolean isUnique(int start, RouteLeg leg) {
        for (int i = start; i < getLegsCount(); i++) {
            RouteLeg leg2 = mLegs.get(i);
            if (leg.getPlace().equals(leg2.getPlace())) return false;
        }
        return true;
    }
}