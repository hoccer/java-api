package com.hoccer.http;

import java.util.HashMap;

import com.hoccer.data.StreamableContent;

public class ResponseHandlerForTesting implements HttpResponseHandler {

    private static final String LOG_TAG                         = "ResponseHandlerForTesting";

    public boolean              areHeadersAvailable             = false;
    public boolean              hasOnHeadersAvailableBeenCalled = false;
    public boolean              hasError                        = false;
    public boolean              isReceiving                     = false;
    public boolean              wasSuccessful                   = false;

    public double               progress                        = -1;
    public StreamableContent    body                            = null;

    @Override
    public void onHeaderAvailable(HashMap<String, String> headers) {
        reset();
        areHeadersAvailable = true;
        hasOnHeadersAvailableBeenCalled = true;
    }

    @Override
    public void onError(int statusCode, StreamableContent body) {
        reset();
        hasError = true;
        this.body = body;
    }

    @Override
    public void onReceiving(double pProgress) {
        progress = pProgress;
    }

    @Override
    public void onSending(double progress) {
        this.progress = progress;
    }

    @Override
    public void onSuccess(int statusCode, StreamableContent body) {
        reset();
        wasSuccessful = true;
        this.body = body;
    }

    void reset() {
        areHeadersAvailable = hasError = isReceiving = wasSuccessful = false;
        body = null;
    }

    @Override
    public void onError(Exception e) {
        hasError = true;
    }

}
