package com.hoccer.client.action;


public interface SendListener extends ActionListener<SendAction> {

    public void sendSucceeded(SendAction pAction);
}