package com.artcom.y60.http;

import java.io.IOException;

import org.apache.http.HttpResponse;

public class HttpClientException extends HttpException {

    public HttpClientException(String pUrl, HttpResponse pResponse) throws IOException {
        super(pUrl, pResponse);
    }

    public HttpClientException(String pUrl, int pStatusCode) {
        super(pUrl, pStatusCode);
    }
}
