package com.hoccer.client.environment;

import java.util.Date;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.hoccer.util.HoccerLoggers;

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
	
	protected final static Logger LOG =
			HoccerLoggers.getLogger(EnvironmentProvider.class);

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
	 * Returns a timestamp for provider-specific use
	 * @return
	 */
	public long getTimestamp() {
		return new Date().getTime() / 1000L;
	}
	
	/**
	 * Subclasses should call this when they have new data
	 * 
	 * Data will be submitted at an unspecified interval
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
	
	/**
	 * Subclasses should override this to perform startup actions
	 */
	public void start() { }
	
	/**
	 * Subclasses should override this to perform shutdown actions
	 */
	public void stop() { }
	
}
