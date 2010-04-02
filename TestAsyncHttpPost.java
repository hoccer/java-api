package com.artcom.y60.http;

import android.test.suitebuilder.annotation.Suppress;

import com.artcom.y60.TestHelper;

public class TestAsyncHttpPost extends HttpTestCase {
    
    AsyncHttpPost mHttpPost;
    
    public void testCreating() throws Exception {
        
        mHttpPost = new AsyncHttpPost(getServer().getUri());
        mHttpPost.start();
        
        TestHelper.blockUntilTrue("request should have been performed by now", 3000,
                new TestHelper.Condition() {
                    
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mHttpPost.wasSuccessful();
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
                        return mHttpPost.wasSuccessful();
                    }
                });
        assertTrue("should be successful", requestStatus.wasSuccessful);
        
    }
}
