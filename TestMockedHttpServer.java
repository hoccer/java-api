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

    public void test404HttpGet() throws Exception {

        boolean gotTheError = false;
        try {
            HttpHelper.getAsString(getServer().getUri() + "/not-existing");
        } catch (HttpClientException e) {
            gotTheError = e.getHttpResponse().getStatusLine().getStatusCode() == 404;
        }
        assertTrue("our mock server should respond with 404 'not found'", gotTheError);
    }

    public void testDelayedHttpGet() throws Exception {

        getServer().setResponseDelay(1000);
        long time = System.currentTimeMillis();
        HttpHelper.getAsString(getServer().getUri());

        TestHelper.assertGreater("our mock server should respond to GET", 1000, System
                .currentTimeMillis()
                - time);
    }

    public void testHttpPostWithParametersInBody() throws Exception {
        assertEquals("our mock server should respond to POST", "hello mock server", HttpHelper
                .post(getServer().getUri(), "message=hello mock server",
                        "application/x-www-form-urlencoded", "text/plain"));
    }

    public void testHttpPostWithParametersInUrl() throws Exception {
        assertEquals("our mock server should respond to POST", "hello", HttpHelper.post(getServer()
                .getUri()
                + "/?message=hello", "", "application/x-www-form-urlencoded", "text/plain"));
    }

    public void testHttpPostWithStringDataInBody() throws Exception {
        assertEquals("mock server should store text which was posted in request body",
                "this is plain string data, as said by the content-type header", HttpHelper.post(
                        getServer().getUri(),
                        "this is plain string data, as said by the content-type header",
                        "text/plain", "text/plain"));

    }

    public void testEmptyHttpPost() throws Exception {
        assertEquals("our mock server should respond to an empty POST", "no data posted",
                HttpHelper.post(getServer().getUri(), "", "", "text/plain"));
    }

    public void test404HttpPost() throws Exception {
        boolean gotTheError = false;
        try {
            HttpHelper.post(getServer().getUri() + "/not-existing", "hello mock server",
                    "text/plain", "text/plain");
        } catch (HttpClientException e) {
            gotTheError = e.getHttpResponse().getStatusLine().getStatusCode() == 404;
        }
        assertTrue("our mock server should respond with 404 'not found'", gotTheError);
    }

    public void testHttpPut() throws Exception {
        String uri = getServer().getUri() + "/test-resource";
        HttpHelper.putAsString(uri, "I've been putted");
        assertEquals("our mock server should receive the PUT request", "PUT", getServer()
                .getLastRequest().method);
        assertEquals(
                "our mock server should store and serve the putted text at the provided location",
                "I've been putted", HttpHelper.getAsString(uri));
    }

    public void testHttpPutToRoot() throws Exception {
        boolean hadExpectedException = false;
        try {
            HttpHelper.putAsString(getServer().getUri(), "I've been putted");
        } catch (HttpClientException e) {
            hadExpectedException = true;
            assertEquals("mock server should response with forbidden", 403, e.getStatusCode());
        }
        assertTrue("our mock server should deny creating a resource at /", hadExpectedException);
    }

    public void testPuttingUnknownMimeTypedData() throws Exception {

        boolean hadExpectedException = false;
        String uri = getServer().getUri() + "/test-resource";
        try {
            HttpHelper.putAsString(uri, "I've been putted", "text/unknown");
        } catch (HttpServerException e) {
            hadExpectedException = true;
            assertEquals("mock server should respond with not implemted", 501, e.getStatusCode());
        }
        assertTrue("our mock server should not be able to create resource", hadExpectedException);
    }

    public void testPuttingXmlMimeTypedData() throws Exception {

        String uri = getServer().getUri() + "/test-resource";
        HttpHelper.putAsString(uri, "I've been putted", "text/xml");
        assertEquals("our mock server should receive the PUT request", "PUT", getServer()
                .getLastRequest().method);
        assertEquals(
                "our mock server should store and serve the putted xml data at the provided location",
                "I've been putted", HttpHelper.getAsString(uri));
    }

}
