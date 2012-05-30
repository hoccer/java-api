package com.hoccer.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.hoccer.api.ApiSigningTools;
import com.hoccer.api.ClientConfig;
import com.hoccer.client.environment.EnvironmentManager;
import com.hoccer.util.HoccerLoggers;

/**
 * Environment submitter
 * 
 * The client creates an instance of this to care about
 * submitting the environment at regular intervals.
 * 
 * @author ingo
 *
 */
final class Submitter extends ClientThread {

	// XXX move elsewhere
    protected String sign(String url) {
        return ApiSigningTools.sign(url, mConfig.getApiKey(), mConfig.getSharedSecret());
    }
	
	
	private static final Logger LOG = HoccerLoggers.getLogger(Submitter.class);

	/** Minimum time to wait between submissions */
	private static final int RESUBMIT_DELAY = 1000;

	/** Fixed base delay on failure backoff */
	private static final int BACKOFF_FIXED_DELAY = 5000;

	/** Random extra delay on failure backoff */
	private static final int BACKOFF_RANDOM_DELAY = 5000;

	/** Fixed base delay for autosubmission after success */
	private static final int AUTOSUBMIT_FIXED_DELAY = 15000;

	/** Random extra delay before autosubmission after success */
	private static final int AUTOSUBMIT_RANDOM_DELAY = 5000;

	/** Back-reference to client */
	HoccerClient mClient;
	
	ClientConfig mConfig;

	EnvironmentManager mEnvironmentManager;

	/**
	 * Time of earliest allowable resubmission
	 */
	Date mNotBefore;
	
	long mLastLatency;
	JSONObject mLastStatus;

	/**
	 * Default constructor
	 * 
	 * Initializes the submitter without starting it.
	 * 
	 * @param pClient
	 */
	public Submitter(HoccerClient pClient) {
		super(pClient.getHttpClient(), LOG);
		mClient = pClient;
		mConfig = pClient.getConfig();
		mEnvironmentManager = pClient.getEnvironmentManager();
	}

	/**
	 * Trigger an environment update at the next possible time
	 * 
	 * Note that environment submission is always limited by RESUBMIT_DELAY.
	 */
	public void trigger() {

        LOG.info("Submitter triggered");
		this.interrupt();
	}

