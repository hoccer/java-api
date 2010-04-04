package com.artcom.y60.http;

import com.artcom.y60.TestHelper;

public class TestAsyncHttpGet extends HttpTestCase {
    
    AsyncHttpGet mHttpGet;
    
    public void testCreating() throws Exception {
        
        mHttpGet = new AsyncHttpGet(getServer().getUri());
        mHttpGet.start();
        
        assertFalse("http get should run asyncrunous", mHttpGet.isDone());
        TestHelper.blockUntilTrue("request should have been performed by now", 8000,
                new TestHelper.Condition() {
                    
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mHttpGet.isDone();
                    }
                });
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
}
