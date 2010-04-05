package com.artcom.y60.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import com.artcom.y60.Logger;

public class MockHttpServer extends NanoHTTPD {
    
    private static String                     LOG_TAG          = "HttpServerForTesting";
    private static int                        PORT             = 4000;
    private final HashMap<String, Properties> mPostedResources = new HashMap<String, Properties>();
    private int                               mResponseDelay   = 0;
    private ClientRequest                     mLastRequest;
    
    public MockHttpServer() throws IOException {
        super(PORT);
    }
    
    public void setResponseDelay(int pMilliseconds) {
        mResponseDelay = pMilliseconds;
    }
    
    ClientRequest getLastRequest() {
        return mLastRequest;
    }
    
    @Override
    public Response serve(ClientRequest request) {
        Logger.v(LOG_TAG, request);
        
        mLastRequest = request;
        
        try {
            Thread.sleep(mResponseDelay);
        } catch (InterruptedException e) {
            Logger.e(LOG_TAG, e);
        }
        
        if (!request.uri.equals("/") && !mPostedResources.containsKey(request.uri)) {
            return new NanoHTTPD.Response(HTTP_NOTFOUND, MIME_PLAINTEXT, "Not Found");
        }
        
        if (request.method.equals("GET")) {
            String msg = null;
            if (mPostedResources.containsKey(request.uri)) {
                msg = mPostedResources.get(request.uri).getProperty("message",
                        "no message was given");
            } else {
                msg = "I'm a mock server for test purposes";
            }
            return new NanoHTTPD.Response(HTTP_OK, MIME_PLAINTEXT, msg);
        } else if (request.method.equals("POST")) {
            String newResource = "/" + UUID.randomUUID();
            mPostedResources.put(newResource, request.parameters);
            
            NanoHTTPD.Response response = new NanoHTTPD.Response(HTTP_REDIRECT, MIME_PLAINTEXT,
                    "see other: " + "http://localhost:" + PORT + newResource);
            response.addHeader("Location", "http://localhost:" + PORT + newResource);
            return response;
        }
        
        return new NanoHTTPD.Response(HTTP_NOTIMPLEMENTED, MIME_PLAINTEXT, "not implemented");
    }
    
    public String getUri() {
        return "http://localhost:" + PORT + "/";
    }
}
