package com.hoccer.client;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

public class ClientThread extends Thread {

	private final Logger LOG;
	
	HttpClient mClient;
	
	Object mRequestLock;
		
	HttpRequestBase mRequest;
	
	public ClientThread(Logger pLogger) {
		LOG = pLogger;
		
		mRequestLock = new Object();
		mRequest = null;
	}
	
	private boolean beginRequest(HttpRequestBase pRequest) {
		boolean success;
		synchronized(mRequestLock) {
			if(mRequest != null) {
				LOG.warning("Trying to perform requests in parallel, ignoring new request");
				success = false;
			} else {
				mRequest = pRequest;
				success = true;
			}
		}
		return success;
	}
	
	private void finishRequest() {
		synchronized (mRequestLock) {
			mRequest = null;
		}
	}
	
	protected HttpRequest abortRequest() {
		HttpRequestBase abortedRequest = null;
		synchronized(mRequestLock) {
			abortedRequest = mRequest;
			abortedRequest.abort();
			mRequest = null;
		}
		return abortedRequest;
	}
	
	protected HttpResponse executeRequest(HttpRequestBase pRequest) {
		HttpResponse response = null;
		if(beginRequest(pRequest)) {
			try {
				response = mClient.execute(pRequest);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			finishRequest();
		}
		return response;
	}
	
}
