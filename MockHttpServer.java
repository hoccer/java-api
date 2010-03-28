package com.artcom.y60.http;

import java.io.IOException;
import java.util.Properties;

import com.artcom.y60.Logger;

public class MockHttpServer extends NanoHTTPD {
    
    private static String LOG_TAG = "HttpServerForTesting";
    private static int    PORT    = 4000;
    
    public MockHttpServer() throws IOException {
        super(PORT);
    }
    
    @Override
    public Response serve(String uri, String method, Properties header, Properties parms) {
        Logger.v(LOG_TAG, method, " '", uri, "' ");
        String msg = "I'm a mock server for test purposes";
        return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, msg);
    }
    
    public String getUri() {
        return "http://localhost:" + PORT + "/";
    }
    
}
