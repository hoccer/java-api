package com.artcom.y60.http;

import android.test.FlakyTest;

import com.artcom.y60.TestHelper;

public class TestAsyncHttpPost extends HttpTestCase {
    
    AsyncHttpPost mHttpPost;
    
    @FlakyTest
    public void testExecution() throws Exception {
        
        getServer().setResponseDelay(1000);
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
        assertTrue("request should be running, but progress is " + mHttpPost.getProgress() + "%",
                mHttpPost.isRunning());
        
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
    
    public void testGettingNoifiedViaResponseHandler() throws Exception {
        
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
        
    }
    
}
