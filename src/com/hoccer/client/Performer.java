package com.hoccer.client;

import java.util.logging.Logger;

import com.hoccer.api.Linccer;
import com.hoccer.client.action.Action;
import com.hoccer.util.HoccerLoggers;

public class Performer extends Thread {

	private static final Logger LOG = HoccerLoggers.getLogger(Performer.class);

	boolean mShutdown;
	
	HoccerClient mClient;

	Linccer mLinker;
	
	Action mAction;
	
	Performer(HoccerClient pClient) {
		mShutdown = false;
		mClient = pClient;
		mLinker = pClient.getLinker();
	}
	
	/**
	 * Shut down the performer
	 * 
	 * Terminates the action performance thread.
	 * 
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
	
	protected void submitAction(Action pAction) {
		if(mAction != null) {
			LOG.warning("Action already in progress, ignoring new action");
			return;
		}
		mAction = pAction;
		this.interrupt();
	}
	
	private void performAction() {
		Action action = mAction;
		if(action != null) {
			action.perform(mLinker);
			mAction = null;
		}
	}
	
	@Override
	public void run() {
		LOG.info("Performer started");

		while(!mShutdown) {
			if(mAction == null) {
				try {
					Thread.sleep(60*60*1000);
				} catch (InterruptedException e) {
					// this just means we should go ahead
				}
			}
			
			if(mShutdown) {
				break;
			}
			
			performAction();
			
			if(mShutdown) {
				break;
			}
		}
		
		LOG.info("Performer terminated");
	}

}
