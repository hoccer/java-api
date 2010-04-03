package com.artcom.y60.http;

import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.Suppress;

import com.artcom.y60.TestHelper;

public class TestAsyncHttpPost extends HttpTestCase {
    
    AsyncHttpPost mHttpPost;
    
    @UiThreadTest
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
        
        TestHelper.blockUntilTrue("request should have been performed by now", 8000,
                new TestHelper.Condition() {
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mHttpPost.isDone();
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
