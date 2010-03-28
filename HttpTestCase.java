package com.artcom.y60.http;

import android.test.AndroidTestCase;

public class HttpTestCase extends AndroidTestCase {
    
    private MockHttpServer mServer;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mServer = new MockHttpServer();
    }
    
    @Override
    protected void tearDown() throws Exception {
        mServer.quit();
        super.tearDown();
    }
    
    protected MockHttpServer getServer() {
        return mServer;
    }
    
}
