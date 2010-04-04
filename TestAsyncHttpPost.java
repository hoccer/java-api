package com.artcom.y60.http;

import com.artcom.y60.TestHelper;

public class TestAsyncHttpPost extends HttpTestCase {
    
    AsyncHttpPost mHttpPost;
    
    public void testExecution() throws Exception {
        
        getServer().setResponseDelay(200);
        mHttpPost = new AsyncHttpPost(getServer().getUri());
        mHttpPost.start();
        
        TestHelper.blockUntilTrue("request should have started by now", 1000,
                new TestHelper.Condition() {
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mHttpPost.getProgress() > 0;
                    }
                });
        assertTrue("request should have started, but progress is " + mHttpPost.getProgress() + "%",
                mHttpPost.isRunning());
        
        TestHelper.blockUntilTrue("request should have got the response by now", 4000,
                new TestHelper.Condition() {
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mHttpPost.getProgress() > 1;
                    }
                });
        
        TestHelper.blockUntilTrue("request should have been performed by now", 4000,
                new TestHelper.Condition() {
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mHttpPost.isDone();
                    }
                });
    }
    
    public void testEmptyPost() throws Exception {
        
        mHttpPost = new AsyncHttpPost(getServer().getUri());
        mHttpPost.start();
        
        TestHelper.blockUntilEquals("request should have been performed by now", 2000,
                "no message was given", new TestHelper.Measurement() {
                    
                    @Override
                    public Object getActualValue() throws Exception {
                        return mHttpPost.getBodyAsString();
                    }
                });
    }
    
    public void testGettingResultWithoutPerformingTheRequest() throws Exception {
        
        mHttpPost = new AsyncHttpPost(getServer().getUri());
        TestHelper.blockUntilEquals("request should give empty body before started", 1000, "",
                new TestHelper.Measurement() {
                    
                    @Override
                    public Object getActualValue() throws Exception {
                        return mHttpPost.getBodyAsString();
                    }
                });
    }
    
    public void testGettingNoifiedAbooutSuccessViaResponseHandler() throws Exception {
        
        mHttpPost = new AsyncHttpPost(getServer().getUri());
        ResponseHandlerForTesting requestStatus = new ResponseHandlerForTesting();
        mHttpPost.registerResponseHandler(requestStatus);
        mHttpPost.start();
        Thread.sleep(50);
        assertTrue("request should have started", mHttpPost.isRunning());
        assertTrue("should be connecting", requestStatus.isConnecting);
        TestHelper.blockUntilTrue("request should have been performed by now", 3000,
                new TestHelper.Condition() {
                    
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mHttpPost.isDone();
                    }
                });
        assertTrue("should be successful", requestStatus.wasSuccessful);
        assertNotNull("should have an response body", requestStatus.body);
        assertEquals("response should come from mocked server", "no message was given",
                requestStatus.body.toString());
    }
    
    public void testGettingNoifiedAbooutFailureViaResponseHandler() throws Exception {
        
        mHttpPost = new AsyncHttpPost(getServer().getUri() + "/not-a-valid-address");
        final ResponseHandlerForTesting requestStatus = new ResponseHandlerForTesting();
        mHttpPost.registerResponseHandler(requestStatus);
        mHttpPost.start();
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
    
}
