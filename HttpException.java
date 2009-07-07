package com.artcom.y60.http;

import org.apache.http.HttpResponse;

public abstract class HttpException extends Exception {

    // Static Methods ----------------------------------------------------

    public static void throwIfError(HttpResponse pResponse) throws HttpServerException,
                    HttpClientException {

        int status = pResponse.getStatusLine().getStatusCode();
        if (status >= 400 && status < 500) {
            throw new HttpClientException(pResponse);
        }
        if (status >= 500 && status < 600) {
            throw new HttpServerException(pResponse);
        }
    }

    // Instance Variables ------------------------------------------------

    private int mStatusCode;

    private HttpResponse mResponse;

    public HttpException(int pStatusCode) {

        super("HTTP failed with status code " + pStatusCode);
        mStatusCode = pStatusCode;
    }

    public HttpException(HttpResponse pResponse) {

        super("HTTP failed with status code " + pResponse.getStatusLine().getStatusCode());
        mStatusCode = pResponse.getStatusLine().getStatusCode();
        mResponse = pResponse;
    }

    // Public Instance Methods -------------------------------------------

    public int getStatusCode() {

        return mStatusCode;
    }

    public HttpResponse getHttpResponse() {

        return mResponse;
    }

    public boolean hasHttpResponse() {

        return mResponse != null;
    }
}
