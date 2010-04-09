package com.artcom.y60.http;

import com.artcom.y60.TestHelper;

public class TestAsyncPut extends HttpTestCase {
    
    private AsyncHttpPut mRequest;
    
    public void testExecution() throws Exception {
        
        getServer().setResponseDelay(200);
        mRequest = new AsyncHttpPut(getServer().getUri());
        mRequest.start();
        
        TestHelper.blockUntilTrue("request should have started by now", 1000,
                new TestHelper.Condition() {
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mRequest.getProgress() > 0;
                    }
                });
        assertTrue("request should have started, but progress is " + mRequest.getProgress() + "%",
                mRequest.isRunning());
        
        TestHelper.blockUntilTrue("request should have got the response by now", 4000,
                new TestHelper.Condition() {
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mRequest.getProgress() > 1;
                    }
                });
        
        assertRequestIsDone(mRequest);
    }
    
    public void testStoringStringData() throws Exception {
        String uri = getServer().getUri() + "/data";
        mRequest = new AsyncHttpPut(uri);
        mRequest.setBody("my data string");
        mRequest.start();
        assertRequestIsDone(mRequest);
        assertEquals("the putted data should be returned as answer from the server",
                "my data string", mRequest.getBodyAsString());
        assertEquals("the putted data should be getrievable via http GET", "my data string",
                HttpHelper.getAsString(uri));
        
    }
}
