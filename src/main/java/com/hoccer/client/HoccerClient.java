package com.hoccer.client;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hoccer.api.ClientConfig;
import com.hoccer.api.Linccer;
import com.hoccer.client.action.Action;
import com.hoccer.client.environment.EnvironmentManager;
import com.hoccer.client.environment.EnvironmentProvider;
import com.hoccer.util.HoccerLoggers;

/**
 * Entry point to the Hoccer client API
 * 
 * @author ingo
 */
public final class HoccerClient {

	static final Logger LOG     = HoccerLoggers.getLogger(HoccerClient.class);

	private static final int STATE_INITIALIZED = 0;
	private static final int STATE_READY       = 1;
	private static final int STATE_RUNNING     = 2;

	/** Current client state */
	private int mState;

	/** Display name of the client */
	private String mName;

	/** Service URL and API key configuration */
	private ClientConfig mConfig;

	/** Table of currently known peers */
	private HashMap<String, HoccerPeer> mPeersByPublicId;

	/** Linker service used by this client */
	private Linccer mLinker;
	
	/** HTTP client magic */
	private HoccerHttp mHttp;

	/** Performer for this client */
	private Performer mPerformThread;
	/** Peeker for this client */
	private Peeker mPeekThread;
	/** Submitter for this client */
	private Submitter mSubmitThread;

	/** Environment manager for this client */
	private EnvironmentManager mEnvironmentManager;

	/** Peer data listeners */
	private Vector<PeerListener> mPeerListeners;

	/**
	 * Default constructor
	 * 
	 * Initializes the client without side effects.
	 */
	public HoccerClient() {
		mName = null;

		mState = STATE_INITIALIZED;

		mPeerListeners = new Vector<PeerListener>();

		mPeersByPublicId = new HashMap<String, HoccerPeer>();
	}

	/**
	 * Internal method for accessing the linker
	 * 
	 * May return null if the client is not initialized.
	 * 
	 * Our worker threads are only running when there
	 * is a linker, so they can trust not to receive null.
	 * 
	 * @return the linker used by this client or null
	 */
	protected Linccer getLinker() {
		return mLinker;
	}
	
	protected ClientConfig getConfig() {
		return mConfig;
	}
	
	protected HttpClient getHttpClient() {
		return mHttp.getClient();
	}

	protected EnvironmentManager getEnvironmentManager() {
		return mEnvironmentManager;
	}
	
	protected Submitter getSubmitter() {
		return mSubmitThread;
	}

	public String getClientName() {
		return mName;
	}

	public String getClientId() {
		return mConfig.getClientId().toString();
	}

	/**
	 * Set the name to be used by this client
	 * 
	 * Can always be called regardless of client state.
	 * 
	 * @param pName
	 */
	public synchronized void setClientName(String pName) {
		mName = pName;
		triggerSubmitter();
	}

	/**
	 * Return a list of all currently known peers
	 * @return list of peers
	 */
	public synchronized Vector<HoccerPeer> getPeers() {
		return new Vector<HoccerPeer>(mPeersByPublicId.values());
	}

	public synchronized JSONObject getEnvJSON() {
		if(mLinker != null) {
			return mEnvironmentManager.buildEnvironment();
		}
		return null;
	}

	/**
	 * Configure the client to use the given client config
	 * 
	 * @param pConfig to use
	 */
	public synchronized void configure(ClientConfig pConfig) {
		if(mState != STATE_INITIALIZED) {
			LOG.warning("Client already configured, ignoring configuration");
			return;
		}

		LOG.info("Configuring client");

		mConfig = pConfig;

		mEnvironmentManager = new EnvironmentManager(this);

		mLinker = new Linccer(mConfig);
		mLinker.autoSubmitEnvironmentChanges(false);
		
		mHttp = new HoccerHttp();

		if(mName == null) {
			mName = "<" + pConfig.getApplicationName() + ">";
		}

		mState = STATE_READY;
	}

	/**
	 * Start the client, initiating submission and peeking
	 */
	public synchronized void start() {
		if(mState != STATE_READY) {
			if(mState == STATE_RUNNING) {
				LOG.warning("Client already running, ignoring request to start");
			} else {
				LOG.warning("Client not ready, ignoring request to start");
			}
			return;
		}

		LOG.info("Starting client");

		mState = STATE_RUNNING;

		LOG.info("Starting submitter");
		mSubmitThread = new Submitter(this);
		mSubmitThread.start();
		
		LOG.info("Starting peeker");
		mPeekThread = new Peeker(this);
		mPeekThread.start();

		LOG.info("Starting performer");
		mPerformThread = new Performer(this);
		mPerformThread.start();

	}

	/**
	 * Stop the client, stopping submission and peeking
	 */
	public synchronized void stop() {
		if(mState != STATE_RUNNING) {
			LOG.warning("Client not running, ignoring request to stop");
			return;
		}

		LOG.info("Stopping client");

		LOG.info("Shutting down performer");
		mPerformThread.shutdown();
		mPerformThread = null;
		
		LOG.info("Shutting down peeker");
		mPeekThread.shutdown();
		mPeekThread = null;

		LOG.info("Shutting down submitter");
		mSubmitThread.shutdown();
		mSubmitThread = null;

		LOG.info("Executing shutdown actions");
		shutdownActions();

		mHttp.dropClient();
		
		LOG.info("Client has stopped");
		mState = STATE_READY;
	}
	
	public synchronized void perform(Action pAction) {
		mPerformThread.submitAction(pAction);
	}

	/**
	 * Register the given environment provider
	 */
	public void registerEnvironmentProvider(EnvironmentProvider pProvider) {
		mEnvironmentManager.registerEnvironmentProvider(pProvider);
	}

