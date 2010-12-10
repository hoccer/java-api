package com.hoccer.http;

import junit.framework.TestCase;

import com.hoccer.tools.TestHelper;

public class HttpTestCase extends TestCase {

    private MockHttpServer mServer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mServer = new MockHttpServer();
        System.gc();
    }

    @Override
    protected void tearDown() throws Exception {
        mServer.quit();
        System.gc();
        super.tearDown();
    }

    protected MockHttpServer getServer() {
        return mServer;
    }

    protected void blockUntilRequestIsDone(final AsyncHttpRequest pRequest) throws Exception {
        TestHelper.blockUntilTrue("request should have been performed by now", 3000,
                new TestHelper.Condition() {

                    @Override
                    public boolean isSatisfied() throws Exception {
                        return pRequest.isTaskCompleted();
                    }
                });
    }

    protected void blockUntilHeadersAvailable(final ResponseHandlerForTesting requestStatus)
            throws Exception {
        TestHelper.blockUntilTrue("headers should be there", 2000, new TestHelper.Condition() {

            @Override
            public boolean isSatisfied() throws Exception {
                return requestStatus.hasOnHeadersAvailableBeenCalled;
            }
        });
    }

    protected void blockUntilHeadersAvailable(final AsyncHttpRequest request) throws Exception {
        TestHelper.blockUntilTrue("headers should be there", 2000, new TestHelper.Condition() {

            @Override
            public boolean isSatisfied() throws Exception {
                return request.wasSuccessful();
            }
        });
    }

    public void testDummy() {

    }
}
