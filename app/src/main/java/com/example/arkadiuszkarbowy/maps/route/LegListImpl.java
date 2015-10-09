package com.example.arkadiuszkarbowy.maps.route;

import android.content.Context;

import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.MyPlace;

import java.util.ArrayList;

/**
 * Created by arkadiuszkarbowy on 09/10/15.
 */
public class LegListImpl implements LegList {
    private ArrayList<Leg> mLegs;
    private String mThrough;
    private Context mContext;

    public LegListImpl(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        String from = mContext.getResources().getString(R.string.from);
        mThrough = mContext.getResources().getString(R.string.through);
        String to = mContext.getResources().getString(R.string.to);
        mLegs = new ArrayList<>();
        mLegs.add(new Leg(from));
        mLegs.add(new Leg(to));
    }

    @Override
    public void insertLeg() {
        int beforeDestination = getLegsCount() - 1;
        mLegs.add(beforeDestination, new Leg(mThrough));
    }

    @Override
    public boolean isSet() {
        for (Leg leg : mLegs)
            if (!leg.hasPlace()) return false;

        for (int i = 0; i < getLegsCount() - 1; i++) {
            Leg leg = mLegs.get(i);
            if (!isUnique(i + 1, leg)) return false;
        }
        return true;

    }

    @Override
    public void assign(MyPlace myPlace, int mLegNumber) {
        mLegs.get(mLegNumber).setPlace(myPlace);
    }

    private boolean isUnique(int start, Leg leg) {
        for (int i = start; i < getLegsCount(); i++) {
            Leg leg2 = mLegs.get(i);
            if (leg.getPlace().equals(leg2.getPlace())) return false;
        }
        return true;
    }

    @Override
    public ArrayList<Leg> getLegs() {
        return mLegs;
    }

    public int getLegsCount() {
        return mLegs.size();
    }
}