package com.artcom.y60.http;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

public class AsyncHttpGet extends AsyncHttpRequest {
    
    public AsyncHttpGet(String pUrl) {
        super(pUrl);
    }
    
    public AsyncHttpGet(String pUrl, HttpClient pHttpClient) {
        super(pUrl, pHttpClient);
    }
    
    @Override
    protected HttpRequestBase createRequest(String pUrl) {
        HttpGet request = new HttpGet(pUrl);
        return request;
    }
    
}
