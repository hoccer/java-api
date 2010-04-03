package com.artcom.y60.http;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPut;

public class AsyncHttpPut extends AsyncHttpRequest {
    
    public AsyncHttpPut(String pUrl) {
        super(pUrl);
    }
    
    @Override
    protected HttpEntityEnclosingRequestBase createRequest(String pUrl) {
        HttpPut request = new HttpPut(pUrl);
        return request;
    }
}
