package com.artcom.y60.http;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public abstract class AsyncHttpRequestWithBody extends AsyncHttpRequest {
    
    public AsyncHttpRequestWithBody(String pUrl) {
        super(pUrl);
    }
    
    private void setData(String pStringData) {
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
