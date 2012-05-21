package com.hoccer.client.action;

public interface ActionListener<A extends Action> {

	/**
	 * Called when the server reports a collision of actions
	 * @param pAction that collided
	 */
    public void actionCollided(A pAction);
	
	/**
	 * Called when the action could not be submitted
	 * @param pAction that failed
	 */
    public void actionFailed(A pAction);
	
	/**
	 * Called when the action expires without starting a transfer
	 * @param pAction that expired
	 */
    public void actionExpired(A pAction);
	
	/**
	 * Called when the action is aborted by the client
	 * @param pAction that was aborted
	 */
    public void actionAborted(A pAction);
	
}
