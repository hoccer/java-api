package com.artcom.y60.http;

import android.test.UiThreadTest;

import com.artcom.y60.TestHelper;

public class TestAsyncHttpGet extends HttpTestCase {
    
    AsyncHttpGet mHttpGet;
    
    @UiThreadTest
    public void testCreating() throws Exception {
        
        mHttpGet = new AsyncHttpGet();
        mHttpGet.execute(getServer().getUri());
        
        assertFalse("http get should run asyncrunous", mHttpGet.hasFinished());
        TestHelper.blockUntilTrue("request should have been performed by now", 8000,
                new TestHelper.Condition() {
                    
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mHttpGet.hasFinished();
                    }
                });
        
    }
}
