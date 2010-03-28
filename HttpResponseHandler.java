package com.artcom.y60.http;

import org.apache.http.HttpResponse;

public interface HttpResponseHandler {
    
    public void onConnecting();
    
    public void onSuccess(HttpResponse response);
    
    public void onError(HttpResponse response);
    
    public void onReceiving(double progress);
}
