package com.example.arkadiuszkarbowy.maps.route;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.arkadiuszkarbowy.maps.db.MyPlace;

/**
 * Created by arkadiuszkarbowy on 07/10/15.
 */
public class Leg implements Parcelable {
    private String mPrefix;
    private MyPlace mPlace;

    public Leg(String prefix) {
        mPrefix = prefix;
    }

    public String getPrefix() {
        return mPrefix;
    }

    public String getTitle() {
        return mPlace != null ? mPlace.getTitle(): "";
    }

    public void setPlace(MyPlace place) {
        this.mPlace = place;
    }

    public boolean hasPlace(){
        return mPlace != null;
    }

    public MyPlace getPlace() {
        return mPlace;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPrefix);
        dest.writeParcelable(this.mPlace, flags);
    }

    protected Leg(Parcel in) {
        this.mPrefix = in.readString();
        this.mPlace = in.readParcelable(MyPlace.class.getClassLoader());
    }

    public static final Parcelable.Creator<Leg> CREATOR = new Parcelable.Creator<Leg>() {
        public Leg createFromParcel(Parcel source) {
            return new Leg(source);
        }

        public Leg[] newArray(int size) {
            return new Leg[size];
        }
    };
}
