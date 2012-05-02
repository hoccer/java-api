package com.hoccer.client.environment;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * GPS provider for statically positioned clients
 * 
 * This provider has a static location which it always considers current.
 * 
 * @author ingo
 */
public class StaticGPSProvider extends EnvironmentProvider {

	static final String TAG = "gps";
	
	boolean mValid;
	
	double mLatitude;
	double mLongitude;
	int mAccuracy;
	
	public StaticGPSProvider() {
		super("gps");
		
		mValid = false;
	}

	public synchronized void updatePosition(
			double pLatitude, double pLongitude, int pAccuracy) {
		
		mLatitude = pLatitude;
		mLongitude = pLongitude;
		mAccuracy = pAccuracy;
		
		mValid = true;
		
		dataChanged();
	}
	
	public double getTimestamp() {
		return new Date().getTime() / 1000L;
	}
	
	@Override
	public synchronized void updateEnvironment(JSONObject pEnvironment)
			throws JSONException {
		// bail out if we don't have a location
		if(!mValid) {
			return;
		}
		
		// create our node
		JSONObject gps = new JSONObject();
		
		// add static location data
		gps.put("latitude", mLatitude);
		gps.put("longitude", mLongitude);
		gps.put("accuracy", mAccuracy);
		
		// add current timestamp
		gps.put("timestamp", getTimestamp());
		
		// attach gps node to environment
		pEnvironment.put(TAG, gps);
	}
	
}
