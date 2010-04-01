package com.artcom.y60.http;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

public class AsyncHttpGet extends AsyncHttpRequest {
    
    @Override
    protected HttpRequestBase createRequest(String pUrl) {
        HttpGet request = new HttpGet(pUrl);
        return request;
    }
    
}
