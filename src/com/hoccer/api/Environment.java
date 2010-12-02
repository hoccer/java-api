package com.hoccer.api;

import java.util.*;

import org.json.*;

public class Environment {

    private LocationMeasurement mGpsMeasurement;
    private LocationMeasurement mNetworkMeasurement;
    private WifiMeasurement     mWifiMeasurement;

    /**
     * Save location which is obtained from the gps unit.
     */
    public void setGpsMeasurement(double latitude, double longitude, int accuracy, Date timestamp) {
        mGpsMeasurement = new LocationMeasurement(latitude, longitude, accuracy, timestamp);
    }

    /**
     * Save location which is obtained from cell-towers and wifi accesspoints.
     */
    public void setNetworkMeasurement(double latitude, double longitude, int accuracy,
            Date timestamp) {
        mNetworkMeasurement = new LocationMeasurement(latitude, longitude, accuracy, timestamp);
    }

    public void setWifiMeasurement(String[] bssids, Date date) {
        mWifiMeasurement = new WifiMeasurement(bssids, date);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject environment = new JSONObject();
        if (mGpsMeasurement != null) {
            environment.put("gps", mGpsMeasurement.toJson());
        }
        if (mNetworkMeasurement != null) {
            environment.put("network", mNetworkMeasurement.toJson());
        }
        if (mWifiMeasurement != null) {
            environment.put("wifi", mWifiMeasurement.toJson());
        }
        return environment;
    }

    private class LocationMeasurement {
        private final double latitude;
        private final double longitude;
        private final int    accuracy;
        private final Date   timestamp;

        public LocationMeasurement(double latitude, double longitude, int accuracy, Date timestamp) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.accuracy = accuracy;
            this.timestamp = timestamp;
        }

        public JSONObject toJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put("latitude", latitude);
            json.put("longitude", longitude);
            json.put("accuracy", accuracy);
            json.put("timestamp", timestamp.getTime() / 1000L);
            return json;
        }
    }

    private class WifiMeasurement {
        private final String[] bssids;
        private final Date     timestamp;

        public WifiMeasurement(String[] bssids, Date timestamp) {
            this.bssids = bssids;
            this.timestamp = timestamp;
        }

        public JSONObject toJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put("bssids", new JSONArray(bssids));

            json.put("timestamp", timestamp.getTime() / 1000L);
            return json;
        }
    }
}
