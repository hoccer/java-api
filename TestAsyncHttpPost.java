package com.artcom.y60.http;

import android.test.suitebuilder.annotation.Suppress;

import com.artcom.y60.TestHelper;

public class TestAsyncHttpPost extends HttpTestCase {
    
    AsyncHttpPost mHttpPost;
    
    public void testExecution() throws Exception {
        
        mHttpPost = new AsyncHttpPost(getServer().getUri());
        mHttpPost.start();
        
        TestHelper.blockUntilTrue("request should have started by now", 1000,
                new TestHelper.Condition() {
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mHttpPost.getProgress() > 0;
                    }
                });
        
        TestHelper.blockUntilTrue("request should have got the response by now", 1000,
                new TestHelper.Condition() {
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mHttpPost.getProgress() > 1;
                    }
                });
        
        TestHelper.blockUntilTrue("request should have been performed by now", 2000,
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
    
    @Suppress
    public void testGettingNoified() throws Exception {
        
        mHttpPost = new AsyncHttpPost(getServer().getUri());
        ResponseHandlerForTesting requestStatus = new ResponseHandlerForTesting();
        // mHttpPost.registerAsyncResponseHandler(requestStatus);
        mHttpPost.run();
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
