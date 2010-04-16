package com.artcom.y60.http;

import java.io.OutputStream;

import org.apache.http.Header;

public interface HttpResponseHandler {

    public void onHeaderAvailable(Header[] headers);

    public void onSuccess(int statusCode, OutputStream body);

    public void onError(int statusCode, OutputStream body);

    public void onReceiving(double progress);
}
