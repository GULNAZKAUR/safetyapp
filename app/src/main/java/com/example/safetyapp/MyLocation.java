package com.example.safetyapp;

public class MyLocation {

    double latitude = 31, longitude = 75;

    public MyLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public MyLocation() {

    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

