package com.artcom.y60.http;

import org.apache.http.HttpResponse;

public class HttpClientException extends HttpException {

    public HttpClientException(HttpResponse pResponse) {
        super(pResponse);
    }

    public HttpClientException(int pStatusCode) {
        super(pStatusCode);
    }

}
