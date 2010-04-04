package com.artcom.y60.http;

import java.io.OutputStream;

public interface HttpResponseHandler {
    
    public void onConnecting();
    
    public void onSuccess(int statusCode, OutputStream body);
    
    public void onError(int statusCode, OutputStream body);
    
    public void onReceiving(double progress);
}
