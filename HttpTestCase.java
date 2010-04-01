package com.artcom.y60.http;

import android.test.InstrumentationTestCase;

public class HttpTestCase extends InstrumentationTestCase {
    
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
