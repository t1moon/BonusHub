package com.techpark.BonusHub;

/**
 * Created by mike on 27.09.17.
 */

public class Location {
    private String address;
    private double latitude;
    private double longitude;

    public Location(String addr, double lat, double lon) {
        address = addr;
        latitude = lat;
        longitude = lon;
    }

    public String getAddress() {
        return address;
    }

    public double getLongtitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
