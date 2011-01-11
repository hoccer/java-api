package com.hoccer.api;

import org.json.JSONException;
import org.json.JSONObject;

public class EnvironmentStatus {

    private int mQuality = 0;
    private int mDevices = 0;

    public EnvironmentStatus(JSONObject statusResponse) throws JSONException {
        mQuality = statusResponse.getInt("quality");
        mDevices = statusResponse.getInt("devices");
    }

    public int getQuality() {
        return mQuality;
    }

    public int getDevices() {
        return mDevices;
    }
}
