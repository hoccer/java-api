package com.hoccer.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ClientThread extends Thread {

	private final Logger LOG;

	HttpClient mClient;

	boolean mShutdown;

	Object mRequestLock;
	HttpRequestBase mRequest;

	public ClientThread(HttpClient pClient, Logger pLogger) {
		LOG = pLogger;

		mClient = pClient;
		
		mShutdown = false;

		mRequestLock = new Object();
		mRequest = null;
	}

	public void abortThread() {
		mShutdown = true;
		this.abortRequest();
		this.interrupt();
	}

	protected boolean checkAbort() {
		return mShutdown;
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

	protected String responseToString(HttpResponse pResponse) {
		String result = null;
		HttpEntity entity = pResponse.getEntity();

		if(entity != null) {
			long contentLength = entity.getContentLength();

			if(contentLength > 2048) {
				LOG.warning("Response is larger than 2048 bytes, ignoring.");
			} else {
				try {
					result = EntityUtils.toString(entity, HTTP.UTF_8);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}

		return result;
	}

	protected JSONObject responseToJSON(HttpResponse pResponse) {
		JSONObject result = null;

		String string = responseToString(pResponse);

		if(string != null) {
			JSONTokener tokener = new JSONTokener(string);
			Object parseResult = null;

			try {
				parseResult = tokener.nextValue();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if(parseResult != null) {
				if(parseResult instanceof JSONObject) {
					result = (JSONObject) parseResult;
				} else {
					LOG.warning("Expected a JSON object, got " + parseResult.toString());
				}
			}
		}

		return result;
	}

}
