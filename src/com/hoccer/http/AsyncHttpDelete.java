package com.hoccer.http;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

public class AsyncHttpDelete extends AsyncHttpRequest {

    public AsyncHttpDelete(String pUrl) {
        super(pUrl);
    }

    public AsyncHttpDelete(String pUrl, DefaultHttpClient pHttpClient) {
        super(pUrl, pHttpClient);
    }

    @Override
    protected HttpRequestBase createRequest(String pUrl) {
        return new HttpDelete(pUrl);
    }

}
