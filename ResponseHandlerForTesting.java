package com.artcom.y60.http;

import java.io.OutputStream;

import org.apache.http.Header;

import com.artcom.y60.Logger;

public class ResponseHandlerForTesting implements HttpResponseHandler {

    private static final String LOG_TAG             = "ResponseHandlerForTesting";

    boolean                     areHeadersAvailable = false;
    boolean                     hasError            = false;
    boolean                     isReceiving         = false;
    boolean                     wasSuccessful       = false;

    double                      progress            = -1;
    OutputStream                body                = null;

    @Override
    public void onHeaderAvailable(Header[] headers) {
        Logger.v(LOG_TAG, "onHeaderAvailable called");
        reset();
        areHeadersAvailable = true;
    }

    @Override
    public void onError(int statusCode, OutputStream body) {
        Logger.v(LOG_TAG, "onError called");
        reset();
        hasError = true;
        this.body = body;
    }

    @Override
    public void onReceiving(double pProgress) {
        Logger.v(LOG_TAG, "onReceiving called");
        reset();
        isReceiving = true;
        progress = pProgress;
    }

    @Override
    public void onSuccess(int statusCode, OutputStream body) {
        Logger.v(LOG_TAG, "onSuccess called");
        reset();
        wasSuccessful = true;
        this.body = body;
    }

    void reset() {
        areHeadersAvailable = hasError = isReceiving = wasSuccessful = false;
        body = null;
    }
}
