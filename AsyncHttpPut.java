package com.artcom.y60.http;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

public class AsyncHttpPut extends AsyncHttpRequest {
    
    @Override
    protected HttpRequestBase createRequest(String pUrl) {
        HttpPut request = new HttpPut(pUrl);
        insertData(request);
        return request;
    }
}
