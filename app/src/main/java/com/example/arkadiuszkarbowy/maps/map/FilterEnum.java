package com.example.arkadiuszkarbowy.maps.map;

import android.content.Context;

import com.example.arkadiuszkarbowy.maps.R;

/**
 * Created by akarbowy on 03.01.2016.
 */
public enum FilterEnum {
    RADIUS_500m(0, 500, R.string.m500),
    RADIUS_1000m(1, 1000, R.string.m1000),
    RADIUS_2000m(2, 2000, R.string.m2000),
    RADIUS_5000m(3, 5000, R.string.m5000),
    RADIUS_DISABLED(4, -1, R.string.world);

    public int position;
    public int value;
    private int stringId;

    FilterEnum(int position, int value, int stringId) {
        this.position = position;
        this.value = value;
        this.stringId = stringId;
    }

    public static FilterEnum get(int which) {
        switch (which) {
            case 0:
                return RADIUS_500m;
            case 1:
                return RADIUS_1000m;
            case 2:
                return RADIUS_2000m;
            case 3:
                return RADIUS_5000m;
            default:
                return RADIUS_DISABLED;
        }
    }

    public static CharSequence[] getTitlesArray(Context context) {
        return new CharSequence[]{
                context.getString(RADIUS_500m.stringId),
                context.getString(RADIUS_1000m.stringId),
                context.getString(RADIUS_2000m.stringId),
                context.getString(RADIUS_5000m.stringId),
                context.getString(RADIUS_DISABLED.stringId)};

    }
}
