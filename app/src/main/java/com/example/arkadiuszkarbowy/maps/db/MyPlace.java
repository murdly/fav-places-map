package com.example.arkadiuszkarbowy.maps.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by arkadiuszkarbowy on 29/09/15.
 */
public class MyPlace implements Parcelable {
    private long id;
    private String apiId;
    private String title;
    private String address;
    private double latitude;
    private double longitude;

    public MyPlace() {
    }

    public String getApiId() {
        return apiId;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLocationString() {
        return "(" + latitude + ":" + longitude + ")";
    }

    public MarkerOptions getMarkerOptions() {
        return new MarkerOptions().position(new LatLng(latitude, longitude)).title(title);
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAddress(String address) {
        this.address = cutTitleFrom(address);
    }

    private String cutTitleFrom(String address) {
        if (address.contains(title)) {
            int start = address.indexOf(",");
            if (start != -1) return address.substring(++start).trim();
        }
        return address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.apiId);
        dest.writeString(this.title);
        dest.writeString(this.address);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    protected MyPlace(Parcel in) {
        this.id = in.readLong();
        this.apiId = in.readString();
        this.title = in.readString();
        this.address = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Parcelable.Creator<MyPlace> CREATOR = new Parcelable.Creator<MyPlace>() {
        public MyPlace createFromParcel(Parcel source) {
            return new MyPlace(source);
        }

        public MyPlace[] newArray(int size) {
            return new MyPlace[size];
        }
    };
}
