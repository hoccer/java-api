/*******************************************************************************
 * Copyright (C) 2009, 2010, Hoccer GmbH Berlin, Germany <www.hoccer.com>
 * 
 * These coded instructions, statements, and computer programs contain
 * proprietary information of Hoccer GmbH Berlin, and are copy protected
 * by law. They may be used, modified and redistributed under the terms
 * of GNU General Public License referenced below. 
 *    
 * Alternative licensing without the obligations of the GPL is
 * available upon request.
 * 
 * GPL v3 Licensing:
 * 
 * This file is part of the "Linccer Java-API".
 * 
 * Linccer Java-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Linccer Java-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Linccer Java-API. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.hoccer.api;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public void setWifiMeasurement(List<String> bssids, Date date) {
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
        private final Collection<String> bssids;
        private final Date               timestamp;

        public WifiMeasurement(List<String> bssids, Date timestamp) {
            this.bssids = bssids;
            this.timestamp = timestamp;
        }

        public JSONObject toJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put("bssids", new JSONArray(this.bssids));

            json.put("timestamp", timestamp.getTime() / 1000L);
            return json;
        }
    }
}
