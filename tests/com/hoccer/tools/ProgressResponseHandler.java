package com.hoccer.tools;

import java.util.HashMap;

import com.hoccer.data.StreamableContent;
import com.hoccer.http.HttpResponseHandler;

public abstract class ProgressResponseHandler implements HttpResponseHandler {

    @Override
    public void onError(int statusCode, StreamableContent body) {
    }

    @Override
    public void onError(Exception e) {
    }

    @Override
    public void onHeaderAvailable(HashMap<String, String> headers) {
    }

    @Override
    public void onSuccess(int statusCode, StreamableContent body) {
    }

}
