/*******************************************************************************
 * Copyright (C) 2009, 2010, Hoccer GmbH Berlin, Germany <www.hoccer.com>
 * 
 * These coded instructions, statements, and computer programs contain
 * proprietary information of Linccer GmbH Berlin, and are copy protected
 * by law. They may be used, modified and redistributed under the terms
 * of GNU General Public License referenced below. 
 *    
 * Alternative licensing without the obligations of the GPL is
 * available upon request.
 * 
 * GPL v3 Licensing:
 * 
 * This file is part of the "Linccer Java-API".
 * 
 * Linccer Java-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Linccer Java-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Linccer Java-API. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.hoccer.http;

import junit.framework.TestCase;

import com.hoccer.tools.TestHelper;

public class AsyncHttpTestCase extends TestCase {

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
