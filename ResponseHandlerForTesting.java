package com.artcom.y60.http;

import java.io.OutputStream;

public class ResponseHandlerForTesting implements HttpResponseHandler {
    
    boolean      isConnecting  = false;
    boolean      hasError      = false;
    boolean      isReceiving   = false;
    boolean      wasSuccessful = false;
    
    double       progress      = -1;
    OutputStream body          = null;
    
    @Override
    public void onConnecting() {
        reset();
        isConnecting = true;
    }
    
    @Override
    public void onError(int statusCode, OutputStream body) {
        reset();
        hasError = true;
        this.body = body;
    }
    
    @Override
    public void onReceiving(double pProgress) {
        reset();
        isReceiving = true;
        progress = pProgress;
    }
    
    @Override
    public void onSuccess(int statusCode, OutputStream body) {
        reset();
        wasSuccessful = true;
        this.body = body;
    }
    
    void reset() {
        isConnecting = hasError = isReceiving = wasSuccessful = false;
        body = null;
    }
}
