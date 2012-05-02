package com.hoccer.client.action;

public interface ActionListener {

	/**
	 * Called when the server reports a collision of actions
	 * @param pAction that collided
	 */
	public void actionCollided(Action pAction);
	
	/**
	 * Called when the action could not be submitted
	 * @param pAction that failed
	 */
	public void actionFailed(Action pAction);
	
	/**
	 * Called when the action expires without starting a transfer
	 * @param pAction that expired
	 */
	public void actionExpired(Action pAction);
	
	/**
	 * Called when the action is aborted by the client
	 * @param pAction that was aborted
	 */
	public void actionAborted(Action pAction);
	
}
