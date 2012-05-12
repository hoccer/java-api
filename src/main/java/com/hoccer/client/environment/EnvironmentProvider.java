package com.hoccer.client.environment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Base class for environment providers
 * 
 * Environment providers contribute to the client environment.
 * 
 * They are identified by their tag, which is used as the property
 * name in the environment JSON object. Each of these tags must
 * be unique, meaning that you can register only one provider per type.
 * 
 * @author ingo
 */
public abstract class EnvironmentProvider {

	/** Tag for provided information */
	private final String mTag;
	
	/** Client to notify on changes */
	private EnvironmentManager mManager;

	/**
	 * Base constructor for subclasses
	 * @param pTag of the provided information
	 */
	protected EnvironmentProvider(String pTag) {
		mTag = pTag;
	}
	
	/**
	 * Retrieve the tag of the information provided
	 * @return the tag
	 */
	public String getTag() {
		return mTag;
	}
	
	/**
	 * Internal method for retrieving the env manager
	 * @return
	 */
	protected EnvironmentManager getManager() {
		return mManager;
	}
	
	/**
	 * Internal method for setting up a back-reference to the env manager
	 * @param pManager
	 */
	protected void setManager(EnvironmentManager pManager) {
		mManager = pManager;
	}
	
	/**
	 * Subclasses should call this when they have new data
	 */
	protected void dataChanged() {
		if(mManager != null) {
			mManager.providerDataChanged(this);
		}
	}
	
	/**
	 * Subclasses should implement this to add data to the given environment
	 * @param pEnvironment
	 * @throws JSONException
	 */
	public abstract void updateEnvironment(JSONObject pEnvironment) throws JSONException;
	
}
