package com.hoccer.client;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;

import com.hoccer.api.Linccer;
import com.hoccer.api.UpdateException;
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
final class Submitter extends Thread {

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

	/** Shutdown flag for threaded run loop */
	boolean mShutdown;

	/** Back-reference to client */
	HoccerClient mClient;

	/** Linker service used for submission */
	Linccer      mLinker;

	/**
	 * Time of earliest allowable resubmission
	 */
	Date mNotBefore;

	/**
	 * Default constructor
	 * 
	 * Initializes the submitter without starting it.
	 * 
	 * @param pClient
	 */
	public Submitter(HoccerClient pClient) {
		LOG.setLevel(Level.FINE);
		mShutdown = false;
		mClient = pClient;
		mLinker = pClient.getLinker();
	}

	/**
	 * Trigger an environment update at the next possible time
	 * 
	 * Note that environment submission is always limited by RESUBMIT_DELAY.
	 */
	public void trigger() {
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
		// set shutdown flag
		mShutdown = true;
		// interrupt operations
		this.interrupt();
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
		while(!mShutdown) {
			// obey the not-before time
			waitForNotBefore();
			
			// abort when shutting down
			if(mShutdown) {
				break;
			}
			
			// submit the environment
			boolean success = submitEnvironment();
			
			// abort when shutting down
			if(mShutdown) {
				break;
			}
			
			// wait for next submission cycle
			waitForNextCycle(success);
			
			// abort when shutting down
			if(mShutdown) {
				break;
			}
		};

		// retract/delete environment from server
		LOG.info("Retracting environment");
		try {
			mLinker.disconnect();
		} catch (UpdateException e) {
			e.printStackTrace();
		}

		LOG.info("Submitter stopped");
	}

	private boolean submitEnvironment() {
		boolean success = false;
		LOG.info("Submitting environment");
		try {
			// perform the submission itself
			mLinker.submitEnvironment();

			// mark iteration as success
			success = true;

			// compute not-before time
			Date now = new Date();
			mNotBefore = new Date(now.getTime() + RESUBMIT_DELAY);
			LOG.fine("Next submission not before " + RESUBMIT_DELAY + " msecs pass");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UpdateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}

	private void waitForNotBefore() {
		Date now = new Date();
		while(now.before(mNotBefore)) {
			// compute new delay
			now = new Date();
			double delay = mNotBefore.getTime() - now.getTime();
			// abort when shutting down
			if(mShutdown) {
				break;
			}
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

	private void waitForNextCycle(boolean cycleWasSuccessful) {
		double waitingTime;
		if(cycleWasSuccessful) {
			waitingTime = AUTOSUBMIT_FIXED_DELAY + (AUTOSUBMIT_RANDOM_DELAY * Math.random());
			LOG.fine("Submission succeeded, next submission in " + waitingTime + " msecs");
		} else {
			waitingTime = BACKOFF_FIXED_DELAY + (BACKOFF_RANDOM_DELAY * Math.random());
			LOG.warning("Submission failed, backing off for " + waitingTime + " msecs"); 
		}
		// XXX better handling for trigger()
		try {
			Thread.sleep(Math.round(waitingTime));
		} catch (InterruptedException e) {
			// ignore and continue
		}
	}

}