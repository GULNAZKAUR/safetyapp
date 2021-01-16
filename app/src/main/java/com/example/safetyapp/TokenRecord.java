package com.example.safetyapp;

public class TokenRecord {
    String devicetoken;

    public TokenRecord(String devicetoken) {
        this.devicetoken = devicetoken;
    }

    public TokenRecord() {
    }

    public String getDevicetoken() {
        return devicetoken;
    }

    public void setDevicetoken(String devicetoken) {
        this.devicetoken = devicetoken;
    }
}
