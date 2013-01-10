package com.hoccer.client.environment;

import java.util.Enumeration;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.hoccer.client.HoccerClient;

public class EnvironmentManager {

	HoccerClient mClient;

	long mLastLatency;
	
	Vector<EnvironmentProvider> mProviders;

	public EnvironmentManager(HoccerClient pClient) {
		mClient = pClient;
		mProviders = new Vector<EnvironmentProvider>();
	}

	public synchronized void registerEnvironmentProvider(EnvironmentProvider pProvider) {
		mProviders.add(pProvider);
		pProvider.setManager(this);
		providerDataChanged(pProvider);
	}
	
	public synchronized void start() {
		for(EnvironmentProvider provider : mProviders) {
			provider.start();
		}
	}
	
	public synchronized void stop() {
		for(EnvironmentProvider provider : mProviders) {
			provider.stop();
		}
	}

	public synchronized JSONObject buildEnvironment() {
		JSONObject environment = new JSONObject();
		
		// build basic client data
		try {
			environment.put("client_name", mClient.getClientName());
			environment.put("latency", mLastLatency);
			String channel = mClient.getChannel();
			if(channel != null) {
				JSONObject channelRoot = new JSONObject();
				channelRoot.put("name", channel);
				environment.put("channel", channelRoot);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// build provider data
		Enumeration<EnvironmentProvider> providers = mProviders.elements();
		while(providers.hasMoreElements()) {
			EnvironmentProvider provider = providers.nextElement();
			try {
				provider.updateEnvironment(environment);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return environment;
	}
	
	public synchronized void updateLatency(long pLatency) {
		mLastLatency = pLatency;
	}

	protected synchronized void providerDataChanged(EnvironmentProvider pProvider) {
		mClient.triggerSubmitter();
	}

}
