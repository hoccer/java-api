package com.artcom.y60.http;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public abstract class AsyncHttpRequestWithBody extends AsyncHttpRequest {
    
    public AsyncHttpRequestWithBody(String pUrl) {
        super(pUrl);
    }
    
    public AsyncHttpRequestWithBody(String pUrl, HttpClient pHttpClient) {
        super(pUrl, pHttpClient);
    }
    
    public void setBody(String pStringData) {
        HttpHelper.insert(pStringData, "text/txt", getRequest());
    }
    
    @Override
    protected HttpEntityEnclosingRequestBase getRequest() {
        return (HttpEntityEnclosingRequestBase) super.getRequest();
    }
    
    // private void setData(HttpEntity entity) {
    // mData = entity;
    // }
    //    
    // private void setData(InputStream iStream, String pContentType) {
    // mData = new InputStreamEntity(iStream, 1000);
    // mContentType = pContentType;
    // }
    
}
