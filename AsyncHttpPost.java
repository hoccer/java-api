package com.artcom.y60.http;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;

public class AsyncHttpPost extends AsyncHttpRequest {
    
    public AsyncHttpPost(String pUrl) {
        super(pUrl);
    }
    
    @Override
    protected HttpEntityEnclosingRequestBase createRequest(String pUrl) {
        HttpPost request = new HttpPost(pUrl);
        return request;
    }
    
}
