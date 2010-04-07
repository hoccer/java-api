package com.artcom.y60.http;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

public class AsyncHttpGet extends AsyncHttpRequest {
    
    public AsyncHttpGet(String pUrl) {
        super(pUrl);
    }
    
    public AsyncHttpGet(String pUrl, DefaultHttpClient pHttpClient) {
        super(pUrl, pHttpClient);
    }
    
    @Override
    protected HttpRequestBase createRequest(String pUrl) {
        HttpGet request = new HttpGet(pUrl);
        return request;
    }
    
}
