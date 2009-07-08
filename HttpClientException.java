package com.artcom.y60.http;

import java.io.IOException;

import org.apache.http.HttpResponse;

public class HttpClientException extends HttpException {

    public HttpClientException(HttpResponse pResponse) throws IOException {
        super(pResponse);
    }

    public HttpClientException(int pStatusCode) {
        super(pStatusCode);
    }

}
