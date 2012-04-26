package com.hoccer.client;

import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.hoccer.api.ClientActionException;
import com.hoccer.api.Linccer;
import com.hoccer.util.HoccerLoggers;

/**
 * Background thread for peeking group membership
 * 
 * Created by the client to continuously update the peer list.
 * 
 * @author ingo
 */
class Peeker extends Thread {

	static final Logger LOG = HoccerLoggers.getLogger(Peeker.class);

	/** Shutdown flag for run loop */
	private boolean mShutdown;

	/** Group ID retrieved during last peek */
	private String mLastGroupId;

	/** Back-reference to client instance */
	HoccerClient mClient;

	/** Linker service for operations */
	Linccer mLinker;

	/**
	 * Creates a peeker thread
	 * 
	 * The thread will not be started.
	 * 
	 * @param pClient to report back to
	 */
	public Peeker(HoccerClient pClient) {
		mShutdown = false;
		mClient = pClient;
		mLinker = pClient.getLinker();
	}

	/**
	 * Shuts this peeker down
	 * 
	 * Blocks until peeker has joined.
	 */
	public void shutdown() {
		// set shutdown flag
		mShutdown = true;
		// interrupt current operation
		this.interrupt();
		mLinker.abortPeek();
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
			if(mShutdown) {
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
		try {
			result = mLinker.peek(mLastGroupId);
		} catch (ClientActionException e) {
			mLastGroupId = null;
			e.printStackTrace();
		}

		// if we have a result then remember group id
		if(result != null) {
			try {
				if(result.has("group_id")) {
					mLastGroupId = result.getString("group_id");
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

}