	/**
	 * Listener interface for monitoring peer data
	 */
	public interface PeerListener {
		void peerAdded(HoccerPeer peer);
		void peerRemoved(HoccerPeer peer);
		void peerUpdated(HoccerPeer peer);
	}

	/**
	 * Register the given peer listener
	 * @param pListener to register
	 */
	public synchronized void registerPeerListener(PeerListener pListener) {
		mPeerListeners.add(pListener);
	}

	/**
	 * Unregister the given peer listener
	 * @param pListener to unregister
	 */
	public synchronized void unregisterPeerListener(PeerListener pListener) {
		mPeerListeners.remove(pListener);
	}

	/**
	 * Callback from peeker
	 * 
	 * This method is called from the peeker thread to update the client
	 * and all its listeners with information from the given peek response.
	 * 
	 * It will parse and analyze the response and call registered listeners.
	 * 
	 * @param peekResponse to act upon
	 */
	protected synchronized void peekResult(JSONObject peekResponse) {
		LOG.fine("Got new peek result " + peekResponse.toString());

		// collect set of peeked peers to track removals
		HashSet<HoccerPeer> peekedPeers = new HashSet<HoccerPeer>();

		// collect list of added/removed/kept peers for actions
		Vector<HoccerPeer> peersAdded = new Vector<HoccerPeer>();
		Vector<HoccerPeer> peersRemoved = new Vector<HoccerPeer>();
		Vector<HoccerPeer> peersKept = new Vector<HoccerPeer>();

		// parse peek result
		try {
			// get the group data
			JSONArray group = peekResponse.getJSONArray("group");

			// report number of peers
			LOG.fine("Peek returned " + group.length() + " peers");

			// parse peers one by one
			for(int i = 0; i < group.length(); i++) {
				JSONObject groupPeer = group.getJSONObject(i);

				try {
					HoccerPeer peer;

					String publicIdString = groupPeer.getString("id");	

					// do we know this peer?
					if(mPeersByPublicId.containsKey(publicIdString)) {
						// peer was known and will be kept
						peer = mPeersByPublicId.get(publicIdString);
						peersKept.add(peer);
					} else {
						// peer was unknown and will be added
						peer = new HoccerPeer(publicIdString);
						peersAdded.add(peer);
					}

					// update peer fields from group entry
					peer.updateFromGroupEntry(groupPeer);

					// remember the peer
					peekedPeers.add(peer);
				} catch (JSONException e) {
					LOG.warning("Failed to parse peeked group entry " + groupPeer.toString());
				}
			}
		} catch (JSONException e) {
			LOG.warning("Failed to parse peeked group " + peekResponse.toString());
		}

		// find out which peers have been removed
		Iterator<HoccerPeer> it = mPeersByPublicId.values().iterator();
		while(it.hasNext()) {
			HoccerPeer p = it.next();
			if(!peekedPeers.contains(p)) {
				peersRemoved.add(p);
			}
		}

		// execute add/remove/keep callbacks
		peekActions(peersAdded, peersRemoved, peersKept);
	}

	/**
	 * Internal method for dispatching actions for a peek response
	 * 
	 * @param peersAdded
	 * @param peersRemoved
	 * @param peersKept
	 */
	private void peekActions(
			Vector<HoccerPeer> peersAdded,
			Vector<HoccerPeer> peersRemoved,
			Vector<HoccerPeer> peersKept) {
		Enumeration<HoccerPeer> peers;
		Enumeration<PeerListener> listeners;

		// actions for newly added peers
		peers = peersAdded.elements();
		while(peers.hasMoreElements()) {
			HoccerPeer peer = peers.nextElement();

			LOG.fine("Peer " + peer.getName() + "/" + peer.getPublicId() + " added");

			// add peer to client table
			mPeersByPublicId.put(peer.getPublicId(), peer);

			// call listeners
			listeners = mPeerListeners.elements();
			while(listeners.hasMoreElements()) {
				PeerListener l = listeners.nextElement();
				l.peerAdded(peer);
			}
		}

		// actions for removed peers
		peers = peersRemoved.elements();
		while(peers.hasMoreElements()) {
			HoccerPeer peer = peers.nextElement();

			LOG.fine("Peer " + peer.getName() + "/" + peer.getPublicId() + " removed");

			// remove peer from client table
			mPeersByPublicId.remove(peer.getPublicId());

			// call listeners
			listeners = mPeerListeners.elements();
			while(listeners.hasMoreElements()) {
				PeerListener l = listeners.nextElement();
				l.peerRemoved(peer);
			}
		}

		// actions for updated/unchanged peers
		peers = peersKept.elements();
		while(peers.hasMoreElements()) {
			HoccerPeer peer = peers.nextElement();

			LOG.fine("Peer " + peer.getName() + "/" + peer.getPublicId() + " kept");

			// call listeners
			listeners = mPeerListeners.elements();
			while(listeners.hasMoreElements()) {
				PeerListener l = listeners.nextElement();
				l.peerUpdated(peer);
			}
		}
	}

	/**
	 * Internal method for actions performed on shutdown
	 */
	private void shutdownActions() {
		// call listeners to remove all peers
		Iterator<HoccerPeer> peers;
		peers = mPeersByPublicId.values().iterator();
		while(peers.hasNext()) {
			HoccerPeer peer = peers.next();
			Enumeration<PeerListener> listeners;
			listeners = mPeerListeners.elements();
			while(listeners.hasMoreElements()) {
				PeerListener listener = listeners.nextElement();
				listener.peerRemoved(peer);
			}
		}
		// clear the peer database
		mPeersByPublicId.clear();
	}

	public synchronized void triggerSubmitter() {
		if(mSubmitThread != null) {
			mSubmitThread.trigger();
		}
	}

}
