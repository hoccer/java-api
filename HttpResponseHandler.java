package com.artcom.y60.http;

import org.apache.http.Header;

import com.artcom.y60.data.StreamableContent;

public interface HttpResponseHandler {

    public void onHeaderAvailable(Header[] headers);

    public void onSuccess(int statusCode, StreamableContent body);

    public void onError(int statusCode, StreamableContent body);

    public void onReceiving(double progress);
}
