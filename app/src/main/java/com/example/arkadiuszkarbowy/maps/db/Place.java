package com.example.arkadiuszkarbowy.maps.db;

/**
 * Created by arkadiuszkarbowy on 29/09/15.
 */
public class Place {
    private long id;
    private String title;
    private String address;
    private double latitude;
    private double longitude;

    public Place(){

    }
    public Place(long id, String title, String address, double latitude, double longitude) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
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
        return latitude + ":" + longitude;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
