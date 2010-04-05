package com.artcom.y60.http;

import org.apache.http.impl.client.DefaultHttpClient;

import com.artcom.y60.TestHelper;

public class TestAsyncHttpGet extends HttpTestCase {
    
    AsyncHttpGet mHttpGet;
    
    private void assertRequestIsDone() throws Exception {
        TestHelper.blockUntilTrue("request should have been performed by now", 1000,
                new TestHelper.Condition() {
                    
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mHttpGet.isDone();
                    }
                });
    }
    
    public void testCreating() throws Exception {
        
        mHttpGet = new AsyncHttpGet(getServer().getUri());
        mHttpGet.start();
        
        assertFalse("http get should run asyncrunous", mHttpGet.isDone());
        assertRequestIsDone();
    }
    
    public void testGettingResultFromRequestObject() throws Exception {
        
        mHttpGet = new AsyncHttpGet(getServer().getUri());
        mHttpGet.start();
        
        TestHelper.blockUntilEquals("request should respond with a text", 2000,
                "I'm a mock server for test purposes", new TestHelper.Measurement() {
                    
                    @Override
                    public Object getActualValue() throws Exception {
                        return mHttpGet.getBodyAsString();
                    }
                });
    }
    
    public void testGettingResultWithoutPerformingTheRequest() throws Exception {
        
        mHttpGet = new AsyncHttpGet(getServer().getUri());
        TestHelper.blockUntilEquals("request should give empty body before started", 1000, "",
                new TestHelper.Measurement() {
                    
                    @Override
                    public Object getActualValue() throws Exception {
                        return mHttpGet.getBodyAsString();
                    }
                });
    }
    
    public void testGettingNoifiedAboutSuccessViaResponseHandler() throws Exception {
        
        getServer().setResponseDelay(200);
        mHttpGet = new AsyncHttpGet(getServer().getUri());
        ResponseHandlerForTesting requestStatus = new ResponseHandlerForTesting();
        mHttpGet.registerResponseHandler(requestStatus);
        mHttpGet.start();
        Thread.sleep(50);
        assertTrue("request should have started, but is at " + mHttpGet.getProgress() + "%",
                mHttpGet.isRunning());
        assertTrue("should be connecting", requestStatus.isConnecting);
        assertRequestIsDone();
        assertTrue("should be successful", requestStatus.wasSuccessful);
        assertNotNull("should have an response body", requestStatus.body);
        assertEquals("response should come from mocked server",
                "I'm a mock server for test purposes", requestStatus.body.toString());
    }
    
    public void testGettingNoifiedAboutFailureViaResponseHandler() throws Exception {
        
        mHttpGet = new AsyncHttpGet(getServer().getUri() + "/not-a-valid-address");
        final ResponseHandlerForTesting requestStatus = new ResponseHandlerForTesting();
        mHttpGet.registerResponseHandler(requestStatus);
        mHttpGet.start();
        TestHelper.blockUntilTrue("request should have called onError", 2000,
                new TestHelper.Condition() {
                    
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return requestStatus.hasError;
                    }
                });
        assertFalse("should not be successful", requestStatus.wasSuccessful);
        assertNotNull("should have no response body", requestStatus.body);
        assertEquals("response from mocked server should describe 404", "Not Found",
                requestStatus.body.toString());
    }
    
    public void testDefaultUserAgentStringInRequest() throws Exception {
        mHttpGet = new AsyncHttpGet(getServer().getUri());
        mHttpGet.start();
        assertRequestIsDone();
        assertEquals("User-Agent string in HTTP header shuld be y60", "Y60/1.0 Android",
                getServer().getLastRequest().header.getProperty("user-agent"));
    }
    
    public void testDefaultUserAgentStringInRequestWithCustomHttpClient() throws Exception {
        mHttpGet = new AsyncHttpGet(getServer().getUri(), new DefaultHttpClient());
        mHttpGet.start();
        assertRequestIsDone();
        assertEquals("User-Agent string in HTTP header shuld be y60", "Y60/1.0 Android",
                getServer().getLastRequest().header.getProperty("user-agent"));
    }
}
