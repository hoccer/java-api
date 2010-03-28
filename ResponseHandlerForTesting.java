package com.artcom.y60.http;

import org.apache.http.HttpResponse;

public class ResponseHandlerForTesting implements HttpResponseHandler {
    
    boolean      isConnecting  = false;
    boolean      hasError      = false;
    boolean      isReceiving   = false;
    boolean      wasSuccessful = false;
    
    double       progress      = -1;
    HttpResponse response      = null;
    
    @Override
    public void onConnecting() {
        reset();
        isConnecting = true;
    }
    
    @Override
    public void onError(HttpResponse pResponse) {
        reset();
        hasError = true;
        response = pResponse;
    }
    
    @Override
    public void onReceiving(double pProgress) {
        reset();
        isReceiving = true;
        progress = pProgress;
    }
    
    @Override
    public void onSuccess(HttpResponse pResponse) {
        reset();
        wasSuccessful = true;
        response = pResponse;
    }
    
    void reset() {
        isConnecting = hasError = isReceiving = wasSuccessful = false;
    }
}
