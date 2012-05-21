package com.hoccer.client.action;

import org.json.JSONObject;

public interface ReceiveListener extends ActionListener<ReceiveAction> {

    public void receiveSucceeded(JSONObject pResult, ReceiveAction pAction);
}
