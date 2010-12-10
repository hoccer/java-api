package com.hoccer.http;

import java.util.HashMap;

import com.hoccer.data.StreamableContent;

public interface HttpResponseHandler {

    public void onHeaderAvailable(HashMap<String, String> headers);

    public void onSuccess(int statusCode, StreamableContent body);

    public void onError(int statusCode, StreamableContent body);

    public void onReceiving(double progress);

    public void onSending(double progress);

    public void onError(Exception e);
}
