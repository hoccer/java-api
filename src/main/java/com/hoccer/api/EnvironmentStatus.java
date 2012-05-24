package com.hoccer.api;

import org.json.JSONException;
import org.json.JSONObject;

public class EnvironmentStatus {

    private int        mQuality           = 0;
    private int        mDevices           = 0;

    private int        mCoordinateQuality = 0;
    private String     mCoordinateInfo    = "no_data";

    private int        mWifiQuality       = 0;
    private String     mWifiInfo          = "no_data";
    private final long mCreatedAt;

    public EnvironmentStatus(JSONObject statusResponse) throws JSONException {

        mQuality = statusResponse.getInt("quality");
        mDevices = statusResponse.getInt("devices");

        mCoordinateQuality = statusResponse.getJSONObject("coordinates").getInt("quality");
        mCoordinateInfo = statusResponse.getJSONObject("coordinates").getString("info");

        mWifiQuality = statusResponse.getJSONObject("wifi").getInt("quality");
        mWifiInfo = statusResponse.getJSONObject("wifi").getString("info");

        mCreatedAt = System.currentTimeMillis();
    }

    public long getCreationTime() {
        return mCreatedAt;
    }

    public int getQuality() {
        return mQuality;
    }

    public int getDevices() {
        return mDevices;
    }

    public int getCoordinateQuality() {
        return mCoordinateQuality;
    }

    public String getCoordinateInfo() {
        return mCoordinateInfo;
    }

    public int getWifiQuality() {
        return mWifiQuality;
    }

    public String getWifiInfo() {
        return mWifiInfo;
    }
}
