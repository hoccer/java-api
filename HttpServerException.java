package com.artcom.y60.http;

import org.apache.http.HttpResponse;

public class HttpServerException extends HttpException {

    public HttpServerException(HttpResponse pResponse) {
        super(pResponse);
    }

    public HttpServerException(int pStatusCode) {
        super(pStatusCode);
    }

}
