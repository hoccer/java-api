package com.hoccer.client.action;

import com.hoccer.api.Linccer;

public class SendAction extends Action {

	public SendAction(Mode pMode, SendListener pListener) {
		super(Type.SEND, pMode, pListener);
	}

	@Override
	public void perform(Linccer pLinker) {
		// TODO Auto-generated method stub
		
	}
	
}
