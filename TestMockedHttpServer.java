package com.artcom.y60.http;

import java.net.SocketException;

import com.artcom.y60.HttpHelper;

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
    
    public void testHttpPost() throws Exception {
        assertEquals("our mock server should respond to POST", "hello mock server", HttpHelper
                .post(getServer().getUri(), "message=hello mock server", "txt/text", "txt/text"));
    }
}