	/**
	 * Shut down the submitter
	 * 
	 * Terminates the submission thread.
	 * 
	 * The client environment will be retracted/deleted during shutdown.
	 */
	public void shutdown() {
		// abort operations
		abortThread();
		// loop until joined
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

	@Override
	public void run() {
		LOG.info("Submitter started");

		// make sure we submit an environment on start
		mNotBefore = new Date();

		// submit the environment repeatedly
		while(true) {
			// obey the not-before time
			waitForNotBefore();

			// abort when shutting down
			if(checkAbort()) {
				break;
			}

			// construct the environment to be sent
			JSONObject environment = mEnvironmentManager.buildEnvironment();

			// abort when shutting down
			if(checkAbort()) {
				break;
			}

			// submit the environment
			boolean success = false;
			if(environment != null) {
				success = submitEnvironment(environment);
				mEnvironmentManager.updateLatency(mLastLatency);
			}

			// abort when shutting down
			if(checkAbort()) {
				break;
			}

			// wait for next submission cycle
			waitForNextCycle(success);

			// abort when shutting down
			if(checkAbort()) {
				break;
			}
		};
		
		retractEnvironment();

		LOG.info("Submitter stopped");
	}

	private boolean submitEnvironment(JSONObject environment) {
		boolean success = false;
		LOG.fine("Submitting environment");

		// perform the request
		success = submitEnvironmentRequest(environment);

		// compute new not-before time
		Date now = new Date();
		mNotBefore = new Date(now.getTime() + RESUBMIT_DELAY);
		LOG.fine("Next submission not before " + RESUBMIT_DELAY + " msecs pass");

		return success;
	}

	private boolean submitEnvironmentRequest(JSONObject environment) {
		String uri = mConfig.getClientUri() + "/environment";
		
		long startTime, endTime;

		LOG.fine("Submitting to " + uri);
		
		// compose the request
		HttpPut request = new HttpPut(sign(uri));
		HttpResponse response = null;
		
		// serialize and encode the environment
		try {

            LOG.fine("Environment: " + environment.toString());
			request.setEntity(new StringEntity(environment.toString(), "UTF-8"));

		} catch (UnsupportedEncodingException e) {
			// should not happen
			e.printStackTrace();
		}
		
		// perform the request
		startTime = System.currentTimeMillis();
		response = executeRequest(request);
		endTime = System.currentTimeMillis();
		
		// deal with the response
		if(response != null) {
			int statusCode = response.getStatusLine().getStatusCode();
			
            LOG.info("Submission returned code " + statusCode);
			
			// we only accept CREATED responses as success
			if(statusCode == HttpStatus.SC_CREATED) {
				JSONObject status = responseToJSON(response);
				
				// returned status must be valid
				if(status != null) {					
					LOG.fine("Submission status " + status.toString());

					// update status and last latency
					mLastStatus = status;
					mLastLatency = endTime - startTime;
					
					// submission succeeded
					return true;
				}
			} else {

                String responseStr = "<couldn't read HTTP response body>";

                try {

                    InputStream in = response.getEntity().getContent();
                    InputStreamReader isr = new InputStreamReader(in);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder builder = new StringBuilder();
                    String line = null;

                    while ((line = br.readLine()) != null) {
                        builder.append(line + "\n");
                    }

                    br.close();

                    responseStr = builder.toString();

                } catch (Throwable t) {

                    LOG.warning("While trying to read the response body: " + t.getMessage());
                }

                LOG.warning("Submission returned unknown status code " + statusCode + ", body was: " + responseStr);
			}
		}
		
		// submission failed in all other cases
		return false;
	}

	private void retractEnvironment() {
		// retract/delete environment from server
		LOG.info("Retracting environment");
		
		// compose URI
		String uri = mConfig.getClientUri() + "/environment";

		// sign URI and compose request
		HttpDelete request = new HttpDelete(sign(uri));
		
		// execute request, ignore results
		if(executeRequest(request) == null) {
			LOG.warning("Failed to retract environment");
		}
	}

	private void waitForNotBefore() {
		Date now = new Date();
		// loop until earliest possible time reached
		while(now.before(mNotBefore)) {
			// compute new delay
			now = new Date();
			double delay = mNotBefore.getTime() - now.getTime();
			// abort when shutting down
			if(checkAbort()) {
				break;
			}
			// might have blocked, so re-verify delay
			if(delay > 0.0) {
				// log about it
				LOG.fine("Too early for resubmission, waiting " + delay + " msecs");
				// try to sleep as long as required
				try {
					Thread.sleep(Math.round(delay));
				} catch (InterruptedException e) {
					// ignore and continue
				}
			}
		}
	}

	private void waitForNextCycle(boolean cycleWasSuccessful) {
		double waitingTime;
		// determine how long to wait depending on success
		if(cycleWasSuccessful) {
			waitingTime = AUTOSUBMIT_FIXED_DELAY + (AUTOSUBMIT_RANDOM_DELAY * Math.random());
			LOG.fine("Submission succeeded, next submission in " + waitingTime + " msecs");
		} else {
			waitingTime = BACKOFF_FIXED_DELAY + (BACKOFF_RANDOM_DELAY * Math.random());
			LOG.warning("Submission failed, backing off for " + waitingTime + " msecs"); 
		}
		// sleep, allowing trigger() to interrupt
		if(waitingTime > 0.0) {
			try {
				Thread.sleep(Math.round(waitingTime));
			} catch (InterruptedException e) {
				// ignore and continue
				// this means that we got triggered
			}
		}
	}

}