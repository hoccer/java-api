package com.artcom.y60.http;

import java.io.IOException;

import org.apache.http.HttpResponse;

public class HttpServerException extends HttpException {

    public HttpServerException(HttpResponse pResponse) throws IOException {
        super(pResponse);
    }

    public HttpServerException(int pStatusCode) {
        super(pStatusCode);
    }

}
