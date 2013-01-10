package com.hoccer.client;

import org.json.JSONException;
import org.json.JSONObject;

public class HoccerPeer {	
	
	String mPublicId;
	String mName;
	
	public HoccerPeer(String pPublicId) {
		mPublicId = pPublicId;
	}
	
	public String getPublicId() {
		return mPublicId;
	}
	
	public String getName() {
		return mName;
	}
	
	void updateFromGroupEntry(JSONObject pGroupEntry)
			throws JSONException {
		String name = pGroupEntry.getString("name");
		
		mName = name;
	}
	
	public String toString() {
		return mPublicId + "/" + mName;
	}
	
}
