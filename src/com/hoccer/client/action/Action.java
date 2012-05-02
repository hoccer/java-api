package com.hoccer.client.action;

import java.util.logging.Logger;

import com.hoccer.api.Linccer;
import com.hoccer.util.HoccerLoggers;

public abstract class Action {

	static protected final Logger LOG = HoccerLoggers.getLogger(Action.class);
	
	public enum Type {
		SEND, RECEIVE
	}
	
	public enum Mode {
		ONE_TO_ONE, ONE_TO_MANY
	}
	
	Type mType;
	Mode mMode;
	
	ActionListener mListener;
	
	protected Action(Type pType, Mode pMode, ActionListener pListener) {
		mType = pType;
		mMode = pMode;
		mListener = pListener;
	}
	
	abstract public void perform(Linccer pLinker);
	
	protected String getModeString() {
		switch(mMode) {
		case ONE_TO_ONE: return "1:1";
		case ONE_TO_MANY: return "1:n";
		}
		return null;
	}
	
	protected void onActionFailed() {
		mListener.actionFailed(this);
	}
	
	protected void onActionCollided() {
		mListener.actionCollided(this);
	}
	
	protected void onActionExpired() {
		mListener.actionExpired(this);
	}
	
}
