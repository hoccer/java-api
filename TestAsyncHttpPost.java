package com.artcom.y60.http;

import com.artcom.y60.TestHelper;

public class TestAsyncHttpPost extends HttpTestCase {
    
    AsyncHttpPost mHttpPost;
    
    public void testCreating() throws Exception {
        
        mHttpPost = new AsyncHttpPost();
        mHttpPost.execute(getServer().getUri());
        
        TestHelper.blockUntilTrue("request should have been performed by now", 3000,
                new TestHelper.Condition() {
                    
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mHttpPost.hasFinished();
                    }
                });
    }
    
    public void testGettingNoified() throws Exception {
        
        mHttpPost = new AsyncHttpPost();
        ResponseHandlerForTesting requestStatus = new ResponseHandlerForTesting();
        mHttpPost.registerAsyncResponseHandler(requestStatus);
        mHttpPost.execute(getServer().getUri());
        assertTrue("should be connecting", requestStatus.isConnecting);
        
        TestHelper.blockUntilTrue("request should have been performed by now", 3000,
                new TestHelper.Condition() {
                    
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mHttpPost.hasFinished();
                    }
                });
        assertTrue("should be successful", requestStatus.wasSuccessful);
        
    }
}
