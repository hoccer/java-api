package com.hoccer.client.environment;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StaticWifiProvider extends EnvironmentProvider {

	static final String TAG = "wifi";
	
	Vector<String> mBSSIDs = new Vector<String>();
	
	public StaticWifiProvider() {
		super(TAG);
	}
	
	public synchronized void updateBSSIDs(List<String> pBSSIDs) {
		mBSSIDs = new Vector<String>(pBSSIDs);
		dataChanged();
	}
	
	@Override
	public synchronized void updateEnvironment(JSONObject pEnvironment)
			throws JSONException {
		// bail out if we don't have any information
		if(mBSSIDs.size() < 1) {
			return;
		}
		
		// create our node
		JSONObject wlan = new JSONObject();
		
		// add array of bssids
		JSONArray bssids = new JSONArray();
		Enumeration<String> e = mBSSIDs.elements();
		while(e.hasMoreElements()) {
			String bssid = e.nextElement();
			bssids.put(bssid);
		}
		wlan.put("bssids", bssids);
		
		// add current timestamp
		wlan.put("timestamp", getTimestamp());
		
		// attach gps node to environment
		pEnvironment.put(TAG, wlan);
	}
	
}
