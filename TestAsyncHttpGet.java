package com.artcom.y60.http;

import com.artcom.y60.TestHelper;

public class TestAsyncHttpGet extends HttpTestCase {
    
    AsyncHttpGet mHttpGet;
    
    public void testCreating() throws Exception {
        
        mHttpGet = new AsyncHttpGet(getServer().getUri());
        mHttpGet.start();
        
        assertFalse("http get should run asyncrunous", mHttpGet.wasSuccessful());
        TestHelper.blockUntilTrue("request should have been performed by now", 8000,
                new TestHelper.Condition() {
                    
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mHttpGet.wasSuccessful();
                    }
                });
        
    }
}
