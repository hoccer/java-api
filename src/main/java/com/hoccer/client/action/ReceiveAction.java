package com.hoccer.client.action;

import org.json.JSONObject;

import com.hoccer.api.BadModeException;
import com.hoccer.api.ClientActionException;
import com.hoccer.api.CollidingActionsException;
import com.hoccer.api.Linccer;

public class ReceiveAction extends Action<ReceiveListener> {

	public ReceiveAction(Mode pMode, ReceiveListener pListener) {
		super(Type.RECEIVE, pMode, pListener);
	}

	@Override
	public void perform(Linccer pLinker) {
		JSONObject result = null;
		
		LOG.info("Performing receive");
		
		try {
			result = pLinker.receive(getModeString());
		} catch (BadModeException e) {
			onActionFailed();
			e.printStackTrace();
		} catch (ClientActionException e) {
			onActionFailed();
			e.printStackTrace();
		} catch (CollidingActionsException e) {
			onActionCollided();
			e.printStackTrace();
		}
		
		if(result == null) {
			LOG.info("Receive expired");
			onActionExpired();
		} else {
			LOG.info("Receive succeeded: " + result.toString());
		}
	}
	
}
