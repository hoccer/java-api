package com.artcom.y60.http;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

public class AsyncHttpPost extends AsyncHttpRequest {
    
    public AsyncHttpPost(String pUrl) {
        super(pUrl);
    }
    
    @Override
    protected HttpRequestBase createRequest(String pUrl) {
        HttpPost request = new HttpPost(pUrl);
        // insertData(request);
        return request;
    }
}
