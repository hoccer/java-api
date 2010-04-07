package com.artcom.y60.http;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class AsyncHttpPost extends AsyncHttpRequestWithBody {
    
    public AsyncHttpPost(String pUrl) {
        super(pUrl);
    }
    
    public AsyncHttpPost(String pUrl, DefaultHttpClient pHttpClient) {
        super(pUrl, pHttpClient);
    }
    
    @Override
    protected HttpEntityEnclosingRequestBase createRequest(String pUrl) {
        HttpPost request = new HttpPost(pUrl);
        return request;
    }
    
}
