package com.hoccer.client;

import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

import com.hoccer.api.ClientConfig;
import com.hoccer.util.HoccerLoggers;

/**
 * Background thread for peeking group membership
 * 
 * Created by the client to continuously update the peer list.
 * 
 * @author ingo
 */
class Peeker extends ClientThread {

	static final Logger LOG = HoccerLoggers.getLogger(Peeker.class);

	/** Shutdown flag for run loop */
	private boolean mShutdown;

	/** Group ID retrieved during last peek */
	private String mLastGroupId;

	/** Back-reference to client instance */
	HoccerClient mClient;

	/** */
	ClientConfig mConfig;

	/**
	 * Creates a peeker thread
	 * 
	 * The thread will not be started.
	 * 
	 * @param pClient to report back to
	 */
	public Peeker(HoccerClient pClient) {
		super(pClient.getHttpClient(), LOG);
		mShutdown = false;
		mClient = pClient;
		mConfig = pClient.getConfig();
	}

	/**
	 * Shuts this peeker down
	 * 
	 * Blocks until peeker has joined.
	 */
	public void shutdown() {
		// initiate abort
		this.abortThread();
		// wait for thread to finish
		boolean joined = false;
		while(!joined) {
			try {
				this.join();
				joined = true;
			} catch (InterruptedException e) {
				// ignore and repeat
			}
		}
	}

	/**
	 * Peeking loop
	 */
	@Override
	public void run() {
		LOG.info("Peeker started");

		// until commanded to shut down
		while(!mShutdown) {
			JSONObject peekResult;

			// try to peek
			peekResult = peek();

			// abort when shutting down
			if(checkAbort()) {
				break;
			}

			// if peeking failed
			if(peekResult == null) {
				// then back off before next peek
				LOG.warning("Peek failed");
				backOff();
			} else {
				// else process the response
				LOG.fine("Peek returned");
				mClient.peekResult(peekResult);
			}
		}

		LOG.info("Peeker terminated");
	}

	/**
	 * Peek operation
	 * @return JSON result or null
	 */
	private JSONObject peek() {
		JSONObject result = null;

		// log peeking operation
		if(mLastGroupId == null) {
			LOG.fine("Peeking without previous group");
		} else {
			LOG.fine("Peeking based on group " + mLastGroupId);
		}

		// block in peek operation 
		result = peekRequest(mLastGroupId);

		// if we have a result then remember the group
		// id for the next peek. on failure, we forget
		// the group id.
		if(result == null) {
			mLastGroupId = null;
		} else {
			try {
				if(result.has("group_id")) {
					mLastGroupId = result.getString("group_id");
				} else {
					mLastGroupId = null;
				}
			} catch (JSONException e) {
				mLastGroupId = null;
				e.printStackTrace();
			}
		}

		// return raw results
		return result;
	}

	/**
	 * Back off after failure
	 */
	private void backOff() {
		double backOffTime = 5000.0 + (5000.0 * Math.random());

		LOG.fine("Backing off for "
				+ Double.toString(backOffTime) + " msecs");

		try {
			Thread.sleep(Math.round(backOffTime));
		} catch (InterruptedException e) {
			// ignore and continue
		}
	}

	private JSONObject peekRequest(String lastGroupId) {
		String uri = mConfig.getClientUri() + "/peek";

		if(lastGroupId != null) {
			uri += "?group_id=" + lastGroupId;
		}

		while(true) {
			LOG.fine("Peeking with URI " + uri);

			HttpRequestBase request = new HttpGet(uri);

			HttpResponse response = executeRequest(request);
			
			// return on failure
			if(response == null) {
				return null;
			}

			// get the status code
			int statusCode = response.getStatusLine().getStatusCode();

			// when we got a result then return it
			if(statusCode == HttpStatus.SC_OK) {
				return responseToJSON(response);
			}

			// if we timed out then retry immediately
			if(statusCode == HttpStatus.SC_GATEWAY_TIMEOUT) {
				continue;
			}

			// fail when getting anything else
			return null;
		}
	}

}