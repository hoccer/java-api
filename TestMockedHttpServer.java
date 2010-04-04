package com.artcom.y60.http;

import java.net.SocketException;

import com.artcom.y60.TestHelper;

public class TestMockedHttpServer extends HttpTestCase {
    
    public void testStartingAndStoppingTheServer() throws Exception {
        NanoHTTPD server = new NanoHTTPD(6066);
        assertEquals("NanoHTTP should respond", 200, HttpHelper
                .getStatusCode("http://localhost:6066"));
        server.quit();
        boolean wasRefused = false;
        try {
            HttpHelper.getStatusCode("http://localhost:6066");
        } catch (SocketException e) {
            wasRefused = true;
        }
        assertTrue("NanoHTTP should not respond", wasRefused);
    }
    
    public void testHttpGet() throws Exception {
        assertEquals("our mock server should respond to GET",
                "I'm a mock server for test purposes", HttpHelper.getAsString(getServer().getUri()));
    }
    
    public void testDelayedHttpGet() throws Exception {
        
        getServer().setResponseDelay(1000);
        long time = System.currentTimeMillis();
        HttpHelper.getAsString(getServer().getUri());
        
        TestHelper.assertGreater("our mock server should respond to GET", 1000, System
                .currentTimeMillis()
                - time);
    }
    
    public void testHttpPost() throws Exception {
        assertEquals("our mock server should respond to POST", "hello mock server", HttpHelper
                .post(getServer().getUri(), "message=hello mock server", "txt/text", "txt/text"));
    }
    
    public void testEmptyHttpPost() throws Exception {
        assertEquals("our mock server should respond to an empty POST", "no message was given",
                HttpHelper.post(getServer().getUri(), "", "", ""));
    }
    
}
